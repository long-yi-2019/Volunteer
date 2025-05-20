package com.example.myapplication;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
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

import com.example.myapplication.Entity.Activity;

import java.util.ArrayList;
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
        RecyclerView recyclerView = view.findViewById(R.id.activities_recycler_view);
        Button personalInfoButton = view.findViewById(R.id.personal_info_button);
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
                    Navigation.findNavController(view).navigate(R.id.action_activitySearchFragment_to_activityDetailsFragment, bundle);
                });
                recyclerView.setAdapter(adapter);
            }
        });



        // 设置角色显示
        viewModel.getUserRole().observe(getViewLifecycleOwner(), role -> {
            roleTextView.setText("角色: " + role);
            recyclerView.setVisibility(role.equals("Volunteer") ? View.VISIBLE : View.GONE);
            publishButton.setVisibility(role.equals("Organizer") || role.equals("Admin") ? View.VISIBLE : View.GONE);
        });


        // 设置RecyclerView（仅志愿者）

//        ActivityAdapter adapter = new ActivityAdapter(dbHelper.getActivitiesByUsername(Username), activity -> {
//            Toast.makeText(requireContext(), "点击活动: " + activity.getName(), Toast.LENGTH_SHORT).show();
//        });
//        recyclerView.setAdapter(adapter);

        // 导航按钮
        personalInfoButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_personalInfoFragment));
//        passwordChangeButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_passwordChangeFragment));
        publishButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_activityPublishFragment));

        return view;
    }

//    private List<Activity> getSampleActivities() {
//        List<Activity> activities = new ArrayList<>();
//        // 1. 校园环境维护类
//        activities.add(new Activity(0, "学堂新居·家蒸护航", "", "校园社区",
//                "为教职工宿舍区提供搬家协助和环境整理服务",
//                10, 0, "2025-05-10 08:00", "2025-05-10 10:00", 2, "ongoing", "1"));
//
//        // 2. 开学迎新活动
//        activities.add(new Activity(1, "新生导航·温暖启程", "", "校门口广场",
//                "帮助新生搬运行李、引导报到流程、校园导览",
//                50, 32, "2025-09-01 07:00", "2025-09-01 17:00", 6, "pending", "2"));
//
//        // 3. 图书馆志愿服务
//        activities.add(new Activity(2, "书香守护者", "", "校图书馆",
//                "图书整理、读者引导、阅读区秩序维护",
//                15, 8, "2025-05-15 13:30", "2025-05-15 16:30", 3, "ongoing", "3"));
//
//        // 4. 环保回收行动
//        activities.add(new Activity(3, "绿色校园·废品回收", "", "全校各宿舍楼",
//                "可回收物分类收集、环保知识宣传",
//                20, 12, "2025-05-20 09:00", "2025-05-20 11:30", 2, "upcoming", "1"));
//
//        // 5. 支教帮扶活动
//        activities.add(new Activity(4, "周末课堂·爱心支教", "", "农民工子弟学校",
//                "为周边社区儿童提供学科辅导和兴趣课程",
//                30, 25, "2025-05-16 08:30", "2025-05-16 11:30", 3, "ongoing", "4"));
//
//        // 6. 运动会志愿服务
//        activities.add(new Activity(6, "校运会后勤保障", "", "学校操场",
//                "担任裁判助理、场地维护、医疗点支持",
//                40, 40, "2025-10-12 08:00", "2025-10-12 17:00", 8, "pending", "5"));
//
//        // 7. 食堂文明督导
//        activities.add(new Activity(7, "光盘行动监督员", "", "学生食堂",
//                "倡导节约粮食、维持排队秩序",
//                10, 6, "2025-05-11 11:00", "2025-05-11 12:30", 1, "completed", "2"));
//
//        // 8. 防疫志愿服务
//        activities.add(new Activity(8, "校园防疫先锋队", "", "校医院",
//                "协助体温检测、防疫物资分发",
//                8, 8, "2025-05-09 07:30", "2025-05-09 18:00", 5, "completed", "3"));        return activities;
//    }
}
