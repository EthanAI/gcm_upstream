package com.example.etsmith.gcmupstream;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;

/**
 * Created by etsmith on 2/4/16.
 */
public class MyGcmTaskService extends GcmTaskService {
    private final String TAG = getClass().getSimpleName();
    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.d(TAG, "Network is ready, sending message!");

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String senderId = taskParams.getExtras().getString(Constants.SENDER_ID);
        String messageText = taskParams.getExtras().getString(Constants.EXTRA_KEY_MESSAGE);

        try {
            // Create the bundle for sending the message.
            Bundle message = new Bundle();
            message.putString(Constants.ACTION, Constants.UPSTREAM_MESSAGE);
            message.putString(Constants.EXTRA_KEY_MESSAGE, messageText);

            gcm.send(
                    senderId + Constants.senderIdSuffix,
                    String.valueOf(System.currentTimeMillis()),
                    message);
            Log.d(TAG, "Message sent " + messageText);
        } catch (IOException e) {
            Log.e(TAG, "Message failed", e);
        }

        return 0;
    }
}
