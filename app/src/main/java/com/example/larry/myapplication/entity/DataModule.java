package com.example.larry.myapplication.entity;

import com.android.volley.HttpProcessException;
import com.android.volley.IDData;
import com.example.larry.myapplication.network.NetworkModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chenlijian on 2015/8/12.
 * 数据处理模块
 */


public class DataModule {

    public static final int CodeSucess = 1;

    private int code;
    private String txt;
    private Object extra;

    public DataModule(int code, String str, Object data) {
        this.code = code;
        this.txt = str;
        this.extra = data;
    }

    public void setCode(int code){
        this.code = code;
    }

    public int code() {
        return code;
    }

    public void setStr(String txt){
        this.txt = txt;
    }

    public String str() {
        return txt;
    }

    public void setExtra(Object obj) {
        extra = obj;
    }

    public Object getExtra() {
        return extra;
    }

    public <T> T tryExtra(Class<T> klass) {
        return (T) extra;
    }




    @Override
    public String toString() {
    // TODO Auto-generated method stub
    StringBuilder sb = new StringBuilder("{\"code\":\"");
    sb.append(code);
    sb.append(",\"str\":\"");
    sb.append(txt);
    sb.append("\",\"data\":");
    sb.append(extra == null ? "null" : extra.toString());
    sb.append("}");
    return sb.toString();
}







/*


    */
/**
     * 解析用户登录状态(登陆后得到用户登陆的CHBRsp),得到LoginState对象
     *//*

    public LoginState parseLoginState() throws HttpProcessException {
        try {
            JSONObject json = (JSONObject) getExtra();

            LoginState state = new LoginState();

            state.setToken(json.getString("token"));
            state.setOverTime(json.getString("overTime"));

            extra = null;
            return state;

        } catch (Exception e) {
            throw badResponseException(e);
        }
    }
*/


    public ArrayList<Album> getAlbum() throws Exception{
        ArrayList<Album> list;
        try {
            Gson gson = new Gson();
            list = gson.fromJson(getExtra().toString(),new TypeToken<List<Album>>() {
            }.getType());
            return list;
        } catch (Exception e) {
            throw e;
        }
    }


    public ArrayList<Artist> getArtist() throws Exception{
        ArrayList<Artist> list;
        try {
            Gson gson = new Gson();
            list = gson.fromJson(getExtra().toString(),new TypeToken<List<Artist>>() {
            }.getType());
            return list;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 解析图片相对路径
     */
    public String parsePath() {
        return tryExtra(JSONObject.class).optString("path");
    }


    /**
     * 解析当前城市的新闻列表
     */
    public IDData getList() throws HttpProcessException {
        try {
            JSONObject json = tryExtra(JSONObject.class);
            IDData data = new IDData(json.optInt("count", -1), null);
            JSONArray array = json.optJSONArray("data");


            extra = data;
            return data;
        } catch (Exception e) {
            throw badResponseException(e);
        }
    }



    HttpProcessException badResponseException(Exception e) {
        return new HttpProcessException(e, extra == null ? "" : extra.toString(), NetworkModule.BadResponse);
    }


}
