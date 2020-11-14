package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.broadcast.cBroadcastMsmAuthPhoneFire;
import com.virtualcode7ecuadorvigitrack.trys.models.cUser;
import com.virtualcode7ecuadorvigitrack.trys.provider.cClientProvider;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderAuth;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderClientRating;
import com.virtualcode7ecuadorvigitrack.trys.provider.cProviderUploadPhoto;

import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class CompleteCodePhoneActivity extends AppCompatActivity
{
    private String phone;
    private TextView mTextViewCountTime;
    private TextView mTextviewNewIntento;
    private Button mButtonConfirmaPhone;
    private  int count_time = 60;
    private String code_sms = "";
    private AlertDialog mAlertDialog;
    private EditText mEditText1;
    private EditText mEditText2;
    private EditText mEditText3;
    private EditText mEditText4;
    private EditText mEditText5;
    private EditText mEditText6;

    private Handler mHandlerCountTime;
    private cFirebaseProviderAuth mFirebaseProviderAuth;
    private cClientProvider mClientProvider;


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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_code_phone);
        phone = getIntent().getStringExtra("phone");
        mFirebaseProviderAuth = new cFirebaseProviderAuth();
        mClientProvider = new cClientProvider();
        mTextviewNewIntento = findViewById(R.id.id_textview_intentar_nuevamente);
        mTextViewCountTime = findViewById(R.id.id_textview_time);
        mButtonConfirmaPhone = findViewById(R.id.id_btn_confirmar_sms);


        mEditText1 = findViewById(R.id.id_text_num_1);
        mEditText2 = findViewById(R.id.id_text_num_2);
        mEditText3 = findViewById(R.id.id_text_num_3);
        mEditText4 = findViewById(R.id.id_text_num_4);
        mEditText5 = findViewById(R.id.id_text_num_5);
        mEditText6 = findViewById(R.id.id_text_num_6);

        vaciarTextView();


        mHandlerCountTime = new Handler();

        mHandlerCountTime.postDelayed(mRunnable,1000);

        mTextviewNewIntento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                vaciarTextView();
                startCodeVerification();
            }
        });

        mButtonConfirmaPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (!mEditText1.getText().toString().isEmpty() ||
                        !mEditText2.getText().toString().isEmpty() ||
                        !mEditText3.getText().toString().isEmpty() ||
                        !mEditText4.getText().toString().isEmpty() ||
                        !mEditText5.getText().toString().isEmpty() ||
                        !mEditText6.getText().toString().isEmpty())
                {
                    String code = mEditText1.getText().toString()+mEditText2.getText().toString()
                            +mEditText3.getText().toString()+mEditText4.getText().toString()
                            +mEditText5.getText().toString()+mEditText6.getText().toString();
                    if (code.equals(code_sms))
                    {
                        /**UPDATE PHONE FIREBASE**/
                        updatePhoneFirebase();
                    }else
                        {
                            Toasty.error(CompleteCodePhoneActivity.this,"CÓDIGO NO VÁLIDO",Toasty.LENGTH_LONG).show();
                        }
                }else
                    {
                        Toasty.error(CompleteCodePhoneActivity.this,"Existen Datos vacios",Toasty.LENGTH_SHORT).show();
                    }
            }
        });


        startCodeVerification();
    }

    private void vaciarTextView()
    {
        mEditText1.setText("");
        mEditText2.setText("");
        mEditText3.setText("");
        mEditText4.setText("");
        mEditText5.setText("");
        mEditText6.setText("");
    }

    private void startCodeVerification()
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+593"+phone,
                60, TimeUnit.SECONDS, CompleteCodePhoneActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
                {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
                    {
                        /**  Complete Verification Phone **/
                        /** MSM LLEGO PARAR TIMECOUNT**/
                        //Log.e("CODE",code_Firebase_);
                        /*Toasty.info(CompleteCodePhoneActivity.this,phoneAuthCredential.getSmsCode().toString(),
                                Toasty.LENGTH_LONG).show();*/
                        code_sms = phoneAuthCredential.getSmsCode().toString();
                        mHandlerCountTime.removeCallbacks(mRunnable);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e)
                    {
                        /** Verification Fail **/
                        Log.e("FAIL",e.getMessage());
                    }
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull
                            PhoneAuthProvider.ForceResendingToken forceResendingToken)
                    {
                        /** CodeSent **/
                        Log.e("CODESENT",s);
                    }
                });
    }

    private void updatePhoneFirebase()
    {
        mAlertDialog =  new SpotsDialog.Builder()
                .setContext(CompleteCodePhoneActivity.this)
                .setMessage("ACTUALIZANDO")
                .setCancelable(false)
                .build();
        mAlertDialog.show();


        mClientProvider.UpdatePhone(mFirebaseProviderAuth.getmFirebaseAuth().getCurrentUser()
                .getUid(),phone).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    mAlertDialog.cancel();
                    mAlertDialog.dismiss();
                    Toasty.success(CompleteCodePhoneActivity.this,"DATOS ACTUALIZADOS",
                            Toasty.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                mAlertDialog.cancel();
                mAlertDialog.dismiss();
                Toasty.error(CompleteCodePhoneActivity.this,e.getMessage().toString(),Toasty.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandlerCountTime!=null)
        {
            mHandlerCountTime.removeCallbacks(mRunnable);
            count_time = 60;
        }
    }
}