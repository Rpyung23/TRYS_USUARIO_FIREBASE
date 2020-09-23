package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesStatusCodes;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.includes.cToolbar;
import com.virtualcode7ecuadorvigitrack.trys.provider.cClientProvider;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderAuth;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderWorking;
import com.virtualcode7ecuadorvigitrack.trys.provider.cGeoFire;
import com.virtualcode7ecuadorvigitrack.trys.provider.cProviderCalendar;
import com.virtualcode7ecuadorvigitrack.trys.runnable.cRunnableTrazos;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class InicioActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener
{

    private GoogleMap mGoogleMap;
    private Toolbar toolbar;
    private SupportMapFragment mSupportMapFragment;
    private DrawerLayout mDrawerLayout;
    private View mView_start_end;
    private LocationRequest mLocationRequest;/**CONFIGURACIONES PARA OBTENER LAS POSICIIONES**/
    private FusedLocationProviderClient mFusedLocationProviderClient;/**LLAMA A LAS POSICIONES DEL LOCATION CALLBACK**/

    private EditText mEditTextMiPosition;

    private cClientProvider mClientProvider;

    private TextView mEditTextMyDestino;

    private cProviderCalendar mProviderCalendar;

    private NavigationView navigationView;

    private cFirebaseProviderAuth mFirebaseProviderAuth;

    private static  int AUTOCOMPLETE_REQUEST_CODE=150;

    private LatLng mLatLngDestino;

    private Marker markerIam;

    private Marker markerIamDestino;

    private Button mButtonGotoTaxi;

    private cFirebaseProviderWorking mFirebaseProviderWorking;

    private cRunnableTrazos mRunnableTrazos;

    private Marker mMarkerDriving;

    private AlertDialog alertDialog_showPreviewSolicitud;

    private ValueEventListener mValueEventListenerAllDriving;

    LocationCallback mLocationCallback = new LocationCallback()
    {
        @Override
        public void onLocationResult(LocationResult locationResult)
        {
            for (Location location : locationResult.getLocations())
            {
                if (location!=null)
                {
                    if (markerIam!=null)
                    {
                        markerIam.remove();
                    }

                    markerIam = mGoogleMap.addMarker(new MarkerOptions().position(new
                            LatLng(location.getLatitude(),location.getLongitude()))
                            .title("YO")
                            //.draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_i_am)));

                    if (Geocoder.isPresent())
                    {
                        List<Address> adresses = null;
                        try {
                            adresses = new Geocoder(InicioActivity.this)
                                    .getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            mEditTextMiPosition.setText(adresses.get(0).getAddressLine(0).toString());
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    mFusedLocationProviderClient.removeLocationUpdates(this);
                }else
                    {
                        Toast.makeText(InicioActivity.this, "MAPVIEW NULL"
                                , Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };


    private View viewHeaderMenu;
    /**Alert dialog sin Carrera sin destino**/
    private AlertDialog mAlertDialogSinDestino;
    private Button mButtonAlertDialogSinDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);


        mProviderCalendar = new cProviderCalendar();

        toolbar = findViewById(R.id.id_toolbar_mapview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapview);
        mDrawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        mView_start_end = findViewById(R.id.id_container_start_end_position);
        mEditTextMiPosition = findViewById(R.id.id_edittext_mi_position);
        mEditTextMyDestino = findViewById(R.id.id_edittext_mi_destino);
        mButtonGotoTaxi = findViewById(R.id.id_btn_gotoTaxi);
        viewHeaderMenu = navigationView.getHeaderView(0);


        mFirebaseProviderAuth = new cFirebaseProviderAuth();

        mClientProvider = new cClientProvider();

        mFirebaseProviderWorking = new cFirebaseProviderWorking();

        configurarDrawerLayout();
        Places.initialize(InicioActivity.this,getString(R.string.api_key_google_maps));
        mFusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(InicioActivity.this);

        mSupportMapFragment.getMapAsync(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.id_history:
                        Intent intent = new Intent(InicioActivity.this,HistoryBookingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.id_singOut:
                        if (mFirebaseProviderAuth.getmFirebaseAuth().getCurrentUser()!=null)
                        {
                            mFirebaseProviderAuth.getmFirebaseAuth().signOut();
                            openActivityLogin();
                            finish();
                        }
                        break;
                }
                return false;
            }
        });

        mEditTextMyDestino.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS
                        ,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder
                        (AutocompleteActivityMode.OVERLAY, fields)
                        //.setTypeFilter(TypeFilter.ADDRESS)
                        //.setTypeFilter(TypeFilter.ESTABLISHMENT)
                        //.setTypeFilter(TypeFilter.CITIES)
                        .setCountry("ECU")
                        .build(InicioActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
        mButtonGotoTaxi.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (markerIam != null && markerIamDestino != null)
                {
                    showAlertDialogPreviewSolicitud();
                }else
                    {
                       if(markerIamDestino==null)
                       {
                           /** CREAR ALERT CARRERA SIN DESTINO**/

                           showALertDialogSinDestino();
                       }
                    }
            }
        });
    }

    private void showALertDialogSinDestino()
    {
        View mView = LayoutInflater.from(InicioActivity.this).inflate(R.layout.alert_dialog_sin_destino,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(InicioActivity.this);
        builder.setView(mView);
        mButtonAlertDialogSinDestino = mView.findViewById(R.id.id_btn_gotoTaxiSinDestino);
        mAlertDialogSinDestino = builder.create();
        mAlertDialogSinDestino.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAlertDialogSinDestino.show();
        mButtonAlertDialogSinDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                /** OPERACIONES DE ENVIO DE SOLICITUD**/
                //Toast.makeText(InicioActivity.this, "CLIK", Toast.LENGTH_SHORT).show();
                FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference("Token/"
                        +mFirebaseProviderAuth.getmFirebaseAuth().getUid());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists())
                        {
                            Intent intent = new Intent(InicioActivity.this,
                                    SoliciteTaxiActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("lat_start",markerIam.getPosition().latitude);
                            intent.putExtra("long_start",markerIam.getPosition().longitude);
                            intent.putExtra("lat_end",0);
                            intent.putExtra("long_end",0);
                            intent.putExtra("id_client",mFirebaseProviderAuth.getmFirebaseAuth().getUid());
                            intent.putExtra("price",0);
                            intent.putExtra("distance",0);
                            intent.putExtra("time",0);
                            intent.putExtra("fecha",mProviderCalendar.getFecha());
                            intent.putExtra("token_phone_client",String.valueOf(snapshot.child("token")
                                    .getValue().toString()));
                            intent.putExtra("tipo",11);
                            startActivity(intent);
                            mAlertDialogSinDestino.cancel();
                            mAlertDialogSinDestino.dismiss();
                            finish();
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


    private void showAlertDialogPreviewSolicitud()
    {
        View view_alert = LayoutInflater.from(InicioActivity.this).inflate(R.layout.view_preview_solicitud
                ,null);

        TextView textView_start = view_alert.findViewById(R.id.id_textview_incio_solicitud);
        TextView textView_end = view_alert.findViewById(R.id.id_text_destino_solicitud);
        TextView textView_time = view_alert.findViewById(R.id.id_text_tiempo_solicitud);
        final TextView textView_price = view_alert.findViewById(R.id.id_text_precio_solicitud);
        TextView textView_km = view_alert.findViewById(R.id.id_text_km_solicitud);

        Button mButtonConfirmar = view_alert.findViewById(R.id.btn_confirmar_solicitud);

        AlertDialog.Builder builder = new AlertDialog.Builder(InicioActivity.this);
        builder.setView(view_alert);

        textView_start.setText(mEditTextMiPosition.getText().toString());
        textView_end.setText(mEditTextMyDestino.getText().toString());
        textView_km.setText(mRunnableTrazos.getKm_reco()+" km");
        textView_time.setText(mRunnableTrazos.getMinu_reco()+" minutos");
        textView_price.setText(mRunnableTrazos.getPrecio_pago_carrera()+" $");

        mButtonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference("Token/"
                        +mFirebaseProviderAuth.getmFirebaseAuth().getUid());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists())
                        {
                            DecimalFormatSymbols separadoresPersonalizados = new DecimalFormatSymbols();
                            separadoresPersonalizados.setDecimalSeparator('.');
                            DecimalFormat priceFormat = new DecimalFormat("##.00", separadoresPersonalizados);
                            Intent intent = new Intent(InicioActivity.this,
                                    SoliciteTaxiActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("lat_start",markerIam.getPosition().latitude);
                            intent.putExtra("long_start",markerIam.getPosition().longitude);
                            intent.putExtra("lat_end",markerIamDestino.getPosition().latitude);
                            intent.putExtra("long_end",markerIamDestino.getPosition().longitude);
                            intent.putExtra("id_client",mFirebaseProviderAuth.getmFirebaseAuth().getUid());
                            intent.putExtra("price",Double.parseDouble(priceFormat.format(mRunnableTrazos
                                    .getPrecio_pago_carrera())));
                            intent.putExtra("distance",mRunnableTrazos.getKm_reco());
                            intent.putExtra("time",mRunnableTrazos.getMinu_reco());
                            intent.putExtra("fecha",mProviderCalendar.getFecha());
                            intent.putExtra("token_phone_client",String.valueOf(snapshot.child("token")
                                    .getValue().toString()));
                            intent.putExtra("tipo",1);
                            startActivity(intent);
                            alertDialog_showPreviewSolicitud.cancel();
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                    }
                });
            }
        });

        alertDialog_showPreviewSolicitud = builder.create();
        alertDialog_showPreviewSolicitud.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog_showPreviewSolicitud.show();
    }

    private void openActivityLogin()
    {
        Intent intent = new Intent(InicioActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void configurarDrawerLayout()
    {
        ActionBarDrawerToggle actionBarDrawerToggle = new
                ActionBarDrawerToggle( InicioActivity.this,mDrawerLayout,toolbar,R.string.vacio
                ,R.string.vacio);
        actionBarDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.mGoogleMap = googleMap;
        this.mGoogleMap.setOnMarkerDragListener(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);//intervalo de tiempo en q se actualiza en el mapa
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//mayor precision
        mLocationRequest.setSmallestDisplacement(5);
        mRunnableTrazos = new cRunnableTrazos(InicioActivity.this,mGoogleMap);
        startLocationClient();
        readingGpsAllWorking();
    }

    private void startLocationClient()
    {
        if (ContextCompat.checkSelfPermission(InicioActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback
                    , Looper.myLooper());
        }else
            {
                Toast.makeText(this, "SIN PERMISOS", Toast.LENGTH_SHORT).show();
            }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==AUTOCOMPLETE_REQUEST_CODE)
        {
            if (resultCode==RESULT_OK)
            {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (place!=null)
                {
                    mEditTextMyDestino.setText(place.getAddress());/**Obtengo la direccion**/
                    if (place.getLatLng()!=null)
                    {
                        mLatLngDestino = place.getLatLng();/**Obtengo las coordenadas**/
                        showMarkerDestino(mLatLngDestino);
                        if(is_day_night())
                        {
                            mRunnableTrazos.setDay_night(1);
                        }
                        config_runnableTrazos();
                    }else
                        {
                            Toast.makeText(this, "NULL LATLNG", Toast.LENGTH_SHORT)
                                    .show();
                        }
                }
            }else if(resultCode== AutocompleteActivity.RESULT_ERROR)
                {
                    Toast.makeText(this, "ERROR AUTOCOMPLETE", Toast.LENGTH_SHORT)
                            .show();
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.e("Error",status.getStatusMessage());
                }
        }
    }


    private boolean is_day_night()
    {
        boolean is_day = true;/**dia**/

        Calendar calendar = Calendar.getInstance();
        int hora = calendar.getTime().getHours();
        Log.i("HORA : ",""+hora);
        if (hora>=18 && hora<=23)
        {
            is_day = false;
        }
        return is_day;
    }

    private void config_runnableTrazos()
    {
        mRunnableTrazos.setLatitud_final(markerIamDestino.getPosition().latitude);
        mRunnableTrazos.setLongitud_final(markerIamDestino.getPosition().longitude);
        mRunnableTrazos.setLatitud_inicio(markerIam.getPosition().latitude);
        mRunnableTrazos.setLongitud_inicio(markerIam.getPosition().longitude);
        mRunnableTrazos.run();
    }

    private void showMarkerDestino(LatLng latLng)
    {
        if (markerIamDestino!=null)
        {
            markerIamDestino.remove();
        }
        markerIamDestino = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_destino))
                .draggable(true)
                .title("DESTINO"));
    }


    @Override
    protected void onPostResume()
    {
        llenarPhotoNamePhone();
        verificarBooking();
        super.onPostResume();
    }

    private void llenarPhotoNamePhone()
    {
        final CircleImageView circleImageView = viewHeaderMenu.findViewById(R.id.id_circle_profile_header_menu_view);
        final TextView textViewName = viewHeaderMenu.findViewById(R.id.id_textview_name_profile_header_menu_view);
        final TextView textViewPhone = viewHeaderMenu.findViewById(R.id.id_textview_phone_profile_header_menu_view);
        mClientProvider.readProfileClient(mFirebaseProviderAuth.getmFirebaseAuth().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    Picasso.with(InicioActivity.this)
                            .load(snapshot.child("Photo").getValue().toString())
                            .placeholder(R.drawable.loading_photo)
                            .error(R.drawable.error_image_load)
                            .into(circleImageView);
                    textViewName.setText(snapshot.child("Name").getValue().toString());
                    textViewPhone.setText(snapshot.child("Phone").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readingGpsAllWorking()
    {
        mValueEventListenerAllDriving = mFirebaseProviderWorking.getmDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    for (DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        if (mMarkerDriving!=null)
                        {
                            mMarkerDriving.remove();
                        }
                        Log.e("KEY",snapshot1.getKey());
                        mMarkerDriving = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(snapshot1.child("l").child("0").getValue().toString()),
                                   Double.parseDouble(snapshot1.child("l").child("1").getValue()
                                     .toString())))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi_rastreo)));
                    }
                    //Log.e("l",snapshot.child("l").child("0").getValue().toString());
                }else
                    {
                        if (mMarkerDriving!=null)
                        {
                            mMarkerDriving.remove();
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void verificarBooking()
    {
        FirebaseDatabase mFirebaseDatabaseV = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReferenceV = mFirebaseDatabaseV.getReference("Booking");
        mDatabaseReferenceV.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    for (DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        String key = snapshot1.child("id_client").getValue().toString();
                        if (key.equals(mFirebaseProviderAuth.getmFirebaseAuth().getUid()) &&
                                snapshot1.child("status").getValue().toString().equals("create"))
                        {
                            Intent intent_ = new Intent(InicioActivity.this
                                    ,BookingActivity.class);

                            intent_.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent_.putExtra("id_driver",snapshot1.child("id_driver").getValue().toString());
                            intent_.putExtra("latitud_start",Double.parseDouble(snapshot1.child("latitud_start")
                                    .getValue().toString()));
                            intent_.putExtra("longitud_start",Double.parseDouble(snapshot1.child("longitud_start")
                                    .getValue().toString()));
                            intent_.putExtra("latitud_end",Double.parseDouble(snapshot1.child("latitud_end")
                                    .getValue().toString()));
                            intent_.putExtra("longitud_end",Double.parseDouble(snapshot1.child("longitud_end")
                                    .getValue().toString()));
                            intent_.putExtra("price",Double.parseDouble(snapshot1.child("price")
                                    .getValue().toString()));
                            startActivity(intent_);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        mFirebaseProviderWorking.getmDatabaseReference()
                .removeEventListener(mValueEventListenerAllDriving);
        super.onDestroy();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker)
    {
        if(marker.getId().equals(markerIamDestino.getId()))
        {
            if (markerIam!=null)
            {
                mRunnableTrazos.deletepolilineas();
                mRunnableTrazos.setLatitud_final(markerIamDestino.getPosition().latitude);
                mRunnableTrazos.setLongitud_final(markerIamDestino.getPosition().longitude);

                if(is_day_night())
                {
                    mRunnableTrazos.setDay_night(1);
                }

                mRunnableTrazos.run();
            }
        }else if(marker.getId().equals(markerIam.getId()))
        {

        }
    }
}