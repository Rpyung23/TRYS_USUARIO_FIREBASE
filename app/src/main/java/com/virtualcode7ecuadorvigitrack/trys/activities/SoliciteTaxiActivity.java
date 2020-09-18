package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.system.Os;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.models.cSolicitudTaxi;
import com.virtualcode7ecuadorvigitrack.trys.provider.cBookingDriver;
import com.virtualcode7ecuadorvigitrack.trys.provider.cGeoFire;
import com.virtualcode7ecuadorvigitrack.trys.volley.cVolleyNotification;

import java.util.EventListener;

public class SoliciteTaxiActivity extends AppCompatActivity
{

    private String id_client;
    private double latitud_inicio;
    private double longitud_inicio;
    private double latitud_fin;
    private double longitud_fin;
    private double price;
    private String fecha;
    private  int tipo;
    private double time;
    private double distance;



    private ValueEventListener mValueEventListener;

    private String token_phone_client;

    private TextView textView_estado_solicitud;

    private cGeoFire mGeoFire;


    private ChildEventListener mChildEventListener;

    private cBookingDriver mBookingDriver;

    private cVolleyNotification OvolleyNotification;


    private boolean isdriver=false;

    private cSolicitudTaxi OSolicitudTaxi;

    private DatabaseReference mDatabaseReference;


    private TextView mTextViewCount;

    private Handler mHandler;

    private static int cont= 30;

    private float mRadio = 1;

    private GeoQueryEventListener geoQueryEventListener;

