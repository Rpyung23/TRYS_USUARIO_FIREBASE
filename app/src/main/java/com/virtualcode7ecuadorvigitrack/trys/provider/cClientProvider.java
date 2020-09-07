package com.virtualcode7ecuadorvigitrack.trys.provider;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.virtualcode7ecuadorvigitrack.trys.models.cUser;

import java.util.HashMap;

public class cClientProvider
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    public cClientProvider()
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
    }

    public Task<Void> createNewClient(cUser Ou)
    {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Name",Ou.getName());
        hashMap.put("Photo",Ou.getPhoto_url());
        hashMap.put("Email",Ou.getEmail());
        hashMap.put("Phone",Ou.getPhone());
        return mDatabaseReference.child("Clients").child(Ou.getId_token_()).setValue(hashMap);
    }

    public DatabaseReference getmDatabaseReference() {
        return mDatabaseReference;
    }
}
