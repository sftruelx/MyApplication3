package com.example.larry.myapplication.utils;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.example.larry.myapplication.PlaybackControlsFragment;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.media.ConstMsg;

/**
 * Created by Larry on 2016/1/21.
 */
public class TestActivity extends AppCompatActivity {

    private static final String TAG = LogHelper.makeLogTag(TestActivity.class);
    protected Intent intent;
    protected PlaybackControlsFragment mControlsFragment;
    public void showPlaybackControls() {
        LogHelper.d(TAG, "showPlaybackControls");
        if (NetworkHelper.isOnline(this)) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.animator.slide_in_from_bottom, R.animator.slide_out_to_bottom,
                            R.animator.slide_in_from_bottom, R.animator.slide_out_to_bottom)
                    .show(mControlsFragment)
                    .commit();
        }
    }

    public void hidePlaybackControls() {
        LogHelper.d(TAG, "hidePlaybackControls");
        getFragmentManager().beginTransaction()
                .hide(mControlsFragment)
                .commit();
    }


    public void sendBroadcastToService(int state) {
        Intent intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
        intent.putExtra(ConstMsg.SONG_STATE, state);
        sendBroadcast(intent);
        LogHelper.i(TAG, "发送控制广播" + state);
    }
}
