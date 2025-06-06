package com.example.myapplication;

import android.annotation.SuppressLint;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Entity.Activity;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private List<Activity> activities;
    private OnActivityClickListener listener;

    public interface OnActivityClickListener {
        void onActivityClick(Activity activity);
    }

    public ActivityAdapter(List<Activity> activities, OnActivityClickListener listener) {
        this.activities = activities;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Activity activity = activities.get(position);
        holder.nameTextView.setText(activity.getName());
        holder.locationTextView.setText(activity.getArea());
        holder.timeTextView.setText(activity.getBeginTime());
        holder.durationTextView.setText(String.valueOf(activity.getVolunteerTime())+"小时");
        holder.endingTimeTextView.setText(activity.getEndTime());
        holder.activityUserTextView.setText(
                activity.getActualCount()+" / " +activity.getCount()+"人"
        );
        String imageUrl = activity.getPicture();
        Glide.with(holder.imageView.getContext())
                .load(imageUrl != null && !imageUrl.isEmpty() ? imageUrl : null)
                .fallback(R.mipmap.logo)
                .into(holder.imageView);
        holder.itemView.setOnClickListener(v -> listener.onActivityClick(activity));
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView locationTextView;
        TextView timeTextView;
        TextView durationTextView;
        TextView endingTimeTextView;
        TextView activityUserTextView;

        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.activity_name_text);
            locationTextView = itemView.findViewById(R.id.activity_location_text);
            timeTextView = itemView.findViewById(R.id.activity_time_text);
            endingTimeTextView = itemView.findViewById(R.id.activity_ending_time_text);
            durationTextView = itemView.findViewById(R.id.activity_duration_text);
            activityUserTextView = itemView.findViewById(R.id.activity_user_text);
            imageView = itemView.findViewById(R.id.imageView3);
        }
    }
}