package com.example.larry.myapplication.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.larry.myapplication.network.NetworkModule;

/**
 * Created by Larry on 2016/1/15.
 */
public class MyFragment extends Fragment {
    private NetworkModule networkModule;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkModule = ((UILApplication)getActivity().getApplication()).getNetworkModule();

    }

    public NetworkModule  getNetworkModule(){
        return networkModule;
    }


}
