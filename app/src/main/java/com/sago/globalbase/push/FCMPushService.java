package com.sago.globalbase.push;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.appsflyer.FirebaseMessagingServiceListener;
import com.google.firebase.messaging.RemoteMessage;
import com.sago.globalbase.R;

import java.util.Map;

/**
 * xxxxx
 *
 * @author Sago丶
 * @date 2021/3/1
 */

public class FCMPushService extends FirebaseMessagingServiceListener {
    public static final String FCM_CHANNEL_ID = "fcm_channel_id";
    public static final String FCM_CHANNEL_NAME = "Custom Notifications";
    private static final int FCM_NOTIFY_ID = 200;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        //卸载，清除app数据后会走这里，代表token更新

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        if (data != null) {
            String title = data.get("title");
            String content = data.get("content");
            String open_type = data.get(FCMPushJumpActivity.KEY_OPEN_TYPE);
            String open_value = data.get(FCMPushJumpActivity.KEY_OPEN_VALUE);
            sendNotification(title, content, open_type, open_value);
        }
    }

    @Override
    public void onDeletedMessages() {
    }

    private void sendNotification(String title, String content, String open_type, String open_value) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(FCM_CHANNEL_ID, FCM_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, FCMPushJumpActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(FCMPushJumpActivity.KEY_OPEN_TYPE, open_type);
        bundle.putString(FCMPushJumpActivity.KEY_OPEN_VALUE, open_value);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, FCM_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        notificationManager.notify(FCM_NOTIFY_ID, notificationBuilder.build());
    }
}