    Runnable mRunnable = new Runnable() {
        @Override
        public void run()
        {
            if (mTextViewCount.getText().toString().equals("00"))
            {
                /** Time out**/
                mHandler.removeCallbacks(this);

                if (mValueEventListener!=null){mBookingDriver.getmDatabaseReference()
                        .removeEventListener(mValueEventListener);}
                //mGeoQuery.removeAllListeners();
                textView_estado_solicitud.setText("Tiempo De Espera Agotado");

            }else
                {
                    cont  = cont-1;
                    if (cont>9)
                    {
                        mTextViewCount.setText(String.valueOf(cont));
                    }else
                        {
                            mTextViewCount.setText("0"+String.valueOf(cont));
                        }
                    mHandler.postDelayed(this,1000);
                }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicite_taxi);


        latitud_inicio = getIntent().getDoubleExtra("lat_start",0);
        longitud_inicio = getIntent().getDoubleExtra("long_start",0);
        latitud_fin = getIntent().getDoubleExtra("lat_end",0);
        longitud_fin = getIntent().getDoubleExtra("long_end",0);
        id_client = getIntent().getStringExtra("id_client");
        price = getIntent().getDoubleExtra("price",0);
        fecha = getIntent().getStringExtra("fecha");
        token_phone_client = getIntent().getStringExtra("token_phone_client");
        time = getIntent().getDoubleExtra("time",0);
        distance = getIntent().getDoubleExtra("distance",0);
        tipo = getIntent().getIntExtra("tipo",0);

        textView_estado_solicitud = findViewById(R.id.id_textview_estado_solicitud);
        mTextViewCount = findViewById(R.id.id_textview_cont_tiempo);

        mBookingDriver = new cBookingDriver();
        OSolicitudTaxi = new cSolicitudTaxi();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Token");

        OvolleyNotification = new cVolleyNotification(SoliciteTaxiActivity.this);




        OSolicitudTaxi.setToken_client_phone(token_phone_client);
        OSolicitudTaxi.setId_token_client(id_client);
        OSolicitudTaxi.setFecha(fecha);
        OSolicitudTaxi.setPrice(price);
        OSolicitudTaxi.setLatitud_start(latitud_inicio);
        OSolicitudTaxi.setLongitud_start(longitud_inicio);
        OSolicitudTaxi.setLatitud_end(latitud_fin);
        OSolicitudTaxi.setLongitud_end(longitud_fin);
        OSolicitudTaxi.setStatus("create");
        OSolicitudTaxi.setTime(time);
        OSolicitudTaxi.setDistance(distance);
        OSolicitudTaxi.setTipo(tipo);

        mGeoFire = new cGeoFire();
        /*mGeoFire.aroundTaxi(new LatLng(latitud_inicio,longitud_inicio)
                ,1,textView_estado_solicitud,OSolicitudTaxi
                ,OvolleyNotification);*/

        aroundTaxi(new LatLng(latitud_inicio,longitud_inicio));
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable,1000);

    }

    private void aroundTaxi(final LatLng latLng)
    {
        mGeoFire.aroundTaxi2(latLng,mRadio).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location)
            {
                if (!isdriver)
                {
                    isdriver=true;
                    Log.e("NOTI","ENVIANDO");
                    readTokenDriver(key);
                }
            }

            @Override
            public void onKeyExited(String key)
            {

            }
            @Override
            public void onKeyMoved(String key, GeoLocation location)
            {

            }

            @Override
            public void onGeoQueryReady()
            {

                if (!isdriver)
                {
                    mRadio = mRadio+0.1f;
                    Log.e("NOTI","RADIO : "+mRadio);
                    if (mRadio>5)
                    {
                        Log.e("NOTI","NO ENCONTRO");
                        isdriver=true;
                        return;
                    }else
                        {
                            aroundTaxi(latLng);
                        }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error)
            {
                Log.e("NOTI",error.getMessage());
            }
        });
    }

    private void readTokenDriver(final String id_driver)
    {
        Log.e("KEY",id_driver);
        mDatabaseReference.child(id_driver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    OSolicitudTaxi.setId_token_drivers(id_driver);

                    OSolicitudTaxi.setToken_driver_phone(String.valueOf(snapshot.child("token")
                            .getValue().toString()));
                    //OvolleyNotification.createNotificationServer(OSolicitudTaxi);
                    OvolleyNotification.createNotificationServerFirebase(OSolicitudTaxi);
                    textView_estado_solicitud.setText("ESPERANDO RESPUESTA DEL TAXI");

                    readBookingDriver(id_driver,OSolicitudTaxi);

                }else
                    {
                        return;
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void readBookingDriver(final String key,final cSolicitudTaxi Os)
    {
        mChildEventListener = mBookingDriver.getmDatabaseReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                if (snapshot.getKey().equals(key))
                {
                    if (snapshot.child("id_client").getValue().toString()
                            .equals(OSolicitudTaxi.getId_token_client()))
                    {
                        Log.e("KEY NODO",key);
                        eventEscuchaCambiosStatus(key,Os);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void eventEscuchaCambiosStatus(final String key,final cSolicitudTaxi solicitudTaxi)
    {
        mValueEventListener = mBookingDriver.getmDatabaseReference().child(key)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    mDatabaseReference.removeEventListener(mChildEventListener);/** Elimino los eventos de exuchas de nodos**/

                    if (snapshot.child("status").getValue().toString().equals("create"))
                    {
                        /** Driver Acepto **/
                        Intent intent = new Intent(SoliciteTaxiActivity.this
                                ,BookingActivity.class);
                        intent.putExtra("id_driver",key);
                        intent.putExtra("price",solicitudTaxi.getPrice());
                        intent.putExtra("latitud_start",solicitudTaxi.getLatitud_start());
                        intent.putExtra("longitud_start",solicitudTaxi.getLongitud_start());
                        intent.putExtra("latitud_end",solicitudTaxi.getLatitud_end());
                        intent.putExtra("longitud_end",solicitudTaxi.getLongitud_end());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }else if(snapshot.child("status").getValue().toString().equals("cancel"))
                    {
                        Toast.makeText(SoliciteTaxiActivity.this, "NO DRIVER"
                                ,Toast.LENGTH_SHORT).show();
                        /** Driver NO Acepto **/
                        Intent intent_ = new Intent(SoliciteTaxiActivity.this
                                ,InicioActivity.class);
                        intent_.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent_);
                        finish();
                    }
                }else
                {
                    Toast.makeText(SoliciteTaxiActivity.this, "AUN NO EXIST OK DRIVER"
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onBackPressed()
    {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
    }

    @Override
    protected void onDestroy()
    {
        if (mHandler!=null)
        {
            mHandler.removeCallbacks(mRunnable);
        }

        if (mValueEventListener!=null){mBookingDriver.getmDatabaseReference()
                .removeEventListener(mValueEventListener);}
        //mGeoQuery.removeGeoQueryEventListener();
        cont = 30;

        /*Intent intent = new Intent(SoliciteTaxiActivity.this,InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);*/
        super.onDestroy();
    }
}