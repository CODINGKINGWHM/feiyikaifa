package com.codingkingwhm.feiyikaifa.feature.drafts;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codingkingwhm.feiyikaifa.R;
import com.codingkingwhm.feiyikaifa.data.local.DraftEntity;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DraftAdapter extends BaseAdapter {

    public interface DeleteListener {
        void onDelete(DraftEntity entity);
    }

    private final LayoutInflater inflater;
    private final DeleteListener deleteListener;
    private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private final List<DraftEntity> items = new ArrayList<>();

    public DraftAdapter(Context context, DeleteListener deleteListener) {
        this.inflater = LayoutInflater.from(context);
        this.deleteListener = deleteListener;
    }

    public void setItems(List<DraftEntity> drafts) {
        items.clear();
        if (drafts != null) {
            items.addAll(drafts);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public DraftEntity getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_draft, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DraftEntity entity = getItem(position);
        holder.title.setText(entity.title);
        holder.pattern.setText(entity.patternName + " · " + entity.syncStatus);
        holder.updated.setText("更新于 " + dateFormat.format(new Date(entity.updatedAt)));
        holder.preview.setImageBitmap(BitmapFactory.decodeFile(entity.previewPath));
        holder.delete.setOnClickListener(view -> deleteListener.onDelete(entity));
        return convertView;
    }

    private static final class ViewHolder {
        final ImageView preview;
        final TextView title;
        final TextView pattern;
        final TextView updated;
        final MaterialButton delete;

        ViewHolder(View root) {
            preview = root.findViewById(R.id.imagePreview);
            title = root.findViewById(R.id.textTitle);
            pattern = root.findViewById(R.id.textPattern);
            updated = root.findViewById(R.id.textUpdated);
            delete = root.findViewById(R.id.buttonDelete);
        }
    }
}
