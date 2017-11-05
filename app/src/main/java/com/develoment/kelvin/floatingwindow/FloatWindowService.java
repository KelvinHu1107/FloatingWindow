package com.develoment.kelvin.floatingwindow;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
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
    LinearLayout linearLayout;


    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setBackgroundColor(Color.argb(66, 255, 0, 0));
        linearLayout.setLayoutParams(layoutParams);
        TextView textView = new TextView(this);
        textView.setText("This is a floating window");
        textView.setTextColor(Color.BLUE);
        textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        final Button button = new Button(this);
        button.setText("Close");
        button.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
// linearLayout.setBackgroundColor(Color.argb(66, 255, 0, 0));
        textView.setLayoutParams(layoutParamsText);

        linearLayout.addView(textView);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(400,150,WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.RIGHT;
        windowManager.addView(button, params);
        params.gravity = Gravity.CENTER | Gravity.CENTER;
        windowManager.addView(linearLayout, params);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeViewImmediate(linearLayout);
                windowManager.removeViewImmediate(button);
                stopSelf();
            }
        });

        linearLayout.setOnTouchListener(new View.OnTouchListener() {
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
                        windowManager.updateViewLayout(linearLayout,updatedParams);
                        break;

                    case MotionEvent.ACTION_BUTTON_PRESS:
                        windowManager.removeViewImmediate(linearLayout);

                    default:break;
                }

                return false;
            }



        });

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
