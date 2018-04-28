package com.haraton.salad.codingharaton.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.haraton.salad.codingharaton.R;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button buttonSingle = findViewById(R.id.start_button_single),
                buttonClient = findViewById(R.id.start_button_client),
                buttonServer = findViewById(R.id.start_button_server);

        buttonSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BluetoothConnectionActivity.class);
                intent.putExtra("single", true);
                startActivity(intent);
            }
        });

        buttonClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ClientActivity.class);
                startActivity(intent);
            }
        });

        buttonServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BluetoothConnectionActivity.class);
                intent.putExtra("single", false);
                startActivity(intent);
            }
        });
    }
}
