package com.slt.lk.oss.dragtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CustomView extends View {

    Bitmap mBitmap;
    Paint paint;
    Paint textPaint;
    int paintColor;
    int signalLevel;
    String rssi;
    List<String[]> pointlist;
    boolean taskstatus = false;
    int radius;



    Boolean longClicked = false;
    float x = 0;
    float y = 0;

    public CustomView(Context context) {
        super(context);
        mBitmap = Bitmap.createBitmap(400, 800, Bitmap.Config.ARGB_8888);
        pointlist = new ArrayList<>();
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28f);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        radius = 60;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (String[] var : pointlist)
        {
            paint = new Paint();
            Log.i("TAG","COLOUE  "+var[2]);
            if(var[2] != ""){
                paint.setColor(Integer.parseInt(var[2]));
            }else{
                paint.setColor(Color.RED);
            }
            paint.setStyle(Paint.Style.FILL);
            paint.setMaskFilter(new BlurMaskFilter(100, BlurMaskFilter.Blur.SOLID));

            Rect bounds = new Rect();
            textPaint.getTextBounds(var[3], 1, var[3].length(), bounds);

            canvas.drawCircle(Float.parseFloat(var[0]), Float.parseFloat(var[1]), radius, paint);
            canvas.drawText(var[3], Float.parseFloat(var[0]), Float.parseFloat(var[1]), textPaint);

            textPaint.getTextBounds(var[4], 0, var[4].length(), bounds);
            canvas.drawText(var[4], Float.parseFloat(var[0]), Float.parseFloat(var[1])+textPaint.getTextSize(), textPaint);
        }
        //canvas.drawCircle(x, y, 50, paint);

    }

    public boolean onTouchEvent(MotionEvent event) {
        if (taskstatus) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                x = event.getX();
                y = event.getY();
                pointlist.add(new String[]{String.valueOf(x), String.valueOf(y), String.valueOf(paintColor), String.valueOf(signalLevel), rssi});
                //   pointlist.add(new String[]{String.valueOf(x), String.valueOf(y), String.valueOf(paintColor),String.valueOf(String.format("%.2f", signalLevel/100D))});
                invalidate();
            }
        }
        return false;
    }

    public void removeLast(){
        if(pointlist.size()>0) {
            pointlist.remove(pointlist.size() - 1);
            invalidate();
        }
    }


    public void removeAll(){
        if(pointlist.size()>0) {
//            for (int i=0; i < pointlist.size(); i++){
//                pointlist.remove(i);
//            }
            pointlist.clear();
            invalidate();
        }
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        invalidate();
    }

    public int getRadius() {
        return radius;
    }

    public void setSignalLevel(int signalLevel) {
        this.signalLevel = signalLevel;
        Log.i("TAG","COLOUExx  "+ getColor(signalLevel/100F));
        this.paintColor = getColor(signalLevel/100F);

    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    public void setTaskstatus(boolean taskstatus) {
        this.taskstatus = taskstatus;
    }

    public int getColor(float p) {

        int FIRST_COLOR =  getResources().getColor(android.R.color.holo_red_dark);
        int SECOND_COLOR =  getResources().getColor(android.R.color.holo_orange_light);
        int THIRD_COLOR =  getResources().getColor(android.R.color.holo_green_light);

        int c0;
        int c1;
        if (p <= 0.5f) {
            p *= 2;
            c0 = FIRST_COLOR;
            c1 = SECOND_COLOR;
        } else {
            p = (p - 0.5f) * 2;
            c0 = SECOND_COLOR;
            c1 = THIRD_COLOR;
        }
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);
        return Color.argb(a, r, g, b);
    }

    private static int ave(int src, int dst, float p) {
        return src + java.lang.Math.round(p * (dst - src));
    }
}
