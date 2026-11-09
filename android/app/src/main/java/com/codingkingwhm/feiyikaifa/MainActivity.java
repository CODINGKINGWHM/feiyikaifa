package com.codingkingwhm.feiyikaifa;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.codingkingwhm.feiyikaifa.common.SystemBarInsets;
import com.codingkingwhm.feiyikaifa.feature.detail.ExhibitDetailActivity;
import com.codingkingwhm.feiyikaifa.feature.search.SearchActivity;
import com.codingkingwhm.feiyikaifa.feature.works.WorksActivity;

public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SystemBarInsets.apply(getWindow(), findViewById(R.id.root));
        findViewById(R.id.buttonSearch).setOnClickListener(v -> open(SearchActivity.class));
        findViewById(R.id.buttonExplore).setOnClickListener(v -> openDetail(1));
        findViewById(R.id.cardExhibitOne).setOnClickListener(v -> openDetail(2));
        findViewById(R.id.cardExhibitTwo).setOnClickListener(v -> openDetail(3));
        findViewById(R.id.navHome).setOnClickListener(v -> findViewById(R.id.scrollContent).scrollTo(0, 0));
        findViewById(R.id.navCreate).setOnClickListener(v -> {
            Intent intent = new Intent(this, com.codingkingwhm.feiyikaifa.feature.drawing.DrawingActivity.class);
            intent.putExtra(com.codingkingwhm.feiyikaifa.feature.drawing.DrawingActivity.EXTRA_PATTERN_ID, 1L);
            startActivity(intent);
        });
        findViewById(R.id.navWorks).setOnClickListener(v -> open(WorksActivity.class));
    }

    private void open(Class<?> target) { startActivity(new Intent(this, target)); }

    private void openDetail(long id) {
        Intent intent = new Intent(this, ExhibitDetailActivity.class);
        intent.putExtra(ExhibitDetailActivity.EXTRA_EXHIBIT_ID, id);
        startActivity(intent);
    }
}
