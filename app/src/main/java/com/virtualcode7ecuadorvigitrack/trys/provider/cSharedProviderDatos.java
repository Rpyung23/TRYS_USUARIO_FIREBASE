package com.virtualcode7ecuadorvigitrack.trys.provider;

import android.content.Context;
import android.content.SharedPreferences;

public class cSharedProviderDatos
{
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public void cSharedProviderDatos(Context mContext)
    {
        this.mContext = mContext;
        mSharedPreferences = mContext.getSharedPreferences("Driver",Context.MODE_PRIVATE);
    }

    public void escribirSharedDriver(String uid)
    {
        SharedPreferences.Editor mEditor = getmSharedPreferences().edit();
        mEditor.putString("uid",uid);
        mEditor.apply();
    }

    public String leerUidSharedDriver()
    {
        return getmSharedPreferences().getString("uid","null");
    }

    public SharedPreferences getmSharedPreferences() {
        return mSharedPreferences;
    }

    public void setmSharedPreferences(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}
