package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.adapter.adapterChats;
import com.virtualcode7ecuadorvigitrack.trys.includes.cToolbar;
import com.virtualcode7ecuadorvigitrack.trys.models.cMessaging;
import com.virtualcode7ecuadorvigitrack.trys.provider.cClientProvider;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderAuth;
import com.virtualcode7ecuadorvigitrack.trys.provider.cProviderToken;
import com.virtualcode7ecuadorvigitrack.trys.volley.cVolleyNotificationSendMessaging;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MessagingActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener
{
    private String id_driver;
    private String id_token_phone;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private cFirebaseProviderAuth mFirebaseProviderAuth;

    private static final int PICK_IMAGE_CHAT = 989;

    private Button mButtonSend;
    private FloatingActionButton mButtonCamera_Galery;
    private EditText mEditTextMessaging;


    private RecyclerView mRecyclerViewChats;
    private ArrayList<cMessaging> mMessagingArrayList;

    private adapterChats mAdapterChats;

    private  boolean banderaSound =false;

    private MediaPlayer mMediaPlayer;

    private cClientProvider mFirebaseProfileClient;


    private String url_photo;
    private String Url_photo_IntentClient;



    private cVolleyNotificationSendMessaging mVolleyNotificationSendMessaging;


    private cProviderToken mProviderToken;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        new cToolbar().showToolbar(MessagingActivity.this,"Mensajeria",true);

        mProviderToken =  new cProviderToken();

        Url_photo_IntentClient = getIntent().getStringExtra("photo_driver");
        id_driver = getIntent().getStringExtra("id_driver");

        mProviderToken.readToken(id_driver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    id_token_phone = snapshot.child("token").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mVolleyNotificationSendMessaging= new cVolleyNotificationSendMessaging(MessagingActivity.this);
        mFirebaseProfileClient = new cClientProvider();

        mButtonSend = findViewById(R.id.id_botton_send_chats);
        mButtonCamera_Galery = findViewById(R.id.id_camera_chats);
        mEditTextMessaging = findViewById(R.id.id_edittext_chats);
        mRecyclerViewChats = findViewById(R.id.id_recyclerview_chats);

        if(mMessagingArrayList==null)
        {
            mMessagingArrayList = new ArrayList<>();
        }else
        {
            mMessagingArrayList.clear();
        }

        mMediaPlayer = new MediaPlayer().create(MessagingActivity.this
                ,R.raw.sound_msm_chat_activo);

        mFirebaseProviderAuth = new cFirebaseProviderAuth();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Messaging");


        mMessagingArrayList = new ArrayList<>();

        banderaSound =true;


        mAdapterChats = new adapterChats(mMessagingArrayList,MessagingActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessagingActivity.this);
        mRecyclerViewChats.setLayoutManager(linearLayoutManager);
        mRecyclerViewChats.setAdapter(mAdapterChats);
        //mRecyclerViewChats.smoothScrollToPosition(mMessagingArrayList.size());



        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!mEditTextMessaging.getText().toString().isEmpty())
                {
                    sendMessaging(mEditTextMessaging.getText().toString(),"text");
                }else
                    {
                        /** Sniper VACIOs**/
                    }
            }
        });

        mButtonCamera_Galery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE_CHAT);
            }
        });

        mFirebaseProfileClient.getmDatabaseReference()
                .child(mFirebaseProviderAuth.getmFirebaseAuth().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists())
                        {
                            url_photo = snapshot.child("Photo").getValue().toString();
                            Log.e("PHOTO",url_photo);
                        }else
                        {
                            Log.e("PHOTO","no exists");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                    }
                });

    }

    private void sendMessaging(final String msm, final String type_msm)
    {
        mDatabaseReference.child(id_driver).push().setValue(creteHashMapMessaging(msm,type_msm)
                , new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref)
            {
                /**ENVIO EXITOSO**/
                if (error == null || error.getMessage().isEmpty())
                {
                    if (type_msm.equals("text"))
                    {
                        mVolleyNotificationSendMessaging.sendNotificationVolley("Nuevo Mensaje",msm,id_token_phone);
                    }else
                        {
                            mVolleyNotificationSendMessaging.sendNotificationVolley("Nuevo Mensaje","Imagen recibida",id_token_phone);
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
    protected void onPostResume()
    {
        readMessaging();
        super.onPostResume();
    }

    private void readMessaging()
    {

        mDatabaseReference.child(id_driver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                if (snapshot.exists())
                {
                    DataSnapshot dataSnapshotMess = snapshot;
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
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {

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
                    Log.e("BASE64 : ",base64);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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
        if (mMediaPlayer.isPlaying())
        {
            mMediaPlayer.stop();
        }
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer)
    {
        mMediaPlayer.start();
    }
}