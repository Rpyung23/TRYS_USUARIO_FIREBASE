package com.virtualcode7ecuadorvigitrack.trys.provider;

import android.content.Context;
import android.content.SharedPreferences;

public class cProviderSharedPreferencesOnBoarding
{
    private Context mContext;

    public cProviderSharedPreferencesOnBoarding(Context mContext) {
        this.mContext = mContext;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void writeSharedOnBoarding(int i)
    {
        SharedPreferences mSharedPreferences = getmContext().getSharedPreferences("OnBoarding",Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putInt("status",i);
        mEditor.apply();
    }

    public int readSharedOnBoarding()
    {
        SharedPreferences mSharedPreferences = getmContext().getSharedPreferences("OnBoarding",Context.MODE_PRIVATE);
        return mSharedPreferences.getInt("status",0);
    }

}
