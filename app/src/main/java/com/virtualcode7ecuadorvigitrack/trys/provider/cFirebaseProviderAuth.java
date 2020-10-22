package com.virtualcode7ecuadorvigitrack.trys.provider;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class cFirebaseProviderAuth
{
    private FirebaseAuth mFirebaseAuth;
    private GoogleSignInAccount googleSignInAccount;

    public cFirebaseProviderAuth()
    {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getmFirebaseAuth()
    {
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
