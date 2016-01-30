/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.larry.myapplication;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.larry.myapplication.entity.Album;
import com.example.larry.myapplication.entity.Artist;
import com.example.larry.myapplication.media.ConstMsg;
import com.example.larry.myapplication.utils.LogHelper;
import com.example.larry.myapplication.utils.MyActivity;

/**
 * A class that shows the Media Queue to the user.
 */
public class PlaybackControlsFragment extends Fragment {

    private static final String TAG = LogHelper.makeLogTag(PlaybackControlsFragment.class);

    MyActivity parentActivity;

    private ImageButton mPlayPause;
    private ImageButton mPlayNext;
    private ImageButton mPlayPrevious;
    private TextView mTitle;
    private TextView mSubtitle;
    private TextView mExtraInfo;
    private ImageView mAlbumArt;
    private RelativeLayout mRelate;
    private ProgressBar seekBar;
    private String mArtUrl;
    private int state ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false);
        mAlbumArt =(ImageView) rootView.findViewById(R.id.album_art);
        mRelate =(RelativeLayout)rootView.findViewById(R.id.play_panel);
        mPlayPause = (ImageButton) rootView.findViewById(R.id.play_pause);
        mPlayNext = (ImageButton)rootView.findViewById(R.id.play_next);
        mPlayPrevious = (ImageButton)rootView.findViewById(R.id.play_previous);
        seekBar = (ProgressBar) rootView.findViewById(R.id.seek_bar);
        mTitle = (TextView)rootView.findViewById(R.id.title);
        mSubtitle = (TextView)rootView.findViewById(R.id.artist);
        mPlayPause.setEnabled(true);
        mPlayNext.setEnabled(true);
        mPlayPrevious.setEnabled(true);
        mPlayPause.setOnClickListener(mButtonListener);
        mPlayNext.setOnClickListener(mButtonListener);
        mPlayPrevious.setOnClickListener(mButtonListener);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogHelper.i(TAG, "播放面板被点击,暂时没什么用");
            }
        });
        parentActivity = (MyActivity) getActivity();
        state = parentActivity.state;
        updateState(state,0,0, null, null);
        return rootView;
    }

    /**
     * 播放面板播放按键或暂停按键
     **/

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            LogHelper.i(TAG, "Button pressed, in state " + state);
            switch (v.getId()) {
                case R.id.play_pause:
                    if (state == ConstMsg.STATE_PAUSED ||
                            state == ConstMsg.STATE_STOPPED ||
                            state == ConstMsg.STATE_NONE) {
                        //通知service播放音乐
                        parentActivity.sendBroadcastToService(ConstMsg.STATE_PLAYING,null,null);
                    } else if (state == ConstMsg.STATE_PLAYING ||
                            state == ConstMsg.STATE_BUFFERING ||
                            state == ConstMsg.STATE_CONNECTING) {
                        parentActivity.sendBroadcastToService(ConstMsg.STATE_PAUSED,null,null);
                    }
                    break;
                case R.id.play_next:
                    parentActivity.sendBroadcastToService(ConstMsg.STATE_NEXT,null,null);
                    break;
                case R.id.play_previous:
                    parentActivity.sendBroadcastToService(ConstMsg.STATE_PREVIOUS,null,null);
                    break;
            }
        }
    };

    public void updateImage(Bitmap newBitmap){
        mAlbumArt.setImageBitmap(newBitmap);
    }
    public void updateColor(int color){
        mRelate.setBackgroundColor(color);
    }

    public void updateState(int state, int duringTime, int during, Artist artist, Album album) {
        this.state = state;
        //根据状态更新按钮形态

        if(artist != null){
            mTitle.setText(artist.getArtistName());
            seekBar.setMax(artist.getArtistTraceLength());
            seekBar.setProgress(duringTime);
//            LogHelper.i(TAG,"Length = " + artist.getArtistTraceLength() + " during " + duringTime);
        }
        if(album != null){
            mSubtitle.setText(album.getAlbumName() + " " + album.getAuthor());
        }

        switch (state) {
            case ConstMsg.STATE_PLAYING:
                mPlayPause.setImageResource(R.drawable.ic_pause_black_36dp);
                break;
            case ConstMsg.STATE_PAUSED:
                mPlayPause.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                break;
            case ConstMsg.STATE_NONE:
                mPlayPause.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                break;
        }

    }
}
