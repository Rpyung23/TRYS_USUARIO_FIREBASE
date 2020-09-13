package com.virtualcode7ecuadorvigitrack.trys.provider;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class cFirebaseProviderClientRating
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public cFirebaseProviderClientRating()
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("RatingClient");
    }

    public Task<Void> createClientRating(String id_client,double rating)
    {
        return mDatabaseReference.child(id_client).setValue(createHashMap(rating));
    }

    private Object createHashMap(double rating)
    {
        HashMap<Object,Object> hashMap = new HashMap<>();
        hashMap.put("rating",rating);
        return hashMap;
    }
}
