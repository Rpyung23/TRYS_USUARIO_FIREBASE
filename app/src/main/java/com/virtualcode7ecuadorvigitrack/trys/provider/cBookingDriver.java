package com.virtualcode7ecuadorvigitrack.trys.provider;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class cBookingDriver
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public cBookingDriver()
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Booking/");
    }

    public DatabaseReference readBookingDriver(String id_driver)
    {
        return mDatabaseReference.child(id_driver);
    }

    public DatabaseReference getmDatabaseReference() {
        return mDatabaseReference;
    }

    public void setmDatabaseReference(DatabaseReference mDatabaseReference) {
        this.mDatabaseReference = mDatabaseReference;
    }

    public Task<Void> updateBookingDriver(String id_driver, String status)
    {
        return mDatabaseReference.child(id_driver).child("status").setValue(status);
                //.updateChildren(createHashmapUpdate(status));
    }

    private Map<String, Object> createHashmapUpdate(String status)
    {
        HashMap<String,Object> hashMap = new HashMap<>();
        return hashMap;
    }
    public DatabaseReference readChangeChildBooking(String id_driver)
    {
        return mDatabaseReference.child(id_driver);
    }
}
