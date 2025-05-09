package com.example.myapplication;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PersonalCenterFragment extends Fragment {

    private VolunteerViewModel viewModel;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_center, container, false);

        // 初始化ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);

        // 绑定视图
        TextView roleTextView = view.findViewById(R.id.role_text);
        RecyclerView recyclerView = view.findViewById(R.id.activities_recycler_view);
        Button personalInfoButton = view.findViewById(R.id.personal_info_button);
        Button passwordChangeButton = view.findViewById(R.id.password_change_button);
        Button publishButton = view.findViewById(R.id.publish_button);

        // 设置角色显示
        viewModel.getUserRole().observe(getViewLifecycleOwner(), role -> {
            roleTextView.setText("角色: " + role);
            recyclerView.setVisibility(role.equals("Volunteer") ? View.VISIBLE : View.GONE);
            publishButton.setVisibility(role.equals("Organizer") || role.equals("Admin") ? View.VISIBLE : View.GONE);
        });

        // 设置RecyclerView（仅志愿者）
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ActivityAdapter adapter = new ActivityAdapter(getSampleActivities(), activity -> {
            Toast.makeText(requireContext(), "点击活动: " + activity.getName(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        // 导航按钮
        personalInfoButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_personalInfoFragment));
        passwordChangeButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_passwordChangeFragment));
        publishButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_activityPublishFragment));

        return view;
    }

    private java.util.List<ActivityItem> getSampleActivities() {
        java.util.List<ActivityItem> activities = new java.util.ArrayList<>();
        activities.add(new ActivityItem("学堂新居, 家蒸护航", "校园社区", "2025-05-10", "2小时"));
        activities.add(new ActivityItem("无偿献血", "校医院", "2025-05-12", "3小时"));
        return activities;
    }
}