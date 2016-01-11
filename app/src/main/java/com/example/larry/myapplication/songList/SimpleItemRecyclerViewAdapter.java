package com.example.larry.myapplication.songList;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.manager.LoadController;
import com.android.volley.manager.RequestManager;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.dummy.DummyContent;
import com.example.larry.myapplication.entity.Classify;
import com.example.larry.myapplication.utils.AlbumArtCache;
import com.example.larry.myapplication.utils.AppUrl;
import com.example.larry.myapplication.utils.BitmapCache;
import com.example.larry.myapplication.utils.Constants;
import com.example.larry.myapplication.utils.LogHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Larry on 2015/12/20.
 */
public class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = LogHelper.makeLogTag(SimpleItemRecyclerViewAdapter.class);
    private RequestQueue mQueue;
    private ImageLoader mImageLoader;
    private LoadController loadControler = null;
    private ArrayList<Classify> list ;
    AlbumArtCache cache = AlbumArtCache.getInstance();

    public SimpleItemRecyclerViewAdapter(Context context, List<DummyContent.DummyItem> items) {

        mQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
//        mValues = items;
        list = cache.getClassifies(AppUrl.testUrl);
        LogHelper.i(TAG,"list = "+ list);
        if(list== null) {
            list = new ArrayList<Classify>();
            loadControler = RequestManager.getInstance().get(AppUrl.testUrl, requestListener, 0);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_list_content, parent, false);
        return new ViewHolder(view);
    }

    ImageView imageView;


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LogHelper.i(TAG, "position = " + position);

        holder.classify = list.get(position);
        holder.mIdView.setText(holder.classify.getTitle());
        holder.mContentView.setText("");
        imageView = holder.mListImg;
        String artUrl = AppUrl.webUrl + holder.classify.getImg_path();
        getImage(imageView, artUrl);
        ObjectAnimator.ofFloat(imageView,"alpha",0.5f,1f).setDuration(500).start();

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.mListImg, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
        mImageLoader.get(artUrl, listener);
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.displayImage(artUrl, imageView, options);

      /*  holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (false) {
                    Bundle arguments = new Bundle();
//                    arguments.putString(SongDetailFragment.ARG_ITEM_ID, holder.classify.getId());
//                    SongDetailFragment fragment = new SongDetailFragment();
//                    fragment.setArguments(arguments);
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.song_detail_container, fragment)
//                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, SongDetailActivity.class);
                    intent.putExtra(SongDetailFragment.ARG_ITEM_ID,  holder.classify.getId());
                    context.startActivity(intent);
                }
            }
        });*/
    }

    private Bitmap createCircleImage(Bitmap source, int min) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(target);
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

    public static Bitmap getRoundCornerImage(Bitmap bitmap, int roundPixels) {
        //创建一个和原始图片一样大小位图
        Bitmap roundConcerImage = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //创建带有位图roundConcerImage的画布
        Canvas canvas = new Canvas(roundConcerImage);
        //创建画笔
        Paint paint = new Paint();
        //创建一个和原始图片一样大小的矩形
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        // 去锯齿
        paint.setAntiAlias(true);
        //画一个和原始图片一样大小的圆角矩形
        canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
        //设置相交模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //把图片画到矩形去
        canvas.drawBitmap(bitmap, null, rect, paint);
        return roundConcerImage;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mListImg;
        public final TextView mIdView;
        public final TextView mContentView;
        public Classify  classify;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mListImg = (ImageView) view.findViewById(R.id.list_img);
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public void getImage(final ImageView imageView, String url) {
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        ImageRequest imageRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                }, 0, 0, scaleType, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageView.setImageResource(R.drawable.nomal_img);
            }
        });
        mQueue.add(imageRequest);
    }
    private RequestManager.RequestListener requestListener = new RequestManager.RequestListener() {

        @Override
        public void onRequest() {

        }

        @Override
        public void onSuccess(String response, Map<String, String> headers, String url, int actionId) {
            System.out.println("actionId:"+actionId+", OnSucess!\n"+response);
            Gson gson = new Gson();
            list = gson.fromJson(response,new TypeToken<List<Classify>>() {
            }.getType());
            cache.putClassify(url,list);

        }

        @Override
        public void onError(String errorMsg, String url, int actionId) {
            System.out.println("actionId:"+actionId+", onError!\n"+errorMsg);
        }
    };



}
