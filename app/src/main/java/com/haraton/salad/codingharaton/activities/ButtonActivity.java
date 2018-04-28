package com.haraton.salad.codingharaton.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.haraton.salad.codingharaton.R;
import com.haraton.salad.codingharaton.applications.MyApplication;
import com.haraton.salad.codingharaton.utils.BluetoothCommander;

public class ButtonActivity extends AppCompatActivity {

    private Button buttonLeftFast, buttonRightFast, buttonLeftSlow, buttonRightSlow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);

        buttonLeftFast = (Button) findViewById(R.id.button_button_left_fast);
        buttonRightFast = (Button) findViewById(R.id.button_button_right_fast);
        buttonLeftSlow = (Button) findViewById(R.id.button_button_left_slow);
        buttonRightSlow = (Button) findViewById(R.id.button_button_right_slow);

        buttonLeftFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) getApplication()).getCommander().send(BluetoothCommander.DIR_LEFT, BluetoothCommander.SPEED_FAST);
            }
        });
        buttonRightFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) getApplication()).getCommander().send(BluetoothCommander.DIR_RIGHT, BluetoothCommander.SPEED_FAST);
            }
        });
        buttonLeftSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) getApplication()).getCommander().send(BluetoothCommander.DIR_LEFT, BluetoothCommander.SPEED_SLOW);
            }
        });
        buttonRightSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) getApplication()).getCommander().send(BluetoothCommander.DIR_RIGHT, BluetoothCommander.SPEED_SLOW);
            }
        });
    }
}
