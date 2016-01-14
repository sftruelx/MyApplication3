package com.example.larry.myapplication.page;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.larry.myapplication.R;
import com.example.larry.myapplication.entity.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 067231 on 2016/1/14.
 */
public class PicTextFragment extends Fragment {

    private static final String Frame_list = "frame_list";
    private static final String Frame_TEXT = "frame_text";

    public static PicTextFragment newInstance(ArrayList<Album> albumList){
        PicTextFragment pt = new PicTextFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Frame_list, albumList);
        pt.setArguments(args);
        return pt;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_one_column_content,container,false);
        List<Album> list = getArguments().getParcelableArrayList(Frame_list);
        HorizontalScrollView v = (HorizontalScrollView) rootView.findViewById(R.id.tab_one_column_linear);
        final LinearLayout linear =new LinearLayout(getContext());//定义一个新的LinearLayout
        linear.setOrientation(LinearLayout.HORIZONTAL);//设置为水平
        for(Album album : list){
            View child = inflater.inflate(R.layout.pic_text,container,false);
            TextView tv= (TextView)child.findViewById(R.id.pic_text1);
            System.out.println(album.getAlbumName() + "-----------------");
            tv.setText(album.getAlbumName());
            linear.addView(child);
        }
        v.addView(linear);

        return rootView;
    }
}
