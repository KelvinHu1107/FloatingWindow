package com.develoment.kelvin.floatingwindow;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Created by kelvinhu1107 on 2017/11/6.
 */

public abstract class BaseFloatingWindowActivity extends AppCompatActivity {

    final static int OVERLAY_PERMISSION_REQ_CODE = 99;
    WindowManager windowManager;
    LinearLayout currentSpeed, speedLimit;
    Button button, showMenu;

    @LayoutRes
    public abstract int getLayoutResId();

    public abstract Drawable getSpeedLimitResId();

    public abstract Drawable getCurrentSpeedResId();

    public abstract Drawable getEventWindowResId();

    public abstract Drawable getCloseFloatingWindowResId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        findView();
        checkPermission();
    }

    private void setClickListener(final WindowManager.LayoutParams params){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeViewImmediate(currentSpeed);
                windowManager.removeViewImmediate(button);
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

    private void findView(){
        showMenu = (Button) findViewById(R.id.showMenu);
    }

    private void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(BaseFloatingWindowActivity.this)) {
                Toast.makeText(this, "can not DrawOverlays", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + BaseFloatingWindowActivity.this.getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            } else {
                showMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initComponent();
                    }
                });
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == OVERLAY_PERMISSION_REQ_CODE){
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Permission is denied by user. Please Check it in Settings", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Allowed", Toast.LENGTH_SHORT).show();
                showMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initComponent();
                    }
                });
            }
        }
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
        speedLimit.setBackground(getSpeedLimitResId());

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
    }

}