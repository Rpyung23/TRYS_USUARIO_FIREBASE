package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.includes.cToolbar;
import com.virtualcode7ecuadorvigitrack.trys.models.cUser;
import com.virtualcode7ecuadorvigitrack.trys.provider.cBookingDriver;
import com.virtualcode7ecuadorvigitrack.trys.provider.cDriverProvider;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderAuth;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderBusy;
import com.virtualcode7ecuadorvigitrack.trys.provider.cProviderSharedUiCondutor;
import com.virtualcode7ecuadorvigitrack.trys.runnable.cRunnableTazosBooking;
import com.virtualcode7ecuadorvigitrack.trys.runnable.cRunnableTrazos;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class BookingActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap mGoogleMap;
    private Toolbar toolbar;
    private SupportMapFragment mSupportMapFragment;

    private String id_driver;
    private double latitud_start;
    private double longitud_start;
    private double latitud_end;
    private double longitud_end;
    private double price;
    private float grados=90;

    private LatLng mLatLngAnt;

    private Marker mMarkerDriver;

    private cUser mUser;

    private final static int REQUEST_CALL_PHONE =8;

    private TextView mTextView_Price;

    private Button mButtonCancel;
    private Button mButtonProfile;
    private Button mButtonChat;
    private Button mButtonCall;

    private cDriverProvider mDriverProvider;

    private cBookingDriver mBookingDriver;

    private Thread mThread;


    private cFirebaseProviderBusy mFirebaseProviderBusy;

    private ValueEventListener mValueEventListener;

    private ChildEventListener mChildEventListenerBooking;
    private cProviderSharedUiCondutor mProviderSharedUiConductor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        id_driver = getIntent().getStringExtra("id_driver");

        latitud_start = getIntent().getDoubleExtra("latitud_start", 0);
        longitud_start = getIntent().getDoubleExtra("longitud_start",0);
        latitud_end = getIntent().getDoubleExtra("latitud_end",0);
        longitud_end = getIntent().getDoubleExtra("longitud_end",0);
        price = getIntent().getDoubleExtra("price",0);
        mProviderSharedUiConductor = new cProviderSharedUiCondutor(BookingActivity.this);

        new cToolbar().showToolbar(BookingActivity.this,"ESPERANDO TAXI"
                ,false);




        mDriverProvider = new cDriverProvider();
        mBookingDriver = new cBookingDriver();
        mFirebaseProviderBusy = new cFirebaseProviderBusy();


        mUser = new cUser();

        mTextView_Price = findViewById(R.id.id_textview_pago_tot);

        mButtonCancel = findViewById(R.id.btn_cancelar_carrera);
        mButtonProfile = findViewById(R.id.btn_driver_detalle);
        mButtonCall = findViewById(R.id.btn_llamada_driver);
        mButtonChat = findViewById(R.id.btn_chat_driver);

        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapview);

        readDriver();

        mTextView_Price.setText(String.valueOf(price));

        mButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showProfileDialog();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog alertDialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                builder.setMessage("Esta seguro de cancelar su petición?");
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        mBookingDriver.updateBookingDriver(id_driver,"cancel")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                Toast.makeText(BookingActivity.this, "CARRERA CANCELADA"
                                        , Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(BookingActivity.this
                                        ,InicioActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        mButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                {
                    if (ContextCompat.checkSelfPermission(BookingActivity.this
                            , Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                    {
                        callDriver();
                    }else
                        {
                            showPermision();
                        }
                }else
                    {
                        callDriver();
                    }
            }
        });

        mButtonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                mDriverProvider.readDriver(id_driver)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if (snapshot.exists())
                                {

                                    Intent intent = new Intent(BookingActivity.this,MessagingActivity.class);
                                    intent.putExtra("id_driver",id_driver);
                                    intent.putExtra("photo_driver",snapshot.child("Photo")
                                            .getValue().toString());
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }else
                                {
                                    Toast.makeText(BookingActivity.this, "Client sin perfil", Toast.LENGTH_SHORT).show();
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

    private void showPermision()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(BookingActivity.this
                ,Manifest.permission.CALL_PHONE))
        {
            /**Alert Volver a Permisos**/
            AlertDialog alertDialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
            builder.setMessage("Permisos de Llamada");
            builder.setPositiveButton("Otorgar", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    ActivityCompat.requestPermissions(BookingActivity.this
                            ,new String[]{Manifest.permission.CALL_PHONE}
                    ,REQUEST_CALL_PHONE);
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        }else
        {
            ActivityCompat.requestPermissions(BookingActivity.this
                    ,new String[]{Manifest.permission.CALL_PHONE}
                    ,REQUEST_CALL_PHONE);
        }
    }

    private void callDriver()
    {
        if (mUser.getPhone()!=null)
        {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+mUser.getPhone()));
            startActivity(intent);
        }else
        {
            Toast.makeText(BookingActivity.this, "SIN NUMERO TELEFONICO"
                    , Toast.LENGTH_SHORT).show();
        }
    }

    private void readDriver()
    {
        mDriverProvider.readDriver(id_driver).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    mUser.setPhoto_url(snapshot.child("Photo").getValue().toString());
                    mUser.setPlaca(snapshot.child("Placa").getValue().toString());
                    mUser.setPhone(snapshot.child("Phone").getValue().toString());
                    mUser.setName(snapshot.child("Name").getValue().toString());
                    mUser.setEmail(snapshot.child("Email").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.mGoogleMap = googleMap;
        cRunnableTazosBooking oR = new cRunnableTazosBooking(BookingActivity.this,mGoogleMap);
        oR.setLatitud_inicio(latitud_start);
        oR.setLongitud_inicio(longitud_start);
        oR.setLatitud_final(latitud_end);
        oR.setLongitud_final(longitud_end);
        mThread = new Thread(oR);
        mThread.run();

        rastreoBusyDriver();

    }

    private void rastreoBusyDriver()
    {
        mValueEventListener = mFirebaseProviderBusy.readBusyDriving(id_driver)
                .addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    if (mMarkerDriver!=null){mMarkerDriver.remove();}

                    double lat = Double.parseDouble(snapshot.child("l").child("0")
                            .getValue().toString());
                    double lng = Double.parseDouble(snapshot.child("l").child("1")
                            .getValue().toString());
                    if (mLatLngAnt==null){mLatLngAnt=new LatLng(lat,lng);}

                    mMarkerDriver = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat,lng)).rotation(getBearing(mLatLngAnt,new LatLng(lat,lng)))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi_rastreo)));

                    mLatLngAnt = new LatLng(lat,lng);


                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMarkerDriver.getPosition().latitude,mMarkerDriver.getPosition().longitude)
                            ,17));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private float getBearing(LatLng begin,LatLng end)
    {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private void showProfileDialog()
    {
        View view1 = LayoutInflater.from(BookingActivity.this)
                .inflate(R.layout.view_profile_driver,null);

        CircleImageView mCircleImageViewDriver = view1.findViewById(R.id.id_circleview_profile);
        TextView mTextViewEmail = view1.findViewById(R.id.textview_email_profile);
        TextView mTextViewPhone = view1.findViewById(R.id.textview_phone_profile);
        TextView mTextViewName = view1.findViewById(R.id.textview_name_profile);
        TextView mTextViewPlate = view1.findViewById(R.id.textview_placa_profile);


        Picasso.with(BookingActivity.this).load(mUser.getPhoto_url())
                .placeholder(R.drawable.loading_photo)
                .error(R.drawable.error_image_load)
                .into(mCircleImageViewDriver);

        mTextViewEmail.setText(mUser.getEmail());
        mTextViewPhone.setText(mUser.getPhone());
        mTextViewName.setText(mUser.getName());
        mTextViewPlate.setText(mUser.getPlaca());

        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
        builder.setView(view1);
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode==REQUEST_CALL_PHONE)
        {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                callDriver();
            }else
                {
                    showPermision();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPostResume() {
        mSupportMapFragment.getMapAsync(this);
        Toasty.error(BookingActivity.this,mProviderSharedUiConductor.leerSharedPreferences(),
                Toasty.LENGTH_LONG).show();
        mChildEventListenerBooking = mBookingDriver.getmDatabaseReference()
                .child(mProviderSharedUiConductor.leerSharedPreferences())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                    {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                    {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot)
                    {
                        Log.e("KEYCHANGE",snapshot.getKey().toString());
                        Intent intent = new Intent(BookingActivity.this,RatingDriverActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("id_driver",mProviderSharedUiConductor.leerSharedPreferences());
                        startActivity(intent);
                        finish();
                        /*if (snapshot.child("status").getValue().toString().equals("cancel"))
                            {
                                /**CARRERA CANCELADA**/
                               /* Log.e("KEYCHANGE","CANCELADA");

                                Toast.makeText(BookingActivity.this, "SU CARRERA HA SIDO CANCELADA "
                                        , Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(BookingActivity.this,RatingDriverActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("id_driver",mProviderSharedUiConductor.leerSharedPreferences());
                                startActivity(intent);
                                finish();

                            }*//*else
                            {
                                /** CARRERA FINALIZADA**/

                                /*Log.e("KEYCHANGE","FINALIZADA");

                                Toast.makeText(BookingActivity.this, "CARRERA FINALIZADA EXITOSAMENTE"
                                        , Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(BookingActivity.this,RatingDriverActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("id_driver",mProviderSharedUiConductor.leerSharedPreferences());
                                startActivity(intent);
                                finish();
                            }*/
                        //Log.e("KEYCHANGE",snapshot.child("status").getValue().toString());
                        //finish();
                    }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                    {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });
        super.onPostResume();
    }

    @Override
    protected void onDestroy()
    {
        mFirebaseProviderBusy.getmDatabaseReference().removeEventListener(mValueEventListener);
        mBookingDriver.getmDatabaseReference().removeEventListener(mChildEventListenerBooking);
        super.onDestroy();
    }

    @Override
    protected void onStart()
    {

        mBookingDriver.getmDatabaseReference()
                .child(mProviderSharedUiConductor.leerSharedPreferences())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (!snapshot.exists())
                {
                    Intent intent = new Intent(BookingActivity.this,RatingDriverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("id_driver",mProviderSharedUiConductor.leerSharedPreferences());
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        super.onStart();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}