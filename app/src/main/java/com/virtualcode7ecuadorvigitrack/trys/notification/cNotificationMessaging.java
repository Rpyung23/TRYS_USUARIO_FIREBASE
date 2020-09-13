package com.virtualcode7ecuadorvigitrack.trys.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.virtualcode7ecuadorvigitrack.trys.*;

public class cNotificationMessaging
{
    private static  final String CHANNEL_ID="com.virtualcode7ecuadorvigitrack.trys.Messaging";
    private Context context;
    private NotificationManager notificationManager;

    public cNotificationMessaging(Context base)
    {
        this.context = base;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public NotificationManager getNotificationManager()
    {
        if (notificationManager==null)
        {
            notificationManager = (NotificationManager) getContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    private NotificationChannel createNotificationChannel()
    {
        NotificationChannel notificationChannel;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            notificationChannel = new NotificationChannel(CHANNEL_ID,"NOTIFICATION MESSAGING",
                    NotificationManager.IMPORTANCE_HIGH);
            return notificationChannel;
        }else
        {
            return null;
        }
    }

    public NotificationCompat.Builder createNotificationWithChannelSolicitudMESSAGING(String title, String body)
    {
        /****/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            getNotificationManager().createNotificationChannel(createNotificationChannel());
        }
        /****/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setSmallIcon(R.drawable.icono_chat);
        builder.setSound(Uri.parse("android.resource://"
                + context.getPackageName() + "/" +R.raw.sound_msm_chat_activo));
        builder.setVibrate(new long[]{500,100,300,500});
        return builder;
    }

    public NotificationCompat.Builder createNotificationWithOutChannelSolicitudMESSAGING(String title,String body)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setSmallIcon(R.drawable.icono_chat);
        builder.setVibrate(new long[]{500,100,300,500});
        builder.setSound(Uri.parse("android.resource://"
                + context.getPackageName() + "/" +R.raw.sound_msm_chat_activo));
        return builder;
    }

}
