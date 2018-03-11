package io.gentrack.platformnotificationdemo;

import android.app.Application;

import com.taplytics.sdk.Taplytics;
import com.taplytics.sdk.TaplyticsPushTokenListener;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Taplytics.startTaplytics(this, getResources().getString(R.string.taplytics_android_sdk_key));
        Taplytics.setTaplyticsPushTokenListener(new TaplyticsPushTokenListener() {
            @Override
            public void pushTokenReceived(String token) {
                System.out.println("Received push token " + token);
            }
        });
    }
}
