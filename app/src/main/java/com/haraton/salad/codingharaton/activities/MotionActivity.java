package com.haraton.salad.codingharaton.activities;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.haraton.salad.codingharaton.R;
import com.haraton.salad.codingharaton.applications.MyApplication;
import com.haraton.salad.codingharaton.utils.Command;

import java.util.ArrayList;
import java.util.Collections;

public class MotionActivity extends AppCompatActivity implements SensorEventListener {

    private final int[] MOTION_ID = { R.string.left_slow, R.string.left_fast, R.string.right_slow, R.string.right_fast };

    private final int ST_UP = 0, ST_DOWN = 1, ST_MID = 2;
    private int mState = ST_UP;
    private boolean able = false, http, sendMotion = true;
    private ArrayList<Float> accs = new ArrayList<>();
    private byte id;

    private SensorManager mSensorManager;
    private Sensor mRotSensor, mAccelSensor;

    private TextView textView, textViewMotion, textViewArduino;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sendMotion = true;
            textViewMotion.setVisibility(View.INVISIBLE);
            textViewArduino.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);

        textView = findViewById(R.id.motion_text_view);
        textViewMotion = findViewById(R.id.motion_text_view_motion);
        textViewArduino = findViewById(R.id.motion_text_view_arduino);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRotSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        http = getIntent().getBooleanExtra("http", true);
        id = getIntent().getByteExtra("id", (byte) 0);
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

                if (sendMotion) {
                    negSum /= n;
                    posSum /= n;
                    byte cmd;
                    if (Math.abs(negSum) < Math.abs(posSum)) { // left
                        if (posSum > 15) cmd = Command.LEFT_FAST;
                        else cmd = Command.LEFT_SLOW;
                    } else {
                        if (negSum > -15) cmd = Command.RIGHT_SLOW;
                        else cmd = Command.RIGHT_FAST;
                    }
                    if (((MyApplication) getApplication()).send(http, id, cmd))
                        showMotion(cmd);
                }
            }
        } else if (event.sensor.equals(mAccelSensor)) {
            if (mState == ST_MID) accs.add(event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void showMotion(byte cmd) {
        textView.setVisibility(View.INVISIBLE);
        textViewMotion.setText(MOTION_ID[cmd]);
        textViewMotion.setVisibility(View.VISIBLE);
        textViewArduino.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessageDelayed(0, Command.getDelay(cmd));
        sendMotion = false;
    }
}
