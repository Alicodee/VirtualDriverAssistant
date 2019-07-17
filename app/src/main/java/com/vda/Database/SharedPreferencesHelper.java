package com.vda.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesHelper {
    private static String IMG_KEY = "img_key";
    private static String LOCATION_KEY = "location_key";
    private static String NAME_KEY = "name_key";
    Context context;
    SharedPreferences sharedPreferences;
    private String name,location;
    public SharedPreferencesHelper(){

    }
    public SharedPreferencesHelper(Context context){
        this.context =context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setProfileImg(String img){
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString(IMG_KEY,img).apply();
    }
    public String getProfileImg(){
        return sharedPreferences.getString(IMG_KEY,"");
    }

    public void setName(String name){
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString(NAME_KEY,name).apply();
    }
    public String getName(){
        return sharedPreferences.getString(NAME_KEY,null);
    }

    public void setLocation(String location){
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString(LOCATION_KEY,location).apply();
    }
    public String getLocation(){
        return sharedPreferences.getString(LOCATION_KEY,null);
    }
}
