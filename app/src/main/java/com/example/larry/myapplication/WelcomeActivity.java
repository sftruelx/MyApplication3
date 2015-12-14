package com.example.larry.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.example.larry.myapplication.banner.SimpleGuideBanner;
import com.example.larry.myapplication.utils.ConfigStore;
import com.example.larry.myapplication.utils.DataProvider;
import com.example.larry.myapplication.utils.ViewFindUtils;
import com.flyco.banner.anim.select.ZoomInEnter;

/**
 * Created by 067231 on 2015/12/14.
 */
public class WelcomeActivity extends Activity {

    private Context context = this;
    private View decorView;
    private boolean isFromBannerHome;
    private Class<? extends ViewPager.PageTransformer> transformerClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_app);

        isFromBannerHome = getIntent().getBooleanExtra("isFromBannerHome", false);
        int position = getIntent().getIntExtra("position", -1);
        transformerClass = position != -1 ? DataProvider.transformers[position] : null;
        decorView = getWindow().getDecorView();
        boolean bool = ConfigStore.isFirstEnter(getBaseContext(),this.getLocalClassName());
        Log.i("this is my logs = ",String.valueOf(bool));
        if(!bool) {
            sgb();

        }else{
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }


    private void sgb() {
        SimpleGuideBanner sgb = ViewFindUtils.find(decorView, R.id.sgb);

        sgb
                .setIndicatorHeight(6)
                .setIndicatorGap(12)
                .setIndicatorCornerRadius(3.5f)
                .setSelectAnimClass(ZoomInEnter.class)
                .setTransformerClass(transformerClass)
                .barPadding(0, 10, 0, 10)
                .setSource(DataProvider.geUsertGuides())
                .startScroll();

        sgb.setOnJumpClickL(new SimpleGuideBanner.OnJumpClickL() {
            @Override
            public void onJumpClick() {
                if (isFromBannerHome) {
                    finish();
                    return;
                }

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}
