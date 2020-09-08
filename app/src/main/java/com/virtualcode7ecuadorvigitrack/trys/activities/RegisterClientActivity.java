package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.includes.cToolbar;
import com.virtualcode7ecuadorvigitrack.trys.models.cUser;
import com.virtualcode7ecuadorvigitrack.trys.provider.cClientProvider;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderAuth;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderClientRanting;

import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class RegisterClientActivity extends AppCompatActivity
{
    private static final int PICK_IMAGE = 400;
    private Button mButtonCreateClient;


    private CircleImageView mCircleImageViewClient;

    private TextInputEditText mTextInputEditTextName;
    private TextInputEditText mTextInputEditTextPhone;
    private TextInputEditText mTextInputEditTextEmail;
    private TextInputEditText mTextInputEditTextPass;

    private LinearLayoutCompat linearLayoutCompat;

    private Snackbar snackbar;

    private AlertDialog alertDialog;

    private cFirebaseProviderAuth mFirebaseProviderAuth;

    private cClientProvider mClientProvider;


    private cFirebaseProviderClientRanting mFirebaseProviderClientRanting;

    private cUser Ou;


    /** Verification Phone **/

    private EditText mEditText1;
    private EditText mEditText2;
    private EditText mEditText3;
    private EditText mEditText4;
    private EditText mEditText5;
    private EditText mEditText6;
    private Button mButtonVeri_Cancel;
    private TextView mTextViewCountTime;
    private TextView mTextViewLabelTime;
    private TextView mTextviewNewIntento;



    private  int count_time = 60;

    private AlertDialog mAlertDialogVerifiPhone;


    private String code_Firebase_;

    private Handler mHandlerCountTime;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run()
        {
            if (mTextViewCountTime.getText().toString().equals("00"))
            {
                /** Time Out**/
                mHandlerCountTime.removeCallbacks(this);
                mTextviewNewIntento.setEnabled(true);
                mTextviewNewIntento.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }else
                {
                    if (count_time>9)
                    {
                        mTextViewCountTime.setText(String.valueOf(count_time));
                    }else
                        {
                            mTextViewCountTime.setText("0"+String.valueOf(count_time));
                        }
                    count_time = count_time - 1;
                    mHandlerCountTime.postDelayed(this,1000);
                }
        }
    };


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);

        linearLayoutCompat = findViewById(R.id.linear_layout_);

        new cToolbar().showToolbar(this,getString(R.string.toolbar_regitro_client)
                ,true);
        mButtonCreateClient = findViewById(R.id.id_button_create_client);
        mCircleImageViewClient = findViewById(R.id.id_circleview_photo_client);
        mTextInputEditTextEmail = findViewById(R.id.id_textinput_email);
        mTextInputEditTextName = findViewById(R.id.id_textinput_name);
        mTextInputEditTextPass = findViewById(R.id.id_textinput_password);
        mTextInputEditTextPhone = findViewById(R.id.id_textinput_phone);


        mFirebaseProviderAuth = new cFirebaseProviderAuth();
        mClientProvider = new cClientProvider();

        mFirebaseProviderClientRanting = new cFirebaseProviderClientRanting();

        Ou = new cUser();

        mHandlerCountTime = new Handler();

        mCircleImageViewClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                /*Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);*/
            }
        });

        mButtonCreateClient.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!mTextInputEditTextPass.getText().toString().isEmpty() &&
                        mTextInputEditTextPass.getText().toString().length()>= 6)
                {
                    if (!mTextInputEditTextPass.getText().toString().isEmpty()  &&
                            !mTextInputEditTextEmail.getText().toString().isEmpty() &&
                            !mTextInputEditTextName.getText().toString().isEmpty() &&
                            !mTextInputEditTextPhone.getText().toString().isEmpty())
                    {
                        Ou.setEmail(mTextInputEditTextEmail.getText().toString());
                        Ou.setName(mTextInputEditTextName.getText().toString());
                        Ou.setPhone(mTextInputEditTextPhone.getText().toString());
                        Ou.setPhoto_url("");
                        showAlertDialogVeriPhone();






                        /*createClient(mTextInputEditTextEmail.getText().toString()
                                ,mTextInputEditTextPass.getText().toString());*/
                    }else
                        {
                            showSnackbar("EXISTEN DATOS VACIOS!!");
                        }
                }else
                    {
                        showSnackbar("PASSWORD NO VALIDO !!!");
                    }
            }
        });
    }

    private void showAlertDialogVeriPhone()
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+593"+mTextInputEditTextPhone.getText().toString(),
                60, TimeUnit.SECONDS, RegisterClientActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
                {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
                    {
                        /**  Complete Verification Phone **/
                        /** MSM LLEGO PARAR TIMECOUNT**/
                        mHandlerCountTime.removeCallbacks(mRunnable);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e)
                    {
                        /** Verification Fail **/
                        Log.e("FAIL",e.getMessage());
                    }
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken)
                    {
                        /** CodeSent **/
                        code_Firebase_ = s;
                        mHandlerCountTime.postDelayed(mRunnable,1000);
                    }
                });

        /** MUESTRA EL ALERT**/
        View view_veri_phone = LayoutInflater.from(RegisterClientActivity.this)
                .inflate(R.layout.phone_verification,null,false);

        mButtonVeri_Cancel = findViewById(R.id.id_btn_verificar_cancelar);
        mTextviewNewIntento = findViewById(R.id.id_textview_intentar_nuevamente);
        mTextViewCountTime = findViewById(R.id.id_textview_time);

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterClientActivity.this);
        builder.setView(view_veri_phone);
        builder.setCancelable(false);
        mAlertDialogVerifiPhone = builder.create();
        mAlertDialogVerifiPhone.show();
    }

    private void createClient(String email, String pass)
    {
        mFirebaseProviderAuth.createUser(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    Ou.setId_token_(mFirebaseProviderAuth.getmFirebaseAuth().getUid());
                    mClientProvider.createNewClient(Ou).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            createRanting();
                        }
                    });
                }else
                    {
                        dismissAlertDialog();
                        Toast.makeText(RegisterClientActivity.this, "FAIL!", Toast.LENGTH_SHORT)
                                .show();
                    }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                dismissAlertDialog();
                Toast.makeText(RegisterClientActivity.this, "Exception : "+e.getMessage()
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createRanting()
    {
        mFirebaseProviderClientRanting.createClientRating(Ou.getId_token_())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            dismissAlertDialog();
                            Toast.makeText(RegisterClientActivity.this, "OK", Toast.LENGTH_SHORT)
                                    .show();
                            Intent intent = new Intent(RegisterClientActivity.this
                                    ,InicioActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void showSnackbar(String mensaje)
    {
        snackbar = Snackbar.make(linearLayoutCompat,mensaje,Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void showAlerDialog()
    {
        alertDialog = new SpotsDialog.Builder()
                .setContext(RegisterClientActivity.this)
                .setMessage("REGISTRANDO...")
                .setCancelable(false)
                .build();
        alertDialog.show();
    }

    private void dismissAlertDialog()
    {
        alertDialog.cancel();
        alertDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == PICK_IMAGE)
        {
            if (resultCode == RESULT_OK)
            {
                mCircleImageViewClient.setImageURI(Uri.parse(data.getData().toString()));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}