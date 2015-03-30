package com.example.harimani.restaurantapp.GCM;

import com.example.harimani.restaurantapp.GCM.GcmBroadcastReceiver;
import com.example.harimani.restaurantapp.MainActivity;
import com.example.harimani.restaurantapp.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * GcmIntentService is an intent service class used for handling GCM push notifications that are received.
 */
public class GcmIntentService extends IntentService {

    /**
     * The int variable to be used for scheduling notifications.
     */
    public static final int NOTIFICATION_ID = 1;

    /**
     * The NotificationManager object used by the app to schedule notifications that are received from GCM server.
     */
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    /**
     * The tag variable used for logging and for notifications display name.
     */
    public static final String TAG = "Restaurant App";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras.getString("message"));
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Restaurant App")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentText(msg);

        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setContentIntent(contentIntent);

        //Vibration
        //mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //LED
        //mBuilder.setLights(Color.RED, 3000, 3000);

        //for custom sound.
        //mBuilder.setSound(Uri.parse("uri://sadfasdfasdf.mp3"));

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
