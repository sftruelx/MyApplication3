package com.example.larry.myapplication.utils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.example.larry.myapplication.PlaybackControlsFragment;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.entity.Album;
import com.example.larry.myapplication.entity.Artist;
import com.example.larry.myapplication.media.ConstMsg;
import com.example.larry.myapplication.media.MusicService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Larry on 2016/1/21.
 */
public class MyActivity extends AppCompatActivity{

    private static final String TAG = LogHelper.makeLogTag(MyActivity.class);
    protected Intent intent;
    protected PlaybackControlsFragment mControlsFragment;
    protected MsgReceiver musicReceiver;
    public   int state = ConstMsg.STATE_NONE;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册广播接收器
        musicReceiver = new MsgReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstMsg.MUSICSERVICE_ACTION);
        registerReceiver(musicReceiver, intentFilter);
        //启动MUSIC服务
        intent = new Intent(this, MusicService.class);
        getApplicationContext().startService(intent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(state != ConstMsg.STATE_NONE) {
            showPlaybackControls();
        }else {
            hidePlaybackControls();
        }
    }

    public static boolean isActivityRunning(Context mContext, String activityClassName){
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if(info != null && info.size() > 0){
            ComponentName component = info.get(0).topActivity;
            if(activityClassName.equals(component.getClassName())){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
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

    public void updateView(int state, Album album, int current){
    };

    public void sendBroadcastToService(int state, ArrayList<Artist> songList, Album album) {
        Intent intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
        intent.putExtra(ConstMsg.SONG_STATE, state);
        intent.putParcelableArrayListExtra(ConstMsg.ARISTLIST,songList);
        intent.putExtra(ConstMsg.ALBUM, album);
        sendBroadcast(intent);
        LogHelper.i(TAG, "发送控制广播" + state);
    }

    public class MsgReceiver extends BroadcastReceiver {
        private int state;
        private MyActivity activity;

        public MsgReceiver(MyActivity activity){
            this.activity = activity;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            state = intent.getIntExtra(ConstMsg.SONG_STATE, 0);
            int during = intent.getIntExtra(ConstMsg.SONG_DURING, 0);
            int currentPosition = intent.getIntExtra(ConstMsg.SONG_PROGRESS, 0);
            Artist artist = (Artist)intent.getParcelableExtra(ConstMsg.SONG_ARTIST);
            Album album = (Album)intent.getParcelableExtra(ConstMsg.ALBUM);
            int current = intent.getIntExtra(ConstMsg.SONG_POSITION,-1);
            LogHelper.i(TAG, "client播放信息" + state + " song " + artist) ;
            //TODO 搞个接口

            mControlsFragment.updateState(state, currentPosition, during, artist, album);
            activity.state = state;
            activity.updateView(state, album, current);
            boolean bool = isActivityRunning(getApplicationContext(),getPackageName()+ "." +getLocalClassName());
            LogHelper.i(TAG,"is activity " + bool + "getLocalClassName() " + getLocalClassName() + " " + getPackageName());
            if(bool) {
                if(state != ConstMsg.STATE_NONE) {
                    activity.showPlaybackControls();
                }else {
                    activity.hidePlaybackControls();
                }
            }

        }

    }

}
