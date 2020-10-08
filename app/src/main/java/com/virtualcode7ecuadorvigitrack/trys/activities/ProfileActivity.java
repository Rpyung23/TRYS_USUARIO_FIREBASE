package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.includes.cToolbar;
import com.virtualcode7ecuadorvigitrack.trys.models.cUser;
import com.virtualcode7ecuadorvigitrack.trys.provider.cClientProvider;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderAuth;
import com.virtualcode7ecuadorvigitrack.trys.provider.cProviderUploadPhoto;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity
{
    private static final int PICK_IMAGE = 896;
    private CircleImageView mCircleImageViewProfile;
    private TextInputEditText mTextInputEditTextEmail;
    private TextInputEditText mTextInputEditTextName;
    private TextInputEditText mTextInputEditTextPhone;
    private Button mButtonUpdate;

    private cFirebaseProviderAuth mFirebaseProviderAuth;
    private cClientProvider mClientProvider;

    private String img_url;
    private String email;
    private String phone;
    private String name;
    private boolean ban_foto=false;
    private cProviderUploadPhoto mProviderUploadPhoto;


    private AlertDialog mAlertDialogProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        new cToolbar().showToolbar(ProfileActivity.this,"PERFIL",true);

        img_url = getIntent().getStringExtra("img_url");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        name = getIntent().getStringExtra("name");

        mFirebaseProviderAuth = new cFirebaseProviderAuth();
        mClientProvider = new cClientProvider();
        mProviderUploadPhoto= new cProviderUploadPhoto(ProfileActivity.this);
        mCircleImageViewProfile = findViewById(R.id.id_circleview_profile);
        mTextInputEditTextEmail = findViewById(R.id.id_inputtext_email);
        mTextInputEditTextName = findViewById(R.id.id_inputtext_name);
        mTextInputEditTextPhone = findViewById(R.id.id_inputtext_phone);
        mButtonUpdate = findViewById(R.id.id_btn_update_profile);

        mTextInputEditTextEmail.setText(email);
        mTextInputEditTextName.setText(name);
        mTextInputEditTextPhone.setText(phone);


        Picasso.with(ProfileActivity.this).load(img_url)
                .placeholder(R.drawable.loading_photo)
                .error(R.drawable.error_image_load)
                .into(mCircleImageViewProfile);

        mCircleImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });


        mButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if ( !mTextInputEditTextPhone.getText().toString().equals(phone)
                        || !mTextInputEditTextName.getText().toString().equals(name)
                        || !mTextInputEditTextEmail.getText().toString().equals(email)
                        ||ban_foto)
                {
                    if (mTextInputEditTextPhone.getText().toString().length()>8
                            && mTextInputEditTextPhone.getText().toString().length()<11)
                    {
                        final cUser mUser = new cUser();
                        mUser.setId_token_(mFirebaseProviderAuth.getmFirebaseAuth().getCurrentUser().getUid());
                        mUser.setPhoto_url(img_url);
                        mUser.setName(mTextInputEditTextName.getText().toString());
                        mUser.setEmail(mTextInputEditTextEmail.getText().toString());
                        mUser.setPhone(mTextInputEditTextPhone.getText().toString());
                        crearProgressDialogUpdate();
                        if (ban_foto)
                        {
                            /**Subir foto primero**/
                            mProviderUploadPhoto.uploadPhoto(createByteImagen())
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            cancelarAlertProgress();
                                            Toasty.error(ProfileActivity.this,e.getMessage().toString()
                                                    ,Toasty.LENGTH_SHORT).show();
                                        }

                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    /** FOTO SUBIDA**/
                                    if (taskSnapshot!=null) {

                                        Toasty.success(ProfileActivity.this,"FOTO OK",Toasty.LENGTH_SHORT).show();
                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        Log.e("PATH",taskSnapshot.getMetadata().getPath());
                                        StorageReference storageRef = storage.getReference(taskSnapshot.getMetadata().getPath());
                                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri)
                                            {
                                                img_url = String.valueOf(uri);
                                                mUser.setPhoto_url(img_url);
                                                clientUpdate(mUser);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e)
                                            {
                                                cancelarAlertProgress();
                                                Toasty.error(ProfileActivity.this,e.getMessage().toString(),
                                                        Toasty.LENGTH_SHORT).show();
                                            }
                                        });

                                    }else
                                    {
                                        cancelarAlertProgress();
                                        Toast.makeText(ProfileActivity.this, "Error Foto",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }else
                        {
                            clientUpdate(mUser);
                        }
                    }else
                        {
                            Toasty.info(ProfileActivity.this,"Numero no valido",Toasty.LENGTH_LONG).show();
                        }
                }else
                    {
                        Toasty.info(ProfileActivity.this,"Datos originales,sin modificar",
                                Toasty.LENGTH_SHORT)
                                .show();
                    }
            }
        });

    }

    private void clientUpdate(cUser mUser)
    {
        mClientProvider.UpdateClient(mUser).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                cancelarAlertProgress();
                Toasty.success(ProfileActivity.this,"Datos Actualizados.."
                        ,Toasty.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                cancelarAlertProgress();
                Toasty.error(ProfileActivity.this,e.getMessage().toString(),
                        Toasty.LENGTH_LONG).show();
            }
        });
    }

    private void cancelarAlertProgress()
    {
        mAlertDialogProgress.cancel();
        mAlertDialogProgress.dismiss();
    }

    private void crearProgressDialogUpdate()
    {
        SpotsDialog.Builder builder = new SpotsDialog.Builder();
        builder.setContext(ProfileActivity.this);
        builder.setMessage("Actualizando!!");
        builder.setCancelable(false);
        mAlertDialogProgress = builder.build();
        mAlertDialogProgress.show();
    }

    private byte[] createByteImagen()
    {
        Bitmap bitmap = ((BitmapDrawable)mCircleImageViewProfile.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == PICK_IMAGE)
        {
            if (resultCode == RESULT_OK)
            {
                mCircleImageViewProfile.setImageURI(Uri.parse(data.getData().toString()));
                ban_foto=true;
            }else
                {
                    ban_foto=false;
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}