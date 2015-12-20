package com.example.larry.myapplication.songList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.dummy.DummyContent;
import com.example.larry.myapplication.utils.AlbumArtCache;

import java.util.List;

/**
 * Created by Larry on 2015/12/20.
 */
public class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private final List<DummyContent.DummyItem> mValues;

    public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
        mValues = items;
    }
    /* if (convertView == null)
            * {
        * 	convertView = View.inflate(context, R.layout.ad_demo, null);
        * }
    * TextView tv_demo = ViewHolderUtils.get(convertView, R.id.tv_demo);
    * ImageView iv_demo = ViewHolderUtils.get(convertView, R.id.iv_demo);
    * */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_list_content, parent, false);
        return new ViewHolder(view);
    }
    ImageView iv ;
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText("标题"+mValues.get(position).id);
        holder.mContentView.setText("内容简介"+mValues.get(position).content);
        iv = holder.mListImg;

        String artUrl =mValues.get(position).url;
        String mCurrentArtUrl = artUrl;
        AlbumArtCache cache = AlbumArtCache.getInstance();
        Bitmap art = cache.getBigImage(artUrl);

        if (art != null) {
            // if we have the art cached or from the MediaDescription, use it:
            iv.setImageBitmap(art);
        } else {
            // otherwise, fetch a high res version and update:
            cache.fetch(artUrl, new AlbumArtCache.FetchListener() {
                @Override
                public void onFetched(String artUrl, Bitmap bitmap, Bitmap icon) {
                    // sanity check, in case a new fetch request has been done while
                    // the previous hasn't yet returned:

                        iv.setImageBitmap(bitmap);

                }
            });
        }
/*
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#555555"));
        if (!TextUtils.isEmpty(mValues.get(position).url)) {
            Glide.with(holder.mView.getContext())
                    .load(mValues.get(position).url)
                    .override(100, 100)
                    .centerCrop()
                    .placeholder(colorDrawable)
                    .into(iv);
        } else {
            iv.setImageDrawable(colorDrawable);
        }
*/

//        iv.setImageResource(R.drawable.song1);
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
