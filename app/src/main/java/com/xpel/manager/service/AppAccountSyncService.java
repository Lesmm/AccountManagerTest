package com.xpel.manager.service;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;


public class AppAccountSyncService extends Service {

    AbstractThreadedSyncAdapter syncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        syncAdapter = new AbstractThreadedSyncAdapter(this, true) {
            @Override
            public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
                Log.d(this.getClass().getCanonicalName(), "onPerformSync");
            }
        };
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }

}
