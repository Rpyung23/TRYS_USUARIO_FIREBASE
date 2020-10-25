package com.virtualcode7ecuadorvigitrack.trys.provider;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class cFirebaseProfileDriver
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public cFirebaseProfileDriver()
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Driver");
    }

    public DatabaseReference readProfileDriver(String id_driver)
    {
        return mDatabaseReference.child(id_driver);
    }

    public Task<Void> updateProfileDriver(String id_driver, String email, String marca, String name, String phone, String photo, String placa)
    {
        return mDatabaseReference.child(id_driver).setValue(createObjectProfile(email,marca,name,phone,photo,placa));
    }

    private Object createObjectProfile(String email, String marca, String name, String phone
            , String photo, String placa)
    {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Email",email);
        hashMap.put("Marca",marca);
        hashMap.put("Name",name);
        hashMap.put("Phone",phone);
        hashMap.put("Photo",photo);
        hashMap.put("Placa",placa);
        return hashMap;
    }
}
