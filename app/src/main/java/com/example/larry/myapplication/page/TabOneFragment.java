package com.example.larry.myapplication.page;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Receiver;
import com.android.volley.TaskHandle;
import com.example.larry.myapplication.R;
import com.example.larry.myapplication.entity.Album;
import com.example.larry.myapplication.entity.DataModule;
import com.example.larry.myapplication.network.NetworkModule;
import com.example.larry.myapplication.utils.UILApplication;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 067231 on 2016/1/14.
 */
public class TabOneFragment extends Fragment implements Receiver<DataModule> {
    private static final String CLASSIFY_TYPE = "classify_type";
    private int classifyType;
    public static TabOneFragment newInstance(int type){
        TabOneFragment tabone = new TabOneFragment();
        Bundle args = new Bundle();
        args.putInt(CLASSIFY_TYPE, type);
        tabone.setArguments(args);
        return tabone;
    }

    private NetworkModule networkModule;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classifyType =  getArguments().getInt(CLASSIFY_TYPE);
        networkModule = ((UILApplication)getActivity().getApplication()).getNetworkModule();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_one_column,container,false);
        beginVolley();
        return rootView;
    }
    private void beginVolley(){
        TaskHandle handle_0 = networkModule.arrangeGetNewsList("arrangeGetNewsList", null, null, 1, 10, null);
        handle_0.setId(0);
        handle_0.setReceiver(this);
        handle_0.pullTrigger();

    }

    @Override
    public void onSucess(TaskHandle handle, DataModule result) {

//        final LinearLayout layout2=new LinearLayout(getContext());//定义一个新的LinearLayout
//        layout2.setOrientation(LinearLayout.HORIZONTAL);//设置为水平
        for(int i=0;i<1;i++) {
            System.out.println("===========================================");
            ArrayList<Album> list = new ArrayList<Album>();
            Album a1 = new Album();
            a1.setId(12L);
            a1.setAlbumName("1111");
            list.add(a1);
            Album a2 = new Album();
            a2.setId(22L);
            a2.setAlbumName("22222");
            list.add(a2);
            Album a3 = new Album();
            a3.setId(33L);
            a3.setAlbumName("3333");
            list.add(a3);
            Album a4 = new Album();
            a4.setId(44L);
            a4.setAlbumName("y444tutyu");
            list.add(a4);
//            getChildFragmentManager().beginTransaction().add(R.id.column_linear_frame, PicTextFragment.newInstance("小编推荐",list)).commit();

        }

        switch (handle.id()){

            case 0:
                if(result.code() == DataModule.CodeSucess){

                }

                break;


        }
    }

    @Override
    public void onError(TaskHandle handle, Throwable error) {
        switch (handle.id()){
            case 0:
                break;


        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

