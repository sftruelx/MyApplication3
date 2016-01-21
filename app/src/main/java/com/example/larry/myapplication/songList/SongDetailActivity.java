package com.example.larry.myapplication.songList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.larry.myapplication.PlaybackControlsFragment;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.entity.Album;
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
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

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

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, SongDetailFragment.newInstance(album,bis)).commit();
//        showPlaybackControls();
//        hidePlaybackControls();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
//            navigateUpTo(new Intent(this, MainActivity.class));
            SongDetailActivity.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
