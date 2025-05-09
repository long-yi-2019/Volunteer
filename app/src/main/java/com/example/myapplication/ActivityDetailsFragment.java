package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ActivityDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_details, container, false);

        // 绑定视图
        TextView nameTextView = view.findViewById(R.id.activity_name_text);
        TextView locationTextView = view.findViewById(R.id.activity_location_text);
        TextView timeTextView = view.findViewById(R.id.activity_time_text);
        TextView durationTextView = view.findViewById(R.id.activity_duration_text);
        Button bookButton = view.findViewById(R.id.book_button);

        // 获取传递的数据
        Bundle args = getArguments();
        if (args != null) {
            nameTextView.setText(args.getString("activity_name", "未知活动"));
            locationTextView.setText(args.getString("activity_location", "未知地点"));
            timeTextView.setText(args.getString("activity_time", "未知时间"));
            durationTextView.setText(args.getString("activity_duration", "未知时长"));
        }

        // 预约按钮
        bookButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "已预约: " + nameTextView.getText(), Toast.LENGTH_SHORT).show();
            // TODO: 实现预约逻辑
        });

        return view;
    }
}
