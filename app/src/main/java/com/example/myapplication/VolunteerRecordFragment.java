package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.Entity.Activity;
import com.example.myapplication.Entity.TimeValidator;

import java.util.ArrayList;
import java.util.List;

public class VolunteerRecordFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_record, container, false);
        Spinner activitySpinner = view.findViewById(R.id.activity_spinner);
        // 创建一个用于提示的列表
        List<String> activityList = new ArrayList<>();
        activityList.add("请选择活动");
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        // 获取活动数据（假设你有一个获取活动的方法）
        List<Activity> activities = new ArrayList<>();
        activities.add(new Activity(0, "学堂新居·家蒸护航", "", "校园社区",
                "为教职工宿舍区提供搬家协助和环境整理服务",
                10, 0, "2025-05-10 08:00", "2025-05-10 10:00", 2, "", "小黑蚁志愿队"));
        if (activities != null && !activities.isEmpty()) {
            for (Activity act : activities) {
                activityList.add(act.getName());
            }
        } else {
            // 如果没有活动，添加“无活动可选”
            activityList.add("无活动");
        }

// 创建适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, activityList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// 设置适配器
        activitySpinner.setAdapter(adapter);



        return view;
    }
}
