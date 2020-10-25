package com.virtualcode7ecuadorvigitrack.trys.provider;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class cFirebaseMessaging
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public cFirebaseMessaging()
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Messaging");
    }

    public DatabaseReference getmDatabaseReference() {
        return mDatabaseReference;
    }

    public void setmDatabaseReference(DatabaseReference mDatabaseReference) {
        this.mDatabaseReference = mDatabaseReference;
    }

    public ChildEventListener readMessaging(String uid)
    {
        return (ChildEventListener) getmDatabaseReference().child(uid);
    }

    public void deleteMessaging(String id_)
    {
        mDatabaseReference.child(id_).removeValue();
    }

}
