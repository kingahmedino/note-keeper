package com.example.notekeeper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NoteKeeperNotification {

    public static final String CHANNEL_ID = "c_id";
    public static final String CHANNEL_NAME = "c_name";
    public static final int NOTIFY_ID = 0;

    public static void notifier(final Context context, final String noteTitle, final String noteText, int noteId){
        final Resources res = context.getResources();
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.logo);

        Intent noteActivityIntent = new Intent(context, NoteActivity.class);
        noteActivityIntent.putExtra(NoteActivity.NOTE_ID, noteId);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setSmallIcon(R.drawable.ic_note_black_24dp)
//                .setContentTitle(noteTitle)
//                .setContentText(noteText)
//                .setPriority(Notification.PRIORITY_DEFAULT)
//                .setLargeIcon(picture)
//                .setTicker("Review note")
//
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(noteText)
//                        .setBigContentTitle(noteTitle)
//                        .setSummaryText("Review note"))
//                .setContentIntent(
//                        PendingIntent.getActivity(
//                                context,
//                                0,
//                                noteActivityIntent,
//                                PendingIntent.FLAG_UPDATE_CURRENT))
//
//                .addAction(
//                        0,
//                        "View all notes",
//                        PendingIntent.getActivity(
//                                context,
//                                0,
//                                new Intent(context, MainActivity.class),
//                                PendingIntent.FLAG_UPDATE_CURRENT))
//
//                // Automatically dismiss the notification when it is touched.
//                .setAutoCancel(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_note_black_24dp)
                .setContentTitle(noteTitle)
                .setContentText(noteText)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setLargeIcon(picture)
                .setTicker("Review note")

                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(noteText)
                        .setBigContentTitle(noteTitle)
                        .setSummaryText("Review note"))

                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                noteActivityIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        )
                )

                .addAction(
                        0,
                        "View all notes",
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT
                        )
                )

                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(NOTIFY_ID, builder.build());
    }
}
