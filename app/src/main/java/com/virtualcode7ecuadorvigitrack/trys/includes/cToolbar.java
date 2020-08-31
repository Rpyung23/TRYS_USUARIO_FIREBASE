package com.virtualcode7ecuadorvigitrack.trys.includes;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.virtualcode7ecuadorvigitrack.trys.R;

public class cToolbar
{
    public static void showToolbar(AppCompatActivity appCompatActivity, String title,boolean up)
    {
        Toolbar toolbar = appCompatActivity.findViewById(R.id.id_toolbar);
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setTitle(title);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(up);
    }
}
