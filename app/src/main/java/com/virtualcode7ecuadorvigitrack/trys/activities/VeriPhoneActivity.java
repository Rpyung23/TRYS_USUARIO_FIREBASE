package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.virtualcode7ecuadorvigitrack.trys.R;

import es.dmoral.toasty.Toasty;

public class VeriPhoneActivity extends AppCompatActivity
{
    private String name;
    private EditText mEditTextPhone;
    private Button mButtonSiguiente;
    private TextView mTextViewWelcome;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veri_phone);
        name = getIntent().getStringExtra("name");
        mEditTextPhone = findViewById(R.id.id_edit_phone_new);
        mButtonSiguiente = findViewById(R.id.id_button_next_new_phone);
        mTextViewWelcome = findViewById(R.id.id_textview_name_client_new_phone);
        mTextViewWelcome.setText("Bienvenido "+name);
        mButtonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (!mEditTextPhone.getText().toString().isEmpty())
                {
                    if (mEditTextPhone.getText().toString().length()>8 &&
                            mEditTextPhone.getText().toString().length()<11)
                    {
                        /****/
                        finish();
                        Intent intent = new Intent(VeriPhoneActivity.this,CompleteCodePhoneActivity.class);
                        intent.putExtra("phone",mEditTextPhone.getText().toString());
                        startActivity(intent);
                    }else
                        {
                            Toasty.info(VeriPhoneActivity.this,"Número telefonico no valido",
                                    Toasty.LENGTH_LONG).show();
                        }
                }else
                    {
                        Toasty.info(VeriPhoneActivity.this,"Por favor ingresar un número telefonico",
                                Toasty.LENGTH_LONG).show();
                    }
            }
        });
    }
}