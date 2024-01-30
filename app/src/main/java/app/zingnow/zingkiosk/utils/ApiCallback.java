package app.zingnow.zingkiosk.utils;

import org.json.JSONObject;

public interface ApiCallback {
        void onDesiredStatusReceived(JSONObject response);
        void onTimeout();
    }

