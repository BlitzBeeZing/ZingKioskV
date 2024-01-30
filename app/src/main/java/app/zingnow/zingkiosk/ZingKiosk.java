package app.zingnow.zingkiosk;

import android.app.Application;

public class ZingKiosk extends Application {
    private static ZingKiosk instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static ZingKiosk getInstance() {
        return instance;
    }
}
