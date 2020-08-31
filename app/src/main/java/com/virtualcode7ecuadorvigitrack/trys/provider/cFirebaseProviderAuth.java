package com.virtualcode7ecuadorvigitrack.trys.provider;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class cFirebaseProviderAuth
{
    private FirebaseAuth mFirebaseAuth;

    public cFirebaseProviderAuth()
    {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getmFirebaseAuth() {
        return mFirebaseAuth;
    }

    public void setmFirebaseAuth(FirebaseAuth mFirebaseAuth) {
        this.mFirebaseAuth = mFirebaseAuth;
    }

    public Task<AuthResult> createUser(String email,String pass)
    {
        return mFirebaseAuth.createUserWithEmailAndPassword(email,pass);
    }

    public Task<AuthResult> loginUp(String email, String pass)
    {
        return mFirebaseAuth.signInWithEmailAndPassword(email, pass);
    }
}
