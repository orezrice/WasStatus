package com.whatstatus.FCM;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.whatstatus.DAL.PeopleDAL;
import com.whatstatus.MainActivity;
import com.whatstatus.Utils;

public class DataManagementService extends FirebaseMessagingService {
    public DataManagementService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("FCMTest", remoteMessage.getData().get("firstName"));
        PeopleDAL.getInstance(MainActivity.getInstance().getApplicationContext()).deleteById(remoteMessage.getData().get("cardId"));
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.loadList();
            }
        });
    }
}
