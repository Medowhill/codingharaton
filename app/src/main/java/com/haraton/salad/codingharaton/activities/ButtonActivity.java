package com.haraton.salad.codingharaton.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.haraton.salad.codingharaton.R;
import com.haraton.salad.codingharaton.applications.MyApplication;
import com.haraton.salad.codingharaton.tasks.HttpTask;
import com.haraton.salad.codingharaton.utils.BluetoothCommander;
import com.haraton.salad.codingharaton.utils.Command;

public class ButtonActivity extends AppCompatActivity {

    private Button buttonLeftFast, buttonRightFast, buttonLeftSlow, buttonRightSlow;
    private ProgressDialog dialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
            dialog = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);

        buttonLeftFast = findViewById(R.id.button_button_left_fast);
        buttonRightFast = findViewById(R.id.button_button_right_fast);
        buttonLeftSlow = findViewById(R.id.button_button_left_slow);
        buttonRightSlow = findViewById(R.id.button_button_right_slow);

        final boolean http = getIntent().getBooleanExtra("http", true);
        final byte id = getIntent().getByteExtra("id", (byte) 0);

        buttonLeftFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act(http, id, Command.LEFT_FAST);
            }
        });
        buttonRightFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act(http, id, Command.RIGHT_FAST);
            }
        });
        buttonLeftSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act(http, id, Command.LEFT_SLOW);
            }
        });
        buttonRightSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act(http, id, Command.RIGHT_SLOW);
            }
        });

        setAvailability();
    }

    private void act(boolean http, byte id, byte command) {
        MyApplication application = (MyApplication) getApplication();
        if (application.send(http, id, command)) {
            dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.arduino_working));
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();
            mHandler.sendEmptyMessageDelayed(0, Command.getDelay(command));
        }
    }

    private void setAvailability() {
        MyApplication application = (MyApplication) getApplication();
        buttonLeftFast.setEnabled(Command.available(Command.LEFT_FAST, application.getDegree()));
        buttonLeftSlow.setEnabled(Command.available(Command.LEFT_SLOW, application.getDegree()));
        buttonRightFast.setEnabled(Command.available(Command.RIGHT_FAST, application.getDegree()));
        buttonRightSlow.setEnabled(Command.available(Command.RIGHT_SLOW, application.getDegree()));
    }
}
