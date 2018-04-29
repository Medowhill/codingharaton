package com.haraton.salad.codingharaton.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.haraton.salad.codingharaton.R;
import com.haraton.salad.codingharaton.applications.MyApplication;

public class ChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        final boolean http = getIntent().getBooleanExtra("http", true);
        final byte id = getIntent().getByteExtra("id", (byte) 0);

        Button buttonButton = (Button) findViewById(R.id.moveByButton) ;
        buttonButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChoiceActivity.this,ButtonActivity.class);
                intent.putExtra("http", http);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        }) ;

        Button motionButton = (Button) findViewById(R.id.moveByMotion) ;
        motionButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChoiceActivity.this,MotionActivity.class);
                intent.putExtra("http", http);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        }) ;

        Button voiceButton = (Button) findViewById(R.id.moveByVoice) ;
        voiceButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChoiceActivity.this,VoiceActivity.class);
                intent.putExtra("http", http);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        }) ;
    }

    @Override
    protected void onDestroy() {
        ((MyApplication) getApplication()).finishBluetooth();
        super.onDestroy();
    }
}
