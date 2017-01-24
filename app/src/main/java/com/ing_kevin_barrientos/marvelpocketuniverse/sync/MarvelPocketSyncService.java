package com.ing_kevin_barrientos.marvelpocketuniverse.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MarvelPocketSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MarvelPocketSyncAdapter sMarvelPocketSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("MarvelPocketSyncService", "onCreate - MarvelPocketSyncService");
        synchronized (sSyncAdapterLock) {
            if (sMarvelPocketSyncAdapter == null) {
                sMarvelPocketSyncAdapter = new MarvelPocketSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMarvelPocketSyncAdapter.getSyncAdapterBinder();
    }
}