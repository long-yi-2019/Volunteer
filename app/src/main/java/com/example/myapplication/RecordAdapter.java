package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Entity.Activity;

import java.util.List;
import com.example.myapplication.Entity.Record;
import com.example.myapplication.Entity.ShowRecord;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder>{
    private List<ShowRecord> records;
    private RecordAdapter.OnRecordClickListener listener;

    public interface OnRecordClickListener {
        void onRecordClick(ShowRecord record);
    }

    public RecordAdapter(List<ShowRecord> records, RecordAdapter.OnRecordClickListener listener) {
        this.records = records;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new RecordAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.ViewHolder holder, int position) {
        ShowRecord record = records.get(position);
        DatabaseHelper dbHelper = new DatabaseHelper(holder.itemView.getContext());
        holder.activityNameTextView.setText(dbHelper.getActivityNameById(record.getActivityId()).getName());
        holder.timeTextView.setText(record.getDate());
        holder.durationTextView.setText(String.valueOf(record.getVolunteerTime()+"小时"));
        int state = record.getState();
        String stateText="待审核";
        int styleResId=R.style.StateTextPending;

        switch (state) {
            case 0:
                stateText = "待审核";
                styleResId = R.style.StateTextPending;
                break;
            case 1:
                stateText = "未通过";
                styleResId = R.style.StateTextRejected;
                break;
            case 2:
                stateText = "已通过";
                styleResId = R.style.StateTextApproved;
                break;
            default:
                break;
        }

        // 动态应用样式
        TextViewCompat.setTextAppearance(holder.recordStateTextView, styleResId);
        holder.recordStateTextView.setText(stateText);

        holder.itemView.setOnClickListener(v -> listener.onRecordClick(record));

    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView activityNameTextView;
        TextView timeTextView;
        TextView durationTextView;
        TextView recordStateTextView;
        ViewHolder(View itemView) {
            super(itemView);
            activityNameTextView = itemView.findViewById(R.id.record_activityName_text);
            timeTextView = itemView.findViewById(R.id.record_time_text);
            durationTextView = itemView.findViewById(R.id.record_duration_text);
            recordStateTextView=itemView.findViewById(R.id.record_state_text);
        }
    }

}
