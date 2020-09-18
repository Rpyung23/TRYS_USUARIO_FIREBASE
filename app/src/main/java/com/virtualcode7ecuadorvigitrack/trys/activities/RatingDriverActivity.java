package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.provider.cDriverProvider;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderClientRating;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderDriverRating;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class RatingDriverActivity extends AppCompatActivity
{
    String id_driver;
    private TextView mTextViewPlaca;
    private RatingBar mRatingBarDriver;
    private CircleImageView mCircleImageViewDriver;
    private Button mButtonSendRating;
    private cDriverProvider mDriverProvider;
    private cFirebaseProviderDriverRating mFirebaseProviderDriverRating;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_driver);

        id_driver = getIntent().getStringExtra("id_driver");

        mFirebaseProviderDriverRating = new cFirebaseProviderDriverRating();

        mTextViewPlaca = findViewById(R.id.id_textview_placa_rating_driver);
        mRatingBarDriver = findViewById(R.id.id_ratingBarDriver);
        mCircleImageViewDriver = findViewById(R.id.id_circleview_photo_driver_rating);
        mButtonSendRating = findViewById(R.id.id_button_send_rating_driver);
        mDriverProvider = new cDriverProvider();
        mButtonSendRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mFirebaseProviderDriverRating.readRatingDriver(id_driver).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists())
                        {
                            /**  **/
                            double rating_val = Double.parseDouble(snapshot.child("rating").getValue().toString())
                                    +mRatingBarDriver.getRating();
                            mFirebaseProviderDriverRating.updateRatingDriver(id_driver,rating_val/2)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        /**UPDATE OK**/
                                        Toasty.success(RatingDriverActivity.this,"Calificación enviada"
                                                ,Toasty.LENGTH_LONG).show();
                                        showActivityInicio();
                                        finish();
                                    }else
                                        {
                                            /**UPDATE FAIL**/
                                            Toasty.error(RatingDriverActivity.this,"Error Rating"
                                                    ,Toasty.LENGTH_LONG).show();
                                            showActivityInicio();
                                            finish();
                                        }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });
            }
        });
    }

    private void showActivityInicio()
    {
        Intent intent = new Intent(RatingDriverActivity.this,InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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