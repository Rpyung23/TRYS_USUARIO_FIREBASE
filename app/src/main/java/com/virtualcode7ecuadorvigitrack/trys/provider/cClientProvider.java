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
        mDatabaseReference = mFirebaseDatabase.getReference("Clients");
    }

    public Task<Void> createNewClient(cUser Ou)
    {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Name",Ou.getName());
        if (!Ou.getPhoto_url().equals(""))
        {
            hashMap.put("Photo",Ou.getPhoto_url());
        }else
            {
                hashMap.put("Photo","null");
            }
        hashMap.put("Email",Ou.getEmail());
        hashMap.put("Phone",Ou.getPhone());
        return mDatabaseReference.child(Ou.getId_token_()).setValue(hashMap);
    }

    public DatabaseReference getmDatabaseReference() {
        return mDatabaseReference;
    }

    public DatabaseReference readProfileClient(String id_client)
    {
        return mDatabaseReference.child(id_client);
    }

    public Task<Void> UpdateClient(cUser oU)
    {
        return mDatabaseReference.child(oU.getId_token_())
                .updateChildren(createHashClientUpdate(oU));
    }

    private HashMap<String,Object> createHashClientUpdate(cUser oU)
    {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Email",oU.getEmail());
        hashMap.put("Name", oU.getName());
        hashMap.put("Phone",oU.getPhone());
        hashMap.put("Photo",oU.getPhoto_url());

        return hashMap;
    }
}
