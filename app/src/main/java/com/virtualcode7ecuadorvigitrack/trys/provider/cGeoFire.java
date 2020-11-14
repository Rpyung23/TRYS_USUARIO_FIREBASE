package com.virtualcode7ecuadorvigitrack.trys.provider;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.virtualcode7ecuadorvigitrack.trys.models.cSolicitudTaxi;
import com.virtualcode7ecuadorvigitrack.trys.volley.cVolleyNotification;

public class cGeoFire
{
    private DatabaseReference mDatabaseReference;
    private GeoFire mGeoFire;
    private GeoQuery mGeoQuery;
    private GeoQuery mge;


    private boolean isdriver=false;

    public cGeoFire()
    {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("working");
        mGeoFire = new GeoFire(mDatabaseReference);

    }


    public GeoQuery aroundTaxi2(LatLng latLng,float radio)
    {
        GeoQuery mQuery =  mGeoFire.queryAtLocation(new GeoLocation(latLng.latitude
                ,latLng.longitude)
                ,radio);
        mQuery.removeAllListeners();
        return mQuery;
    }



    public void aroundTaxi(final LatLng latLng, final float radio,
                           final TextView textView,
                           final cSolicitudTaxi OsolicitudTaxi,
                           final cVolleyNotification OvolleyNotification)
    {

       mGeoQuery = mGeoFire.queryAtLocation
               (new GeoLocation(latLng.latitude, latLng.longitude)
               , radio);
       mGeoQuery.addGeoQueryEventListener(new GeoQueryEventListener()
       {
           @Override
           public void onKeyEntered(String key, GeoLocation location)
           {
               isdriver=true;
               Log.i("KEYTAXI",key);
               String path = "Token/"+key;
               Log.e("PATH",path);
               OsolicitudTaxi.setId_token_drivers(key);

               /** ENVIO NOTIFICACION CON DATOS **/
               DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance()
                       .getReference(path);

               mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
               {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot)
                   {
                       Log.e("GEOFIRE","ONCHANGE");
                       if (snapshot.exists())
                       {
                           Log.e("GEOFIRE","EXIST token");
                           OsolicitudTaxi.setToken_driver_phone(String.valueOf(snapshot.child("token")
                                   .getValue().toString()));
                           OvolleyNotification.createNotificationServer(OsolicitudTaxi);
                           textView.setText("ESPERANDO RESPUESTA DEL TAXI");

                       }else
                       {
                           Log.i("GEOFIRE","NO EXIST token");
                       }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError error)
                   {
                       Log.i("GEOFIRE","CANELLED");
                   }
               });

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
               if (isdriver=false)
               {
                   if (radio>5)
                   {
                       textView.setText("NO SE ENCONTRO TAXIS CERCANOS");
                       return;
                   }else
                   {
                       float radio_ = radio +0.1f;
                       aroundTaxi(latLng,radio_,textView,OsolicitudTaxi,OvolleyNotification);
                   }
               }else
                   {
                       return;
                   }
           }

           @Override
           public void onGeoQueryError(DatabaseError error)
           {

           }
       });
    }


    public GeoQuery getActiveDriver(LatLng mLatLng,float radio)
    {
        mge = mGeoFire.queryAtLocation(new GeoLocation(mLatLng.latitude,
                mLatLng.longitude),radio);
        mge.removeAllListeners();
        return mge;
    }
}
