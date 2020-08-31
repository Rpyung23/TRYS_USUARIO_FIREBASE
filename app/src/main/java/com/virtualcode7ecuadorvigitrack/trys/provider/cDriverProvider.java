package com.virtualcode7ecuadorvigitrack.trys.provider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class cDriverProvider
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public cDriverProvider()
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Driver");
    }

    public DatabaseReference readDriver(String id_driver)
    {
        return mDatabaseReference.child(id_driver);
    }
}
