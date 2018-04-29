package com.haraton.salad.codingharaton.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.haraton.salad.codingharaton.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.haraton.salad.codingharaton.applications.MyApplication;
import com.haraton.salad.codingharaton.utils.Command;
import com.microsoft.bing.speech.SpeechClientStatus;
import com.microsoft.cognitiveservices.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.cognitiveservices.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionResult;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionStatus;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

public class VoiceActivity extends Activity implements ISpeechRecognitionServerEvents
{
    private final int[] MOTION_ID = { R.string.left_slow, R.string.left_fast, R.string.right_slow, R.string.right_fast };

    boolean http;
    byte id;
    boolean sendVoice = true;
    private TextView spokenTextView, spokenResultView, commandView, degreeView;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            sendVoice = true;
            spokenResultView.setVisibility(View.INVISIBLE);
            commandView.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    new AlertDialog.Builder(this)
                            .setMessage("turn off")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .create().show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("check", "code running");
        http = getIntent().getBooleanExtra("http", true);
        id = getIntent().getByteExtra("id", (byte) 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        spokenTextView = findViewById(R.id.spoken_text);
        spokenResultView = findViewById(R.id.spoken_result);
        commandView = findViewById(R.id.command);
        degreeView = findViewById(R.id.degree);
        degreeView.setText("degree: " + String.valueOf(((MyApplication) getApplication()).getDegree()));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    0);

        final ImageButton btnCtrl = findViewById(R.id.btn_ctrl);
        btnCtrl.setSelected(false);
        spokenTextView.setVisibility(View.INVISIBLE);

        btnCtrl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCtrl.setSelected(!btnCtrl.isSelected());
                if (btnCtrl.isSelected()){
                    btnCtrl.setImageResource(R.drawable.voice_stop_button);
                    spokenTextView.setVisibility(View.VISIBLE);
                    voiceMoveActive();
                    Log.i("check", "start");
                }
                else{
                    btnCtrl.setImageResource(R.drawable.voice_start_button);
                    spokenTextView.setVisibility(View.INVISIBLE);
                    voiceMoveInactive();
                    Log.i("check", "stop");
                }
            }
        });

    }

    MicrophoneRecognitionClient micClient = null;
    public String getPrimaryKey() {
        return this.getString(R.string.primaryKey);
    }
    private SpeechRecognitionMode getMode() { return SpeechRecognitionMode.LongDictation; }
    private String getDefaultLocale() {
        return "en-us";
    }
    private String getAuthenticationUri() {
        return this.getString(R.string.authenticationUri);
    }


    public void voiceMoveActive(){
        if (this.micClient == null) {
            this.micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(
                    this,
                    this.getMode(),
                    this.getDefaultLocale(),
                    this,
                    this.getPrimaryKey());
            this.micClient.setAuthenticationUri(this.getAuthenticationUri());
        }

        this.micClient.startMicAndRecognition();

    }

    public void onAudioEvent(boolean recording){
        if (!recording) {
            this.micClient.endMicAndRecognition();
        }
    };

    public void voiceMoveInactive(){
        if (this.micClient != null){
            this.micClient.endMicAndRecognition();
        }
    }


    public void onError(int errorCode, String response){
        Log.i("check", "Error code: " + SpeechClientStatus.fromInt(errorCode) + " " + errorCode+"\n"+
                "Error text: " + response);
    };

    public void onFinalResponseReceived(RecognitionResult response){
        Log.i("check", "result Method Called");
        boolean isFinalDicationMessage = this.getMode() == SpeechRecognitionMode.LongDictation &&
                (response.RecognitionStatus == RecognitionStatus.EndOfDictation ||
                        response.RecognitionStatus == RecognitionStatus.DictationEndSilenceTimeout);
        if (null != this.micClient && ((this.getMode() == SpeechRecognitionMode.ShortPhrase) || isFinalDicationMessage)) {
            // we got the final result, so it we can end the mic reco.  No need to do this
            // for dataReco, since we already called endAudio() on it as soon as we were done
            // sending all the data.
            this.micClient.endMicAndRecognition();
        }
        if (!isFinalDicationMessage) {
            for (int i = 0; i < response.Results.length; i++) {
                String result = response.Results[i].DisplayText;
                Log.i("-result", result);
                if (sendVoice) {
                    spokenResultView.setVisibility(View.VISIBLE);
                    spokenResultView.setText(result);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            spokenResultView.setVisibility(View.INVISIBLE);
                        }
                    }, 900);
                    int btData = getBTdata(result);
                    byte cmd = -1;
                    if (btData == 11) {
                        cmd = Command.LEFT_SLOW;
                    } else if (btData == 12) {
                        cmd = Command.LEFT_FAST;
                    } else if (btData == 21) {
                        cmd = Command.RIGHT_SLOW;
                    } else if (btData == 22) {
                        cmd = Command.RIGHT_FAST;
                    }
                    if (cmd != -1 && ((MyApplication) getApplication()).send(http, id, cmd))
                        showVoiceCommand(cmd);
                }
            }
        }
    };

    public int getBTdata(String result){
        int direction = 100;
        if (result.toLowerCase().contains("left")){
            direction = 10;
        }
        else if (result.toLowerCase().contains("right")){
            direction = 20;
        }
        int speed = 100;
        if (result.toLowerCase().contains("slow")){
            speed = 1;
        }
        else if (result.toLowerCase().contains("fast")){
            speed = 2;
        }
        return direction+speed;
    }

    public void onIntentReceived(java.lang.String payload){};

    public void onPartialResponseReceived(java.lang.String response){
        Log.i("check", "partial Result Method Called");
    };

    public void showVoiceCommand(byte cmd){
        commandView.setVisibility(View.VISIBLE);
        commandView.setText(MOTION_ID[cmd]);
        degreeView.setText("degree: " + String.valueOf(((MyApplication) getApplication()).getDegree()));
        handler.sendEmptyMessageDelayed(0, Command.getDelay(cmd));
    }

}
