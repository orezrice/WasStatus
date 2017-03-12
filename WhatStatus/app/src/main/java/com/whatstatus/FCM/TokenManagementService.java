package com.whatstatus.FCM;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.whatstatus.HttpRequest;

import java.util.HashMap;

public class TokenManagementService extends FirebaseInstanceIdService {

    public TokenManagementService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        // Get the token from the shared prefrences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String currToken = sharedPref.getString("FCMToken", "none");
        HashMap<String, String> tokenData = new HashMap<String, String>();

        // If the application doesn't have token yet
        if (currToken.equals("none")) {
            tokenData.put("token", FirebaseInstanceId.getInstance().getToken());
            HttpRequest addReq = new HttpRequest("addToken", tokenData, "http://socialchat.16mb.com/api.php");
            addReq.execute();
        } else { // Token updated
            tokenData.put("oldToken", currToken);
            tokenData.put("newToken", FirebaseInstanceId.getInstance().getToken());
            HttpRequest refreshReq = new HttpRequest("refreshToken", tokenData, "http://socialchat.16mb.com/api.php");
            refreshReq.execute();

        }

        // Put the new token in the shared prefrences
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("FCMToken", FirebaseInstanceId.getInstance().getToken());
        editor.commit();
    }
}