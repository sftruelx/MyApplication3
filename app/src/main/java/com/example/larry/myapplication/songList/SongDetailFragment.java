package com.example.larry.myapplication.songList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.cache.plus.ImageLoader;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.entity.Album;
import com.example.larry.myapplication.entity.DummyContent;
import com.example.larry.myapplication.utils.AppUrl;
import com.example.larry.myapplication.utils.MyFragment;
import com.example.larry.myapplication.utils.T;
import com.example.larry.myapplication.utils.UILApplication;

import java.util.List;


/**
 * A fragment representing a single Song detail screen.
 * This fragment is either contained in a {@link SongListActivity}
 * in two-pane mode (on tablets) or a {@link SongDetailActivity}
 * on handsets.
 */
public class SongDetailFragment extends MyFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Album mItem;
    private Bitmap bitmap;
    private ImageView image;
    private ImageLoader mImageLoader;
    private CollapsingToolbarLayout toolbar;
    private TextView txt;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the vffdfdxassade
     * fragment (e.g. upon screen orientation changes).
     */
    public static SongDetailFragment newInstance(Album album , byte[] bis) {
        SongDetailFragment sdf = new SongDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM_ID, album);
        args.putByteArray("bitmap", bis);
        sdf.setArguments(args);
        return sdf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = new ImageLoader(((UILApplication)getActivity().getApplication()).mQueue);
        mItem = (Album) getArguments().getParcelable(ARG_ITEM_ID);
        byte[] bis = getArguments().getByteArray("bitmap");
        bitmap= BitmapFactory.decodeByteArray(bis, 0, bis.length);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_list, container, false);
        View recyclerView = rootView.findViewById(R.id.item_list);
        Activity activity = this.getActivity();
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        txt = (TextView) activity.findViewById(R.id.toolar_text);
        toolbar = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);

        ImageView mButton = (ImageView)activity.findViewById(R.id.play_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T.showShort(getContext(),"=====================");
            }
        });
        // Show the dummy content as text in a TextView.
        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.song_detail)).setText(mItem.getAlbumName());
        }
        image = (ImageView) activity.findViewById(R.id.backdrop);
        colorChange();
        applyBlur();
        return rootView;
    }
    /**
     * 界面颜色的更改
     */
    private void colorChange( ) {
        // 用来提取颜色的Bitmap
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        // Palette的部分
        Palette.Builder from = Palette.from(bitmap);
//        int color1 =from.generate().getMutedColor(getResources().getColor(android.R.color.transparent));
//        int color2 =from.generate().getVibrantColor(getResources().getColor(android.R.color.transparent));
//        int color3 =from.generate().getDarkMutedColor(getResources().getColor(android.R.color.transparent));
//        int color4 =from.generate().getLightMutedColor(getResources().getColor(android.R.color.transparent));
//        int color5 =from.generate().getLightVibrantColor(getResources().getColor(android.R.color.transparent));
        int color =from.generate().getDarkVibrantColor(getResources().getColor(android.R.color.transparent));
        toolbar.setContentScrimColor(colorBurn(color));
       /* Palette.generateAsync(bitmap,24, new Palette.PaletteAsyncListener() {
            *//**
             * 提取完之后的回调方法
             *//*
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();
            *//* 界面颜色UI统一性处理,看起来更Material一些 *//*
                toolbar.setContentScrimColor(colorBurn(vibrant.getRgb()));
//                toolbar.sette(vibrant.getTitleTextColor());
                // 其中状态栏、游标、底部导航栏的颜色需要加深一下，也可以不加，具体情况在代码之后说明
//                toolbar.setIndicatorColor(colorBurn(vibrant.getRgb()));

//                toolbar.setBackgroundColor(vibrant.getRgb());
            }
        });*/
    }

    /**
     * 颜色加深处理
     *
     * @param RGBValues
     *            RGB的值，由alpha（透明度）、red（红）、green（绿）、blue（蓝）构成，
     *            Android中我们一般使用它的16进制，
     *            例如："#FFAABBCC",最左边到最右每两个字母就是代表alpha（透明度）、
     *            red（红）、green（绿）、blue（蓝）。每种颜色值占一个字节(8位)，值域0~255
     *            所以下面使用移位的方法可以得到每种颜色的值，然后每种颜色值减小一下，在合成RGB颜色，颜色就会看起来深一些了
     * @return
     */
    private int colorBurn(int RGBValues) {
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 - 0.1));
        green = (int) Math.floor(green * (1 - 0.1));
        blue = (int) Math.floor(blue * (1 - 0.1));
        return Color.rgb(red, green, blue);
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    private void applyBlur() {
        image.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                image.getViewTreeObserver().removeOnPreDrawListener(this);
                image.buildDrawingCache();
                Bitmap bmp = image.getDrawingCache();
                blur(bmp, txt);
                return true;
            }
        });
    }

    private void blur(Bitmap bkg, View view) {

        float radius = 20;
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()),
                (int) (view.getMeasuredHeight()), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft(), -view.getTop());
        canvas.drawBitmap(bkg, 0, 0, null);

        RenderScript rs = RenderScript.create(getActivity());
        Allocation overlayAlloc = Allocation.createFromBitmap(
                rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);
        view.setBackground(new BitmapDrawable(
                getResources(), overlay));
        rs.destroy();
    }

    @Override
    public String toString() {
        return "RenderScript";
    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

}
