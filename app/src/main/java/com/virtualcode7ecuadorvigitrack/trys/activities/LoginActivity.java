package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.RegisterListenerMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.models.cUser;
import com.virtualcode7ecuadorvigitrack.trys.provider.cClientProvider;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderAuth;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderClientRating;
import com.virtualcode7ecuadorvigitrack.trys.provider.cProviderToken;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity
{
    private static final int RC_SIGN_IN = 25689;
    private Button mButtonRegister;

    private Button mButtonLogin;

    private CircleImageView mCircleImageViewGoogle;

    private TextInputEditText mTextInputEditTextEmail;
    private TextInputEditText mTextInputEditTextPass;

    private cFirebaseProviderAuth mFirebaseProviderAuth;


    private static int REQUEST_FINE_LOCATION=100;

    private AlertDialog alertDialog_permission;

    private android.app.AlertDialog mAlertDialogLogin;


    private AlertDialog mAlertDialogOPCRegister;

    /** FACEBOOK **/
    private CallbackManager mCallbackManager;
    /** Google **/

    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean banCreateUser = false;

    private android.app.AlertDialog mAlertDialogAccediendoFragmentLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.e("M",Build.MODEL.toString());
        Log.e("M",Build.RADIO.toString());
        Log.e("M",Build.DISPLAY);
        Log.e("M",Build.getRadioVersion());
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.e("SC",String.valueOf(metrics.scaledDensity));
        Log.e("DP",String.valueOf(metrics.densityDpi));
        Log.e("D",""+metrics.density);
        Log.e("HP",""+metrics.heightPixels);
        Log.e("WP",""+metrics.widthPixels);
        Log.e("RV",Build.getRadioVersion());



        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this,mGoogleSignInOptions);

        mFirebaseProviderAuth = new cFirebaseProviderAuth();
        mCallbackManager = CallbackManager.Factory.create();

        mButtonRegister = findViewById(R.id.id_registro_client);
        mButtonLogin = findViewById(R.id.id_login_client);
        mTextInputEditTextEmail = findViewById(R.id.id_textinput_mail);
        mTextInputEditTextPass = findViewById(R.id.id_textinput_password);
        mCircleImageViewGoogle = findViewById(R.id.id_circle_view_gmail);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if (checkPermission())
            {
                if (mFirebaseProviderAuth.getmFirebaseAuth().getCurrentUser()!=null)
                {
                    abrirActivityInicio();
                }
            }else
                {
                    showrequestPermissions();
                }
        }else
            {
                if (mFirebaseProviderAuth.getmFirebaseAuth().getCurrentUser()!=null)
                {
                    abrirActivityInicio();
                }
            }
        mButtonLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!mTextInputEditTextEmail.getText().toString().isEmpty() &&
                        !mTextInputEditTextPass.getText().toString().isEmpty())
                {
                    int size = mTextInputEditTextEmail.getText().length();
                    String email_part = mTextInputEditTextEmail.getText().toString();
                    if (!email_part.contains("@vigitrack.com"))
                    {
                        /*Toasty.error(LoginActivity.this,mTextInputEditTextPass.getText().toString(),Toasty.LENGTH_LONG).show();*/
                        showProgressLogin();

                        mFirebaseProviderAuth.loginUp(mTextInputEditTextEmail.getText().toString()
                                ,mTextInputEditTextPass.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            abrirActivityInicio();
                                        }else
                                        {
                                            Toast.makeText(LoginActivity.this, task.toString()
                                                    , Toast.LENGTH_SHORT).show();
                                            hideProgreesLogin();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Toasty.error(LoginActivity.this,e.getMessage(),
                                        Toasty.LENGTH_LONG).show();
                            }
                        });
                    }else
                        {
                            Toasty.info(LoginActivity.this,"EMAIL NO CORRESPONDE !!",
                                    Toasty.LENGTH_LONG).show();
                            hideProgreesLogin();
                        }
                }else
                {
                    Toasty.info(LoginActivity.this,"EXISTEN DATOS VACIOS",
                            Toasty.LENGTH_LONG).show();
                    hideProgreesLogin();
                }
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CardView mCardFacebook;
                CardView mCardEmail;
                CardView mCardGoogle;
                View view1 = LayoutInflater.from(LoginActivity.this).inflate(R.layout.alert_tipos_register,null);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
                mBuilder.setView(view1);
                mCardFacebook = view1.findViewById(R.id.id_card_register_facebook);
                mCardEmail = view1.findViewById(R.id.id_card_register_email);
                mCardGoogle = view1.findViewById(R.id.id_card_register_google);

                mAlertDialogOPCRegister = mBuilder.create();
                mAlertDialogOPCRegister.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                mCardFacebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                       LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                                Arrays.asList("email","public_profile"));

                        mAlertDialogOPCRegister.dismiss();
                    }
                });
                mCardEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        mAlertDialogOPCRegister.dismiss();
                        openActivityRegisterClient();
                    }
                });
                mCardGoogle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        banCreateUser = true;
                        signInGoogle();
                    }
                });
                mAlertDialogOPCRegister.show();
            }
        });

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        Log.e("LoginResult",loginResult.toString());
                    }

                    @Override
                    public void onCancel()
                    {
                        Toasty.info(LoginActivity.this,"FACEBOOK CANCELADO"
                                ,Toasty.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        Toasty.error(LoginActivity.this, exception.getMessage(),Toasty.LENGTH_LONG)
                                .show();
                    }
                });

        mCircleImageViewGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                /**Login Google**/
                banCreateUser = false;
                signInGoogle();
            }
        });

    }

    private void signInGoogle() {
        // Launches the sign in flow, the result is returned in onActivityResult

        showAlerDialogFragmentLogin();

        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void showAlerDialogFragmentLogin()
    {
        SpotsDialog.Builder mBuilder = new SpotsDialog.Builder();
        mBuilder.setContext(LoginActivity.this)
                .setMessage("Accediendo");
        mAlertDialogAccediendoFragmentLogin = mBuilder.build();
        mAlertDialogAccediendoFragmentLogin.show();
    }


    private void showrequestPermissions()
    {
        if (ContextCompat.checkSelfPermission(LoginActivity.this
                ,Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this
                    ,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                /**Alert Solicita Permisos Por varias veces **/
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("PERMISOS");
                builder.setMessage("Otorgar permisos");
                builder.setPositiveButton("Solicitar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        ActivityCompat.requestPermissions(LoginActivity.this
                                ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                                ,REQUEST_FINE_LOCATION);
                    }
                });
                alertDialog_permission = builder.create();
                alertDialog_permission.show();
            }else
                {
                    ActivityCompat.requestPermissions(LoginActivity.this
                            ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                            ,REQUEST_FINE_LOCATION);
                }
        }
    }

    private void abrirActivityInicio()
    {
        updatetokenPhone();
        Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openActivityRegisterClient()
    {
        Intent intent = new Intent(LoginActivity.this, RegisterClientActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean checkPermission()
    {
        boolean ispermission=false;
        if (ContextCompat.checkSelfPermission(LoginActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
         ispermission=true;
        }
        return ispermission;
    }
    private void updatetokenPhone()
    {
        cProviderToken mProviderToken  = new cProviderToken();
        mProviderToken.realToken(mFirebaseProviderAuth);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //mCallbackManager.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case RC_SIGN_IN:
                hideProgreesFragment();
                showProgressLogin();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                task.addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task)
                    {
                        if (task.isSuccessful())
                        {
                            /** TODO BN **/
                            GoogleSignInAccount googleSignInAccount = task.getResult();
                            Log.i("Google","ID : "+googleSignInAccount.getId());
                            Log.i("Google","TOKEN : "+googleSignInAccount.getIdToken());
                            Log.i("Google",googleSignInAccount.getEmail());
                            Log.i("Google",googleSignInAccount.getDisplayName());
                            Log.i("Google","SIN CELULAR");
                            Log.i("Google", String.valueOf(googleSignInAccount.getPhotoUrl()));

                            firebaseAutRegisterhWithGoogle(googleSignInAccount);

                        }else
                            {
                                Toasty.error(LoginActivity.this,task.getException().getMessage(),Toasty.LENGTH_LONG)
                                        .show();
                                hideProgreesLogin();
                            }
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toasty.error(LoginActivity.this,e.getMessage(),Toasty.LENGTH_LONG).show();
                        hideProgreesLogin();
                    }
                });
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAutRegisterhWithGoogle(final GoogleSignInAccount googleSignInAccount)
    {
        AuthCredential mAuthCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(),null);

        mFirebaseProviderAuth.getmFirebaseAuth().signInWithCredential(mAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    /**  Activity Inicio **/
                    if (banCreateUser)
                    {
                        registerUserRealTime(googleSignInAccount,task.getResult().getUser().getUid());
                    }else
                        {
                            cClientProvider oCP = new cClientProvider();
                            oCP.getmDatabaseReference().child(task.getResult().getUser().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    if (snapshot.exists())
                                    {
                                        abrirActivityInicio();
                                    }else
                                        {
                                            registerUserRealTime(googleSignInAccount,task.getResult().getUser().getUid());
                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                }else
                    {
                        Toasty.error(LoginActivity.this,task.getException().getMessage(),
                                Toasty.LENGTH_LONG).show();
                        hideProgreesLogin();
                    }
            }
        });
    }

    private void registerUserRealTime(GoogleSignInAccount googleSignInAccount, final String uid)
    {
        cClientProvider mClientProvider = new cClientProvider();
        final cUser oU = new cUser();
        oU.setEmail(googleSignInAccount.getEmail());
        oU.setId_token_(uid);
        oU.setName(googleSignInAccount.getDisplayName());
        oU.setPhone("1234567890");
        oU.setPhoto_url(googleSignInAccount.getPhotoUrl().toString());
        mClientProvider.createNewClient(oU).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    /**REgister el Rating**/
                    registerRating(oU.getId_token_());
                    abrirActivityInicio();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toasty.error(LoginActivity.this,"No se pudo completar el registro Google",Toasty.LENGTH_LONG).show();
                hideProgreesLogin();
            }
        });
    }

    private void registerRating(String id_token_)
    {
        new cFirebaseProviderClientRating().createClientRating(id_token_,5)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            abrirActivityInicio();
                        }else
                            {
                                Toasty
                                        .info(LoginActivity.this,"No se pudo completar el registro",
                                                Toasty.LENGTH_LONG).show();
                            }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toasty.error(LoginActivity.this,e.getMessage().toString(),Toasty.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_FINE_LOCATION)
        {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                if (mFirebaseProviderAuth.getmFirebaseAuth().getCurrentUser()!=null)
                {
                    abrirActivityInicio();
                }
            }else
                {
                    showrequestPermissions();
                }
        }
    }

    @Override
    protected void onPostResume()
    {


        super.onPostResume();
    }

    public void showProgressLogin()
    {
        SpotsDialog.Builder builder = new SpotsDialog.Builder();
        builder.setContext(LoginActivity.this);
        builder.setMessage("Ingresando...");
        builder.setCancelable(false);
        mAlertDialogLogin = builder.build();
        mAlertDialogLogin.show();
    }
    public void  hideProgreesLogin()
    {
        if (mAlertDialogLogin!=null)
        {
            mAlertDialogLogin.cancel();
            mAlertDialogLogin.dismiss();
        }
    }
    @Override
    protected void onDestroy()
    {
        if (mAlertDialogLogin!=null){hideProgreesLogin();}
        if (mAlertDialogOPCRegister!=null)
        {
            mAlertDialogOPCRegister.cancel();
            mAlertDialogOPCRegister.hide();
        }
        hideProgreesFragment();
        super.onDestroy();
    }

    private void hideProgreesFragment()
    {
        if (mAlertDialogAccediendoFragmentLogin!=null)
        {
            mAlertDialogAccediendoFragmentLogin.cancel();
            mAlertDialogAccediendoFragmentLogin.hide();
        }
    }

    @Override
    public void onBackPressed()
    {
        return;
    }

}