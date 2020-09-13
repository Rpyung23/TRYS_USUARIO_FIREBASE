package com.virtualcode7ecuadorvigitrack.trys.provider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class cFirebaseHistoryBooking
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private Query mQuery;
    public cFirebaseHistoryBooking()
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("HistoryBooking");
    }

    public DatabaseReference readHistoryBooking(String id_client)
    {
        return mDatabaseReference;
    }
}
