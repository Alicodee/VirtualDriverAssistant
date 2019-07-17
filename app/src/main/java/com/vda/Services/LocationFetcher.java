package com.vda.Services;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.vda.Activities.Dashbaord;
import com.vda.Activities.Speedometer;
import com.vda.Classes.GpsUtils;
import com.vda.Database.SharedPreferencesHelper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationFetcher extends Service {
    String SERVICE = "a_response";
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    SharedPreferencesHelper preferencesHelper;
    private boolean isGPS = false;

    Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();



        preferencesHelper =new SharedPreferencesHelper(getApplicationContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Log.d(SERVICE, "onCreate: service created ");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display t_location !", Toast.LENGTH_SHORT).show();
        }

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult != null){
                    int s =(int) ((locationResult.getLastLocation().getSpeed()*3600)/1000);
                    Log.d(SERVICE, "onLocationResult: "+ s);
                //    Toast.makeText(LocationFetcher.this, "Speed: "+ s +" KM/hr", Toast.LENGTH_LONG).show();


                    Intent intent = new Intent("GPSSpeedUpdates");
                    // You can also include some extra data.
                    intent.putExtra("speed", s);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    setAddress(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());

                }





            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

    }

    private void setAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addresses != null){
            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                Log.d(SERVICE, "Call back " + " , " +address + "");
                //Toast.makeText(LocationFetcher.this, "Address: "+address, Toast.LENGTH_SHORT).show();
                preferencesHelper.setLocation(address);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(SERVICE, " service started ");
        return super.onStartCommand(intent, flags, startId);

    }
}
