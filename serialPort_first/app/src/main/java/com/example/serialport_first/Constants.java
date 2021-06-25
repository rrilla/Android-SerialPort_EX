package com.example.serialport_first;

class Constants {

   static String application_id = "com.example.serialport_first";

    // values have to be globally unique
    static final String INTENT_ACTION_GRANT_USB = application_id + ".GRANT_USB";
    static final String INTENT_ACTION_DISCONNECT = application_id + ".Disconnect";
    static final String NOTIFICATION_CHANNEL = application_id + ".Channel";
    static final String INTENT_CLASS_MAIN_ACTIVITY = application_id + ".MainActivity";

    // values have to be unique within each app
    static final int NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001;

    private Constants() {}
}
