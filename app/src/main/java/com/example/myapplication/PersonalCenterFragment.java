package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Entity.Activity;

import java.util.List;

public class PersonalCenterFragment extends Fragment {


    private String Username;
    private ActivityAdapter adapter;
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_center, container, false);
        // 绑定视图
        TextView roleTextView = view.findViewById(R.id.role_text);
        TextView userName = view.findViewById(R.id.user_name);
        RecyclerView recyclerView = view.findViewById(R.id.activities_recycler_view);
        Button personalInfoButton = view.findViewById(R.id.personal_info_button);
        View verifyNumber = view.findViewById(R.id.verify_number);
        TextView readyPointNumber = view.findViewById(R.id.ready_point_number);
//        Button passwordChangeButton = view.findViewById(R.id.password_change_button);
        Button publishButton = view.findViewById(R.id.publish_button);

        // 初始化ViewModel
        VolunteerViewModel viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);
        // 名字
        viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {
            Username = name;
            System.out.println("!!!!!!!!!!"+Username);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());


        viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {
            Username = name;
            Log.d("PersonalCenter", "当前用户名: " + Username); // 使用Log代替System.out

            // 在这里执行依赖用户名的操作
            if (Username != null) {
                List<Activity> activities = dbHelper.getActivitiesByUsername(Username);
//                List<Activity> activities = getSampleActivities();
                adapter = new ActivityAdapter(activities, activity -> {
                    // 导航到活动详情
                    Bundle bundle = new Bundle();
                    bundle.putString("activity_name", activity.getName());
                    bundle.putString("activity_location", activity.getArea());
                    bundle.putString("activity_time", activity.getBeginTime());
                    bundle.putString("activity_duration", String.valueOf(activity.getVolunteerTime()));
                    bundle.putInt("activity_id", activity.getId());
                    Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_activityDetailsFragment, bundle);
                });
                recyclerView.setAdapter(adapter);
            }
        });



        // 设置角色显示
        viewModel.getUserRole().observe(getViewLifecycleOwner(), role -> {
            String s;
            if (role.equals("Volunteer")){
                s = "志愿者";
            } else if (role.equals("Organizer")) {
                s="组织者";
            }else {
                s="管理员";
            }
            roleTextView.setText("角色: " + s);
            recyclerView.setVisibility(role.equals("Volunteer") ? View.VISIBLE : View.GONE);
            publishButton.setVisibility(role.equals("Organizer") || role.equals("Admin") ? View.VISIBLE : View.GONE);
            verifyNumber.setVisibility(role.equals("Admin") ? View.VISIBLE : View.GONE);
        });
        viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {
            userName.setText(name);
            readyPointNumber.setText(String.valueOf(dbHelper.getActivityNumberByUserName(name)));
            System.out.println(dbHelper.getActivityNumberByUserName(name));
        });



        // 设置RecyclerView（仅志愿者）

//        ActivityAdapter adapter = new ActivityAdapter(dbHelper.getActivitiesByUsername(Username), activity -> {
//            Toast.makeText(requireContext(), "点击活动: " + activity.getName(), Toast.LENGTH_SHORT).show();
//        });
//        recyclerView.setAdapter(adapter);

        // 导航按钮
        personalInfoButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_personalInfoFragment));
        publishButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_activityPublishFragment));

        return view;
    }
}
