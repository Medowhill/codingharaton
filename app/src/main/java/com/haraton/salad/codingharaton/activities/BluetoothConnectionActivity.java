package com.haraton.salad.codingharaton.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.haraton.salad.codingharaton.R;
import com.haraton.salad.codingharaton.utils.BluetoothCommander;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnectionActivity extends AppCompatActivity {

    private final int REQ_BLUETOOTH = 0;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) finish();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQ_BLUETOOTH);
        } else {
            connect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    connect();
                } else {
                    finish();
                }
                break;
        }
    }

    private BluetoothCommander connect() {
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        if (!bondedDevices.isEmpty()) {
            for (BluetoothDevice device : bondedDevices) {
                Log.i("test-bluetooth", device.getAddress() +"," + device.getName());
                if(device.getAddress().equals(getString(R.string.bluetooth_device_address))) {
                    try {
                        BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(UUID.randomUUID());
                        socket.connect();
                        return new BluetoothCommander(socket);
                    } catch (IOException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
