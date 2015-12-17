package com.example.larry.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RemoteViews;

import com.example.larry.myapplication.media.ConstMsg;
import com.example.larry.myapplication.media.MusicService;
import com.example.larry.myapplication.utils.ConfigStore;
import com.example.larry.myapplication.utils.LogHelper;
import com.example.larry.myapplication.utils.NetworkHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = LogHelper.makeLogTag(MainActivity.class);
    protected Intent mIntent;
    protected PlaybackControlsFragment mControlsFragment;
    protected MsgReceiver musicReceiver;
    /******通知栏*****/
    int notification_id=19172439;
    NotificationManager nm;
    Handler handler=new Handler();
    Notification notification;
    /******通知栏*****/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //注册广播接收器
        musicReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstMsg.MUSICCLIENT_ACTION);
        registerReceiver(musicReceiver, intentFilter);
        //启动MUSIC服务
        Intent intent = new Intent(this,MusicService.class);
        getApplicationContext().startService(intent);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout,new TablayoutFragment()).commit();
        boolean bool = ConfigStore.isFirstEnter(getBaseContext(),this.getLocalClassName());
        if(!bool) {
            drawer.openDrawer(GravityCompat.START);
            ConfigStore.writeFirstEnter(getBaseContext(), this.getLocalClassName());
        }
        mControlsFragment = (PlaybackControlsFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_playback_controls);
        if (mControlsFragment == null) {
            throw new IllegalStateException("Mising fragment with id 'controls'. Cannot continue.");
        }
        showPlaybackControls();

    }

    private void initNotification(){
        //建立notification,前面有学习过，不解释了，不懂看搜索以前的文章
        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notification=new Notification(R.drawable.ic_launcher,"图标边的文字",System.currentTimeMillis());
        notification.contentView = new RemoteViews(getPackageName(),R.layout.notification);
        //使用notification.xml文件作VIEW
        notification.contentView.setProgressBar(R.id.pb, 100,0, false);
        //设置进度条，最大值 为100,当前值为0，最后一个参数为true时显示条纹
        //（就是在Android Market下载软件，点击下载但还没获取到目标大小时的状态）
        Intent notificationIntent = new Intent(this,NotifactionActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        notification.contentIntent = contentIntent;
        showNotification();
    }

    public void showNotification(){
        nm.notify(notification_id, notification);
    }
    /**
     *向后台Service发送控制广播ConstMsg 里面都有
     *@param state int state 控制状态码
     * */
    protected void sendBroadcastToService(int state) {
        Intent intent=new Intent();
        intent.setAction(ConstMsg.MUSICCLIENT_ACTION);
        intent.putExtra("control", state);
        sendBroadcast(intent);
        LogHelper.i(TAG,"发送控制广播" + state);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    protected void showPlaybackControls() {
        LogHelper.d(TAG, "showPlaybackControls");
        if (NetworkHelper.isOnline(this)) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.animator.slide_in_from_bottom, R.animator.slide_out_to_bottom,
                            R.animator.slide_in_from_bottom, R.animator.slide_out_to_bottom)
                    .show(mControlsFragment)
                    .commit();
        }
    }

    protected void hidePlaybackControls() {
        LogHelper.d(TAG, "hidePlaybackControls");
        getFragmentManager().beginTransaction()
                .hide(mControlsFragment)
                .commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            hidePlaybackControls();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //这里应该使用fragment替换掉原有的fragment
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            showPlaybackControls();
            sendBroadcastToService(ConstMsg.STATE_PLAY);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onDestroy() {
        //停止服务
        stopService(mIntent);
        //注销广播
        unregisterReceiver(musicReceiver);
        super.onDestroy();
    }
    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //可以在这里更新播放信息
            int progress = intent.getIntExtra("progress", 0);
//            mProgressBar.setProgress(progress);
            LogHelper.i(TAG,"接收Service传送过来的消息");
        }

    }
}
