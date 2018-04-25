package com.haier.fridge.bletest_phone.help;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

/**
 * Created by haddis on 18-4-21.
 */

public class MyApplication extends Application {
    public static BluetoothSocket bluetoothSocket;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
