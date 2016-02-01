package com.example.larry.myapplication.utils;

import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    protected DownloadCompleteReceiver receiver;
    public   int state = ConstMsg.STATE_NONE;
    boolean isChanging;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册广播接收器
        musicReceiver = new MsgReceiver(this);

        receiver = new DownloadCompleteReceiver();
        //启动MUSIC服务
        intent = new Intent(this, MusicService.class);
        getApplicationContext().startService(intent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstMsg.MUSICSERVICE_ACTION);
        registerReceiver(musicReceiver, intentFilter);
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
        if(musicReceiver!=null)unregisterReceiver(musicReceiver);
        if(receiver != null)unregisterReceiver(receiver);
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

    public void updateView(int state, Album album, Artist artist){
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
            if(isChanging) return;
            state = intent.getIntExtra(ConstMsg.SONG_STATE, 0);
            int during = intent.getIntExtra(ConstMsg.SONG_DURING, 0);
            int currentPosition = intent.getIntExtra(ConstMsg.SONG_POSITION, 0);
            int duringTime = intent.getIntExtra(ConstMsg.SONG_PROGRESS,0);
//            LogHelper.i(TAG, "during time " + duringTime);
            ArrayList<Artist> list = intent.getParcelableArrayListExtra(ConstMsg.SONG_ARTIST);

            if (list != null) {
                Artist artist = list.get(currentPosition);
                Album album = (Album) intent.getParcelableExtra(ConstMsg.ALBUM);
//            LogHelper.i(TAG, "client接收信息" + state + " song " + album) ;
                int color = intent.getIntExtra(ConstMsg.SONG_COLOR, -1);
                byte[] bis = intent.getByteArrayExtra(ConstMsg.SONG_ICON);
                if (color != -1) {
                    mControlsFragment.updateColor(color);
                }
                if (bis != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
//                bitmap = BitmapHelper.scaleBitmap(bitmap,75,75);
                    mControlsFragment.updateImage(bitmap);
                }
                mControlsFragment.updateState(state, duringTime, during, artist, album);
                activity.state = state;
                activity.updateView(state, album, artist);
                boolean bool = isActivityRunning(getApplicationContext(), getPackageName() + "." + getLocalClassName());
//            LogHelper.i(TAG,"is activity " + bool + "getLocalClassName() " + getLocalClassName() + " " + getPackageName());
                if (bool) {
                    if (state != ConstMsg.STATE_NONE) {
                        activity.showPlaybackControls();
                    } else {
                        activity.hidePlaybackControls();
                    }
                }

            }
        }

    }
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                LogHelper.i(""," download complete! id : "+downId);
                T.showShort(getApplicationContext(), intent.getAction()+"id : "+downId);
            }
        }
    }

}
