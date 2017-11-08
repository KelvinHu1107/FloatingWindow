package com.develoment.kelvin.floatingwindow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    final static int REQUEST_CODE_FINE_GPS = 100;
    WindowManager windowManager;
    LinearLayout currentSpeed, speedLimit;
    Button button, showMenu;
    LocationManager locationManager;
    LocationListener locationListener;
    TextView textView, lat, lon;

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
            if (!Settings.canDrawOverlays(BaseFloatingWindowActivity.this)) {Toast.makeText(this, "can not DrawOverlays", Toast.LENGTH_SHORT).show();
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v("kelvinkelvin", "!ACCESS_FINE_LOCATION");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_FINE_GPS);
        } else {
            initGps();
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
        if (requestCode == REQUEST_CODE_FINE_GPS) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.v("kelvinkelvin", "REQUEST_CODE_FINE_GPS failed");
            }
            else {
                initGps();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void initGps() {
        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(textView != null) {
                    textView.setText(String.valueOf(updateSpeedByLocation(location))+" Km/h");
                }

                if(lat != null){
                    lat.setText("Lat: "+String.valueOf(location.getLatitude()));
                }

                if(lon != null){
                    lon.setText("Lon: "+String.valueOf(location.getLongitude()));
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

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
        LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        // linearLayout.setBackgroundColor(Color.argb(66, 255, 0, 0));

        currentSpeed.setBackgroundColor(Color.argb(66, 255, 0, 0));
        currentSpeed.setLayoutParams(layoutParams);
        currentSpeed.setOrientation(LinearLayout.VERTICAL);
        speedLimit.setBackground(getSpeedLimitResId());

        textView = new TextView(this);
        lat = new TextView(this);
        lon = new TextView(this);
        textView.setText("0.0 Km/h");
        textView.setTextColor(Color.BLUE);
        lat.setTextColor(Color.BLUE);
        lon.setTextColor(Color.BLUE);
        textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        lat.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        lon.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        textView.setLayoutParams(layoutParamsText);
        lat.setLayoutParams(layoutParamsText);
        lon.setLayoutParams(layoutParamsText);

        //currentSpeed.addView(speedLimit);
        currentSpeed.addView(textView);
        currentSpeed.addView(lat);
        currentSpeed.addView(lon);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(400,500,WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.RIGHT;
        windowManager.addView(button, params);
        params.gravity = Gravity.CENTER | Gravity.CENTER;
        windowManager.addView(currentSpeed, params);

        setClickListener(params);
    }

    private int updateSpeedByLocation(Location location) {
        int tempSpeed = (int) (location.getSpeed() * 3.6); // m/s --> Km/h
        return tempSpeed;
    }

}