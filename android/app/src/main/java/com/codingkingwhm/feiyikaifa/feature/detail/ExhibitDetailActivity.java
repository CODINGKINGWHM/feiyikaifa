package com.codingkingwhm.feiyikaifa.feature.detail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codingkingwhm.feiyikaifa.R;
import com.codingkingwhm.feiyikaifa.common.ShadowArtView;
import com.codingkingwhm.feiyikaifa.common.SystemBarInsets;
import com.codingkingwhm.feiyikaifa.data.model.ExhibitCatalog;
import com.codingkingwhm.feiyikaifa.data.model.ExhibitItem;
import com.codingkingwhm.feiyikaifa.feature.drawing.DrawingActivity;
import com.codingkingwhm.feiyikaifa.feature.poster.PosterActivity;
import com.codingkingwhm.feiyikaifa.feature.stage.StageActivity;
import com.google.android.material.button.MaterialButton;

public class ExhibitDetailActivity extends AppCompatActivity {
    public static final String EXTRA_EXHIBIT_ID = "exhibit_id";
    private ExhibitItem exhibit;
    private SharedPreferences favorites;
    private MaterialButton favoriteButton;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_detail);
        SystemBarInsets.apply(getWindow(), findViewById(R.id.root));
        exhibit = ExhibitCatalog.find(getIntent().getLongExtra(EXTRA_EXHIBIT_ID, 1));
        favorites = getSharedPreferences("favorites", MODE_PRIVATE);
        ((ShadowArtView) findViewById(R.id.artView)).setVariant(exhibit.getArtVariant());
        setText(R.id.textTitle, exhibit.getName());
        setText(R.id.textMeta, exhibit.getRole() + "  ·  " + exhibit.getRegion() + "  ·  " + exhibit.getEra());
        setText(R.id.textStory, exhibit.getStory());
        setText(R.id.textMeaning, exhibit.getPatternMeaning());
        favoriteButton = findViewById(R.id.buttonFavorite);
        refreshFavorite();
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
        favoriteButton.setOnClickListener(v -> toggleFavorite());
        findViewById(R.id.buttonTrace).setOnClickListener(v -> {
            Intent intent = new Intent(this, DrawingActivity.class);
            intent.putExtra(DrawingActivity.EXTRA_PATTERN_ID, exhibit.getPatternId());
            startActivity(intent);
        });
        findViewById(R.id.buttonStage).setOnClickListener(v -> startActivity(new Intent(this, StageActivity.class)));
        findViewById(R.id.buttonPoster).setOnClickListener(v -> {
            Intent intent = new Intent(this, PosterActivity.class);
            intent.putExtra(PosterActivity.EXTRA_PUPPET_VARIANT, exhibit.getArtVariant());
            startActivity(intent);
        });
    }

    private void setText(int id, String value) { ((TextView) findViewById(id)).setText(value); }

    private void toggleFavorite() {
        String key = "exhibit_" + exhibit.getId();
        boolean next = !favorites.getBoolean(key, false);
        favorites.edit().putBoolean(key, next).apply();
        refreshFavorite();
        Toast.makeText(this, next ? "已收藏到展馆" : "已取消收藏", Toast.LENGTH_SHORT).show();
    }

    private void refreshFavorite() {
        boolean saved = favorites.getBoolean("exhibit_" + exhibit.getId(), false);
        favoriteButton.setText(saved ? "♥ 已收藏" : "♡ 收藏");
    }
}
