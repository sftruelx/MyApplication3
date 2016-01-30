package com.example.larry.myapplication.songList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Receiver;
import com.android.volley.TaskHandle;
import com.android.volley.cache.plus.ImageLoader;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.entity.Album;
import com.example.larry.myapplication.entity.Artist;
import com.example.larry.myapplication.entity.DataModule;
import com.example.larry.myapplication.media.ConstMsg;
import com.example.larry.myapplication.swipe.ProgressFragment;
import com.example.larry.myapplication.utils.LogHelper;
import com.example.larry.myapplication.utils.MyActivity;
import com.example.larry.myapplication.utils.T;
import com.example.larry.myapplication.utils.UILApplication;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class SongDetailFragment extends ProgressFragment implements Receiver<DataModule> {

    public static final String ARG_ITEM_ID = "item_id";
    private View mContentView;


    private RecyclerView mListView;
    private Album album;
    private Bitmap bitmap;
    private Bitmap newBitmap;
    private ImageView image;
    private ImageLoader mImageLoader;
    private CollapsingToolbarLayout toolbar;
    private TextView txt;
    private int color;
    private ArrayList<Artist> list;

    public static SongDetailFragment newInstance(Album album, byte[] bis) {
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
        mImageLoader = new ImageLoader(((UILApplication) getActivity().getApplication()).mQueue);
        album = (Album) getArguments().getParcelable(ARG_ITEM_ID);
        byte[] bis = getArguments().getByteArray("bitmap");
        bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.item_list, container, false);
        mListView = (RecyclerView) mContentView.findViewById(R.id.item_list);
        Activity activity = this.getActivity();

        txt = (TextView) activity.findViewById(R.id.toolar_text);
        toolbar = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);

        ImageView mButton = (ImageView) activity.findViewById(R.id.play_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                T.showShort(getContext(),"==========播放专辑中的所有文件===========");
                Intent intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
                intent.putExtra(ConstMsg.SONG_STATE, ConstMsg.STATE_PLAYING);
                intent.putParcelableArrayListExtra(ConstMsg.ARISTLIST, list);
                intent.putExtra(ConstMsg.ALBUM, album);
                intent.putExtra(ConstMsg.SONG_COLOR, color);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bitmapByte = baos.toByteArray();
                intent.putExtra(ConstMsg.SONG_ICON, bitmapByte);
                getActivity().sendBroadcast(intent);
            }
        });

        image = (ImageView) activity.findViewById(R.id.backdrop);
        colorChange();
        applyBlur();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(mContentView);
        setEmptyText(R.string.empty);
        setContentShown(false);
        obtainData(0);
    }


    private void obtainData(int type) {
        TaskHandle handle_0 = getNetworkModule().getArtists(String.valueOf(album.getId()));
        handle_0.setId(type);
        handle_0.setReceiver(this);
        handle_0.pullTrigger();
    }

    public SongRecyclerViewAdapter mListAdapter;

    @Override
    public void onSucess(TaskHandle handle, DataModule result) {
        switch (handle.id()) {

            case 0:
                try {

                    mListView = (RecyclerView) mContentView.findViewById(R.id.item_list);
                    list = result.getArtist();
                    mListAdapter = new SongRecyclerViewAdapter(list);
                    mListView.setAdapter(mListAdapter);
                    setContentShown(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    public ArrayList<Artist> getSongList() {
        return list;
    }

    @Override
    public void onError(TaskHandle handle, Throwable error) {

    }

    private void colorChange() {
        // 用来提取颜色的Bitmap
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        // Palette的部分
        Palette.Builder from = Palette.from(bitmap);
        Palette palette = from.generate();
        Palette.Swatch vibrant =
                palette.getVibrantSwatch();

//        color =from.generate().getDarkVibrantColor(getResources().getColor(android.R.color.transparent));
        if (vibrant != null) {
            color = colorBurn(vibrant.getRgb());
        } else {
            color = R.color.black;
        }
//        color = vibrant.getPopulation(); //白色
//        color = vibrant.getBodyTextColor();//白色
//        color = vibrant.getTitleTextColor();
        toolbar.setContentScrimColor(colorBurn(color));

    }


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

        float radius = 25;
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


    public class SongRecyclerViewAdapter
            extends RecyclerView.Adapter<SongRecyclerViewAdapter.ViewHolder> {

        private final List<Artist> mValues;

        public SongRecyclerViewAdapter(List<Artist> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        public  String cal(int second) {
            int h = 0;
            int d = 0;
            int s = 0;
            int temp = second % 3600;
            if (second > 3600) {
                h = second / 3600;
                if (temp != 0) {
                    if (temp > 60) {
                        d = temp / 60;
                        if (temp % 60 != 0) {
                            s = temp % 60;
                        }
                    } else {
                        s = temp;
                    }
                }
            } else {
                d = second / 60;
                if (second % 60 != 0) {
                    s = second % 60;
                }
            }

            String H ="";
            if(h>0){
                H = h + ":";
            }
            String M ="";
            if(d>10){
                M = d + ":";
            }else{
                M = "0" + d + ":";
            }
            String S ="";
            if(s>10){
                S = s + "";
            }else{
                S += "0" + s;
            }

            return H + M + S ;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mImage.setColorFilter(color);
            holder.mFavor.setColorFilter(color);
            holder.mOption.setColorFilter(color);

            holder.mItem = mValues.get(position);
            if (holder.mItem.getState() == ConstMsg.STATE_NONE) {
                holder.mImage.setImageResource(R.drawable.ic_play_arrow_black_36dp);
            } else {
                holder.mImage.setImageResource(R.drawable.ic_pause_black_36dp);
            }
//            holder.mIdView.setText(String.valueOf(mValues.get(position).getArtistId()));
            holder.mContentView.setText(mValues.get(position).getArtistName());
            holder.mView.setTag(holder.mItem.getArtistId());
            holder.mTextTime.setText(cal(holder.mItem.getArtistTraceLength()));

            holder.mFavor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    T.showShort(getContext(), "==========favor===========" + (Boolean) holder.mFavor.getTag());
                    if (!(Boolean) holder.mFavor.getTag()) {
                        holder.mFavor.setImageResource(R.drawable.ic_star_on);
                        holder.mFavor.setTag(Boolean.TRUE);
                    } else {
                        holder.mFavor.setImageResource(R.drawable.ic_star_off);
                        holder.mFavor.setTag(Boolean.FALSE);
                    }
                }
            });
            holder.mOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    T.showShort(getContext(), "==========download===========");
                }
            });
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.favor:
                            LogHelper.i("onClick", "+favor");
                            break;
                        case R.id.option:
                            break;
                    }
                    Intent intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
                    intent.putExtra(ConstMsg.SONG_STATE, ConstMsg.STATE_PLAYING);
                    ArrayList<Artist> mList = new ArrayList<Artist>();
                    mList.add(holder.mItem);
                    intent.putParcelableArrayListExtra(ConstMsg.ARISTLIST, mList);
                    intent.putExtra(ConstMsg.ALBUM, album);
                    intent.putExtra(ConstMsg.SONG_COLOR, color);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] bitmapByte = baos.toByteArray();
                    intent.putExtra(ConstMsg.SONG_ICON, bitmapByte);
                    getActivity().sendBroadcast(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public List<Artist> getItem() {
            return mValues;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mImage;
            public final TextView mIdView;
            public final TextView mContentView;
            public final TextView mTextTime;
            public final ImageView mFavor;
            public final ImageView mOption;
            public Artist mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImage = (ImageView) view.findViewById(R.id.item_image);
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                mTextTime = (TextView) view.findViewById(R.id.item_time);
                mFavor = (ImageView) view.findViewById(R.id.favor);
                mOption = (ImageView) view.findViewById(R.id.option);
                mFavor.setTag(Boolean.FALSE);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

}
