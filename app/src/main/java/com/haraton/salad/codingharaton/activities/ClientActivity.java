package com.haraton.salad.codingharaton.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.haraton.salad.codingharaton.R;
import com.haraton.salad.codingharaton.tasks.HttpTask;

public class ClientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        final EditText editText = findViewById(R.id.client_edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.matches("[0-9][0-9][0-9]")) {
                    int i = Integer.parseInt(str);
                    if (0 <= i && i <= 127) {
                        final byte id = (byte) i;

                        final ProgressDialog dialog = new ProgressDialog(ClientActivity.this);
                        dialog.setMessage(getString(R.string.client_dialog_verifying));
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setCancelable(false);
                        dialog.show();

                        new HttpTask(getApplicationContext(), HttpTask.TASK_CLIENT, new HttpTask.Callback() {
                            @Override
                            public void onResult(byte result) {
                                dialog.dismiss();
                                if (result == 1) {
                                    Intent intent = new Intent(getApplicationContext(), ChoiceActivity.class);
                                    intent.putExtra("http", true);
                                    intent.putExtra("id", id);
                                    startActivity(intent);
                                } else {
                                    editText.setText("");
                                    new AlertDialog.Builder(ClientActivity.this)
                                            .setMessage(R.string.client_dialog_wrong)
                                            .setPositiveButton(R.string.ok, null)
                                            .setCancelable(false)
                                            .show();
                                }
                            }
                        }).execute(id);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
