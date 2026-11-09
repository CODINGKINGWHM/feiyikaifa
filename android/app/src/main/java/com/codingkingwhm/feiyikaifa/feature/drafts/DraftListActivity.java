package com.codingkingwhm.feiyikaifa.feature.drafts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.codingkingwhm.feiyikaifa.R;
import com.codingkingwhm.feiyikaifa.common.SystemBarInsets;
import com.codingkingwhm.feiyikaifa.data.local.DraftEntity;
import com.codingkingwhm.feiyikaifa.data.local.DraftRepository;
import com.codingkingwhm.feiyikaifa.data.local.ResultCallback;
import com.codingkingwhm.feiyikaifa.feature.drawing.DrawingActivity;

import java.util.List;

public class DraftListActivity extends AppCompatActivity {

    private DraftRepository repository;
    private DraftAdapter adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drafts);
        SystemBarInsets.apply(getWindow(), findViewById(R.id.root));

        repository = new DraftRepository(this);
        emptyView = findViewById(R.id.textEmpty);
        ListView listView = findViewById(R.id.listDrafts);
        adapter = new DraftAdapter(this, this::confirmDelete);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            DraftEntity entity = adapter.getItem(position);
            Intent intent = new Intent(this, DrawingActivity.class);
            intent.putExtra(DrawingActivity.EXTRA_DRAFT_ID, entity.id);
            startActivity(intent);
        });
        findViewById(R.id.buttonBack).setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        repository.loadAll(new ResultCallback<List<DraftEntity>>() {
            @Override
            public void onSuccess(List<DraftEntity> result) {
                adapter.setItems(result);
                emptyView.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Throwable error) {
                Toast.makeText(DraftListActivity.this, "读取草稿列表失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmDelete(DraftEntity entity) {
        new AlertDialog.Builder(this)
                .setMessage("确定删除草稿“" + entity.title + "”吗？")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> repository.delete(
                        entity,
                        new ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                reload();
                            }

                            @Override
                            public void onError(Throwable error) {
                                Toast.makeText(DraftListActivity.this, "删除失败", Toast.LENGTH_LONG).show();
                            }
                        }
                ))
                .show();
    }
}
