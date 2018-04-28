package com.haraton.salad.codingharaton.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.haraton.salad.codingharaton.R;
import com.haraton.salad.codingharaton.applications.MyApplication;
import com.haraton.salad.codingharaton.tasks.HttpTask;

public class ServerActivity extends AppCompatActivity {

    private Thread mThread;
    private byte id;
    private boolean run;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        final TextView textView = findViewById(R.id.server_text_view);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.server_dialog_getting));
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        mThread =
                new Thread() {
                    @Override
                    public void run() {
                        while (run) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                            }
                            new HttpTask(getApplicationContext(), HttpTask.TASK_CMD_GET, new HttpTask.Callback() {
                                @Override
                                public void onResult(byte result) {
                                    if (0 <= result && result <= 3)
                                        ((MyApplication) getApplication()).send(false, (byte) 0, result);
                                }
                            }).execute(id);
                        }
                    }
                };

        new HttpTask(getApplicationContext(), HttpTask.TASK_SERVER, new HttpTask.Callback() {
            @Override
            public void onResult(byte result) {
                id = result;
                textView.setText(String.format("%03d", result));
                dialog.dismiss();

                run = true;
                mThread.start();
            }
        }).execute();
    }

    @Override
    protected void onDestroy() {
        run = false;
        try {
            mThread.join();
        } catch(InterruptedException e){
        }
        ((MyApplication) getApplication()).finishBluetooth();

        super.onDestroy();
    }
}
