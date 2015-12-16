package com.example.larry.myapplication.media;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Ceated by Larry on 2015/12/16.
 * 我的歌单
 *
 */
public class Album {


    private  int current = 0; //当前播放的曲目

    private ArrayList<Song> songArrayList  = new ArrayList<Song>();


    private static final Album Instance = new Album();

    public static Album getInstance() {
        return Instance;
    }


    public void addSong(Song song){
        songArrayList.add(song);
    }

    public void clear(){
        songArrayList.clear();
    }

    public Song getSong(){
       return songArrayList.get(current);
    }

    public Song getSong(int index){
        return songArrayList.get(index);
    }

    public int getCurrent(){
        return current;
    }
}
