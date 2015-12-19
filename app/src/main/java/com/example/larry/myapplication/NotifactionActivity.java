package com.example.larry.myapplication;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.larry.myapplication.media.ConstMsg;
import com.example.larry.myapplication.utils.LogHelper;

import java.util.UUID;

/**
 * Created by 067231 on 2015/12/16.
 */
public class NotifactionActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    int notification_id = 19172439;
    NotificationManager nm;
    Handler handler = new Handler();
    Notification notification;
    MsgReceiver msgReceiver;
    int count = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showNotificationPanel();
    }

    private void showNotificationPanel() {
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notification=new Notification("图标边的文字",System.currentTimeMillis());


        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setDefaults(0);
        notification = builder.build();
        notification.contentView = new RemoteViews(getPackageName(), R.layout.notification);

        notification.contentView.setProgressBar(R.id.pb, 100, 0, false);
        notification.contentView.setTextViewText(R.id.title, "这里是歌曲名称++");
//        if(isPlay){
//            mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_pause);
//        }else{
//            mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_play);
//        }
//        定义按钮事件
        Intent play_intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
        play_intent.putExtra(ConstMsg.SONG_STATE, ConstMsg.STATE_PLAYING);
        notification.contentView.setOnClickPendingIntent(R.id.play_pause,
                PendingIntent.getBroadcast(this, ConstMsg.STATE_PLAYING,
                        play_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
        Intent next_intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
        next_intent.putExtra(ConstMsg.SONG_STATE, ConstMsg.STATE_NEXT);
        notification.contentView.setOnClickPendingIntent(R.id.play_next,
                PendingIntent.getBroadcast(this, ConstMsg.STATE_NEXT,
                        next_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
        Intent previous_intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
        previous_intent.putExtra(ConstMsg.SONG_STATE, ConstMsg.STATE_PREVIOUS);
        notification.contentView.setOnClickPendingIntent(R.id.play_previous,
                PendingIntent.getBroadcast(this, ConstMsg.STATE_PREVIOUS,
                        previous_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
        Intent notificationIntent = new Intent(this, NotifactionActivity.class);
        notification.contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstMsg.MUSICCLIENT_ACTION);
        registerReceiver(msgReceiver, intentFilter);
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
        ActivityInfo ai = homeInfo.activityInfo;
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
        startActivitySafely(startIntent);
    }


    private void startActivitySafely(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "没正常启动", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, "存在安全隐患",
                    Toast.LENGTH_SHORT).show();
            Log.e("", "Launcher does not have the permission to launch "
                            + intent
                            + ". Make sure to create a MAIN intent-filter for the corresponding activity "
                            + "or use the exported attribute for this activity.",
                    e);
        }
    }

        public void test(View v) {
            // TODO Auto-generated method stub
            showNotification();//显示notification
            handler.post(run);
        }


    Runnable run = new Runnable() {
        //    放在消息接收器中
        @Override
        public void run() {
            count++;
            notification.contentView.setProgressBar(R.id.pb, 100, count, false);
            //设置当前值为count
            showNotification();//这里是更新notification,就是更新进度条
            if (count <= 100) handler.postDelayed(run, 200);
            //200毫秒count加1
        }

    };


    public void showNotification() {
        LogHelper.i("run" + "showNotification");
        Intent i = new Intent(ConstMsg.MUSICCLIENT_ACTION);
        i.putExtra(ConstMsg.SONG_STATE,ConstMsg.STATE_PLAYING);
        sendBroadcast(i);

        nm.notify(notification_id, notification);
    }

    @Override
    protected void onDestroy() {
        LogHelper.i("Notification", "onDestroy");
        unregisterReceiver(msgReceiver);
        super.onDestroy();
        //注销广播
    }


    public class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(ConstMsg.SONG_STATE, 0);
            int during = intent.getIntExtra(ConstMsg.SONG_DURING, 0);
            LogHelper.i("TAG", "可以在这里更新播放信息" + state);
        }

    }
}