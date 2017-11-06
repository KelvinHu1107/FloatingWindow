package com.develoment.kelvin.floatingwindow;


import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class MainActivity extends BaseFloatingWindowActivity {

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public Drawable getSpeedLimitResId() {
        return getResources().getDrawable(R.mipmap.ic_launcher);
    }

    @Override
    public Drawable getCurrentSpeedResId() {
        return null;
    }

    @Override
    public Drawable getEventWindowResId() {
        return null;
    }

    @Override
    public Drawable getCloseFloatingWindowResId() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
