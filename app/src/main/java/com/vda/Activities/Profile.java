package com.vda.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.vda.Classes.GpsUtils;
import com.vda.Database.SharedPreferencesHelper;
import com.vda.MainActivity;
import com.vda.ObjectDetectionSample;
import com.vda.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.vda.Classes.GpsUtils.GPS_REQUEST;

public class Profile extends AppCompatActivity {

    private static final int PICK_IMG = 4589;
    de.hdodenhof.circleimageview.CircleImageView profileImage;
    Bitmap bitmap;
    String selectedImagePath;
    String covertedIMG;
    SharedPreferencesHelper preferencesHelper;
    ImageView editName, pickLocation;
    EditText etName;
    TextView t_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileImage = findViewById(R.id.profile_image);
        editName = findViewById(R.id.edit_name);
        etName = findViewById(R.id.et_name);
        t_location = findViewById(R.id.t_location);
        pickLocation = findViewById(R.id.pick_location);

        preferencesHelper = new SharedPreferencesHelper(getApplicationContext());
        loadImageFromPref();

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etName.setEnabled(true);
                // etName.setKeepScreenOn(true);
            }
        });
        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Your action on done
                    Log.d("result123", "onEditorAction: "+ etName.getText().toString());
                    preferencesHelper.setName(etName.getText().toString());
                    etName.setEnabled(false);
                    return true;
                }
                return false;
            }
        });
        if(preferencesHelper.getLocation() == null){
            t_location.setText("Your location is not found yet");
        }else {
            t_location.setText(preferencesHelper.getLocation());
        }
        if(preferencesHelper.getName() == null){
            etName.setText("Virtual Driver Assistant");
        }else {
            etName.setText(preferencesHelper.getName());
        }

    }


    public void pickProfileImage(View view) {
        startActivityForResult(getPickImageChooserIntent(),PICK_IMG);
    }
    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = null;
        selectedImagePath = null;
        if (resultCode == RESULT_OK && requestCode == PICK_IMG) {
            String filePath = getImageFilePath(data);
            if (filePath != null) {
                ByteArrayOutputStream byteArrayOutputStreamObject;
                byteArrayOutputStreamObject = new ByteArrayOutputStream();
                bitmap = BitmapFactory.decodeFile(filePath); // load
                bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 800, true);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStreamObject);
                byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
                covertedIMG = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
                //UploadImage();
                profileImage.setImageBitmap(bitmap);
                preferencesHelper.setProfileImg(covertedIMG);
            }

        }

    }
    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());

    }
    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }
    public void loadImageFromPref(){
        preferencesHelper = new SharedPreferencesHelper(getApplicationContext());
        covertedIMG = preferencesHelper.getProfileImg();
        Log.d("a_77", "loadImageFromPref: "+covertedIMG);
        if( !covertedIMG.equalsIgnoreCase("" )){
            byte[] b = Base64.decode(covertedIMG, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            profileImage.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImageFromPref();

    }


    public void goBack(View view) {
        Bundle bndlanimation =
                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation3, R.anim.animation4).toBundle();
        startActivity(new Intent(Profile.this, Dashbaord.class), bndlanimation);
        finish();
    }

    public void signOut(View view) {
        AuthUI.getInstance().signOut(Profile.this)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        preferencesHelper.setName("");
                        preferencesHelper.setProfileImg(null);
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation3, R.anim.animation4).toBundle();
                        startActivity(new Intent(Profile.this, MainActivity.class), bndlanimation);
                        finish();

                    }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, "Failed to sign out!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
