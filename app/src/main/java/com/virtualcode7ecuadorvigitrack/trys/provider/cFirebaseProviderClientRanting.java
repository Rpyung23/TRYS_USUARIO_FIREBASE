package com.virtualcode7ecuadorvigitrack.trys.provider;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class cFirebaseProviderClientRanting
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public cFirebaseProviderClientRanting()
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("RatingClient");
    }

    public Task<Void> createClientRating(String id_client)
    {
        return mDatabaseReference.child(id_client).setValue(createHashMap());
    }

    private Object createHashMap()
    {
        HashMap<Object,Object> hashMap = new HashMap<>();
        hashMap.put("rating",5);
        return hashMap;
    }
}
