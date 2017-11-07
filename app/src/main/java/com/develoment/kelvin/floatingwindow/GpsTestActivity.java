package com.develoment.kelvin.floatingwindow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Iterator;

public class GpsTestActivity extends AppCompatActivity {

    final static int REQUEST_CODE_FINE_GPS = 100;
    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            textView.setText(String.valueOf(location.getLatitude()));
            Log.v("kelvinkelvin", "Current speed:" + location.getSpeed());
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
    private Location location;
    private GpsStatus.Listener gpsStatusListener;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_test);

        textView = (TextView) findViewById(R.id.textView);
        Log.v("kelvinkelvin", "GPS activity");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v("kelvinkelvin", "!ACCESS_FINE_LOCATION");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_FINE_GPS);
        } else {
            initGps();
        }
    }

    private void initGps() {
        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(gps){
            Log.v("kelvinkelvin", "GPS enable");
        }else{
            Log.v("kelvinkelvin", "GPS not enable");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.v("kelvinkelvin", "no permission");
        }else {
            Log.v("kelvinkelvin", "requestLocationUpdates");

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location==null){
                Log.v("kelvinkelvin", "location==null");
                textView.setText("null");
            }else{
                textView.setText(String.valueOf(location.getLatitude()));
            }

            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Log.v("kelvinkelvin", "provider enable");
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                     1000, 0, locationListener);
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_FINE_GPS) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.v("kelvinkelvin", "REQUEST_CODE_FINE_GPS failed");
            }
            else {
                initGps();
            }
        }
    }

    private Criteria getLocationCriteria() {
        Log.v("kelvinkelvin", "getLocationCriteria");
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true); // 设置是否要求速度
        criteria.setCostAllowed(false); // 设置是否允许运营商收费
        criteria.setBearingRequired(false); // 设置是否需要方位信息
        criteria.setAltitudeRequired(false); // 设置是否需要海拔信息
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 设置对电源的需求
        return criteria;
    }

    private void updateSpeedByLocation(Location location) {
        int tempSpeed = (int) (location.getSpeed() * 3.6); // m/s --> Km/h
        int adasSpeed = tempSpeed;
        int recordSpeed = tempSpeed;

        double nowLatitude = location.getLatitude();
        double nowLongitude = location.getLongitude();

        Log.v("kelvinkelvin", "Speed:" + tempSpeed);

    }

    @Override
    protected void onDestroy() {
        if (locationManager != null) {
            locationManager.removeGpsStatusListener(gpsStatusListener);
        }
        super.onDestroy();
    }



}
