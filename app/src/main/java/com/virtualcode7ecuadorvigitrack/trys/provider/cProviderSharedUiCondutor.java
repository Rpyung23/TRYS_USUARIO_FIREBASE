package com.virtualcode7ecuadorvigitrack.trys.provider;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.content.ContextCompat;

public class cProviderSharedUiCondutor
{
    private Context mContext;

    public cProviderSharedUiCondutor(Context mContext) {
        this.mContext = mContext;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void writeSharedPreferences(String uid)
    {
        SharedPreferences mSharedPreferences = getmContext().getSharedPreferences("UID_CONDUCTOR",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("uid",uid);
        editor.apply();
    }
    public String leerSharedPreferences()
    {
        SharedPreferences mSharedPreferences = getmContext().getSharedPreferences("UID_CONDUCTOR",Context.MODE_PRIVATE);
        return mSharedPreferences.getString("uid","error");
    }
}
