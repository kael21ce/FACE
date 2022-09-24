package org.techtown.face.utilites;

import android.content.Context;
import android.content.SharedPreferences;

import org.techtown.face.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PreferenceManager {
    private final SharedPreferences sharedPreferences ;
    public PreferenceManager(Context context){
        this.sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME,Context.MODE_PRIVATE);
    }
    public void putBoolean(String key, Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }
    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }
    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public String getString(String key){
        return sharedPreferences.getString(key,null);
    }
    public void putInt(String key, int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }
    public Integer getInt(String key){
        return sharedPreferences.getInt(key,0);
    }
    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    public void putFloat(String key, float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }
    public Float getFloat(String key) {
        return sharedPreferences.getFloat(key,0);
    }

}
