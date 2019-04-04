package com.soft.transport;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WebService extends Service {
    // @androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
