package com.social.mannu.integrationchatapp.fcmnotification;

import android.util.Log;

import com.applozic.mobicomkit.api.notification.MobiComPushReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMListenerService extends FirebaseMessagingService {

    private static final String TAG = "FCMListener";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.i(TAG,"Message data:"+remoteMessage.getData());

        if(remoteMessage.getData().size()>0){
            if (MobiComPushReceiver.isMobiComPushNotification(remoteMessage.getData())) {
                Log.i(TAG, "Notification processing...");
                MobiComPushReceiver.processMessageAsync(this, remoteMessage.getData());
                return;
            }
        }

    }
}
