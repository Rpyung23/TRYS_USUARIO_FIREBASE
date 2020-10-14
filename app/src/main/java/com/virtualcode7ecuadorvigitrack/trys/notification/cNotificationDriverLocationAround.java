package com.virtualcode7ecuadorvigitrack.trys.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.virtualcode7ecuadorvigitrack.trys.R;

public class cNotificationDriverLocationAround
{
    private Context context;
    private final static String CODE_CHANEL_NOTIFICATION_AROUND = "com.virtualcode7ecuadorvigitrack.trys";
    private NotificationManager mNotificationManager;

    public cNotificationDriverLocationAround(Context context)
    {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public NotificationManager getmNotificationManager()
    {
        if (mNotificationManager==null)
        {
            mNotificationManager = (NotificationManager) getContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mNotificationManager;
    }

    public void setmNotificationManager(NotificationManager mNotificationManager) {
        this.mNotificationManager = mNotificationManager;
    }

    private NotificationChannel crearChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(CODE_CHANEL_NOTIFICATION_AROUND
                    ,"CHANNEL_DRIVER_LOCATION_AROUND",NotificationManager.IMPORTANCE_HIGH);

/*
            /**Creo los atributos del audio**/
           /* AudioAttributes.Builder builder = new AudioAttributes.Builder()
                    //.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) /**ESpecifico que es musica**/
                   // .setUsage(AudioAttributes.USAGE_NOTIFICATION);/**Que lo voy ausar como notificacion**/

            /*if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
            {
                /**Permito que cualquiera aceda al sonido**/
                /*builder.setAllowedCapturePolicy(AudioAttributes.ALLOW_CAPTURE_BY_ALL);
            }*/

            /*AudioTrack audioTrack = new AudioTrack.Builder()
                    .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                    .build();*/

            //builder.setFlags(audioTrack.getPerformanceMode());
           /* notificationChannel.setSound(Uri.parse("android.resource://"
                    + context.getPackageName() + "/" +R.raw.sound_driver_around),builder.build());*/

            return notificationChannel;
        }else
            {
                return null;
            }
    }

    public Notification.Builder createNotificationWithChannel(String title, String body)
    {

        Notification.Builder builder = new Notification.Builder(getContext());
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            getmNotificationManager().createNotificationChannel(crearChannel());
            builder.setSmallIcon(R.drawable.logo_notificaciones_app);
            builder.setContentTitle(title);
            builder.setContentText(body);
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            builder.setShowWhen(true);
            builder.setVibrate(new long[]{500,100,500,100});
            builder.setChannelId(CODE_CHANEL_NOTIFICATION_AROUND);
            builder.setStyle(new Notification.BigTextStyle().bigText(body));

        }
        return builder;
    }

    public Notification.Builder createNotificationWithOutChannel(String title,String body)
    {
        Notification.Builder builder = new Notification.Builder(getContext());
        builder.setSmallIcon(R.drawable.logo_notificaciones_app);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setShowWhen(true);
        builder.setVibrate(new long[]{500,100,500,100});
        builder.setStyle(new Notification.BigTextStyle().bigText(body));
        builder.setSound(Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.sound_driver_around));
        return builder;
    }
}
