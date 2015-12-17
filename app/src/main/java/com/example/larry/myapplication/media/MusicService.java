package com.example.larry.myapplication.media;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.larry.myapplication.utils.LogHelper;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Larry on 2015/12/16.
 */
public class MusicService extends Service {
    protected static String TAG = LogHelper.makeLogTag(MusicService.class);
    private Intent intent = new Intent("com.example.larry.myapplication.RECEIVER");
    Timer mTimer;
    TimerTask mTimerTask;
    static boolean isChanging=false;//互斥变量，防止定时器与SeekBar拖动时进度冲突
    //创建一个媒体播放器的对象
    static MediaPlayer mediaPlayer;
    //创建一个Asset管理器的的对象
    AssetManager assetManager;
    //存放音乐名的数组
//    应该放个储存歌曲的对象数组才好

    Album album = Album.getInstance();

    String[]musics=new String[]{"taoshengyijiu-maoning.mp3", "youcaihua-chenglong.mp3","You Are The One.mp3" };
    //当前的播放的音乐
    int current=0;
    //当前播放状态
    int state= ConstMsg.STATE_NON;
    //记录Timer运行状态
    boolean isTimerRunning=false;
    @Override
    public void onCreate() {
        super.onCreate();
        LogHelper.i(TAG,"service 启动！");
        //注册接收器
        MusicSercieReceiver receiver=new MusicSercieReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(ConstMsg.MUSICCLIENT_ACTION);
        registerReceiver(receiver, filter);
        mediaPlayer=new MediaPlayer();
        assetManager=getAssets();
        //为mediaPlayer的完成事件创建监听器
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
//              mTimer.cancel();//取消定时器
                current++;
                prepareAndPlay(current);
            }
        });

    }
    /**
     * 装载和播放音乐
     * @param index int index 播放第几首音乐的索引
     * */
    protected void prepareAndPlay(int index) {
        // TODO Auto-generated method stub
//        if (isTimerRunning) {//如果Timer正在运行
//            mTimer.cancel();//取消定时器
//            isTimerRunning=false;
//        }
        if (index>2) {
            current=index=0;
        }
        if (index<0) {
            current=index=2;
        }
        //发送广播停止前台Activity更新界面
        Intent intent=new Intent();
        intent.putExtra("current", index);
        intent.setAction(ConstMsg.MUSICSERVICE_ACTION);
        sendBroadcast(intent);
        try {
            //TODO 读取SD卡内的语音文件
            //获取assets目录下指定文件的AssetFileDescriptor对象
            AssetFileDescriptor assetFileDescriptor=assetManager.openFd(musics[current]);
            mediaPlayer.reset();//初始化mediaPlayer对象
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            //准备播放音乐
            mediaPlayer.prepare();
            //播放音乐
            mediaPlayer.start();

            //getDuration()方法要在prepare()方法之后，否则会出现Attempt to call getDuration without a valid mediaplayer异常
//            MusicBox.skbMusic.setMax(mediaPlayer.getDuration());//设置SeekBar的长度
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    //----------定时器记录播放进度---------//
//        每500ms发送一次广播
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                isTimerRunning=true;
                if(isChanging==true)//当用户正在拖动进度进度条时不处理进度条的的进度
                    return;
//                MusicBox.skbMusic.setProgress(mediaPlayer.getCurrentPosition());
            }
        };
        //每隔10毫秒检测一下播放进度
        mTimer.schedule(mTimerTask, 0, 10);
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
            int control=intent.getIntExtra("control", -1);
            LogHelper.i(TAG,"接收前台Activity发来的广播" + control);
            switch (control) {
                case ConstMsg.STATE_PLAY://播放音乐
                    if (state==ConstMsg.STATE_PAUSE) {//如果原来状态是暂停
                        mediaPlayer.start();
                    }else if (state!=ConstMsg.STATE_PLAY) {
                        prepareAndPlay(current);
                    }
                    state=ConstMsg.STATE_PLAY;
                    break;
                case ConstMsg.STATE_PAUSE://暂停播放
                    if (state==ConstMsg.STATE_PLAY) {
                        mediaPlayer.pause();
                        state=ConstMsg.STATE_PAUSE;
                    }
                    break;
                case ConstMsg.STATE_STOP://停止播放
                    if (state==ConstMsg.STATE_PLAY||state==ConstMsg.STATE_PAUSE) {
                        mediaPlayer.stop();
                        state=ConstMsg.STATE_STOP;
                    }
                    break;
                case ConstMsg.STATE_PREVIOUS://上一首
                    prepareAndPlay(--current);
                    state=ConstMsg.STATE_PLAY;
                    break;
                case ConstMsg.STATE_NEXT://下一首
                    prepareAndPlay(++current);
                    state=ConstMsg.STATE_PLAY;
                    break;
                default:
                    break;
            }
        }

    }
}
