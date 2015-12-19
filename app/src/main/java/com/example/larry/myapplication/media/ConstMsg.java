package com.example.larry.myapplication.media;

/**
 * Created by Larry on 2015/12/16.
 */
public class ConstMsg {

    public static final String MUSIC_INF="MUSIC INFO";
    //MusicPanel接收器所能响应的Action
    public static final String MUSICCLIENT_ACTION="com.example.larry.myapplication.MUSICPANEL_ACTION";
    //MusicService接收器所能响应的Action
    public static final String MUSICSERVICE_ACTION="com.example.larry.myapplication.MUSICSERVICE_ACTION";
    //初始化flag
    public static final int STATE_NONE=0x122;
    //播放的flag
    public static final int STATE_PLAYING=0x123;
    //暂停的flag
    public static final int STATE_PAUSED=0x124;
    //停止放的flag
    public static final int STATE_STOPPED=0x125;
    //播放上一首的flag
    public static final int STATE_PREVIOUS=0x126;
    //播放下一首的flag
    public static final int STATE_NEXT=0x127;

    public static final int STATE_BUFFERING=0x128;

    public static final int STATE_CONNECTING=0x129;
    //菜单关于选项的itemId
    public static final int MENU_ABOUT=0x200;
    //菜单退出选的项的itemId
    public static final int MENU_EXIT=0x201;

    public static final String SONG_TITLE = "song title";
    public static final String SONG_ARTIST = "song artist";
    public static final String SONG_PROGRESS = "song progress";
    public static final String SONG_TIME = "song time";
    public static final String SONG_ICON = "song icon";
    public static final String SONG_DURING = "song during";
    public static final String SONG_STATE = "song state";

    public ConstMsg() {
        // TODO Auto-generated constructor stub
    }

}
