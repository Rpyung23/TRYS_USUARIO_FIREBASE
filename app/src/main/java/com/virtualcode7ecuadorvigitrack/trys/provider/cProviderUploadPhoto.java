package com.virtualcode7ecuadorvigitrack.trys.provider;

import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.virtualcode7ecuadorvigitrack.trys.R;

public class cProviderUploadPhoto
{
    private FirebaseStorage mFirebaseStorage;
    private Context mContext;
    private StorageReference storageReference;
    private UploadTask uploadTask;

    public cProviderUploadPhoto(Context context_)
    {
        this.mContext = context_;
        this.mFirebaseStorage = FirebaseStorage.getInstance(mContext.getResources()
                .getString(R.string.instance_firebase_storage));
        this.storageReference = mFirebaseStorage.getReference("Client");
    }

    public UploadTask uploadPhoto(byte[] data_img)
    {
        uploadTask = storageReference.child((System.currentTimeMillis()/100+".png")).putBytes(data_img);
        return uploadTask;
    }
}
