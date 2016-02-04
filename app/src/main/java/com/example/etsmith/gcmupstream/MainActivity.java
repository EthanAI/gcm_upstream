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

    public final String defaultIDName = "wigglesTheCat";
    public final String defaultTopic = "/topics/myTopic";
    public final String defaultText = "Test Message about Wiggles";

    public String senderID;
    public String token = null;

    public GoogleCloudMessaging gcm;
    public GcmPubSub pubSub;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private BroadcastReceiver mDownstreamBroadcastReceiver;

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

        Log.d(TAG, "Package name: " + getApplicationContext().getPackageName());
        // If Play Services is not up to date, quit the app.
        senderID = getString(R.string.gcm_defaultSenderId);
        Log.d(TAG, "SenderID: " + senderID);

        // Get singetons
        gcm = GoogleCloudMessaging.getInstance(this);
        pubSub = GcmPubSub.getInstance(this);

        Log.d(TAG, "InstanceID: " + InstanceID.getInstance(getApplicationContext()).getId());

        // Subscribe to Downstream messages. Also gets registration confirmation it seems.
        mDownstreamBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String from = intent.getStringExtra(RegistrationConstants.SENDER_ID);
                Bundle data = intent.getBundleExtra(RegistrationConstants.EXTRA_KEY_BUNDLE);
                String message = data.getString(RegistrationConstants.EXTRA_KEY_MESSAGE);

                Log.d(TAG, "Received from >" + from + "< with >" + data.toString() + "<");
                Log.d(TAG, "Message: " + message);

                String action = data.getString(RegistrationConstants.ACTION);
                String status = data.getString(RegistrationConstants.STATUS);

                if (RegistrationConstants.REGISTER_NEW_CLIENT.equals(action) &&
                        RegistrationConstants.STATUS_REGISTERED.equals(status)) {
                    Log.d(TAG, "Registration Success");
                } else if (RegistrationConstants.UNREGISTER_CLIENT.equals(action) &&
                        RegistrationConstants.STATUS_UNREGISTERED.equals(status)) {
                    token = "";
                    Log.d(TAG, "Unregistration Success");
                } else {
                    Log.d(TAG, "I dont know what happened: " + data.toString());
                }
            }
        };
        Log.d(TAG, "Register Downstream Receiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDownstreamBroadcastReceiver,
                new IntentFilter(RegistrationConstants.NEW_DOWNSTREAM_MESSAGE));


//        // Not sure what this guy is for...
//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                boolean sentToken = intent.getBooleanExtra(
//                        RegistrationConstants.SENT_TOKEN_TO_SERVER, false);
//
//                token = intent.getStringExtra(RegistrationConstants.EXTRA_KEY_TOKEN);
//                if (!sentToken) {
//                    Log.d(TAG, "Registration failed.");
//                }
//            }
//        };
//        Log.d(TAG, "Register RegistrationReceiver");
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(RegistrationConstants.REGISTRATION_COMPLETE));



//        unregisterClient();

        registerClient();
//        subscribeToTopic();
//        sendMessage();

    }

    public static String getServerUrl(String senderId) {
        return senderId + "@gcm.googleapis.com";
    }

    // Needs to be off main thread
    public void registerClient() {
        // Get the sender ID
        final String stringId = defaultIDName;

        // Run off main thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    token = InstanceID.getInstance(getApplicationContext()).getToken(senderID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null); // TODO: MyID or target Server ID??

                    Log.d(TAG, "registerClient " + token);
                    Log.d(TAG, "registerClient " + senderID);
                    Log.d(TAG, "registerClient " + stringId);

                    // Create registrationBundle for registration with the server.
                    Bundle registrationBundle = new Bundle();

                    registrationBundle.putString(RegistrationConstants.ACTION, RegistrationConstants.REGISTER_NEW_CLIENT);
                    registrationBundle.putString(RegistrationConstants.REGISTRATION_TOKEN, token);
                    registrationBundle.putString(RegistrationConstants.STRING_IDENTIFIER, stringId);

                    // Send the registrattion request
                    gcm.send(
                            getServerUrl(senderID),
                            String.valueOf(System.currentTimeMillis()),
                            registrationBundle);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void unregisterClient() {
        // Create the bundle for registration with the server.
        Bundle unregistrationBundle = new Bundle();
        unregistrationBundle.putString(RegistrationConstants.ACTION, RegistrationConstants.UNREGISTER_CLIENT);
        unregistrationBundle.putString(RegistrationConstants.REGISTRATION_TOKEN, token);

        try {
            Log.d(TAG, "unregister");
            gcm.send(
                    getServerUrl(senderID),
                    String.valueOf(System.currentTimeMillis()),
                    unregistrationBundle);
        } catch (IOException e) {
            Log.e(TAG, "Message failed", e);
        }
    }

    public void sendMessage(final String text) {

        // Create the bundle for sending the message.
        Bundle message = new Bundle();
        message.putString(RegistrationConstants.ACTION, RegistrationConstants.UPSTREAM_MESSAGE);
        message.putString(RegistrationConstants.EXTRA_KEY_MESSAGE, text);

        try {
            gcm.send(
                    getServerUrl(senderID),
                    String.valueOf(System.currentTimeMillis()),
                    message);
            Log.d(TAG, "Message sent successfully? " + text);
        } catch (IOException e) {
            Log.e(TAG, "Message failed", e);
        }
    }

    public void subscribeToTopic(final String topic) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(token == null) {
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
