package com.develoment.kelvin.floatingwindow;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Kelvin on 2017/11/5.
 */

public class FloatWindowService extends Service {

    WindowManager windowManager;
    LinearLayout currentSpeed, speedLimit;
    Button button;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();

        initComponent();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initComponent(){

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        currentSpeed = new LinearLayout(this);
        speedLimit = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        button = new Button(this);
        button.setText("Close");
        button.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        // linearLayout.setBackgroundColor(Color.argb(66, 255, 0, 0));

        currentSpeed.setBackgroundColor(Color.argb(66, 255, 0, 0));
        currentSpeed.setLayoutParams(layoutParams);
        currentSpeed.setOrientation(LinearLayout.VERTICAL);
        speedLimit.setBackground(getResources().getDrawable(R.mipmap.ic_launcher));

        final TextView textView = new TextView(this);
        textView.setText("0.0 Km/h");
        textView.setTextColor(Color.BLUE);
        textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        textView.setLayoutParams(layoutParamsText);

        currentSpeed.addView(speedLimit);
        currentSpeed.addView(textView);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(400,500,WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.RIGHT;
        windowManager.addView(button, params);
        params.gravity = Gravity.CENTER | Gravity.CENTER;
        windowManager.addView(currentSpeed, params);

        setClickListener(params);
        task(textView);
    }

    private void setClickListener(final WindowManager.LayoutParams params){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeViewImmediate(currentSpeed);
                windowManager.removeViewImmediate(button);
                stopSelf();
            }
        });

        currentSpeed.setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams updatedParams = params;
            int x,y;
            float touchX,touchY;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x= updatedParams.x;
                        y=updatedParams.y;
                        touchX = motionEvent.getRawX();
                        touchY = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updatedParams.x = (int)(x+(motionEvent.getRawX() - touchX));
                        updatedParams.y = (int)(y+(motionEvent.getRawY() - touchY));
                        windowManager.updateViewLayout(currentSpeed,updatedParams);
                        break;
                    default:break;
                }

                return false;
            }

        });
    }

    private void task(final TextView textView){
        final Runnable mUpdateUITimerTask = new Runnable() {
            public void run() {
                // do whatever you want to change here, like:
                textView.setText("100 Km/h");
            }
        };

        final Runnable mUpdateUITimerTask2 = new Runnable() {
            public void run() {
                // do whatever you want to change here, like:
                textView.setText("80 Km/h");
            }
        };
        final Handler mHandler = new Handler();

        mHandler.postDelayed(mUpdateUITimerTask, 3 * 1000);
        mHandler.postDelayed(mUpdateUITimerTask2, 4 * 1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
