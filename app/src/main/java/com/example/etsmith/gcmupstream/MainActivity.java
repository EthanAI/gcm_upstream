package com.example.etsmith.gcmupstream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    public final String defaultIdName = "wigglesTheCat";
    public final String defaultTopic = "/topics/myTopic";
    public final String defaultText = "Test Message about Wiggles";
    public final String senderIdSuffix = "@gcm.googleapis.com";

    public String senderId;
    public String token = null;

    public GoogleCloudMessaging gcm;
    public GcmPubSub pubSub;

    // Handle button clicks
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.register:
                registerClient();
                break;
            case R.id.unregister:
                unregisterClient();
                break;
            case R.id.subscribe:
                subscribeToTopic(defaultTopic);
                break;
            case R.id.message:
                sendMessage(defaultText);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senderId = getString(R.string.gcm_defaultSenderId);

        Log.d(TAG, "Package name: " + getApplicationContext().getPackageName());
        Log.d(TAG, "SenderID: " + senderId);
        Log.d(TAG, "InstanceID: " + InstanceID.getInstance(getApplicationContext()).getId());

        // Get singetons
        gcm = GoogleCloudMessaging.getInstance(this);
        pubSub = GcmPubSub.getInstance(this);

//        unregisterClient();

        registerClient();
        subscribeToTopic(defaultTopic);
//        sendMessage();

    }

    // Needs to be off main thread
    public void registerClient() {
        final String stringId = defaultIdName; // Just a goofy name to label this device

        // Run off main thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    token = InstanceID.getInstance(getApplicationContext()).getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                    Log.d(TAG, "registerClient " + token);
                    Log.d(TAG, "registerClient " + senderId);
                    Log.d(TAG, "registerClient " + stringId);

                    // Create registrationBundle for registration with the server.
                    Bundle registrationBundle = new Bundle();

                    registrationBundle.putString(Constants.ACTION, Constants.REGISTER_NEW_CLIENT);
                    registrationBundle.putString(Constants.REGISTRATION_TOKEN, token);
                    registrationBundle.putString(Constants.STRING_IDENTIFIER, stringId);

                    // Send the registrattion request
                    gcm.send(
                            senderId + senderIdSuffix,
                            String.valueOf(System.currentTimeMillis()),
                            registrationBundle);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void unregisterClient() {
        try {
            Log.d(TAG, "unregister");

            // Create the bundle for registration with the server.
            Bundle unregistrationBundle = new Bundle();
            unregistrationBundle.putString(Constants.ACTION, Constants.UNREGISTER_CLIENT);
            unregistrationBundle.putString(Constants.REGISTRATION_TOKEN, token);

            // Send request to GCM
            gcm.send(
                    senderId + senderIdSuffix,
                    String.valueOf(System.currentTimeMillis()),
                    unregistrationBundle);
        } catch (IOException e) {
            Log.e(TAG, "Message failed", e);
        }
    }

    public void sendMessage(final String text) {
        try {
            // Create the bundle for sending the message.
            Bundle message = new Bundle();
            message.putString(Constants.ACTION, Constants.UPSTREAM_MESSAGE);
            message.putString(Constants.EXTRA_KEY_MESSAGE, text);

            gcm.send(
                    senderId + senderIdSuffix,
                    String.valueOf(System.currentTimeMillis()),
                    message);
            Log.d(TAG, "Message sent " + text);
        } catch (IOException e) {
            Log.e(TAG, "Message failed", e);
        }
    }

    public void subscribeToTopic(final String topic) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(token == null) { // Wait until registration has completed. Very kludgy solution.
                }
                Log.d(TAG, "Subscribing to: " + topic);
                Log.d(TAG, "With Token:             " + token);
                try {
                    pubSub.subscribe(token, topic, null);
                } catch (IOException e) {
                    Log.e(TAG, "Subscribe to topic failed", e);
                }
            }
        }).start();
    }
}
