package com.virtualcode7ecuadorvigitrack.trys.provider;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class cFirebaseProviderDriverRating
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public cFirebaseProviderDriverRating()
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("RatingDriver");
    }


    public DatabaseReference readRatingDriver(String id_driver)
    {
        return mDatabaseReference.child(id_driver);
    }

    public Task<Void> updateRatingDriver(String id_driver, double rating)
    {
        return mDatabaseReference.child(id_driver).setValue(UpdateRating(rating));
    }

    private Object UpdateRating(double rating)
    {
        HashMap<Object,Object> hashMap = new HashMap<>();
        hashMap.put("rating",rating);
        return hashMap;
    }
}
