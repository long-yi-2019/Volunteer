package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Entity.Activity;

import java.util.List;
import com.example.myapplication.Entity.Record;
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder>{
    private List<Record> records;
    private RecordAdapter.OnRecordClickListener listener;

    public interface OnRecordClickListener {
        void onRecordClick(Record record);
    }

    public RecordAdapter(List<Record> records, RecordAdapter.OnRecordClickListener listener) {
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
        Record record = records.get(position);
        holder.activityNameTextView.setText(record.getActivityName());
        holder.timeTextView.setText(record.getDate().toString());
        holder.durationTextView.setText(String.valueOf(record.getVolunteerTime()));
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
        ViewHolder(View itemView) {
            super(itemView);
            activityNameTextView = itemView.findViewById(R.id.record_activityName_text);
            timeTextView = itemView.findViewById(R.id.record_time_text);
            durationTextView = itemView.findViewById(R.id.record_duration_text);
        }
    }

}
