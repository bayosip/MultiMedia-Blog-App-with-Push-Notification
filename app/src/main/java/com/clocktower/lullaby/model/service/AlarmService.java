package com.clocktower.lullaby.model.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AlarmService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
