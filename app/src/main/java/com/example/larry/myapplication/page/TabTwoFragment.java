package com.example.larry.myapplication.page;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Receiver;
import com.android.volley.TaskHandle;
import com.android.volley.cache.plus.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.entity.Album;
import com.example.larry.myapplication.entity.DataModule;
import com.example.larry.myapplication.songList.SongDetailActivity;
import com.example.larry.myapplication.swipe.ProgressFragment;
import com.example.larry.myapplication.swipe.SwipeRefreshLayout;
import com.example.larry.myapplication.utils.AppUrl;

import java.io.ByteArrayOutputStream;
import java.util.List;


/**
 * @author
 */
public class TabTwoFragment extends ProgressFragment implements Receiver<DataModule>, SwipeRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnLoadListener {
    protected RecyclerView mListView;
    private AlbumRecyclerViewAdapter mListAdapter;
    SwipeRefreshLayout mSwipeLayout;
    private View mContentView;
    protected ImageLoader mImageLoader;

    public static TabTwoFragment newInstance() {
        TabTwoFragment fragment = new TabTwoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = new ImageLoader( Volley.newRequestQueue(getContext()));
    }
    final SearchFragment sf = new SearchFragment();
    boolean allowHiden = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.tab_two, null);
        getChildFragmentManager().beginTransaction().replace(R.id.search_frame, sf).commit();
        getChildFragmentManager().beginTransaction().hide(sf).commit();
        ImageView mSearchImage = (ImageView) mContentView.findViewById(R.id.image_search);
        mSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allowHiden){
                    getChildFragmentManager().beginTransaction().show(sf).commit();
                    allowHiden = false;
                }else {
                    getChildFragmentManager().beginTransaction().hide(sf).commit();
                    allowHiden = true;
                }
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup content view
        setContentView(mContentView);
        // Setup text for empty content
        setEmptyText(R.string.empty);
        setContentShown(false);
        obtainData(0);
    }


    private void obtainData(int type) {
        TaskHandle handle_0 = getNetworkModule().getAlbums("");
        handle_0.setId(type);
        handle_0.setReceiver(this);
        handle_0.pullTrigger();
    }

    @Override
    public void onSucess(TaskHandle handle, DataModule result) {
        switch (handle.id()) {

            case 0:
                try {

                    mListView = (RecyclerView) mContentView.findViewById(R.id.swipe_list);
                    mListView.setLayoutManager(new StaggeredGridLayoutManager( 1,StaggeredGridLayoutManager.VERTICAL));
                    mListAdapter = new AlbumRecyclerViewAdapter(result.getAlbum());
                    mListView.setAdapter(mListAdapter);
                    mSwipeLayout = (SwipeRefreshLayout) mContentView.findViewById(R.id.swipe_container);
                    mSwipeLayout.setRefreshing(false);
                    mSwipeLayout.setOnRefreshListener(this);
                    mSwipeLayout.setOnLoadListener(this);
                    mSwipeLayout.setColor(android.R.color.holo_blue_bright,
                            android.R.color.holo_green_light,
                            android.R.color.holo_orange_light,
                            android.R.color.holo_red_light);
                    mSwipeLayout.setMode(SwipeRefreshLayout.Mode.PULL_FROM_END);
                    mSwipeLayout.setLoadNoFull(true);
                    setContentShown(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case 1:
                try {
                    mListAdapter.add(result.getAlbum());
                    mSwipeLayout.setLoading(false);
                    mListAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onError(TaskHandle handle, Throwable error) {

    }

    @Override
    public void onLoad() {

        obtainData(1);
    }

    @Override
    public void onRefresh() {

    }

    public class AlbumRecyclerViewAdapter
            extends RecyclerView.Adapter<AlbumRecyclerViewAdapter.ViewHolder> {

        private final List<Album> mValues;

        public AlbumRecyclerViewAdapter(List<Album> items) {

            mValues = items;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.song_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getAlbumName());
            holder.mContentView.setText(mValues.get(position).getDescripe());
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.mPic, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
            mImageLoader.get(AppUrl.webUrl + holder.mItem.getImgPath(), listener);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, SongDetailActivity.class);
                    intent.putExtra(SongDetailActivity.ARG_ITEM_ID, holder.mItem);
                    Bitmap bmp = ((BitmapDrawable) holder.mPic.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] bitmapByte = baos.toByteArray();
                    intent.putExtra("bitmap", bitmapByte);
                    context.startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                }
            });
        }

        public void add(List<Album> albumList) {
            mValues.addAll(albumList);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final ImageView mPic;
            public final TextView mContentView;
            public Album mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                mPic = (ImageView) view.findViewById(R.id.list_img);
            }


            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
