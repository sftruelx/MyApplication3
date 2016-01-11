package com.example.larry.myapplication.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.larry.myapplication.R;
import com.example.larry.myapplication.utils.LogHelper;

/**
 * Created by Larry on 2015/12/25.
 */
public class DrawSnow extends View {

    public int x = 40;
    public int y = 50;


    public DrawSnow(Context context, AttributeSet attrs) {
        super(context, attrs);
        LogHelper.i("draw snow init..","skdlfjlsdkjf");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        创建画笔
        Paint paint = new Paint();
//      设置画笔颜色
        Bitmap  bitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.snow);

        drawImage(canvas,bitmap,x,y);
    }
    public static void drawImage(Canvas canvas, Bitmap blt, int x, int y,
                                 int w, int h, int bx, int by) {
        Rect src = new Rect();// 图片 >>原矩形
        Rect dst = new Rect();// 屏幕 >>目标矩形

        src.left = bx;
        src.top = by;
        src.right = bx + w;
        src.bottom = by + h;

        dst.left = x;
        dst.top = y;
        dst.right = x + w;
        dst.bottom = y + h;
        // 画出指定的位图，位图将自动--》缩放/自动转换，以填补目标矩形
        // 这个方法的意思就像 将一个位图按照需求重画一遍，画后的位图就是我们需要的了
        canvas.drawBitmap(blt, null, dst, null);
        src = null;
        dst = null;
    }

    /**
     * 绘制一个Bitmap
     *
     * @param canvas 画布
     * @param bitmap 图片
     * @param x 屏幕上的x坐标
     * @param y 屏幕上的y坐标
     */

    public static void drawImage(Canvas canvas, Bitmap bitmap, int x, int y) {
        // 绘制图像 将bitmap对象显示在坐标 x,y上
        canvas.drawBitmap(bitmap, x, y, null);
    }

}
