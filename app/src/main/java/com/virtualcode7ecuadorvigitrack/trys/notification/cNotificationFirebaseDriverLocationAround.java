package com.virtualcode7ecuadorvigitrack.trys.notification;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.virtualcode7ecuadorvigitrack.trys.R;

public class cNotificationFirebaseDriverLocationAround extends FirebaseMessagingService
{
    private cNotificationDriverLocationAround mNotificationDriverLocationAround;
    private MediaPlayer mMediaPlayer;
    private cNotificationMessaging mNotificationMessaging;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {
        mMediaPlayer = new MediaPlayer().create(getApplicationContext(), R.raw.sound_driver_around);
        if(!mMediaPlayer.isPlaying())
        {
            mMediaPlayer.start();
        }else
            {
                mMediaPlayer.stop();
                mMediaPlayer.start();
            }
        mNotificationDriverLocationAround = new cNotificationDriverLocationAround(getApplicationContext());
        if (Integer.parseInt(remoteMessage.getData().get("tipo").toString()) == 101)
        {
            /**Crear Sonido**/
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
            {
                /** CON CHANNEL**/
                mNotificationDriverLocationAround.getmNotificationManager().notify(555,
                        mNotificationDriverLocationAround.createNotificationWithChannel(
                                remoteMessage.getNotification().getTitle()
                                ,remoteMessage.getNotification().getBody())
                                .build());
            }else
                {
                    /** Sin CHANNEL**/
                    mNotificationDriverLocationAround.getmNotificationManager().notify(555,
                            mNotificationDriverLocationAround.createNotificationWithOutChannel(
                                    remoteMessage.getNotification().getTitle()
                                    ,remoteMessage.getNotification().getBody())
                                    .build());
                }
        }else if (Integer.parseInt(remoteMessage.getData().get("tipo").toString()) == 109)
        {
            PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
            if (!powerManager.isInteractive())
            {
                /**NO ESTA ACTIVO**/
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
                {
                    mNotificationMessaging = new cNotificationMessaging(getApplicationContext());
                    mNotificationMessaging.getNotificationManager().notify(109,mNotificationMessaging
                            .createNotificationWithChannelSolicitudMESSAGING(remoteMessage.getNotification().getTitle()
                                    ,remoteMessage.getNotification().getBody()).build());
                }else
                {
                    mNotificationMessaging = new cNotificationMessaging(getApplicationContext());
                    mNotificationMessaging.getNotificationManager().notify(109,mNotificationMessaging
                            .createNotificationWithOutChannelSolicitudMESSAGING(remoteMessage.getNotification().getTitle()
                                    ,remoteMessage.getNotification().getBody()).build());
                }
            }

        }
    }

    @Override
    public void onDeletedMessages()
    {
        Log.e("NOTI","ON DELETE MESSAGES");
        super.onDeletedMessages();
    }
}
