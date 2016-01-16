package com.example.larry.myapplication.songList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.larry.myapplication.R;
import com.example.larry.myapplication.entity.DummyContent;
import com.example.larry.myapplication.utils.ViewFindUtils;

/**
 * Created by 067231 on 2015/12/21.
 */
public class SongListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.song_list,container,false);
        View recyclerView =  ViewFindUtils.find(rootView,R.id.song_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager( getContext()));
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this.getContext(), DummyContent.ITEMS));
//        recyclerView.addItemDecoration(new DividerItemDecoration(
//                getActivity(), DividerItemDecoration.VERTICAL_LIST));
    }

}
