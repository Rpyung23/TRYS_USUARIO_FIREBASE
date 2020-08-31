package com.virtualcode7ecuadorvigitrack.trys.provider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.virtualcode7ecuadorvigitrack.trys.models.cSolicitudTaxi;

import java.util.HashMap;

public class cSolicitudProvider
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public cSolicitudProvider()
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
    }

    public void createSolicitud(cSolicitudTaxi OSolicitudTaxi)
    {
        mDatabaseReference.child("Solicitud")
                .child(OSolicitudTaxi.getId_token_drivers())
                .setValue(generarHashMap(OSolicitudTaxi));
    }

    private Object generarHashMap(cSolicitudTaxi oSolicitudTaxi)
    {
        HashMap<Object,Object> hashMap = new HashMap<>();

        hashMap.put("Token_client",oSolicitudTaxi.getId_token_client());
        hashMap.put("Fecha",oSolicitudTaxi.getFecha());
        hashMap.put("Latitud_start",oSolicitudTaxi.getLatitud_start());
        hashMap.put("Longitud_start",oSolicitudTaxi.getLatitud_start());
        hashMap.put("Latitud_end",oSolicitudTaxi.getLatitud_end());
        hashMap.put("Longitud_end",oSolicitudTaxi.getLongitud_end());
        hashMap.put("Price",oSolicitudTaxi.getPrice());
        hashMap.put("Status",oSolicitudTaxi.getStatus());

        return hashMap;
    }
}
