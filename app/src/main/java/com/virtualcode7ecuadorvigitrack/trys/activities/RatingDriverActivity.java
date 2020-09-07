package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.provider.cDriverProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingDriverActivity extends AppCompatActivity
{
    String id_driver;
    private TextView mTextViewPlaca;
    private RatingBar mRatingBarDriver;
    private CircleImageView mCircleImageViewDriver;
    private Button mButtonSendRating;
    private cDriverProvider mDriverProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_driver);

        id_driver = getIntent().getStringExtra("id_driver");

        mTextViewPlaca = findViewById(R.id.id_textview_placa_rating_driver);
        mRatingBarDriver = findViewById(R.id.id_ratingBarDriver);
        mCircleImageViewDriver = findViewById(R.id.id_circleview_photo_driver_rating);
        mButtonSendRating = findViewById(R.id.id_button_send_rating_driver);
        mDriverProvider = new cDriverProvider();
    }

    @Override
    protected void onPostResume()
    {
        llenarDatos();
        super.onPostResume();
    }

    private void llenarDatos()
    {
        mDriverProvider.readDriver(id_driver).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    Picasso.with(RatingDriverActivity.this)
                            .load(snapshot.child("Photo").getValue().toString())
                            .error(R.drawable.error_image_load)
                            .placeholder(R.drawable.loading_photo)
                            .into(mCircleImageViewDriver);
                    mTextViewPlaca.setText(snapshot.child("Placa").getValue().toString().toUpperCase());



                    /**
                     * ACTUALIZAR LOS RATINGDRIVER
                     * */
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(RatingDriverActivity.this,InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}