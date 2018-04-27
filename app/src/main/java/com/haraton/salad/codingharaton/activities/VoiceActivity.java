package
 com.haraton.salad.codingharaton.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.haraton.salad.codingharaton.R;
public class VoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        final TextView instrTxt = findViewById(R.id.instruction);
        final ImageButton ctrlBtn =(ImageButton) findViewById(R.id.controlbutton);
        ctrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctrlBtn.setSelected(!ctrlBtn.isPressed());
                if (ctrlBtn.isPressed()) { //clicked -> default
                    ctrlBtn.setImageResource(R.drawable.voiceStartButton);
                    instrTxt.setText(R.string.instruction_default);
                }
                else {//default -> clicked
                    ctrlBtn.setImageResource(R.drawable.voiceStopButton);
                    instrTxt.setText(R.string.instruction_clicked);
                }


            }
        });


    }


}
