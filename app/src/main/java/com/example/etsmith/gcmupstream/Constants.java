// Copyright Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.etsmith.gcmupstream;

public class Constants {

    public static final String SENDER_ID = "senderId";
    public static final String STRING_IDENTIFIER = "stringIdentifier";
    public static final String EXTRA_KEY_MESSAGE = "message";

    public static final String REGISTER_NEW_CLIENT = "register_new_client";
    public static final String UNREGISTER_CLIENT = "unregister_client";
    public static final String REGISTRATION_TOKEN = "registration_token";
    public static final String UPSTREAM_MESSAGE = "upstream_message";
    public static final String ACTION = "action";
    public static final String STATUS = "status";
    public static final String STATUS_REGISTERED = "registered";
    public static final String STATUS_UNREGISTERED = "unregistered";

    // Constants used by the demo GCM playground server
    public static final String NOTIFICATION_BUNDLE = "notification";
    public static final String TOPIC_ROOT = "/topics/";

    public static final String senderIdSuffix = "@gcm.googleapis.com";

}