package com.haraton.salad.codingharaton.utils;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothCommander {

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

    public void send(byte command) {
        try {
            mOutputStream.write(new byte[] { command });
        } catch(IOException e) {
            Log.e("test-bluetooth", "ioe", e);
        }
    }

    public void finish() {
        try {
            mOutputStream.close();
            mInputStream.close();
            mSocket.close();
        } catch(IOException e) {
            Log.e("test-bluetooth", "ioe", e);
        }
    }
}
