package com.haraton.salad.codingharaton.utils;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

public class BluetoothCommander {

    public static int DIR_LEFT = 0, DIR_RIGHT = 1, SPEED_SLOW = 0, SPEED_FAST = 1;

    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;

    public BluetoothCommander(BluetoothSocket socket) {
        this.mSocket = socket;
        try {
            this.mOutputStream = socket.getOutputStream();
        } catch(IOException e) {
            Log.e("bluetoothcommander", e.getMessage());
        }
    }

    public void send(int direction, int speed) {
    }
}
