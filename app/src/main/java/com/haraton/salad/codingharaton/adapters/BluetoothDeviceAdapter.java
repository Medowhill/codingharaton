package com.haraton.salad.codingharaton.adapters;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haraton.salad.codingharaton.R;

import java.util.ArrayList;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder> {

    private ArrayList<BluetoothDevice> mDevices;
    private OnClickListener mListener;

    public static abstract class OnClickListener {
        public abstract void onClick(BluetoothDevice device);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView textViewName, textViewAddress;

        private BluetoothDevice mDevice;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.bluetooth_device_card_view);
            textViewName = view.findViewById(R.id.bluetoothDevice_textView_name);
            textViewAddress = view.findViewById(R.id.bluetoothDevice_textView_address);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(mDevice);
                }
            });
        }

        void setDevice(BluetoothDevice device) {
            mDevice = device;
            String name = device.getName();
            textViewName.setText((name == null) ? "<NoName>" : name);
            textViewAddress.setText(device.getAddress());
        }
    }

    public BluetoothDeviceAdapter(ArrayList<BluetoothDevice> mDevices, OnClickListener listener) {
        this.mDevices = mDevices;
        this.mListener = listener;
    }

    public BluetoothDeviceAdapter(OnClickListener listener) {
        this(new ArrayList<BluetoothDevice>(), listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_device, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setDevice(mDevices.get(position));
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public void addDevice(BluetoothDevice device) {
        mDevices.add(device);
    }

    public void clearDevices() {
        mDevices.clear();
    }
}
