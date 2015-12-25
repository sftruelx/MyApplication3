package com.example.larry.myapplication.demo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.larry.myapplication.R;

/**
 * Created by 067231 on 2015/12/25.
 */
public class TabOneDemoFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_one_demo,container,false);
        return view;
    }
}
