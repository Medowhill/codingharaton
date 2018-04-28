package com.haraton.salad.codingharaton.utils;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothCommander {

    public static byte DIR_LEFT = 0, DIR_RIGHT = 1, SPEED_SLOW = 0, SPEED_FAST = 1;

    private BluetoothSocket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    public BluetoothCommander(BluetoothSocket socket) {
        this.mSocket = socket;
        try {
            this.mOutputStream = socket.getOutputStream();
            this.mInputStream = socket.getInputStream();
        } catch(IOException e) {
            Log.e("test-bluetooth", "ioe", e);
        }
    }

    public void send(byte direction, byte speed) {
        try {
            mOutputStream.write(new byte[] { direction, speed });
        } catch(IOException e) {
            Log.e("test-bluetooth", "ioe", e);
        }
    }
}
