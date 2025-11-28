package com.fortitude.shamsulkarim.ieltsfordory.utility.sync;

import android.content.Context;
import com.fortitude.shamsulkarim.ieltsfordory.domain.sync.NetworkChecker;
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper;

public class DefaultNetworkChecker implements NetworkChecker {
    private final Context context;
    public DefaultNetworkChecker(Context context){ this.context = context; }
    @Override
    public boolean isConnected() {
        return ConnectivityHelper.isConnectedToNetwork(context);
    }
}

