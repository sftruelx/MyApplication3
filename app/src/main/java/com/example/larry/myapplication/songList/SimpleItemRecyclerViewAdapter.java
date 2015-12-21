package com.example.larry.myapplication.songList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.dummy.DummyContent;
import com.example.larry.myapplication.utils.AlbumArtCache;
import com.example.larry.myapplication.utils.CircleBitmapDisplayer;
import com.example.larry.myapplication.utils.Constants;
import com.example.larry.myapplication.utils.LogHelper;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by Larry on 2015/12/20.
 */
public class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = LogHelper.makeLogTag(SimpleItemRecyclerViewAdapter.class);
    private final List<DummyContent.DummyItem> mValues;
    private DisplayImageOptions options;
    public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.nomal_img)
                .showImageOnFail(R.drawable.nomal_img)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)     //设置图片的解码类型
                .displayer(new FadeInBitmapDisplayer(1000))//是否图片加载好后渐入的动画时间
                .build();
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_list_content, parent, false);
        return new ViewHolder(view);
    }

    ImageView imageView;
    int p;

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LogHelper.i(TAG, "position = " + position);
        p = position;
        holder.mItem = mValues.get(position);
        holder.mIdView.setText("标题" + mValues.get(position).id);
        holder.mContentView.setText("内容简介" + mValues.get(position).content);
        imageView = holder.mListImg;
        String artUrl = Constants.IMAGES[position];
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(artUrl, imageView, options);
        /*String mCurrentArtUrl = artUrl;
        AlbumArtCache cache = AlbumArtCache.getInstance();
        Bitmap art = cache.getBigImage(artUrl);

        if (art != null) {
            imageView.setImageBitmap(art);
        } else {
            cache.fetch(artUrl, new AlbumArtCache.FetchListener() {
                @Override
                public void onFetched(String artUrl, Bitmap bitmap, Bitmap icon) {
                    imageView.setImageBitmap(bitmap);
                }
            });
        }
*/
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (false) {
                    Bundle arguments = new Bundle();
                    arguments.putString(SongDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//                    SongDetailFragment fragment = new SongDetailFragment();
//                    fragment.setArguments(arguments);
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.song_detail_container, fragment)
//                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, SongDetailActivity.class);
                    intent.putExtra(SongDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mListImg;
        public final TextView mIdView;
        public final TextView mContentView;
        public DummyContent.DummyItem mItem;

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
}
