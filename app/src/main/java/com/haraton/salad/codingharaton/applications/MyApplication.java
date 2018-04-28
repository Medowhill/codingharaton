package com.haraton.salad.codingharaton.applications;

import android.app.Application;

import com.haraton.salad.codingharaton.tasks.HttpTask;
import com.haraton.salad.codingharaton.utils.BluetoothCommander;

public class MyApplication extends Application {
    private BluetoothCommander commander;

    public void setCommander(BluetoothCommander commander) {
        this.commander = commander;
    }

    public void send(boolean http, byte id, byte command) {
        if (http)
            new HttpTask(getApplicationContext(), HttpTask.TASK_CMD_POST, null).execute(id, command);
        else if (commander != null)
            commander.send(command);
    }

    public void finishBluetooth() {
        if (commander != null) commander.finish();
    }
}
