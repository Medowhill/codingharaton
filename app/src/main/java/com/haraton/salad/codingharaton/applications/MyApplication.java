package com.haraton.salad.codingharaton.applications;

import android.app.Application;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.haraton.salad.codingharaton.R;
import com.haraton.salad.codingharaton.tasks.HttpTask;
import com.haraton.salad.codingharaton.utils.BluetoothCommander;
import com.haraton.salad.codingharaton.utils.Command;

public class MyApplication extends Application {
    private BluetoothCommander commander;
    private int degree = 93;

    public void setCommander(BluetoothCommander commander) {
        this.commander = commander;
        this.degree = commander.initialize();
    }

    public boolean send(boolean http, byte id, byte command) {
        if (Command.available(command, degree)) {
            degree += Command.degreeDiff(command);
            normalizeDegree();
            if (http)
                new HttpTask(getApplicationContext(), HttpTask.TASK_CMD_POST, null).execute(id, command);
            else if (commander != null)
                commander.send(command);
            return true;
        } else {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            Toast.makeText(getApplicationContext(), R.string.command_unavailable, Toast.LENGTH_SHORT).show();
            if (vibrator != null)
                vibrator.vibrate(500);
            return false;
        }
    }

    private void normalizeDegree() {
        if (degree > 180) degree = 180;
        else if (degree < 0) degree = 0;
    }

    public void finishBluetooth() {
        if (commander != null) commander.finish();
    }

    public int getDegree() {
        return degree;
    }
}
