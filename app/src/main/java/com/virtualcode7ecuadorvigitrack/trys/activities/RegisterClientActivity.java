package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.broadcast.cBroadcastMsmAuthPhoneFire;
import com.virtualcode7ecuadorvigitrack.trys.includes.cToolbar;
import com.virtualcode7ecuadorvigitrack.trys.models.cUser;
import com.virtualcode7ecuadorvigitrack.trys.provider.cClientProvider;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderAuth;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderClientRating;
import com.virtualcode7ecuadorvigitrack.trys.provider.cProviderUploadPhoto;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class RegisterClientActivity extends AppCompatActivity implements View.OnClickListener
{

    private static final int PICK_IMAGE = 400;
    private static final String SMS_ACTION_RECIVED = "android.provider.Telephony.SMS_RECEIVED";
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


    private cFirebaseProviderClientRating mFirebaseProviderClientRanting;

    private cUser Ou;
    private String url_photo_init;
    private cProviderUploadPhoto mProviderUploadPhoto;
    private AlertDialog mAlertDialogProgress;

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

    private AlertDialog mAlertDialogNoVerificationPhone;

    private AlertDialog alertDialogSMSRECIVED;

    private final static  int REQUEST_CODE_READ_SMS =450;

    private  int count_time = 60;

    private AlertDialog mAlertDialogVerifiPhone;

    cBroadcastMsmAuthPhoneFire mBroadcastMsmAuthPhoneFire;

    private String code_Firebase_="null";

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

        callBroadCastReciverSms();

        mButtonCreateClient = findViewById(R.id.id_button_create_client);
        mCircleImageViewClient = findViewById(R.id.id_circleview_photo_client);
        mTextInputEditTextEmail = findViewById(R.id.id_textinput_email);
        mTextInputEditTextName = findViewById(R.id.id_textinput_name);
        mTextInputEditTextPass = findViewById(R.id.id_textinput_password);
        mTextInputEditTextPhone = findViewById(R.id.id_textinput_phone);
        mBroadcastMsmAuthPhoneFire = new cBroadcastMsmAuthPhoneFire();

        mFirebaseProviderAuth = new cFirebaseProviderAuth();
        mClientProvider = new cClientProvider();
        Ou = new cUser();
        mFirebaseProviderClientRanting = new cFirebaseProviderClientRating();

        if (ContextCompat.checkSelfPermission(RegisterClientActivity.this
                , Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED)
        {
           showPermissionReadSMS();
        }

        mHandlerCountTime = new Handler();

        mCircleImageViewClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
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


    private void callBroadCastReciverSms()
    {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(SMS_ACTION_RECIVED);
        registerReceiver(mBroadcastMsmAuthPhoneFire,intentFilter);
    }


    private void showPermissionReadSMS()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterClientActivity.this
                ,Manifest.permission.RECEIVE_SMS))
        {
            /**ALERT VOLVER A PEDIR PERMISOS**/
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterClientActivity.this);
            builder.setTitle("Solicitud de Permisos");
            builder.setMessage("Porfavor otorgar permisos.");
            builder.setCancelable(false);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    alertDialogSMSRECIVED.cancel();
                    alertDialogSMSRECIVED.dismiss();
                    //showPermissionReadSMS();
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                    {
                        ActivityCompat.requestPermissions(RegisterClientActivity.this
                                ,new String[]{Manifest.permission.RECEIVE_SMS},REQUEST_CODE_READ_SMS);
                    }
                }
            });
            alertDialogSMSRECIVED = builder.create();
            alertDialogSMSRECIVED.show();
        }else
            {
                ActivityCompat.requestPermissions(RegisterClientActivity.this,
                        new String[]{Manifest.permission.RECEIVE_SMS},REQUEST_CODE_READ_SMS);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode==REQUEST_CODE_READ_SMS)
        {
            if (grantResults.length>0 && grantResults[0] == (PackageManager.PERMISSION_GRANTED))
            {
                Toast.makeText(RegisterClientActivity.this, "Permisos Ok!!!", Toast.LENGTH_SHORT).show();
            }else
                {
                    showPermissionReadSMS();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showAlertDialogVeriPhone()
    {
        mHandlerCountTime.postDelayed(mRunnable,1000);
        /** MUESTRA EL ALERT**/
        View view_veri_phone = LayoutInflater.from(RegisterClientActivity.this)
                .inflate(R.layout.phone_verification,null,false);

        mProviderUploadPhoto = new cProviderUploadPhoto(RegisterClientActivity.this);

        mButtonVeri_Cancel = view_veri_phone.findViewById(R.id.id_btn_verificar_cancelar);
        mTextviewNewIntento = view_veri_phone.findViewById(R.id.id_textview_intentar_nuevamente);
        mTextViewCountTime = view_veri_phone.findViewById(R.id.id_textview_time);


        mEditText1 = view_veri_phone.findViewById(R.id.id_text_num_1);
        mEditText2 = view_veri_phone.findViewById(R.id.id_text_num_2);
        mEditText3 = view_veri_phone.findViewById(R.id.id_text_num_3);
        mEditText4 = view_veri_phone.findViewById(R.id.id_text_num_4);
        mEditText5 = view_veri_phone.findViewById(R.id.id_text_num_5);
        mEditText6 = view_veri_phone.findViewById(R.id.id_text_num_6);

        mEditText1.setText("");
        mEditText2.setText("");
        mEditText3.setText("");
        mEditText4.setText("");
        mEditText5.setText("");
        mEditText6.setText("");


        mButtonVeri_Cancel = view_veri_phone.findViewById(R.id.id_btn_verificar_cancelar);


        mBroadcastMsmAuthPhoneFire.setmEditTextDijit1(mEditText1);
        mBroadcastMsmAuthPhoneFire.setmEditTextDijit2(mEditText2);
        mBroadcastMsmAuthPhoneFire.setmEditTextDijit3(mEditText3);
        mBroadcastMsmAuthPhoneFire.setmEditTextDijit4(mEditText4);
        mBroadcastMsmAuthPhoneFire.setmEditTextDijit5(mEditText5);
        mBroadcastMsmAuthPhoneFire.setmEditTextDijit6(mEditText6);

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterClientActivity.this);
        builder.setView(view_veri_phone);
        builder.setCancelable(false);
        mAlertDialogVerifiPhone = builder.create();
        mAlertDialogVerifiPhone.show();







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
                        //Log.e("CODE",code_Firebase_);
                        code_Firebase_ = phoneAuthCredential.getSmsCode();
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

        mButtonVeri_Cancel.setOnClickListener(this);
    }

    private void createClient(final String email, final String pass)
    {
        showAlerDialog();
        /**
         * Upload Imagen
         * **/


        mProviderUploadPhoto.uploadPhoto(createByteImagen()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toasty.error(RegisterClientActivity.this,e.getMessage().toString(),Toasty.LENGTH_SHORT).show();
                cancelarAlertProgress();
            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                /** FOTO SUBIDA**/
                if (taskSnapshot!=null) {
                    //
                    // Driver --->Carpeta
                    // %2F
                    //?alt=media&token=5b88a844-15af-4ce8-b55f-ff392cf11b3c
                    Toasty.success(RegisterClientActivity.this,"FOTO OK",Toasty.LENGTH_SHORT).show();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    Log.e("PATH",taskSnapshot.getMetadata().getPath());
                    StorageReference storageRef = storage.getReference(taskSnapshot.getMetadata().getPath());
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri)
                        {
                            url_photo_init = String.valueOf(uri);
                            createClientFirebase(email,pass);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            cancelarAlertProgress();
                            Toasty.error(RegisterClientActivity.this,e.getMessage().toString(),
                                    Toasty.LENGTH_SHORT).show();
                        }
                    });

                    /*url_photo_init = "https://firebasestorage.googleapis.com/v0/b/trys-19c9e.appspot.com/o/Driver%2F"+
                            taskSnapshot.getMetadata().getPath()+"?alt=media&token="+taskSnapshot.getStorage().get;*/
                }else
                {
                    cancelarAlertProgress();
                    Toast.makeText(RegisterClientActivity.this, "Error Foto", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void createClientFirebase(String email,String pass)
    {
        mFirebaseProviderAuth.createUser(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    Ou.setId_token_(mFirebaseProviderAuth.getmFirebaseAuth().getUid());
                    Ou.setPhoto_url(url_photo_init);
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
        mFirebaseProviderClientRanting.createClientRating(Ou.getId_token_(),5)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            dismissAlertDialog();
                            Toast.makeText(RegisterClientActivity.this, "Registro Completo", Toast.LENGTH_SHORT)
                                    .show();
                            getApplicationContext().unregisterReceiver(mBroadcastMsmAuthPhoneFire);
                            Intent intent = new Intent(RegisterClientActivity.this
                                    ,InicioActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
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


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.id_btn_verificar_cancelar:

                if (code_Firebase_.equals(mEditText1.getText().toString()+mEditText2.getText().toString()
                        +mEditText3.getText().toString()+mEditText4.getText().toString()
                        +mEditText5.getText().toString()+mEditText6.getText().toString()))
                {
                    createClient(mTextInputEditTextEmail.getText().toString(),mTextInputEditTextPass.getText().toString());
                }else
                    {
                        createAlertDialogErrorVerificationPhone();
                    }
                break;
        }
    }

    private void createAlertDialogErrorVerificationPhone()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterClientActivity.this);
        builder.setTitle("Error de verificación");
        builder.setMessage("Desea continuar sin verificar su número teléfonico");
        builder.setCancelable(false);
        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                mAlertDialogNoVerificationPhone.cancel();
                createClient(mTextInputEditTextEmail.getText().toString()
                        ,mTextInputEditTextPass.getText().toString());
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                mAlertDialogNoVerificationPhone.cancel();
                finish();
            }
        });
        mAlertDialogNoVerificationPhone = builder.create();
        mAlertDialogNoVerificationPhone.show();
    }

    private void cancelarAlertProgress()
    {
        alertDialog.cancel();
        alertDialog.dismiss();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        if (mHandlerCountTime!=null)
        {
            mHandlerCountTime.removeCallbacks(mRunnable);
        }
        super.onDestroy();
    }
    private byte[] createByteImagen()
    {
        Bitmap bitmap = ((BitmapDrawable)mCircleImageViewClient.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }
}