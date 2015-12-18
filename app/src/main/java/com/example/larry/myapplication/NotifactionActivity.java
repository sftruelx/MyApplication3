package com.example.larry.myapplication;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

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
        setContentView(R.layout.main);

        Button bt1 = (Button) findViewById(R.id.bt1);
        bt1.setOnClickListener(bt1lis);
        Button bt2 = (Button) findViewById(R.id.bt2);
        bt2.setOnClickListener(bt2lis);

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

//        定义按钮事件
        Intent play_intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
        play_intent.putExtra(ConstMsg.SONG_STATE,ConstMsg.STATE_PLAY);
        notification.contentView.setOnClickPendingIntent(R.id.play_pause,
                PendingIntent.getBroadcast(this, ConstMsg.STATE_PLAY,
                        play_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
        Intent next_intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
        next_intent.putExtra(ConstMsg.SONG_STATE,ConstMsg.STATE_NEXT);
        notification.contentView.setOnClickPendingIntent(R.id.play_next,
                PendingIntent.getBroadcast(this, ConstMsg.STATE_NEXT,
                        next_intent ,
                        PendingIntent.FLAG_UPDATE_CURRENT));
        Intent previous_intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
        previous_intent.putExtra(ConstMsg.SONG_STATE,ConstMsg.STATE_PREVIOUS);
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
    }

    View.OnClickListener bt1lis = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            showNotification();//显示notification
            handler.post(run);
        }

    };
    Runnable run = new Runnable() {
        //    放在消息接收器中
        @Override
        public void run() {
            // TODO Auto-generated method stub
            count++;
            notification.contentView.setProgressBar(R.id.pb, 100, count, false);
            //设置当前值为count
            showNotification();//这里是更新notification,就是更新进度条
            if (count <= 100) handler.postDelayed(run, 200);
            //200毫秒count加1
        }

    };
    View.OnClickListener bt2lis = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            nm.cancel(notification_id);
            //清除notification
        }

    };

    public void showNotification() {
        nm.notify(notification_id, notification);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(msgReceiver);
        super.onDestroy();
        //注销广播
    }

/*
    public void showButtonNotify(){
        NotificationCompat.Builder mBuilder = new Builder(this);
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_custom_button);
        mRemoteViews.setImageViewResource(R.id.custom_song_icon, R.drawable.sing_icon);
        //API3.0 以上的时候显示按钮，否则消失
        mRemoteViews.setTextViewText(R.id.tv_custom_song_singer, &quot;周杰伦&quot;);
        mRemoteViews.setTextViewText(R.id.tv_custom_song_name, &quot;七里香&quot;);
        //如果版本号低于（3。0），那么不显示按钮
        if(BaseTools.getSystemVersion() &lt;= 9){
            mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.GONE);
        }else{
            mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
        }
        //
        if(isPlay){
            mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_pause);
        }else{
            mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_play);
        }
        //点击的事件处理
        Intent buttonIntent = new Intent(ACTION_BUTTON);
		*/
/* 上一首按钮 *//*

        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PREV_ID);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_prev, intent_prev);
		*/
/* 播放/暂停  按钮 *//*

        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
        PendingIntent intent_paly = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_play, intent_paly);
		*/
/* 下一首 按钮  *//*

        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
        PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_next, intent_next);

        mBuilder.setContent(mRemoteViews)
                .setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker(&quot;正在播放&quot;)
        .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(true)
                .setSmallIcon(R.drawable.sing_icon);
        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(notifyId, notify);
    }
*/

    public class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(ConstMsg.SONG_STATE, 0);
            int during = intent.getIntExtra(ConstMsg.SONG_DURING, 0);
            LogHelper.i("TAG", "可以在这里更新播放信息" + state);
        }

    }
}