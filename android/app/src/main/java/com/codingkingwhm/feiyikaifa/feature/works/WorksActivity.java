package com.codingkingwhm.feiyikaifa.feature.works;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codingkingwhm.feiyikaifa.R;
import com.codingkingwhm.feiyikaifa.MainActivity;
import com.codingkingwhm.feiyikaifa.common.ShadowArtView;
import com.codingkingwhm.feiyikaifa.common.SystemBarInsets;
import com.codingkingwhm.feiyikaifa.feature.drawing.DrawingActivity;
import com.codingkingwhm.feiyikaifa.feature.drafts.DraftListActivity;
import com.codingkingwhm.feiyikaifa.feature.poster.PosterActivity;

public class WorksActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_works);
        SystemBarInsets.apply(getWindow(), findViewById(R.id.root));
        ((ShadowArtView) findViewById(R.id.workArt)).setVariant(3);
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
        findViewById(R.id.cardDrafts).setOnClickListener(v -> startActivity(new Intent(this, DraftListActivity.class)));
        findViewById(R.id.buttonCreate).setOnClickListener(v -> startActivity(new Intent(this, PosterActivity.class)));
        findViewById(R.id.navMuseum).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent); finish();
        });
        findViewById(R.id.navCreate).setOnClickListener(v -> {
            Intent intent = new Intent(this, DrawingActivity.class);
            intent.putExtra(DrawingActivity.EXTRA_PATTERN_ID, 1L);
            startActivity(intent);
        });
        findViewById(R.id.navWorks).setOnClickListener(v -> { });
    }

    @Override protected void onResume() {
        super.onResume();
        String uri = getSharedPreferences("works", MODE_PRIVATE).getString("last_poster_uri", null);
        String title = getSharedPreferences("works", MODE_PRIVATE).getString("last_poster_title", "今日开演");
        TextView state = findViewById(R.id.textWorkState);
        TextView titleView = findViewById(R.id.textWorkTitle);
        if (uri == null) {
            titleView.setText("等待你的第一张皮影海报"); state.setText("尚未导出作品");
        } else {
            titleView.setText(title); state.setText("已保存到系统相册 · 可继续创作");
        }
    }
}
