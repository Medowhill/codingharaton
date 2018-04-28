package com.haraton.salad.codingharaton.applications;

import android.app.Application;

import com.haraton.salad.codingharaton.utils.BluetoothCommander;

public class MyApplication extends Application {
    private BluetoothCommander commander;

    public void setCommander(BluetoothCommander commander) {
        this.commander = commander;
    }

    public void sendThroughBluetooth(byte command) {
        if (commander != null) commander.send(command);
    }
}
