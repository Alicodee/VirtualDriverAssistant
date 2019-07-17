package com.vda.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;

import android.widget.ImageView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.AwesomeSpeedometer;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vda.Classes.GpsUtils;
import com.vda.R;

import static com.vda.Classes.GpsUtils.GPS_REQUEST;

public class Speedometer extends AppCompatActivity {


    public static TextView title;
    ImageView image;
    int speed;
    AwesomeSpeedometer speedometer;
    public boolean isGPS= false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedometer);

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
                if (isGPSEnable){
                    Toast.makeText(Speedometer.this, "Please wait for few minutes. GPS will take a while to fetch your " +
                            "last location.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Speedometer.this, "GPS is enabled", Toast.LENGTH_SHORT).show();
                }

            }
        });

        speedometer = findViewById(R.id.speedView);
        title = findViewById(R.id.title_speed);
        BroadcastReceiver speedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int speed = intent.getIntExtra("speed",0);
                speedometer.setSpeedAt(speed);
                title.setText("Speed: "+speed+" KM/hr");
            }
        };


        LocalBroadcastManager.getInstance(Speedometer.this).registerReceiver(
                speedReceiver, new IntentFilter("GPSSpeedUpdates"));



    }


}