package com.virtualcode7ecuadorvigitrack.trys.provider;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class cProviderToken
{
    public void realToken(final cFirebaseProviderAuth mFirebaseProviderAuth)
    {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(
                new OnCompleteListener<InstanceIdResult>()
        {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task)
            {
                if (task.isSuccessful())
                {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference();
                    databaseReference.child("Token").child(mFirebaseProviderAuth.getmFirebaseAuth().getUid())
                            .setValue(createHashMap(task.getResult().getToken()))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Log.i("TOKEN","TOKEN CAMBIADO");
                        }
                    });
                }
            }
        });
    }

    private Object createHashMap(String token)
    {
        HashMap<Object,Object> hashMap = new HashMap<>();
        hashMap.put("token",token);
        return hashMap;
    }



}
