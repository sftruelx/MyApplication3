package com.example.larry.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.larry.myapplication.utils.ConfigStore;
import com.example.larry.myapplication.utils.DeviceUuidFactory;
import com.example.larry.myapplication.utils.LogHelper;
import com.example.larry.myapplication.utils.MyActivity;

import java.util.UUID;

public class MainActivity extends MyActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = LogHelper.makeLogTag(MainActivity.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, new TablayoutFragment()).commit();
        boolean bool = ConfigStore.isFirstEnter(getBaseContext(), this.getLocalClassName());
        if (!bool) {
            drawer.openDrawer(GravityCompat.START);
            ConfigStore.writeFirstEnter(getBaseContext(), this.getLocalClassName());
        }
        mControlsFragment = (PlaybackControlsFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_playback_controls);
        if (mControlsFragment == null) {
            throw new IllegalStateException("Mising fragment with id 'controls'. Cannot continue.");
        }
        hidePlaybackControls();
//        Intent toIntent = getIntent();
//        int state = toIntent.getIntExtra(ConstMsg.SONG_STATE,0);
//        int during = intent.getIntExtra(ConstMsg.SONG_DURING, 0);
//        int currentPosition = intent.getIntExtra(ConstMsg.SONG_PROGRESS, 0);
//        showPlaybackControls();
//        mControlsFragment.updateState(state,currentPosition,during);
        DeviceUuidFactory duf = new DeviceUuidFactory(getApplicationContext());

        String SerialNumber = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        LogHelper.i(TAG,"serial number " + duf.getDeviceUuid());
    }

    public static String getUniquePsuedoID() {
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public static final void dialogTitleLineColor(Dialog dialog, int color) {
        Context context = dialog.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = dialog.findViewById(divierId);
        divider.setBackgroundColor(color);
        }
    /*自定义对话框*/
    private void showCustomDia()
    {
        AlertDialog.Builder customDia=new AlertDialog.Builder(MainActivity.this);
        final View viewDia= LayoutInflater.from(MainActivity.this).inflate(R.layout.main, null);
        customDia.setTitle("用户登录");
        customDia.setIcon(R.drawable.ic_action_person);
        customDia.setView(viewDia);
        customDia.setNegativeButton("取消",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showClickMessage("cancel");
            }
        });
        customDia.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
//                EditText diaInput=(EditText) viewDia.findViewById(R.id.txt_cusDiaInput);
                showClickMessage("me");
            }
        });
        Dialog dialog = customDia.create();
        dialog.show();
    }
    /*普通的对话框*/
    private void showNormalDia()
    {
        //AlertDialog.Builder normalDialog=new AlertDialog.Builder(getApplicationContext());
        AlertDialog.Builder normalDia=new AlertDialog.Builder(MainActivity.this);
        normalDia.setIcon(R.drawable.ic_launcher);
        normalDia.setTitle("普通的对话框");
        normalDia.setMessage("普通对话框的message内容");

        normalDia.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                showClickMessage("确定");
            }
        });
        normalDia.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                showClickMessage("取消");
            }
        });
        normalDia.create().show();
    }

    /*显示点击的内容*/
    private void showClickMessage(String message)
    {
        Toast.makeText(MainActivity.this, "你选择的是: "+message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_discard){
            showCustomDia();

        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_discard) {
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
//            sendBroadcastToService(ConstMsg.STATE_PLAYING);
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
        super.onDestroy();
    }


}
