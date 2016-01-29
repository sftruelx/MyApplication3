package com.example.larry.myapplication.songList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.larry.myapplication.PlaybackControlsFragment;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.entity.Album;
import com.example.larry.myapplication.entity.Artist;
import com.example.larry.myapplication.media.ConstMsg;
import com.example.larry.myapplication.utils.LogHelper;
import com.example.larry.myapplication.utils.MyActivity;
import com.example.larry.myapplication.utils.NetworkHelper;
import com.example.larry.myapplication.utils.TestActivity;

import java.util.ArrayList;
import java.util.List;


public class SongDetailActivity extends MyActivity {
    private static final String TAG = LogHelper.makeLogTag(SongDetailActivity.class);
    public static final String ARG_ITEM_ID = "item_id";
    private MyHandler handler;
    private boolean mTwoPane;
    protected Album album;
    private SongDetailFragment  songDetailFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_detail);
        album =  (Album)this.getIntent().getExtras().get(ARG_ITEM_ID);
        byte [] bis=this.getIntent().getByteArrayExtra("bitmap");
        Bitmap bitmap= BitmapFactory.decodeByteArray(bis, 0, bis.length);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);
        ImageView playImage = (ImageView) findViewById(R.id.play_image);
        ImageView image = (ImageView) findViewById(R.id.backdrop);
        playImage.setImageBitmap(bitmap);
        image.setImageBitmap(bitmap);
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {

            appBarLayout.setTitle(album.getAlbumName());
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        }
        mControlsFragment = (PlaybackControlsFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_playback_controls);
        if (mControlsFragment == null) {
            throw new IllegalStateException("Mising fragment with id 'controls'. Cannot continue.");
        }
        songDetailFragment = SongDetailFragment.newInstance(album,bis);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, songDetailFragment).commit();
        hidePlaybackControls();
        handler = new MyHandler();
    }

    @Override
    public void updateView(int state, Album album, Artist artist) {
//        LogHelper.i(TAG, "-------------------------..." + artist);
        Bundle b = new Bundle();// 存放数据
        int current = 0;
        ArrayList<Artist> list = songDetailFragment.getSongList();
        if(list != null) {
            for (int i = 0; i < list.size(); i++) {
                Artist a = list.get(i);
                if (a.getArtistId() == artist.getArtistId()) {
                    current = i;
                    break;
                }
            }
        }
        b.putParcelable(ConstMsg.ALBUM, album);
        b.putInt(ConstMsg.SONG_STATE, state);
        b.putInt(ConstMsg.SONG_POSITION, current);
        Message msg = new Message();
        msg.setData(b);
        handler.handleMessage(msg);
//        handler.post(update_thread);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            SongDetailActivity.this.finish();
            overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
//            navigateUpTo(new Intent(this, MainActivity.class));
            SongDetailActivity.this.finish();
            overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class MyHandler extends Handler {
        public MyHandler() {
        }

        // 子类必须重写此方法，接受数据
        @Override
        public void handleMessage(Message msg) {
//            Log.d("MyHandler","handleMessage。。。。。。");
            super.handleMessage(msg);
            // 此处可以更新UI
            Bundle b = msg.getData();
            Album a = b.getParcelable(ConstMsg.ALBUM);
            int state = b.getInt(ConstMsg.SONG_STATE);
            int current = b.getInt(ConstMsg.SONG_POSITION);
            if(a != null && album != null) {
                if (album.getId() == a.getId()) {
                    if(songDetailFragment != null) {
                        if(songDetailFragment.mListAdapter != null) {
                            List<Artist> list = songDetailFragment.mListAdapter.getItem();
                            for (int i = 0; i < list.size(); i++) {
                                if (i == current) {
                                    list.get(i).setState(state);
                                } else {
                                    list.get(i).setState(ConstMsg.STATE_NONE);
                                }
                            }
                            songDetailFragment.mListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

        }
    }

/*    Runnable update_thread = new Runnable()
    {
        public void run()
        {
            Log.d("update_thread","update_thread。。。。。。");
            songDetailFragment.mListAdapter.updateView(null,null);
        }
    };*/
}