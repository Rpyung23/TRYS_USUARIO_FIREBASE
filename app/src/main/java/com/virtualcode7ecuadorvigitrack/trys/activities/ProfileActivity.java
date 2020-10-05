package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.virtualcode7ecuadorvigitrack.trys.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{
    private CircleImageView mCircleImageViewProfile;
    private TextInputEditText mTextInputEditTextEmail;
    private TextInputEditText mTextInputEditTextName;
    private TextInputEditText mTextInputEditTextPhone;
    private TextInputEditText mTextInputEditTextPass;
    private Button mButtonUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
}