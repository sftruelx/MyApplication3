package com.example.larry.myapplication;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.larry.myapplication.banner.SimpleImageBanner;
import com.example.larry.myapplication.dummy.DummyContent;
import com.example.larry.myapplication.songList.DividerItemDecoration;
import com.example.larry.myapplication.songList.SimpleItemRecyclerViewAdapter;
import com.example.larry.myapplication.songList.SongListActivity;
import com.example.larry.myapplication.songList.SongListFragment;
import com.example.larry.myapplication.utils.BitmapCache;
import com.example.larry.myapplication.utils.DataProvider;
import com.example.larry.myapplication.utils.DepthPageTransformer;
import com.example.larry.myapplication.utils.LogHelper;
import com.example.larry.myapplication.utils.T;
import com.example.larry.myapplication.utils.ViewFindUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Larry on 2015/12/13.
 */
public class TablayoutFragment extends Fragment {

    private static final String TAG = LogHelper.makeLogTag(TablayoutFragment.class);

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_tablayout,container,false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(this.getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
//        添加下行代码后，会出现事件穿透现象
//        mViewPager.setPageTransformer(true,new DepthPageTransformer());
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        return rootView;
    }


    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }




        protected  TextView tv;
        protected  SwipeRefreshLayout swipeRefreshLayout;
        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            int tab_number =  getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = null;
            @LayoutRes int resource ;
            switch (tab_number){
                case 1:
                    resource = R.layout.tab_one;
                    rootView = inflater.inflate(resource, container, false);
                    sib_simple_usage(rootView);
                    tv = (TextView)rootView.findViewById(R.id.tab1_title);
                    tv.setText(new SimpleDateFormat("dd-MMM EEEEE aa", Locale.ENGLISH).format(new Date()));
                    LinearLayout line = (LinearLayout)rootView.findViewById(R.id.add_panel);
                    View v1 = inflater.inflate(R.layout.tab_one_demo,container,false);
                    line.addView(v1);
                    break;
                case 2:
                    resource = R.layout.tab_two;
                    rootView = inflater.inflate(resource, container, false);
//                    RelativeLayout tab2Frame = (RelativeLayout) rootView.findViewById(R.id.tab2_frame);
                    getChildFragmentManager().beginTransaction().replace(R.id.song_list_frame,new SongListFragment()).commit();
//                    if (rootView.findViewById(R.id.song_detail_container) != null) {
//                    }
                    break;
                default:
                    resource = R.layout.tab_three;
                    rootView = inflater.inflate(resource, container, false);
                    break;
            }
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }


        private void sib_simple_usage(final View view) {
            SimpleImageBanner sib = ViewFindUtils.find(view, R.id.sib_simple_usage);

            sib
                    .setSource(DataProvider.getList())
                    .startScroll();

            sib.setOnItemClickL(new SimpleImageBanner.OnItemClickL() {
                @Override
                public void onItemClick(int position) {
                    T.showShort(view.getContext(), "position--->" + position);
                }
            });
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    SpannableString sp = getSpannableString(R.drawable.ic_action_data_usage,"推荐");
                    return  sp;
                case 1:
                    SpannableString sp1 = getSpannableString(R.drawable.ic_action_dial_pad,"分类");
                    return  sp1;
                case 2:
                    SpannableString sp2 = getSpannableString(R.drawable.ic_action_directions,"直播");
                    return  sp2;
                case 3:
                    SpannableString sp3 = getSpannableString(R.drawable.ic_action_discard,"榜单");
                    return  sp3;
                case 4:
                    SpannableString sp4 = getSpannableString(R.drawable.ic_action_dock,"主播");
                    return  sp4;
            }
            return null;
        }

        @NonNull
        private SpannableString getSpannableString(@DrawableRes int id, String title) {
            Drawable dImage = getResources().getDrawable(id);
            dImage.setBounds(0, 0, dImage.getIntrinsicWidth(), dImage.getIntrinsicHeight());
            //这里前面加的空格就是为图片显示
            SpannableString sp = new SpannableString("  "+ title);
            ImageSpan imageSpan = new ImageSpan(dImage, ImageSpan.ALIGN_BOTTOM);
            sp.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sp;
        }


    }
}
