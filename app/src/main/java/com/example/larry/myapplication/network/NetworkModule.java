package com.example.larry.myapplication.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.HttpProcessException;
import com.android.volley.HttpRequest;
import com.android.volley.Processor;
import com.android.volley.TaskCenter;
import com.android.volley.TaskHandle;
import com.android.volley.VolleyRequest;
import com.android.volley.base.NetworkResponse;
import com.android.volley.toolbox.VolleyTickle;
import com.example.larry.myapplication.entity.DataModule;
import com.example.larry.myapplication.utils.AppUrl;
import com.example.larry.myapplication.utils.UILApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by chenlijian on 2015/8/12.
 * 网络模块
 */
public class NetworkModule {

    public static final int RequestWithoutLogin = HttpProcessException.createCustomErrorCode();
    public static final int BadResponse = HttpProcessException.createCustomErrorCode();
    public static final int BadMethod = HttpProcessException.createCustomErrorCode();

    private static final Boolean DEBUG = true;

    private static final String API_URL = "http://api.chihuo888.com/";

    public static final String ACTION_USER_LOGIN_CHANGED = "action.com.example.larry.myapplication.loginStateChanged";

    /****************************************************Volley请求***********************************************/

    /**
     * Volley的String类型的Post请求，成功返回请求Tag和包含JSON内容的CHBRsp,失败返回VolleyError
     */


    private class MyPostString implements Processor<DataModule> {

