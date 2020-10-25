package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.adapter.adapterChats;
import com.virtualcode7ecuadorvigitrack.trys.includes.cToolbar;
import com.virtualcode7ecuadorvigitrack.trys.models.cMessaging;
import com.virtualcode7ecuadorvigitrack.trys.provider.cClientProvider;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseMessaging;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProfileDriver;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderAuth;
import com.virtualcode7ecuadorvigitrack.trys.provider.cProviderSharedUiCondutor;
import com.virtualcode7ecuadorvigitrack.trys.provider.cProviderToken;
import com.virtualcode7ecuadorvigitrack.trys.volley.cVolleyNotificationSendMessaging;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener
{
    private static final int PICK_IMAGE_CHAT = 989;


    private cFirebaseMessaging mFirebaseMessaging;

    private cFirebaseProviderAuth mFirebaseProviderAuth;


    private ImageButton mButtonSend;
    private ImageButton mButtonCamera_Galery;
    private EditText mEditTextMessaging;

    private  boolean banderaSound =false;

    private RecyclerView mRecyclerViewChats;
    private ArrayList<cMessaging> mMessagingArrayList;

    private adapterChats mAdapterChats;

    private ChildEventListener mChildEventListener;

    private MediaPlayer mMediaPlayer;

    private cFirebaseProfileDriver mFirebaseProfileDriver;

    private TextView mTextViewNameProfile;
    private TextView mTextViewTelefonoProfile;


    private String url_photo;
    private String Url_photo_IntentDriver;

    private String id_token_phone;
    private String id_driver;
    private String phone_driver;
    private String name_driver;

    private cVolleyNotificationSendMessaging mVolleyNotificationSendMessaging;
    private cProviderToken mProviderToken;
    private Toolbar mToolbarChats;

    private cProviderSharedUiCondutor mProviderSharedUiCondutor;

    private CircleImageView mCircleImageViewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        mToolbarChats = findViewById(R.id.id_toolbar_chat);
        setSupportActionBar(mToolbarChats);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProviderToken = new cProviderToken();

        mFirebaseMessaging = new cFirebaseMessaging();

        mProviderSharedUiCondutor = new cProviderSharedUiCondutor(MessagingActivity.this);

        Url_photo_IntentDriver = getIntent().getStringExtra("photo_driver");
        id_driver = mProviderSharedUiCondutor.leerSharedPreferences();
        phone_driver = getIntent().getStringExtra("phone_driver");;
        name_driver = getIntent().getStringExtra("name_driver");


        mProviderToken.readToken(id_driver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    id_token_phone = snapshot.child("token").getValue().toString();
                }else
                {
                    id_token_phone="";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                id_token_phone="";
            }
        });

        mVolleyNotificationSendMessaging = new cVolleyNotificationSendMessaging(MessagingActivity.this);

        mCircleImageViewProfile = findViewById(R.id.id_circle_profile_toolbar);

        mMediaPlayer = new MediaPlayer().create(MessagingActivity.this,R.raw.sound_msm_chat_activo);

        mButtonSend = findViewById(R.id.id_botton_send_chats);
        mButtonCamera_Galery = findViewById(R.id.id_camera_galery_chats);
        mEditTextMessaging = findViewById(R.id.id_edittext_chats);
        mRecyclerViewChats = findViewById(R.id.id_recyclerview_chats);
        mTextViewNameProfile = findViewById(R.id.id_textview_name_client);
        mTextViewTelefonoProfile = findViewById(R.id.id_textview_name_phone);


        mTextViewNameProfile.setText(name_driver);
        mTextViewTelefonoProfile.setText(phone_driver);

        if(mMessagingArrayList==null)
        {
            mMessagingArrayList = new ArrayList<>();
        }else
        {
            mMessagingArrayList.clear();
        }

        banderaSound =true;

        mFirebaseProviderAuth = new cFirebaseProviderAuth();


        Picasso.with(MessagingActivity.this).load(Url_photo_IntentDriver)
                .placeholder(R.drawable.loading_photo)
                .error(R.drawable.error_image_load)
                .into(mCircleImageViewProfile);

        mFirebaseProfileDriver = new cFirebaseProfileDriver();

        mAdapterChats = new adapterChats(mMessagingArrayList,MessagingActivity.this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessagingActivity.this);
        mRecyclerViewChats.setLayoutManager(linearLayoutManager);
        mRecyclerViewChats.setAdapter(mAdapterChats);


        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                view.startAnimation(AnimationUtils.loadAnimation(MessagingActivity.this, R.anim.animate_button_chtas));
                if(!mEditTextMessaging.getText().toString().isEmpty())
                {
                    sendMessaging(mEditTextMessaging.getText().toString(),"text");
                }else
                {
                    /** Sniper VACIOs**/
                    Snackbar snackbar = Snackbar.make(mRecyclerViewChats,"Texto vacio"
                            ,Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });


        mFirebaseProfileDriver.readProfileDriver(mFirebaseProviderAuth.getmFirebaseAuth().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        /*
                        if (snapshot.exists())
                        {

                        }else
                        {
                            Log.e("PHOTO","no exists");
                        }*/
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                    }
                });


        mButtonCamera_Galery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                view.startAnimation(AnimationUtils.loadAnimation(MessagingActivity.this, R.anim.animate_button_chtas));
                abrirIntentGallery();
            }
        });


    }

    @Override
    protected void onStart()
    {
        //mMessagingArrayList = new ArrayList<>();
        super.onStart();
    }

    private void abrirIntentGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        //startActivityForResult(intent,PICK_IMAGE_CHAT);
        startActivityForResult(intent,PICK_IMAGE_CHAT);
    }

    private void sendMessaging(final String msm,final String type_msm)
    {
        banderaSound = false;
        mFirebaseMessaging.getmDatabaseReference().
                child(id_driver)
                .push().setValue(creteHashMapMessaging(msm,type_msm)
                , new DatabaseReference.CompletionListener()
                {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref)
                    {
                        /**ENVIO EXITOSO**/
                        if (error == null || error.getMessage().isEmpty())
                        {
                            if(!id_token_phone.equals(""))
                            {
                                if (type_msm.equals("text"))
                                {
                                    mVolleyNotificationSendMessaging.sendNotificationVolley("Nuevo Mensaje",msm,id_token_phone);
                                }else if (type_msm.equals("img"))
                                {
                                    mVolleyNotificationSendMessaging.sendNotificationVolley("Nuevo Mensaje","Imagen recibida",id_token_phone);
                                }
                            }
                            mEditTextMessaging.setText("");
                        }else
                        {
                            Snackbar snackbar = Snackbar.make(mRecyclerViewChats,"No se puedo enviar el mensaje"
                                    ,Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
    }

    private Object creteHashMapMessaging(final String msm,String type_msm)
    {
        HashMap<Object,Object> hashMap = new HashMap<>();
        hashMap.put("id_reciver_send",mFirebaseProviderAuth.getmFirebaseAuth().getUid());
        hashMap.put("messaging",msm);
        hashMap.put("type_msm",type_msm);
        return  hashMap;
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState)
    {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onPostResume()
    {
        readMessaging();
        super.onPostResume();
    }

    private void readMessaging()
    {
/*
        mDatabaseReference.getDatabase().getReference().child(mFirebaseProviderAuth.getmFirebaseAuth().getCurrentUser()
                .getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotMess)
            {
                if (dataSnapshotMess.exists())
                {
                    cMessaging oM = new cMessaging();

                    oM.setId_reciver_send(dataSnapshotMess.child("id_reciver_send")
                            .getValue().toString());

                    if (oM.getId_reciver_send().equals(mFirebaseProviderAuth.getmFirebaseAuth()
                            .getUid()))
                    {
                        oM.setUrl_perfil_driver(url_photo);
                    }else
                    {
                        oM.setUrl_perfil_client(Url_photo_IntentClient);
                    }

                    oM.setMessaging(dataSnapshotMess.child("messaging")
                            .getValue().toString());

                    if (dataSnapshotMess.child("type_msm").exists())

                    {
                        oM.setType_msm(dataSnapshotMess.child("type_msm").getValue().toString());
                    }else

                    {
                        oM.setType_msm("text");
                    }

                    mMessagingArrayList.add(oM);
                    mAdapterChats.notifyDataSetChanged();
                    if (!oM.getId_reciver_send().equals(mFirebaseProviderAuth.getmFirebaseAuth()
                            .getCurrentUser().getUid()))
                    {
                        if (mMediaPlayer.isPlaying())
                        {
                            mMediaPlayer.pause();
                        }
                        mMediaPlayer.start();
                    }

                    mRecyclerViewChats.smoothScrollToPosition(mAdapterChats.getItemCount()-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
*/


        mChildEventListener = mFirebaseMessaging.getmDatabaseReference()
                .child(id_driver).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot mDataSnapshot, @Nullable String previousChildName)
                    {
                        Log.e("KEY q",mDataSnapshot.getKey().toString());

                        //llenarArraysListMessaging(mDataSnapshot);

                        if (mMessagingArrayList.size()!=0)
                        {
                            boolean bandera = false;
                            int cont = 0;
                            while (cont<mMessagingArrayList.size())
                            {
                                if (mMessagingArrayList.get(cont).getUid_msm().equals(mDataSnapshot.getKey().toString()))
                                {
                                    bandera = true;
                                }
                                cont++;
                            }

                            if (!bandera)
                            {
                                llenarArraysListMessaging(mDataSnapshot);
                            }
                        }else
                        {
                            llenarArraysListMessaging(mDataSnapshot);
                        }
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                    {
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot)
                    {
                    }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                    {
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                    }
                });

    }

    private void llenarArraysListMessaging(DataSnapshot mDataSnapshot)
    {
        Log.i("KEY",mDataSnapshot.getKey().toString());
       // Toast.makeText(this, "KEY ;: "+mDataSnapshot.getKey().toLowerCase(), Toast.LENGTH_SHORT).show();

        /** Add Messaging en ArrayList**/
        cMessaging oM = new cMessaging();
        oM.setUid_msm(mDataSnapshot.getKey().toString());

        oM.setId_reciver_send(mDataSnapshot.child("id_reciver_send")
                .getValue().toString());

        if (oM.getId_reciver_send().equals(mFirebaseProviderAuth.getmFirebaseAuth()
                .getUid()))
        {
            oM.setUrl_perfil_driver(url_photo);
        }else
        {
            oM.setUrl_perfil_client(Url_photo_IntentDriver);
        }

        oM.setMessaging(mDataSnapshot.child("messaging")
                .getValue().toString());

        if (mDataSnapshot.child("type_msm").exists())

        {
            oM.setType_msm(mDataSnapshot.child("type_msm").getValue().toString());
        }else

        {
            oM.setType_msm("text");
        }

        mMessagingArrayList.add(oM);

        mAdapterChats.notifyDataSetChanged();
        if (!oM.getId_reciver_send().equals(mFirebaseProviderAuth.getmFirebaseAuth()
                .getCurrentUser().getUid()))
        {
            if (mMediaPlayer.isPlaying())
            {
                mMediaPlayer.pause();
            }
            mMediaPlayer.start();
        }

        mRecyclerViewChats.smoothScrollToPosition(mAdapterChats.getItemCount()-1);

    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(MessagingActivity.this,InicioActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        if(mChildEventListener!=null){mFirebaseMessaging.getmDatabaseReference().removeEventListener(mChildEventListener);}
        if (mMediaPlayer.isPlaying())
        {
            mMediaPlayer.stop();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("URI_INTENT",data.getData().getPath());
        if (requestCode==PICK_IMAGE_CHAT)
        {
            if (resultCode==RESULT_OK)
            {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    String base64 = Base64.encodeToString(bytes,Base64.DEFAULT);
                    sendMessaging(base64,"img");
                    Log.e("BASE64",base64);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("BASE64",e.getMessage());
                }
            }else
            {
                Log.e("BASE64","ERROR INTENT FOTO");
            }
        }else
        {
            Log.e("BASE64","NO INTENT CODE");
        }

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }
}