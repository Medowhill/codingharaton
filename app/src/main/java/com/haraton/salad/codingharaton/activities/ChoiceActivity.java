package com.haraton.salad.codingharaton.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.haraton.salad.codingharaton.R;

public class ChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        Intent intent = new Intent(getApplicationContext(), ChoiceActivity.class);
        startActivity(intent);
    }
}
