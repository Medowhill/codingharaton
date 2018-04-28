package com.haraton.salad.codingharaton.activities;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.haraton.salad.codingharaton.R;
import com.haraton.salad.codingharaton.applications.MyApplication;
import com.haraton.salad.codingharaton.utils.BluetoothCommander;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MotionActivity extends AppCompatActivity implements SensorEventListener {

    private final int ST_UP = 0, ST_DOWN = 1, ST_MID = 2;
    private int mState = ST_UP;
    private boolean able = false;
    private ArrayList<Float> accs = new ArrayList<>();

    private SensorManager mSensorManager;
    private Sensor mRotSensor, mAccelSensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRotSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mRotSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mAccelSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.equals(mRotSensor)) {
            float[] rotationMatrix = new float[9], values = new float[3];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, values);

            if (values[1] < -1) {
                mState = ST_UP;
                able = true;
            } else if (values[1] < -0.2)
                mState = ST_MID;
            else
                mState = ST_DOWN;

            if (able && mState == ST_DOWN) {
                float negSum = 0, posSum = 0;
                int size = accs.size();
                int n = Math.min(5, size);
                Collections.sort(accs);
                for (int i = 0; i < n; i++) {
                    negSum += accs.get(i);
                    posSum += accs.get(size - 1 - i);
                }
                able = false;
                accs.clear();

                negSum /= n;
                posSum /= n;
                if (Math.abs(negSum) < Math.abs(posSum)) { // left
                    if (posSum > 15)
                        ((MyApplication) getApplication()).getCommander().send(BluetoothCommander.DIR_LEFT, BluetoothCommander.SPEED_FAST);
                    else
                        ((MyApplication) getApplication()).getCommander().send(BluetoothCommander.DIR_LEFT, BluetoothCommander.SPEED_SLOW);
                } else {
                    if (negSum > -15)
                        ((MyApplication) getApplication()).getCommander().send(BluetoothCommander.DIR_RIGHT, BluetoothCommander.SPEED_SLOW);
                    else
                        ((MyApplication) getApplication()).getCommander().send(BluetoothCommander.DIR_RIGHT, BluetoothCommander.SPEED_FAST);
                }
            }
        } else if (event.sensor.equals(mAccelSensor)) {
            if (mState == ST_MID) accs.add(event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
