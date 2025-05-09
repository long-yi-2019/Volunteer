package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ActivityPublishFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_publish, container, false);

        // 绑定视图
        EditText nameEditText = view.findViewById(R.id.activity_name_edit_text);
        EditText timeEditText = view.findViewById(R.id.activity_time_edit_text);
        EditText peopleEditText = view.findViewById(R.id.activity_people_edit_text);
        EditText durationEditText = view.findViewById(R.id.activity_duration_edit_text);
        EditText descriptionEditText = view.findViewById(R.id.activity_description_edit_text);
        Button publishButton = view.findViewById(R.id.publish_button);

        // 发布按钮
        publishButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String time = timeEditText.getText().toString();
            String people = peopleEditText.getText().toString();
            String duration = durationEditText.getText().toString();
            String description = descriptionEditText.getText().toString();


            if (name.isEmpty() || time.isEmpty() || people.isEmpty() || duration.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "请填写所有字段", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(requireContext(), "活动已发布: " + name, Toast.LENGTH_SHORT).show();
            // TODO: 实现活动发布逻辑
        });


        return view;
    }
}
