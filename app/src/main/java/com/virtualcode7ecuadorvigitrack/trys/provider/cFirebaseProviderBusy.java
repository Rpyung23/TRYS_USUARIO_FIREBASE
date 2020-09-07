package com.virtualcode7ecuadorvigitrack.trys.provider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class cFirebaseProviderBusy
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public cFirebaseProviderBusy()
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Busy");
    }

    public DatabaseReference readBusyDriving(String id_driver_busy)
    {
        return mDatabaseReference.child(id_driver_busy);
    }

    public DatabaseReference getmDatabaseReference() {
        return mDatabaseReference;
    }

    public void setmDatabaseReference(DatabaseReference mDatabaseReference) {
        this.mDatabaseReference = mDatabaseReference;
    }
}
