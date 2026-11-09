package com.codingkingwhm.feiyikaifa.feature.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codingkingwhm.feiyikaifa.R;
import com.codingkingwhm.feiyikaifa.common.SystemBarInsets;
import com.codingkingwhm.feiyikaifa.data.model.ExhibitCatalog;
import com.codingkingwhm.feiyikaifa.data.model.ExhibitItem;
import com.codingkingwhm.feiyikaifa.feature.detail.ExhibitDetailActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    private LinearLayout results;
    private TextView count;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SystemBarInsets.apply(getWindow(), findViewById(R.id.root));
        results = findViewById(R.id.resultContainer);
        count = findViewById(R.id.textCount);
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
        EditText input = findViewById(R.id.inputSearch);
        input.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { render(s.toString()); }
            public void afterTextChanged(Editable s) { }
        });
        render("");
        input.requestFocus();
    }

    private void render(String query) {
        results.removeAllViews();
        String key = query.trim().toLowerCase(Locale.ROOT);
        int matched = 0;
        for (ExhibitItem item : ExhibitCatalog.all()) {
            String haystack = item.getName() + item.getRole() + item.getRegion() + item.getStory();
            if (!key.isEmpty() && !haystack.toLowerCase(Locale.ROOT).contains(key)) continue;
            matched++;
            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(94));
            lp.bottomMargin = dp(10);
            card.setLayoutParams(lp);
            card.setRadius(dp(18));
            card.setCardBackgroundColor(getColor(R.color.white));
            card.setStrokeColor(0x22B72D24);
            card.setStrokeWidth(dp(1));
            LinearLayout line = new LinearLayout(this);
            line.setPadding(dp(17), dp(13), dp(17), dp(13));
            line.setOrientation(LinearLayout.VERTICAL);
            TextView title = text(item.getName(), 18, R.color.ink, true);
            TextView meta = text(item.getRole() + " · " + item.getRegion() + " · " + item.getEra(), 12, R.color.accent_red, false);
            TextView hint = text(item.getPatternMeaning(), 12, R.color.muted, false);
            line.addView(title); line.addView(meta); line.addView(hint);
            card.addView(line);
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, ExhibitDetailActivity.class);
                intent.putExtra(ExhibitDetailActivity.EXTRA_EXHIBIT_ID, item.getId());
                startActivity(intent);
            });
            results.addView(card);
        }
        count.setText(matched + " 件皮影藏品");
    }

    private TextView text(String value, int sp, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value); view.setTextSize(sp); view.setTextColor(getColor(color));
        if (bold) view.setTypeface(view.getTypeface(), android.graphics.Typeface.BOLD);
        return view;
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
