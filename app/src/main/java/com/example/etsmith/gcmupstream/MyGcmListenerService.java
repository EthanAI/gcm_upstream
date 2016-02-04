/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.etsmith.gcmupstream;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onMessageReceived(String from, final Bundle data) {
        super.onMessageReceived(from, data);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), "Message Received: " + data.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        for(String dataTag : data.keySet()) {
            if(!dataTag.equals(Constants.NOTIFICATION_BUNDLE)) {
                Log.d(TAG, dataTag + ": " + data.getString(dataTag));
            } else {
                Bundle bundle = data.getBundle(dataTag);
                for(String bundleTag : bundle.keySet()) {
                    Log.d(TAG, bundleTag + ": " + data.getString(bundleTag));
                }
            }
        }

        String action = data.getString(Constants.ACTION);
        String status = data.getString(Constants.STATUS);

        if (Constants.REGISTER_NEW_CLIENT.equals(action) &&
                Constants.STATUS_REGISTERED.equals(status)) {
            Log.d(TAG, "Registration Success");
        } else if (Constants.UNREGISTER_CLIENT.equals(action) &&
                Constants.STATUS_UNREGISTERED.equals(status)) {
//            token = "";
            Log.d(TAG, "Unregistration Success");
        } else if(from.startsWith(Constants.TOPIC_ROOT)) {
            Log.d(TAG, "Topic message: " + data.toString());
        } else {
            Log.d(TAG, "Other type of action: " + data.toString());
        }
    }

    @Override
    public void onSendError(String msgId, String error) {
        Log.d(TAG, "onSendError: " + msgId + " " + error);
    }
}