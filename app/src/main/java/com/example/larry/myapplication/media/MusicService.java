package com.example.larry.myapplication.media;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.larry.myapplication.MainActivity;
import com.example.larry.myapplication.NotifactionActivity;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.utils.LogHelper;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Larry on 2015/12/16.
 */
public class MusicService extends Service {
    protected static String TAG = LogHelper.makeLogTag(MusicService.class);

    MusicSercieReceiver receiver;
    static boolean isChanging=false;//互斥变量，防止定时器与SeekBar拖动时进度冲突
    //创建一个媒体播放器的对象
    static MediaPlayer mediaPlayer;
    //创建一个Asset管理器的的对象
    AssetManager assetManager;
    //存放音乐名的数组
//    应该放个储存歌曲的对象数组才好

    Album album = Album.getInstance();
    int during = 0;
    int currentPosition = 0;
    //当前的播放的音乐
    int current=0;
    //当前播放状态
    int state= ConstMsg.STATE_NONE;
    //记录Timer运行状态
    boolean isTimerRunning=false;
    @Override
    public void onCreate() {
        super.onCreate();
        LogHelper.i(TAG,"service 启动！");
        //注册接收器
        receiver=new MusicSercieReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(ConstMsg.MUSICCLIENT_ACTION);
        registerReceiver(receiver, filter);
        mediaPlayer=new MediaPlayer();
        assetManager=getAssets();
        //为mediaPlayer的完成事件创建监听器
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                current++;
                prepare(current);
                sendBroadcastToClient(ConstMsg.STATE_NONE);
            }
        });
        showNotificationPanel();
        showNotification();

    }

    int notification_id = 19172439;
    NotificationManager nm;
    Handler handler = new Handler();
    Notification notification;
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
//        点击通知栏激活activity
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(ConstMsg.SONG_STATE,state);
        notificationIntent.putExtra(ConstMsg.SONG_PROGRESS,currentPosition);
        notificationIntent.putExtra(ConstMsg.SONG_DURING, during);
        notification.contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    }

    public void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(ConstMsg.SONG_STATE,state);
        notificationIntent.putExtra(ConstMsg.SONG_PROGRESS,currentPosition);
        notificationIntent.putExtra(ConstMsg.SONG_DURING, during);
        notification.contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        nm.notify(notification_id, notification);
    }

    protected void sendBroadcastToClient(int state) {
        LogHelper.i(TAG, "发送Service控制广播" + state);
        Intent intent = new Intent(ConstMsg.MUSICSERVICE_ACTION);
        intent.putExtra(ConstMsg.SONG_STATE,state);
        intent.putExtra(ConstMsg.SONG_PROGRESS,currentPosition);
        intent.putExtra(ConstMsg.SONG_DURING, during);
        sendBroadcast(intent);

    }
    /**
     * 装载和播放音乐
     * @param index int index 播放第几首音乐的索引
     * */
    protected void prepare(int index) {
        AssetFileDescriptor fileDesc;
        try {
//            fileDesc = this.getResources()
//                    .openRawResourceFd(R.raw.test);
//            if (fileDesc != null) {
//                mediaPlayer.setDataSource(fileDesc.getFileDescriptor(), fileDesc.getStartOffset(), fileDesc.getLength());
//                fileDesc.close();
//                //准备播放音乐
//            }
//            mediaPlayer.prepare();
            //播放音乐

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendProgress(){
        during = 18000;
        Timer  mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                isTimerRunning=true;
                if(isChanging==true)//当用户正在拖动进度进度条时不处理进度条的的进度
                    return;
                currentPosition = currentPosition + 500;
                if(currentPosition>during){
                    this.cancel();

                }
                notification.contentView.setProgressBar(R.id.pb, during, currentPosition, false);
                showNotification();
                sendBroadcastToClient(ConstMsg.STATE_PLAYING);

            }
        };
        //每隔10毫秒检测一下播放进度
        mTimer.schedule(mTimerTask, 0, 500);
    }

    @Override
    public boolean stopService(Intent name) {
        nm.cancel(notification_id);
       unregisterReceiver(receiver);
        return super.stopService(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    //创建广播接收器用于接收前台Activity发来的广播
    class MusicSercieReceiver extends BroadcastReceiver {
        protected  String TAG = LogHelper.makeLogTag(MusicSercieReceiver.class);
        @Override
        public void onReceive(Context context, Intent intent){
            int control=intent.getIntExtra(ConstMsg.SONG_STATE, 0);
            LogHelper.i(TAG,"接收前台Activity发来的广播" + control);
            switch (control) {
                case ConstMsg.STATE_PLAYING://播放音乐
                    if (state==ConstMsg.STATE_PAUSED) {//如果原来状态是暂停
                        mediaPlayer.start();

                    }else if (state!=ConstMsg.STATE_PLAYING) {
                        prepare(current);
                        mediaPlayer.start();
                        sendProgress();

                    }
                    state=ConstMsg.STATE_PLAYING;
                    break;
                case ConstMsg.STATE_PAUSED://暂停播放
                    if (state==ConstMsg.STATE_PLAYING) {
                        mediaPlayer.pause();
                        state=ConstMsg.STATE_PAUSED;
                    }
                    break;
                case ConstMsg.STATE_STOPPED://停止播放
                    if (state==ConstMsg.STATE_PLAYING||state==ConstMsg.STATE_PAUSED) {
                        mediaPlayer.stop();
                        state=ConstMsg.STATE_STOPPED;
                    }
                    break;
                case ConstMsg.STATE_PREVIOUS://上一首
                    prepare(--current);
                    state=ConstMsg.STATE_PLAYING;
                    break;
                case ConstMsg.STATE_NEXT://下一首
                    prepare(++current);
                    state=ConstMsg.STATE_PLAYING;
                    break;
                default:
                    break;
            }
//            更新通知面板
               switch (state) {
                case ConstMsg.STATE_PLAYING:
                    notification.contentView.setImageViewResource(R.id.play_pause,R.drawable.ic_pause_black_36dp);
                    Intent pause_intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
                    pause_intent.putExtra(ConstMsg.SONG_STATE, ConstMsg.STATE_PAUSED);
                    notification.contentView.setOnClickPendingIntent(R.id.play_pause,
                    PendingIntent.getBroadcast(getApplicationContext(), ConstMsg.STATE_PAUSED,
                            pause_intent,
                            PendingIntent.FLAG_UPDATE_CURRENT));
                    break;
                case ConstMsg.STATE_PAUSED:
                    notification.contentView.setImageViewResource(R.id.play_pause,R.drawable.ic_play_arrow_black_36dp);
                    Intent play_intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
                    play_intent.putExtra(ConstMsg.SONG_STATE, ConstMsg.STATE_PLAYING);
                    notification.contentView.setOnClickPendingIntent(R.id.play_pause,
                    PendingIntent.getBroadcast(getApplicationContext(), ConstMsg.STATE_PLAYING,
                            play_intent,
                            PendingIntent.FLAG_UPDATE_CURRENT));
                    break;
            }

            showNotification();
            sendBroadcastToClient(control);
        }

    }
}
