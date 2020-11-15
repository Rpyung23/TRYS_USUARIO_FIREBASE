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
import com.virtualcode7ecuadorvigitrack.trys.provider.cProviderSharedUiCondutor;
import com.virtualcode7ecuadorvigitrack.trys.provider.cProviderToken;
import com.virtualcode7ecuadorvigitrack.trys.volley.cVolleyNotification;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

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
    private List<cSolicitudTaxi> mSolicitudTaxiList = new ArrayList<>();



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


    private cProviderSharedUiCondutor mProviderSharedUiCondutor;

    private TextView mTextViewCount;

    private Handler mHandler;

    private static int cont= 35;

    private float mRadio = (float) 0.1;

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
        mProviderSharedUiCondutor= new cProviderSharedUiCondutor(SoliciteTaxiActivity.this);

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

                for (cSolicitudTaxi mSolicitudTaxi :mSolicitudTaxiList)
                {
                    if (mSolicitudTaxi.getId_token_drivers().equals(key))
                    {
                        return;
                    }
                }

                cSolicitudTaxi mSolicitudTaxi = new cSolicitudTaxi();

                mSolicitudTaxi.setId_token_drivers(key);

                mSolicitudTaxi.setToken_client_phone(token_phone_client);
                mSolicitudTaxi.setId_token_client(id_client);
                mSolicitudTaxi.setFecha(fecha);
                mSolicitudTaxi.setPrice(price);
                mSolicitudTaxi.setLatitud_start(latitud_inicio);
                mSolicitudTaxi.setLongitud_start(longitud_inicio);
                mSolicitudTaxi.setLatitud_end(latitud_fin);
                mSolicitudTaxi.setLongitud_end(longitud_fin);
                mSolicitudTaxi.setStatus("create");
                mSolicitudTaxi.setTime(time);
                mSolicitudTaxi.setDistance(distance);
                mSolicitudTaxi.setTipo(tipo);


                mSolicitudTaxiList.add(mSolicitudTaxi);
                Log.e("KEYSNOTI"," -> "+key);

               /* METODO FUNCIONAL SOLO Q ENVIA 1 taxi if (!isdriver)
                {
                    isdriver=true;
                    Log.e("NOTI","ENVIANDO");
                    mProviderSharedUiCondutor.writeSharedPreferences(key);
                    readTokenDriver(key);
                }*/
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
                        /***CODIGO PROXIMO ES PARA LEER DATOS DE TODAS LAS KEY**/


                        readTokenDriverList();
                        readBookingDriverList();
                        /****/
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

    private void readTokenDriverList()
    {
        cProviderToken mProviderToken = new cProviderToken();
        mProviderToken.readAllDriversTokens().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                /**READ Token ALL DRIVERs**/
                if (snapshot.exists())
                {
                    for (DataSnapshot mDataSnapshot : snapshot.getChildren())
                    {
                        for (int i = 0; i<mSolicitudTaxiList.size();i++)
                        {
                            if (mSolicitudTaxiList.get(i).getId_token_drivers()
                                    .equals(mDataSnapshot.getKey()))
                            {
                                mSolicitudTaxiList.get(i).setToken_driver_phone(mDataSnapshot.child("token")
                                        .getValue().toString());
                            }
                        }
                    }

                    /**ENVIO AL SERVICE FCM METODO CON LISTA DE DRIVER**/
                    OvolleyNotification.createNotificationServerFirebaseList(mSolicitudTaxiList,0,
                            mSolicitudTaxiList.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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


    private void readBookingDriverList()
    {
        mChildEventListener = mBookingDriver.getmDatabaseReference()
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                    {
                        for (cSolicitudTaxi oS : mSolicitudTaxiList)
                        {
                            if (snapshot.child("id_client").getValue().toString()
                                    .equals(oS.getId_token_client()))
                            {
                                eventEscuchaCambiosStatusList(snapshot.getKey(),oS);
                                return;
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

    private void eventEscuchaCambiosStatusList(final String key,final cSolicitudTaxi solicitudTaxi)
    {
        mValueEventListener = mBookingDriver.getmDatabaseReference().child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists())
                        {
                            mDatabaseReference.removeEventListener(mChildEventListener);/** Elimino los eventos de exuchas de nodos**/
                            if (mValueEventListener!=null)
                            {
                                mBookingDriver.getmDatabaseReference().child(key)
                                        .removeEventListener(mValueEventListener);
                            }

                            if (snapshot.child("status").getValue().toString().equals("create"))
                            {
                                /** Driver Acepto **/
                                Intent intent = new Intent(SoliciteTaxiActivity.this
                                        ,BookingActivity.class);
                                cProviderSharedUiCondutor mProviderSharedUiCondutor =
                                        new cProviderSharedUiCondutor(SoliciteTaxiActivity.this);
                                mProviderSharedUiCondutor.writeSharedPreferences(key);
                                finish();
                                intent.putExtra("id_driver",key);
                                intent.putExtra("price",solicitudTaxi.getPrice());
                                intent.putExtra("latitud_start",solicitudTaxi.getLatitud_start());
                                intent.putExtra("longitud_start",solicitudTaxi.getLongitud_start());
                                intent.putExtra("latitud_end",solicitudTaxi.getLatitud_end());
                                intent.putExtra("longitud_end",solicitudTaxi.getLongitud_end());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }else if(snapshot.child("status").getValue().toString().equals("cancel"))
                            {
                        /*Toast.makeText(SoliciteTaxiActivity.this, "NO DRIVER"
                                ,Toast.LENGTH_SHORT).show();
                        /** Driver NO Acepto **/
                                finish();
                                Intent intent_ = new Intent(SoliciteTaxiActivity.this
                                        ,InicioActivity.class);
                                intent_.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent_);
                            }/*else if(snapshot.child("status").getValue().toString().equals("finalize"))
                    {
                        Intent intent_ = new Intent(SoliciteTaxiActivity.this
                                ,InicioActivity.class);
                        intent_.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent_);
                        finish();
                    }*/
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });
    }


    private void readBookingDriver(final String key,final cSolicitudTaxi Os)
    {
        mChildEventListener = mBookingDriver.getmDatabaseReference()
                .addChildEventListener(new ChildEventListener() {
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
                    if (mValueEventListener!=null)
                    {
                        mBookingDriver.getmDatabaseReference().child(key)
                                .removeEventListener(mValueEventListener);
                    }

                    if (snapshot.child("status").getValue().toString().equals("create"))
                    {
                        /** Driver Acepto **/
                        Intent intent = new Intent(SoliciteTaxiActivity.this
                                ,BookingActivity.class);
                        cProviderSharedUiCondutor mProviderSharedUiCondutor =
                                new cProviderSharedUiCondutor(SoliciteTaxiActivity.this);
                        mProviderSharedUiCondutor.writeSharedPreferences(key);
                        finish();
                        intent.putExtra("id_driver",key);
                        intent.putExtra("price",solicitudTaxi.getPrice());
                        intent.putExtra("latitud_start",solicitudTaxi.getLatitud_start());
                        intent.putExtra("longitud_start",solicitudTaxi.getLongitud_start());
                        intent.putExtra("latitud_end",solicitudTaxi.getLatitud_end());
                        intent.putExtra("longitud_end",solicitudTaxi.getLongitud_end());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else if(snapshot.child("status").getValue().toString().equals("cancel"))
                    {
                        /*Toast.makeText(SoliciteTaxiActivity.this, "NO DRIVER"
                                ,Toast.LENGTH_SHORT).show();
                        /** Driver NO Acepto **/
                        finish();
                        Intent intent_ = new Intent(SoliciteTaxiActivity.this
                                ,InicioActivity.class);
                        intent_.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent_);
                    }/*else if(snapshot.child("status").getValue().toString().equals("finalize"))
                    {
                        Intent intent_ = new Intent(SoliciteTaxiActivity.this
                                ,InicioActivity.class);
                        intent_.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent_);
                        finish();
                    }*/
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

        if (mValueEventListener!=null)
        {
            mBookingDriver.getmDatabaseReference()
                .removeEventListener(mValueEventListener);}
        //mGeoQuery.removeGeoQueryEventListener();
        cont = 30;

        /*Intent intent = new Intent(SoliciteTaxiActivity.this,InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);*/
        super.onDestroy();
    }
}