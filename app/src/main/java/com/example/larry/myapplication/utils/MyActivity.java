package com.example.larry.myapplication.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.larry.myapplication.PlaybackControlsFragment;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.media.ConstMsg;
import com.example.larry.myapplication.media.MusicService;

/**
 * Created by Larry on 2016/1/21.
 */
public class MyActivity extends AppCompatActivity{

    private static final String TAG = LogHelper.makeLogTag(MyActivity.class);
    protected Intent intent;
    protected PlaybackControlsFragment mControlsFragment;
    protected MsgReceiver musicReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册广播接收器
        musicReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstMsg.MUSICSERVICE_ACTION);
        registerReceiver(musicReceiver, intentFilter);
        //启动MUSIC服务
        intent = new Intent(this, MusicService.class);
        getApplicationContext().startService(intent);
    }



    @Override
    protected void onDestroy() {
        LogHelper.i(TAG, "我没了");
        //注销广播
        unregisterReceiver(musicReceiver);
        super.onDestroy();
    }

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

    public class MsgReceiver extends BroadcastReceiver {
        private int state;

        @Override
        public void onReceive(Context context, Intent intent) {
            state = intent.getIntExtra(ConstMsg.SONG_STATE, 0);
            int during = intent.getIntExtra(ConstMsg.SONG_DURING, 0);
            int currentPosition = intent.getIntExtra(ConstMsg.SONG_PROGRESS, 0);

            LogHelper.i(TAG, "播放信息" + state);
            //TODO 搞个接口
        mControlsFragment.updateState(state, currentPosition, during);


        }

    }

}
