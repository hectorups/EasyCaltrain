package com.easycaltrain.library;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String PREF_LASTFROMSTATION = "pref_lastfromstation";

    private SharedPreferences mPrefs;

    public Preferences(Context appContext){
        mPrefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getLastFromStation(){
        return mPrefs.getString(PREF_LASTFROMSTATION, null);
    }

    public void setLastFromStation( String fromStationId ){
        if( fromStationId == null ){
            mPrefs.edit().remove(PREF_LASTFROMSTATION).commit();
        } else {
            mPrefs.edit().putString(PREF_LASTFROMSTATION, fromStationId).commit();
        }
    }

}
