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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.larry.myapplication.MainActivity;
import com.example.larry.myapplication.NotifactionActivity;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.entity.*;
import com.example.larry.myapplication.entity.Album;
import com.example.larry.myapplication.utils.AppUrl;
import com.example.larry.myapplication.utils.LogHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Larry on 2015/12/16.
 */
public class MusicService extends Service {
    protected static String TAG = LogHelper.makeLogTag(MusicService.class);

    MusicSercieReceiver receiver;
    static boolean isChanging = false;//互斥变量，防止定时器与SeekBar拖动时进度冲突
    //创建一个媒体播放器的对象
    static MediaPlayer mediaPlayer;
    //创建一个Asset管理器的的对象
    ArrayList<Artist> songList;
    Album album;
    byte[] bis;
    int color;
    int during = 0;
    int currentPosition = 0;
    //当前的播放的音乐
    int current = 0;
    //当前播放状态
    int state = ConstMsg.STATE_NONE;
    //记录Timer运行状态
    boolean isTimerRunning = false;
    int control;

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        nm.cancelAll();
        mediaPlayer.release();
        LogHelper.i(TAG, "service 结束！");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogHelper.i(TAG, "service 启动！");
        //注册接收器
        receiver = new MusicSercieReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstMsg.MUSICCLIENT_ACTION);
        registerReceiver(receiver, filter);

        showNotificationPanel();

    }

    int notification_id = 19172439;
    NotificationManager nm;
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
        notificationIntent.putExtra(ConstMsg.SONG_STATE, state);
        notificationIntent.putExtra(ConstMsg.SONG_PROGRESS, currentPosition);
        notificationIntent.putExtra(ConstMsg.SONG_DURING, during);
        notification.contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    }

    public void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(ConstMsg.SONG_STATE, state);
        notificationIntent.putExtra(ConstMsg.SONG_PROGRESS, currentPosition);
        notificationIntent.putExtra(ConstMsg.SONG_DURING, during);
        notification.contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        nm.notify(notification_id, notification);
    }

    protected void sendBroadcastToClient(int state) {
//        LogHelper.i(TAG, "发送Service控制广播" + state);
        Intent intent = new Intent(ConstMsg.MUSICSERVICE_ACTION);
        intent.putExtra(ConstMsg.SONG_STATE, state);
        intent.putExtra(ConstMsg.SONG_PROGRESS, currentPosition);
        intent.putExtra(ConstMsg.SONG_DURING, during);
        intent.putExtra(ConstMsg.SONG_ARTIST, songList);
        intent.putExtra(ConstMsg.ALBUM, album);
        intent.putExtra(ConstMsg.SONG_ICON, bis);
        intent.putExtra(ConstMsg.SONG_COLOR, color);
        intent.putExtra(ConstMsg.SONG_POSITION, current);
        sendBroadcast(intent);
        if (state != ConstMsg.STATE_NONE) {
            updateState();
        } else {
            nm.cancelAll();
        }
    }

    protected void sendBroadcastToClientLight(int state) {
//        LogHelper.i(TAG, "发送Service控制广播" + state);
        Intent intent = new Intent(ConstMsg.MUSICSERVICE_ACTION);
        intent.putExtra(ConstMsg.SONG_STATE, state);
        intent.putExtra(ConstMsg.SONG_PROGRESS, currentPosition);
        intent.putExtra(ConstMsg.SONG_DURING, during);
//        intent.putExtra(ConstMsg.SONG_ARTIST, songList);
//        intent.putExtra(ConstMsg.ALBUM, album);
//        intent.putExtra(ConstMsg.SONG_ICON, bis);
//        intent.putExtra(ConstMsg.SONG_COLOR, color);
//        intent.putExtra(ConstMsg.SONG_POSITION, current);
        sendBroadcast(intent);
        if (state != ConstMsg.STATE_NONE) {
            updateState();
        } else {
            nm.cancelAll();
        }
    }

    protected void updateState() {
        switch (state) {
            case ConstMsg.STATE_PLAYING:
                notification.contentView.setTextViewText(R.id.title, ((Artist) songList.get(current)).getArtistName());
                notification.contentView.setTextViewText(R.id.artist, album.getAlbumName() + " " + album.getAuthor());
                notification.contentView.setImageViewResource(R.id.play_pause, R.drawable.ic_pause_black_36dp);
                Intent pause_intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
                pause_intent.putExtra(ConstMsg.SONG_STATE, ConstMsg.STATE_PAUSED);
                notification.contentView.setOnClickPendingIntent(R.id.play_pause,
                        PendingIntent.getBroadcast(getApplicationContext(), ConstMsg.STATE_PAUSED,
                                pause_intent,
                                PendingIntent.FLAG_UPDATE_CURRENT));
                break;
            case ConstMsg.STATE_PAUSED:
                notification.contentView.setImageViewResource(R.id.play_pause, R.drawable.ic_play_arrow_black_36dp);
                Intent play_intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
                play_intent.putExtra(ConstMsg.SONG_STATE, ConstMsg.STATE_PLAYING);
                notification.contentView.setOnClickPendingIntent(R.id.play_pause,
                        PendingIntent.getBroadcast(getApplicationContext(), ConstMsg.STATE_PLAYING,
                                play_intent,
                                PendingIntent.FLAG_UPDATE_CURRENT));
                break;
            case ConstMsg.STATE_NONE:
                notification.contentView.setImageViewResource(R.id.play_pause, R.drawable.ic_play_arrow_black_36dp);
                Intent none_intent = new Intent(ConstMsg.MUSICCLIENT_ACTION);
                none_intent.putExtra(ConstMsg.SONG_STATE, ConstMsg.STATE_PLAYING);
                notification.contentView.setOnClickPendingIntent(R.id.play_pause,
                        PendingIntent.getBroadcast(getApplicationContext(), ConstMsg.STATE_PLAYING,
                                none_intent,
                                PendingIntent.FLAG_UPDATE_CURRENT));
                break;
        }

        showNotification();

    }

    protected void prepare(int index) {
        if (mTimerTask != null) {

            mTimerTask.cancel();
        }
        LogHelper.i(TAG, " index " + index + " current " + current + "  " + songList.toString());
        if (songList.size() > index && index >= 0) {
            final Artist artist = songList.get(index);
            try {
                currentPosition = 0;
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(AppUrl.webUrl + artist.getArtistPath());
                mediaPlayer.prepareAsync();
                //为mediaPlayer的完成事件创建监听器
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mTimerTask != null) {
                            mTimerTask.cancel();
                        }
                        mediaPlayer.release();
                        mediaPlayer = null;
                        LogHelper.i(TAG, "....setOnCompletionListener..........control " + control + " current " + current + " state " + state);
                        state = ConstMsg.STATE_NONE;
                        sendBroadcastToClient(state);
                        if (songList.size() > current + 1) {
                            prepare(++current);
                        }
                    }
                });
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        LogHelper.i(TAG, "prepared......" + mp + " " + mediaPlayer);
                        mediaPlayer = mp;
                        if (mediaPlayer != null) {
                            mediaPlayer.start();
                            state = ConstMsg.STATE_PLAYING;
                            sendBroadcastToClient(state);
                            sendProgress();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            state = ConstMsg.STATE_NONE;
            sendBroadcastToClient(state);
            current = 0;
        }
    }

    private TimerTask mTimerTask;

    public void sendProgress() {
        during = mediaPlayer.getDuration();
        Timer mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
//                LogHelper.i(TAG,"Run...");
                if (state == ConstMsg.STATE_STOPPED || state == ConstMsg.STATE_NONE) {
                    this.cancel();
                }
                isTimerRunning = true;
                if (isChanging == true)//当用户正在拖动进度进度条时不处理进度条的的进度
                    return;
                currentPosition= 0;
                if (mediaPlayer != null) {
                    if(mediaPlayer.getCurrentPosition()>0) {
                        currentPosition = Math.round(mediaPlayer.getCurrentPosition()/1000) ;
                        notification.contentView.setProgressBar(R.id.pb, songList.get(current).getArtistTraceLength(), currentPosition, false);
                    }
                }
                sendBroadcastToClient(state);
//                currentPosition = mediaPlayer.getCurrentPosition();
//                LogHelper.i(TAG,"Run..." + currentPosition + "   " + mediaPlayer.getDuration());
/*                if (currentPosition > during) {
                    this.cancel();
                }*/
//                notification.contentView.setProgressBar(R.id.pb, during, currentPosition, false);
//                showNotification();
//                sendBroadcastToClientLight(ConstMsg.STATE_PLAYING);

            }
        };
        //每隔10毫秒检测一下播放进度
        mTimer.schedule(mTimerTask, 0, 1000);
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
        protected String TAG = LogHelper.makeLogTag(MusicSercieReceiver.class);

        @Override
        public void onReceive(Context context, Intent intent) {

            ArrayList list = intent.getParcelableArrayListExtra(ConstMsg.ARISTLIST);
            if (list != null) {
                songList = list;
                current = 0;
                LogHelper.i(TAG, "接收前台Activity发来的Song" + songList.size());
                album = (Album) intent.getParcelableExtra(ConstMsg.ALBUM);
            }
            byte[] mbis = intent.getByteArrayExtra(ConstMsg.SONG_ICON);
            if (mbis != null) {
                bis = mbis;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
                notification.contentView.setImageViewBitmap(R.id.album_art, bitmap);
                color = intent.getIntExtra(ConstMsg.SONG_COLOR, -1);
            }
            if (songList == null) return; //没歌放什么放
            control = intent.getIntExtra(ConstMsg.SONG_STATE, 0);
            LogHelper.i(TAG, "接收前台Activity发来的广播" + control);

            switch (control) {
                case ConstMsg.STATE_PLAYING://播放音乐
                    if (state == ConstMsg.STATE_PAUSED) {//如果原来状态是暂停
                        mediaPlayer.start();
                        state = ConstMsg.STATE_PLAYING;
                        sendBroadcastToClient(state);
                    } else if (state == ConstMsg.STATE_NONE || state == ConstMsg.STATE_STOPPED) {
                        prepare(current);
                        state = ConstMsg.STATE_PLAYING;
                    } else {
                        mediaPlayer.stop();
                        prepare(current);
                        state = ConstMsg.STATE_STOPPED;
                    }
                    break;
                case ConstMsg.STATE_STOPPED://停止播放
                    if (state == ConstMsg.STATE_PLAYING || state == ConstMsg.STATE_PAUSED) {
                        mediaPlayer.stop();
                        state = ConstMsg.STATE_STOPPED;
                        sendBroadcastToClient(state);
                    }
                    break;
                case ConstMsg.STATE_PAUSED://暂停播放
                    if (state == ConstMsg.STATE_PLAYING) {
                        mediaPlayer.pause();
                        state = ConstMsg.STATE_PAUSED;
                        sendBroadcastToClient(state);
                    }
                    break;
                case ConstMsg.STATE_PREVIOUS://上一首
                    if (songList != null) {
                        if (current - 1 >= 0) {
                            mediaPlayer.stop();
                            state = ConstMsg.STATE_STOPPED;
                            prepare(--current);
                            state = ConstMsg.STATE_PLAYING;
                        }
                    }
                    break;
                case ConstMsg.STATE_NEXT://下一首
                    if (songList != null) {
                        if (songList.size() > current + 1) {
                            mediaPlayer.stop();
                            state = ConstMsg.STATE_STOPPED;
                            prepare(++current);
                            state = ConstMsg.STATE_PLAYING;
                        }
                    }
                    break;
            }
            sendBroadcastToClient(state);
        }
    }
}
