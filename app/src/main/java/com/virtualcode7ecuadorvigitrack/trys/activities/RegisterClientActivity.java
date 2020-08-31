package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.includes.cToolbar;
import com.virtualcode7ecuadorvigitrack.trys.models.cUser;
import com.virtualcode7ecuadorvigitrack.trys.provider.cClientProvider;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderAuth;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class RegisterClientActivity extends AppCompatActivity
{
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

    private cUser Ou;


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
        Ou = new cUser();

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
                        showAlerDialog();
                        Ou.setEmail(mTextInputEditTextEmail.getText().toString());
                        Ou.setName(mTextInputEditTextName.getText().toString());
                        Ou.setPhone(mTextInputEditTextPhone.getText().toString());
                        Ou.setPhoto_url("");
                        createClient(mTextInputEditTextEmail.getText().toString()
                                ,mTextInputEditTextPass.getText().toString());
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
                            dismissAlertDialog();
                            Toast.makeText(RegisterClientActivity.this, "OK", Toast.LENGTH_SHORT)
                                    .show();
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
}