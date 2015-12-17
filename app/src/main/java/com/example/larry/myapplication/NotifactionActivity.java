package com.example.larry.myapplication;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

/**
 * Created by 067231 on 2015/12/16.
 */
public class NotifactionActivity extends Activity {
    /** Called when the activity is first created. */
    int notification_id=19172439;
    NotificationManager nm;
    Handler handler=new Handler();
    Notification notification;
    int count=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button bt1=(Button)findViewById(R.id.bt1);
        bt1.setOnClickListener(bt1lis);
        Button bt2=(Button)findViewById(R.id.bt2);
        bt2.setOnClickListener(bt2lis);
        //建立notification,前面有学习过，不解释了，不懂看搜索以前的文章
        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notification=new Notification(R.drawable.ic_launcher,"图标边的文字",System.currentTimeMillis());
        notification.contentView = new RemoteViews(getPackageName(),R.layout.notification);
        //使用notification.xml文件作VIEW
        notification.contentView.setProgressBar(R.id.pb, 100,0, false);
        //设置进度条，最大值 为100,当前值为0，最后一个参数为true时显示条纹
        //（就是在Android Market下载软件，点击下载但还没获取到目标大小时的状态）
        Intent notificationIntent = new Intent(this,NotifactionActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        notification.contentIntent = contentIntent;
    }
    View.OnClickListener bt1lis=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            showNotification();//显示notification
            handler.post(run);
        }

    };
    Runnable run=new Runnable(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            count++;
            notification.contentView.setProgressBar(R.id.pb, 100,count, false);
            //设置当前值为count
            showNotification();//这里是更新notification,就是更新进度条
            if(count<=100) handler.postDelayed(run, 200);
            //200毫秒count加1
        }

    };
    View.OnClickListener bt2lis=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            nm.cancel(notification_id);
            //清除notification
        }

    };
    public void showNotification(){
        nm.notify(notification_id, notification);
    }
}