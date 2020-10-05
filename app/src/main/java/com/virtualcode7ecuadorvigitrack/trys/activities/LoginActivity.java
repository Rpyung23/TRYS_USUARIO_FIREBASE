package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderAuth;
import com.virtualcode7ecuadorvigitrack.trys.provider.cProviderToken;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity
{
    private Button mButtonRegister;

    private Button mButtonLogin;

    private TextInputEditText mTextInputEditTextEmail;
    private TextInputEditText mTextInputEditTextPass;

    private cFirebaseProviderAuth mFirebaseProviderAuth;


    private static int REQUEST_FINE_LOCATION=100;

    private AlertDialog alertDialog_permission;

    private android.app.AlertDialog mAlertDialogLogin;



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

        mFirebaseProviderAuth = new cFirebaseProviderAuth();

        mButtonRegister = findViewById(R.id.id_registro_client);
        mButtonLogin = findViewById(R.id.id_login_client);
        mTextInputEditTextEmail = findViewById(R.id.id_textinput_mail);
        mTextInputEditTextPass = findViewById(R.id.id_textinput_password);



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
                                        Toast.makeText(LoginActivity.this, "Datos no validos !!!"
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else
                {
                    Toast.makeText(LoginActivity.this, "Existen Datos vacios!!"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openActivityRegisterClient();
            }
        });


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
        showProgressLogin();
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
        mAlertDialogLogin.cancel();
        mAlertDialogLogin.dismiss();
    }
    @Override
    protected void onDestroy()
    {
        if (mAlertDialogLogin!=null){hideProgreesLogin();}
        super.onDestroy();
    }
}