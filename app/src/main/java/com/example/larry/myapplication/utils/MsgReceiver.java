package com.example.larry.myapplication.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.larry.myapplication.media.ConstMsg;

public class MsgReceiver extends BroadcastReceiver {
    private int state;

    @Override
    public void onReceive(Context context, Intent intent) {
        state = intent.getIntExtra(ConstMsg.SONG_STATE, 0);
        int during = intent.getIntExtra(ConstMsg.SONG_DURING, 0);
        int currentPosition = intent.getIntExtra(ConstMsg.SONG_PROGRESS, 0);

        LogHelper.i("", "播放信息" + state);
//        mControlsFragment.updateState(state, currentPosition, during);


    }

}