        @Override
        public DataModule process(final HttpRequest request) throws HttpProcessException {
            NetworkResponse response = VolleyRequest.RequestPostTickle(context,
                    request.getUrl(), request.getRequestTag(), request.getHashMap(), null);

            if (response.statusCode == 200) {
                String result = VolleyTickle.parseResponse(response);
                try {
                    if (DEBUG) Log.e("response", request.getUrl() + result);
//          在这里解析sever返回的数据包
                    JSONObject obj = new JSONObject(result);
                    return new DataModule(obj.getInt("total"),
                            "",
                            obj.isNull("rows") ? null : obj.get("rows"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }


    private class VolleyPostString implements Processor<DataModule> {

        @Override
        public DataModule process(final HttpRequest request) throws HttpProcessException {

            if (DEBUG)
                Log.e("post", request.getUrl() + (request.getHashMap() == null ? "" : request.getHashMap().toString()));

            NetworkResponse response = VolleyRequest.RequestPostTickle(context,
                    request.getUrl(), request.getRequestTag(), request.getHashMap(), null);

            if (response.statusCode == 200) {
                String result = VolleyTickle.parseResponse(response);
                try {
                    if (DEBUG) Log.e("response", request.getUrl() + result);
//          在这里解析sever返回的数据包
                    JSONObject obj = new JSONObject(result);

                    return new DataModule(obj.getInt("code"),
                            obj.getString("str"),
                            obj.isNull("data") ? null : obj.get("data"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }




    /**
     * 需要身份认证的请求类
     */
    private class OauthVolleyPostString implements Processor<DataModule> {
        @Override
        public DataModule process(HttpRequest request) throws HttpProcessException {
            // TODO Auto-generated method stub

            request.addParameter("token",/* getAccessToken(null)*/null);

            DataModule rsp = volleyPostString.process(request);

            if (rsp.str().equals("token验证失败")) {
//                logoutUser();
                throw new HttpProcessException("token验证失败!", RequestWithoutLogin);
            }
            return rsp;
        }
    }

    /**
     * 有后续process请求类(比如登陆成功后请求用户用户信息)
     */
    private class NameVolleyPostString implements Processor<DataModule> {

        private String methodName;
        private Processor<DataModule> p;

        NameVolleyPostString(String name, Processor<DataModule> processor) {
            methodName = name;
            p = processor;
        }

        @Override
        public DataModule process(HttpRequest request) throws HttpProcessException {
            try {
                DataModule rsp = p.process(request);
                Method method = NetworkModule.class.getMethod(methodName, DataModule.class, HttpRequest.class);
                return (DataModule) method.invoke(NetworkModule.this, rsp, request);

            } catch (Throwable e) {
                if (e instanceof HttpProcessException)
                    throw (HttpProcessException) e;
                else if (e instanceof InvocationTargetException) {
                    throw new HttpProcessException(((InvocationTargetException) e).getTargetException(),
                            "invoke error:" + methodName, HttpProcessException.ReadResponseError);
                } else {
                    e.printStackTrace();
                    throw new HttpProcessException(e, "bad method:" + methodName, BadMethod);
                }
            }
        }
    }


    /**
     * Volley的复合类型的Post请求，成功返回请求Tag和包含JSON内容的CHBRsp,失败返回VolleyError
     */
    private class VolleyPostMultipart implements Processor<DataModule> {

        @Override
        public DataModule process(final HttpRequest request) throws Throwable {

            if (DEBUG)
                Log.e("post_multipart", request.getUrl() + (request.getHashMap_file() == null ? "" : request.getHashMap_file().toString()));

            NetworkResponse response = VolleyRequest.RequestMultipartTickle(context, request.getUrl(),
                    request.getRequestTag(), request.getHashMap(), request.getHashMap_file(), null);

            if (response.statusCode == 200) {
                String result = VolleyTickle.parseResponse(response);
                try {
                    if (DEBUG) Log.e("response_multipart", request.getUrl() + result);

                    JSONObject obj = new JSONObject(result);

                    DataModule rsp = new DataModule(obj.getInt("code"),
                            obj.getString("str"),
                            obj.isNull("data") ? null : obj.get("data"));
                    if (rsp.str().equals("token验证失败")) {
//                        logoutUser();
                    }
                    return rsp;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }







    /***************************************
     *inner
     **********************************************/

    private Context context;

    private ReentrantLock loginLock;
    private TaskCenter center;

    private MyPostString myPostString;
    private VolleyPostString volleyPostString;
    private OauthVolleyPostString oauthVolleyPostString;
    private VolleyPostMultipart volleyPostMultipart;

    public NetworkModule(UILApplication app) {
        context = app.getApplicationContext();
        loginLock = new ReentrantLock();
        center = new TaskCenter();

        myPostString = new MyPostString();
        volleyPostString = new VolleyPostString();
        oauthVolleyPostString = new OauthVolleyPostString();
        volleyPostMultipart = new VolleyPostMultipart();
    }




    /*******************************process请求事务,根据自己的业务需求，自定义请求地址以及参数)***************************************************/
    public TaskHandle getAlbums(String type) {
        HttpRequest request = new HttpRequest(AppUrl.WEB_URL);
        request.addParameter("page", Integer.toString(1));
        request.addParameter("rows", Integer.toString(20));
        request.setRequestTag("getAlbum");
        return center.arrange(request, myPostString);
    }

    public TaskHandle getArtists(String albumId) {
        HttpRequest request = new HttpRequest(AppUrl.ARTIST_URL);
        request.addParameter("page", Integer.toString(1));
        request.addParameter("rows", Integer.toString(20));
        request.addParameter("albumId",albumId);
        request.setRequestTag("getArtist");
        return center.arrange(request, myPostString);
    }



    /***************************************各个逻辑模块的网络请求(接口url均为举例，请自行添加)******************************************/

    /**
     * 登陆事务
     */
    public TaskHandle arrangeLogin(String requestTag, String phone, String password, String clientcode) {
        HttpRequest request = new HttpRequest(API_URL + "user/userLogin");
        request.addParameter("phone", phone);
        request.addParameter("password", password);
        request.addParameter("clientcode", clientcode);
        request.setRequestTag(requestTag);
        return center.arrange(request, new NameVolleyPostString("processLogin", volleyPostString));
    }



    /**
     * 发送登陆状态改变的广播
     */
    public void sendLoginChangeBroadcase() {
        context.sendBroadcast(new Intent(ACTION_USER_LOGIN_CHANGED));
    }



    /**
     * 查看新闻列表
     */
    public TaskHandle arrangeGetNewsList(String requestTag, String city, String category, int page, int row, String title) {
        HttpRequest request = new HttpRequest(API_URL + "news/getNews");
        request.addParameter("city", city);
        if (category != null)
            request.addParameter("category", category);
        request.addParameter("page", Integer.toString(page));
        request.addParameter("row", Integer.toString(row));
        if (title != null)
            request.addParameter("title", title);
        request.setRequestTag(requestTag);
        return center.arrange(request, volleyPostString);
    }


}
