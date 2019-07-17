package com.vda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.vda.Activities.Dashbaord;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int ALL_PERMISSIONS_RESULT =333 ;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 555;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "PhoneAuth";
    private static final int RC_SIGN_IN = 4555;
    int counter=60;
    private GoogleApiClient googleApiClient;
    EditText etGetNumber;
    public  static  String phoneNo;
    Spinner spCCode;
    Button logIn;
    TextView resendCodeView, time;
    TextView or;
    LinearLayout signInWithGoogle,mobileAuth;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    boolean onCodeSent = false;
    ProgressBar bar;
    private ArrayList<String> permissions;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        initViews();
        initMethods();
        initLisenters();
//         <uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES" />
//    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
//    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
//    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//    <uses-permission android:name="android.permission.INTERNET" />
//    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
//    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
//    <uses-permission android:name="android.permission.CAMERA" />


                permissions= new ArrayList<>();
        permissionsRejected= new ArrayList<>();
        permissionsToRequest= new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions.add(Manifest.permission.RECORD_AUDIO);


        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.
                        toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mFirebaseAuth.addAuthStateListener(mAuthStateListener);


            }
        },3000);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
//                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                    finish();
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation1,R.anim.animation2).toBundle();
                    startActivity(new Intent(MainActivity.this, Dashbaord.class),bndlanimation);
                    finish();


                } else {
                    // User is signed out
                    // onSignedOutCleanup();
                    bar.setVisibility(View.INVISIBLE);
                    startAnimation();
                }
            }
        };
    }
    private void initMethods() {
        setSpinnerCodes();
    }

    void initViews(){
        etGetNumber = findViewById(R.id.et_number);
        spCCode = findViewById(R.id.spCCode);
        logIn = findViewById(R.id.logIn);
        signInWithGoogle = findViewById(R.id.signInWithGoogle);
        mFirebaseAuth = FirebaseAuth.getInstance();
        resendCodeView = findViewById(R.id.resend_code);
        bar = (ProgressBar) findViewById(R.id.progress_bar);
        time = findViewById(R.id.timer);
        or = findViewById(R.id.text5);
        mobileAuth = findViewById(R.id.mobileAuth);

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .requestProfile()
                        .build();
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();
    }
    void initLisenters(){

    }


    private void sendCode() {

        phoneNo = spCCode.getSelectedItem() + etGetNumber.getText().toString();

        setUpVerificatonCallbacks();
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

    }

    private void setUpVerificatonCallbacks() {
        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        bar.setVisibility(View.INVISIBLE);
                        logIn.setEnabled(true);
                        if (e instanceof FirebaseTooManyRequestsException) {
// SMS quota exceeded
                            Log.d(TAG, "SMS Quota exceeded.");
                            Toast.makeText(MainActivity.this, "SMS Quota exceeded", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        resendToken = forceResendingToken;
                        phoneVerificationId = s;
                        receiveSentCode();
                        logIn.setEnabled(true);
                        startTimier();
                    }
                };
    }

    private void startTimier() {
        resendCodeView.setVisibility(View.INVISIBLE);

        counter = 60;
        new CountDownTimer(60000, 1000){
            public void onTick(long millisUntilFinished){
                time.setText("Remaining time: "+ String.valueOf(counter)+"s");
                counter--;
            }
            public  void onFinish(){
                resendCodeView.setVisibility(View.VISIBLE);
                time.setText("Time out!");
            }
        }.start();
    }

    public void verifyCode() {
        String code = etGetNumber.getText().toString();
        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mFirebaseAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()){
                           logIn.setEnabled(true);
                           bar.setVisibility(View.INVISIBLE);
                           Toast.makeText(MainActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                           Bundle bndlanimation =
                                   ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation1,R.anim.animation2).toBundle();
                           startActivity(new Intent(MainActivity.this, Dashbaord.class),bndlanimation);
                           finish();
                       }else {

                           logIn.setEnabled(true);
                           bar.setVisibility(View.INVISIBLE);
                           Toast.makeText(MainActivity.this, "Incorrect code!", Toast.LENGTH_SHORT).show();
                           onCodeSent = false;
                       }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        logIn.setEnabled(true);
                        bar.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Failed to signed In", Toast.LENGTH_SHORT).show();
                        enterNumber();
                    }
                });
    }

    private void setSpinnerCodes() {
        String[] items = new String[]{"+1", "+92","+91"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        //set the spinners adapter to the previously created one.
        spCCode.setAdapter(adapter);
        spCCode.setSelection(0);

    }

    public void signInWithGoogle(View view) {
        Intent signInIntent =
                Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        bar.setVisibility(View.VISIBLE);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    public void logIn(View view) {

            if (!onCodeSent){
                if(etGetNumber.getText().toString().length() == 10){
                    bar.setVisibility(View.VISIBLE);
                    logIn.setEnabled(false);

                    sendCode();
                    onCodeSent = true;
                }else {
                    etGetNumber.setError("Please enter correct number");
                }

            }else{
                logIn.setEnabled(false);
                bar.setVisibility(View.VISIBLE);
                verifyCode();
            }



    }
    public void resendCode(View view) {
        setUpVerificatonCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
    }
    void receiveSentCode(){

        LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        childParam1.weight = 0.0f;
        spCCode.setLayoutParams(childParam1);
        time.setVisibility(View.VISIBLE);
        resendCodeView.setVisibility(View.VISIBLE);
        spCCode.setVisibility(View.INVISIBLE);

        etGetNumber.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        etGetNumber.setText("");
        etGetNumber.setHint("Enter verification code...");
        logIn.setText("Verify");
        bar.setVisibility(View.INVISIBLE);
    }
    void enterNumber(){
        onCodeSent = false;
        LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        childParam1.weight = 0.7f;
        spCCode.setLayoutParams(childParam1);
        time.setVisibility(View.INVISIBLE);
        resendCodeView.setVisibility(View.INVISIBLE);
        spCCode.setVisibility(View.VISIBLE);
        etGetNumber.setText("");
        etGetNumber.setHint("Enter your number...");
        logIn.setText("Sign In");
        bar.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bar.setVisibility(View.INVISIBLE);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result =
                    Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null)
                    bar.setVisibility(View.VISIBLE);
                authWithFirebase(account);
            } else {
                bar.setVisibility(View.INVISIBLE);
                Toast.makeText(this,"Google sign-in failed.",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void authWithFirebase(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        bar.setVisibility(View.INVISIBLE);
                        if(!task.isSuccessful()){

                            Toast.makeText(getApplicationContext(),"Firebase Authentication failed",Toast.LENGTH_SHORT).show();

                        }else {
                            Bundle bndlanimation =
                                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation1,R.anim.animation2).toBundle();
                            startActivity(new Intent(MainActivity.this, Dashbaord.class),bndlanimation);
                            finish();
                            Toast.makeText(getApplicationContext(),"Signed In",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void startAnimation() {
        TranslateAnimation animateLeft = new TranslateAnimation(-logIn.getWidth(),0 , 0, 0);
        animateLeft.setDuration(500);
        animateLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logIn.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        TranslateAnimation animateRight = new TranslateAnimation(mobileAuth.getWidth(),0 , 0, 0);
        animateRight.setDuration(700);
        animateRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mobileAuth.setVisibility(View.VISIBLE);

                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                fadeIn.setDuration(2000);
               // textView3.startAnimation(fadeIn);
                signInWithGoogle.startAnimation(fadeIn);
                //textView5.startAnimation(fadeIn);
                //textView3.setVisibility(View.VISIBLE);
                signInWithGoogle.setVisibility(View.VISIBLE);
                or.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        logIn.startAnimation(animateLeft);
        mobileAuth.startAnimation(animateRight);
    }
    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }
    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(MainActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }

                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }
}
