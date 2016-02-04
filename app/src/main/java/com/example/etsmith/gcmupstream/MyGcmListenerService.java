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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

//        Log.d(TAG, "data.toString(): " + data.toString());
//        Log.d(TAG, "Received from >" + from + "< with >" + data.toString() + "<");

        String action = data.getString(RegistrationConstants.ACTION);
        String status = data.getString(RegistrationConstants.STATUS);

        if (RegistrationConstants.REGISTER_NEW_CLIENT.equals(action) &&
                RegistrationConstants.STATUS_REGISTERED.equals(status)) {
            Log.d(TAG, "Registration Success");
        } else if (RegistrationConstants.UNREGISTER_CLIENT.equals(action) &&
                RegistrationConstants.STATUS_UNREGISTERED.equals(status)) {
//            token = "";
            Log.d(TAG, "Unregistration Success");
        } else if(from.startsWith("/topics/")) {
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