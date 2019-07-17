package com.vda.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.vda.Classes.GpsUtils;
import com.vda.Database.SharedPreferencesHelper;
import com.vda.MainActivity;
import com.vda.ObjectDetectionSample;
import com.vda.R;
import com.vda.Services.GPSTracker;
import com.vda.Services.LocationFetcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Dashbaord extends AppCompatActivity {
    private static final int PICK_IMG = 4589;
    private de.hdodenhof.circleimageview.CircleImageView imgProfile;

    LinearLayout speedometerView, distanceMeasureView, assistantService,broot,trafficGuideView;
    Spinner spinner;
    ImageView close;
    EditText etNumber;
    Button share;
    private Bitmap bitmap;
    private String selectedImagePath;
    String covertedIMG = "";
    SharedPreferencesHelper preferencesHelper;
    public static String SENT = "SMS_SENT", DELIVERED = "SMS_DELIVERED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbaord);
        speedometerView = findViewById(R.id.speedometerView);
        distanceMeasureView = findViewById(R.id.distanceMeasureView);
        assistantService = findViewById(R.id.assistantServiceView);
        trafficGuideView = findViewById(R.id.traffic_guide_view);
        broot = findViewById(R.id.broot);
        imgProfile = findViewById(R.id.profile_image);
        spinner = findViewById(R.id.spinner);
        close = findViewById(R.id.close);
        etNumber = findViewById(R.id.et_number);
        share = findViewById(R.id.btn_share);

        setSpinnerCodes();

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                boolean isGPS = isGPSEnable;
                if (isGPSEnable){
                    Toast.makeText(Dashbaord.this, "GPS will take a while to fetch your " +
                            "last location.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Dashbaord.this, "GPS is enabled", Toast.LENGTH_SHORT).show();
                }

            }
        });

        loadImageFromPref();
        speedometerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation1, R.anim.animation2).toBundle();
                startActivity(new Intent(Dashbaord.this, Speedometer.class), bndlanimation);

            }
        });
        distanceMeasureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation1, R.anim.animation2).toBundle();
                startActivity(new Intent(Dashbaord.this, DistanceMeasure.class), bndlanimation);

            }
        });
        assistantService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation1, R.anim.animation2).toBundle();
                startActivity(new Intent(Dashbaord.this, ObjectDetectionSample.class), bndlanimation);


            }
        });
        trafficGuideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation1, R.anim.animation2).toBundle();
                startActivity(new Intent(Dashbaord.this, TrafficGuide.class), bndlanimation);


            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(Dashbaord.this, R.anim.slide_to_bottom);
                broot.setAnimation(animation);
                broot.setVisibility(View.INVISIBLE);

            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String simType = spinner.getSelectedItem().toString();
                String number = etNumber.getText().toString();
                if (number.isEmpty() || number.length() < 11){
                    Toast.makeText(Dashbaord.this, "Please enter valid number with country code.", Toast.LENGTH_SHORT).show();
                }else {
                    shareLocationViaSMS(simType,number);
                }
            }
        });

       if (isMyServiceRunning(LocationFetcher.class)){
           Log.d("a_response", "onCreate: already srvice rining");
       }else {
           startService(new Intent(Dashbaord.this, LocationFetcher.class));
       }

       // getLocation();
    }

    private void getLocation() {
        if(isMyServiceRunning(GPSTracker.class)){
            stopService(new Intent(getApplicationContext(),GPSTracker.class));
            Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show();
        }
        startService(new Intent(getApplicationContext(),GPSTracker.class));

      //  startService(new Intent(this, GPSTracker.class));
        GPSTracker gps = new GPSTracker(this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(Dashbaord.this, Locale.getDefault());

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
               Log.d("a_77", "onCreate: " + latitude + " , " + longitude + " , " + city + country + "");
               preferencesHelper.setLocation(address);
           }
       }


    }

    public void pickImage(View view) {
        //startActivityForResult(getPickImageChooserIntent(),PICK_IMG);
        startActivity(new Intent(Dashbaord.this, Profile.class));
    }

    public void loadImageFromPref() {
        preferencesHelper = new SharedPreferencesHelper(getApplicationContext());
        covertedIMG = preferencesHelper.getProfileImg();
        if (!covertedIMG.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(covertedIMG, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            imgProfile.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImageFromPref();
    }

    private void shareLocationViaSMS(String simType, String number) {
        int sim =0;
//        //Getting intent and PendingIntent instance
//        Intent intent=new Intent(getApplicationContext(),Dashbaord.class);
//        PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
//
//        //Get the SmsManager instance and call the sendTextMessage method to send message
//        SmsManager sms=SmsManager.getDefault();
//        sms.sendTextMessage("+923427212801", "+923037051540", "VDA", pi,null);

        if (simType.equals("SIM2")){
            sim = 1;
        }
       // number = number.substring(1,11);
        number = "+"+number;
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        // SEND BroadcastReceiver
        BroadcastReceiver sendSMS = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
//                        showSnackBar(getString(R.string.sms_sent));
//                        Analytics.track(AnalyticsEvents.SEND_REMINDER_SMS_APP_SUCCESS);
                        Toast.makeText(Dashbaord.this, "Delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        showSnackBar(getString(R.string.sms_send_failed_try_again));
//                        Analytics.track(AnalyticsEvents.SEND_REMINDER_SMS_APP_FAILED);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        showSnackBar(getString(R.string.no_service_sms_failed));
//                        Analytics.track(AnalyticsEvents.SEND_REMINDER_SMS_APP_FAILED);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        showSnackBar(getString(R.string.no_service_sms_failed));
//                        Analytics.track(AnalyticsEvents.SEND_REMINDER_SMS_APP_FAILED);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        showSnackBar(getString(R.string.no_service_sms_failed));
//                        Analytics.track(AnalyticsEvents.SEND_REMINDER_SMS_APP_FAILED);
                        break;
                }
            }
        };

        // DELIVERY BroadcastReceiver
        BroadcastReceiver deliverSMS = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), R.string.sms_delivered,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), R.string.sms_not_delivered,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        registerReceiver(sendSMS, new IntentFilter(SENT));
        registerReceiver(deliverSMS, new IntentFilter(DELIVERED));
        String smsText = "Location: "+preferencesHelper.getLocation();
        //+ System.lineSeparator() +" Shared by: "+ preferencesHelper.getName(); // getSmsText();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager localSubscriptionManager = SubscriptionManager.from(getApplicationContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                }
                    // for Activity#requestPermissions for more details.
                return;
            }if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

                SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(0);
                SubscriptionInfo simInfo2 = (SubscriptionInfo) localList.get(1);

                //SendSMS From SIM One
                if (sim == 0){
                    SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId()).sendTextMessage(number, null, smsText, sentPI, deliveredPI);
                }else if (sim == 1){
                    //SendSMS From SIM Two
                    SmsManager.getSmsManagerForSubscriptionId(simInfo2.getSubscriptionId()).sendTextMessage(number, null, smsText, sentPI, deliveredPI);

                }

        }


    } else {
            SmsManager.getDefault().sendTextMessage(number, null, smsText, sentPI, deliveredPI);
            Toast.makeText(getBaseContext(), smsText, Toast.LENGTH_SHORT).show();
        }

        Animation animation = AnimationUtils.loadAnimation(Dashbaord.this, R.anim.slide_to_bottom);
        broot.setAnimation(animation);
        broot.setVisibility(View.INVISIBLE);
    }

    public void shareLocation(View view) {
        //shareLocationViaSMS();
        Animation animation = AnimationUtils.loadAnimation(Dashbaord.this, R.anim.slide_from_bottom);
        broot.setAnimation(animation);
        broot.setVisibility(View.VISIBLE);
    }


    public void setSpinnerCodes(){
        String[] items = new String[]{"SIM1", "SIM2"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void openProfile(View view) {
        Bundle bndlanimation =
                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation1, R.anim.animation2).toBundle();
        startActivity(new Intent(Dashbaord.this, Profile.class), bndlanimation);
    }
}
