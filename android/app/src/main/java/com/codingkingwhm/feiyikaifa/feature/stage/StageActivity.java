package com.codingkingwhm.feiyikaifa.feature.stage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codingkingwhm.feiyikaifa.R;
import com.codingkingwhm.feiyikaifa.common.SystemBarInsets;
import com.codingkingwhm.feiyikaifa.feature.poster.PosterActivity;
import com.google.android.material.button.MaterialButton;

public class StageActivity extends AppCompatActivity {
    private boolean recording;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);
        SystemBarInsets.apply(getWindow(), findViewById(R.id.root));
        PuppetStageView stage = findViewById(R.id.stageView);
        TextView status = findViewById(R.id.textStatus);
        MaterialButton record = findViewById(R.id.buttonRecord);
        stage.setListener(new PuppetStageView.Listener() {
            @Override public void onStatus(String value) { status.setText(value); }
            @Override public void onRecordingChanged(boolean isRecording) {
                recording = isRecording;
                record.setText(isRecording ? "停止记录" : "记录动作");
            }
        });
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
        findViewById(R.id.buttonPose).setOnClickListener(v -> stage.togglePose());
        findViewById(R.id.buttonReset).setOnClickListener(v -> stage.resetPuppet());
        findViewById(R.id.buttonPlay).setOnClickListener(v -> stage.playRecording());
        findViewById(R.id.buttonPoster).setOnClickListener(v -> startActivity(new Intent(this, PosterActivity.class)));
        record.setOnClickListener(v -> {
            recording = !recording;
            if (recording) stage.startRecording();
            else stage.stopRecording();
        });
    }
}
