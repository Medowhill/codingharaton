package com.haraton.salad.codingharaton.tasks;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import com.haraton.salad.codingharaton.R;
import com.haraton.salad.codingharaton.activities.BluetoothConnectionActivity;
import com.haraton.salad.codingharaton.utils.BluetoothCommander;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnectionTask extends AsyncTask<BluetoothDevice, Void, BluetoothCommander> {

    private BluetoothConnectionActivity mActivity;
    private ProgressDialog dialog;

    public BluetoothConnectionTask(BluetoothConnectionActivity activity) {
        mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mActivity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    @Override
    protected BluetoothCommander doInBackground(BluetoothDevice... bluetoothDevices) {
        if (bluetoothDevices.length != 1) return null;

        BluetoothDevice device = bluetoothDevices[0];
        device.setPin(new byte[] {0, 0, 0, 0});
        try {
            BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(mActivity.getString(R.string.bt_uuid)));
            socket.connect();
            return new BluetoothCommander(socket);
        } catch(IOException e) {
            Log.i("test-bluetooth", "ioe", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(BluetoothCommander commander) {
        dialog.dismiss();
        mActivity.afterConnect(commander);
    }
}
