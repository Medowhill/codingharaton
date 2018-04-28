package com.haraton.salad.codingharaton.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.haraton.salad.codingharaton.R;
import com.haraton.salad.codingharaton.adapters.BluetoothDeviceAdapter;
import com.haraton.salad.codingharaton.applications.MyApplication;
import com.haraton.salad.codingharaton.tasks.BluetoothConnectionTask;
import com.haraton.salad.codingharaton.utils.BluetoothCommander;

import java.util.ArrayList;

public class BluetoothConnectionActivity extends AppCompatActivity {

    private final int REQ_BLUETOOTH = 0, REQ_PERMISSION = 1;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothDeviceAdapter mAdapterPaired, mAdapterDiscovered;

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewPaired, recyclerViewDiscovered;

    private final BluetoothDeviceAdapter.OnClickListener mListener = new BluetoothDeviceAdapter.OnClickListener() {
        @Override
        public void onClick(BluetoothDevice device) {
            final BluetoothDevice dev = device;
            final String name = dev.getName();
            new AlertDialog.Builder(BluetoothConnectionActivity.this)
                    .setMessage(String.format(getString(R.string.bluetoothConnection_dialog_connect_msg),
                            (name == null) ? "<NoName>" : name))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            connect(dev);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .setCancelable(false)
                    .create().show();
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                mAdapterDiscovered.addDevice((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                mAdapterDiscovered.notifyDataSetChanged();
            }
        }
    };

    private final Handler mDiscoveryFinishHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);

        toolbar = findViewById(R.id.bluetoothConnection_toolbar);
        progressBar = findViewById(R.id.bluetoothConnection_progressBar);
        recyclerViewPaired = findViewById(R.id.bluetoothConnection_recyclerView_paired);
        recyclerViewDiscovered = findViewById(R.id.bluetoothConnection_recyclerView_discovered);

        setSupportActionBar(toolbar);

        recyclerViewDiscovered.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapterDiscovered = new BluetoothDeviceAdapter(mListener);
        recyclerViewDiscovered.setAdapter(mAdapterDiscovered);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.bluetoothConnection_dialog_unable_msg)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create().show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_PERMISSION);

        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQ_BLUETOOTH);
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        findPairedDevices();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    findPairedDevices();
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.bluetoothConnection_dialog_off_msg)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .create().show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSION:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.bluetoothConnection_dialog_noper_msg)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .create().show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_bluetooth_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bluetoothConnection_menu_discovery:
                discover();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void findPairedDevices() {
        if (mBluetoothAdapter == null) return;

        recyclerViewPaired.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewPaired.setHasFixedSize(true);
        mAdapterPaired = new BluetoothDeviceAdapter(new ArrayList<> (mBluetoothAdapter.getBondedDevices()), mListener);
        recyclerViewPaired.setAdapter(mAdapterPaired);
    }

    private void connect(final BluetoothDevice device) {
        new BluetoothConnectionTask(this).execute(device);
    }

    private void discover() {
        if (mBluetoothAdapter == null || mBluetoothAdapter.isDiscovering()) return;

        mAdapterDiscovered.clearDevices();
        mAdapterDiscovered.notifyDataSetChanged();
        mBluetoothAdapter.startDiscovery();
        progressBar.setVisibility(View.VISIBLE);
        mDiscoveryFinishHandler.sendEmptyMessageDelayed(0, 12000);
    }

    public void afterConnect(BluetoothCommander commander) {
        if (commander != null) {
            ((MyApplication) getApplication()).setCommander(commander);
            Intent intent = new Intent(getApplicationContext(), ChoiceActivity.class);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(BluetoothConnectionActivity.this)
                    .setMessage(R.string.bluetoothConnection_dialog_fail_msg)
                    .setPositiveButton(R.string.ok, null)
                    .setCancelable(false)
                    .create().show();
        }
    }
}
