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

/**
 * An activity representing a single Song detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link SongListActivity}.
 */
public class SongDetailActivity extends MyActivity {
    private static final String TAG = LogHelper.makeLogTag(SongDetailActivity.class);
    public static final String ARG_ITEM_ID = "item_id";
    private MyHandler handler;
    private boolean mTwoPane;
    private SongDetailFragment  songDetailFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_detail);
        Album album =  (Album)this.getIntent().getExtras().get(ARG_ITEM_ID);
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
    public void updateView(Album album, Artist artist) {
        LogHelper.i(TAG, "-------------------------..." + artist);
        Bundle b = new Bundle();// 存放数据
        b.putParcelable(ConstMsg.ALBUM, album);
        b.putParcelable(ConstMsg.SONG_ARTIST, artist);
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
            Log.d("MyHandler","handleMessage。。。。。。");
            super.handleMessage(msg);
            // 此处可以更新UI
            Bundle b = msg.getData();
            Album a = b.getParcelable(ConstMsg.ALBUM);
            Artist a1 = b.getParcelable(ConstMsg.SONG_ARTIST);
            if(a1 != null) {
                songDetailFragment.mListAdapter.updateView(a, a1);
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
