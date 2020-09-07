package com.virtualcode7ecuadorvigitrack.trys.notification;

import android.media.MediaPlayer;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.virtualcode7ecuadorvigitrack.trys.R;

public class cNotificationFirebaseDriverLocationAround extends FirebaseMessagingService
{
    private cNotificationDriverLocationAround mNotificationDriverLocationAround;
    private MediaPlayer mMediaPlayer;
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
        }
    }
}
