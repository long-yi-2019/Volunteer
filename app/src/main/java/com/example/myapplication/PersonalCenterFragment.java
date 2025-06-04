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

import com.example.myapplication.Entity.Account;
import com.example.myapplication.Entity.Activity;
import com.example.myapplication.Entity.Record;
import com.example.myapplication.Entity.ShowRecord;

import java.util.List;

public class PersonalCenterFragment extends Fragment {
    private String Username;
    private ActivityAdapter adapter;

    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(requireContext()); // 初始化
    }

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
        TextView placeholderText = view.findViewById(R.id.placeholder_text);
        View readyNumberLayout = view.findViewById(R.id.ready_number);
        RecyclerView recordsRecyclerView = view.findViewById(R.id.records_recycler_view);
        TextView recordTextView  =view.findViewById(R.id.record_placeholder_text);
        Button volunteerRecordButton = view.findViewById(R.id.volunteer_record_button);
        TextView realName =  view.findViewById(R.id.user_real_name);
        TextView allActivity =  view.findViewById(R.id.all_activity);
        TextView readyVerifyNumber = view.findViewById(R.id.ready_verify_number);
        Button recordButton = view.findViewById(R.id.record_button);
        TextView readyText = view.findViewById(R.id.ready_text);
        TextView timeText = view.findViewById(R.id.time_text);


        // 初始化ViewModel

        VolunteerViewModel viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);
        // 名字
        viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {
            Username = name;
            System.out.println("!!!!!!!!!!"+Username);
            timeText.setText("志愿时长："+ databaseHelper.getUserVolunteerTime(Username)+"h");
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recordsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {
            Username = name;
            // 在这里执行点击切换下方显示内容的操作
            if (Username != null) {
                readyNumberLayout.setOnClickListener(v -> {
                    // 点击 "待参加" 区域执行的操作
                    recordsRecyclerView.setVisibility(View.GONE);
                    recordTextView.setVisibility(View.GONE);
                    Account account = databaseHelper.selectAccount(Username);
                    if (account.getRole().equals("Admin")){
                        setupActivityRecyclerViewForAdmin(recyclerView,placeholderText,view);
                    }else{
                        setupActivityRecyclerView(Username, recyclerView, placeholderText, view);
                    }
                });
                verifyNumber.setOnClickListener(v -> {
                    // 点击 "待审核" 区域执行的操作
//                        Toast.makeText(getActivity(), "待审核区域被点击", Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility(View.GONE);
                    placeholderText.setVisibility(View.GONE);
                    setupRecordRecyclerView(Username, recordsRecyclerView, recordTextView, view);
                });
            }
        });

        // 设置角色显示
        viewModel.getUserRole().observe(getViewLifecycleOwner(), role -> {
            String s;
            if (role.equals("Volunteer")){
                s = "志愿者";
                setupActivityRecyclerView(Username,  recyclerView, placeholderText, view);
            } else if (role.equals("Organizer")) {
                s="组织者";
                allActivity.setText("全部已发布活动");
                recordButton.setText("全部记录");
                timeText.setVisibility(View.GONE);
                setupRecordRecyclerViewByHostId(Username, recordsRecyclerView, recordTextView, view);
                readyVerifyNumber.setText(String.valueOf(databaseHelper.getOrganizerAllRecordsReady(Username)));
            }else {
                s="管理员";
                allActivity.setText("全部活动");
                recordButton.setText("全部记录");
                readyText.setText("待审核活动");
                timeText.setVisibility(View.GONE);
                setupActivityRecyclerViewForAdmin(recyclerView,placeholderText,view);
                readyVerifyNumber.setText(String.valueOf(databaseHelper.getAllRecordsReady()));
            }
            roleTextView.setText("角色: " + s);
            readyNumberLayout.setVisibility(role.equals("Organizer")?View.GONE:View.VISIBLE);
            volunteerRecordButton.setVisibility(role.equals("Volunteer")?View.VISIBLE:View.GONE);
            publishButton.setVisibility(role.equals("Organizer") || role.equals("Admin") ? View.VISIBLE : View.GONE);
            verifyNumber.setVisibility(role.equals("Volunteer") ? View.GONE : View.VISIBLE);
        });

//  设置红点的数字
        viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {
            userName.setText(name);
            System.out.println(databaseHelper.selectAccount(name).toString());
            String real_name = databaseHelper.selectAccount(name).getName();
            System.out.println(real_name);
            if(real_name != null && !real_name.isEmpty() && !real_name.equals(" ")){
                realName.setText("姓名："+ real_name);
            }else{
                realName.setText("姓名：暂无，请登记" );
            }

            System.out.println(databaseHelper.getActivityNumberByUserName(name));
            Account account = databaseHelper.selectAccount(name);
            if (account.getRole().equals("Admin")){
                readyPointNumber.setText(String.valueOf(databaseHelper.getActivityNumberWaitForVerify()));
            }else {
                readyPointNumber.setText(String.valueOf(databaseHelper.getActivityNumberByUserName(name)));
            }
        });


        // 导航按钮
        personalInfoButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_personalInfoFragment));
        publishButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_activityPublishFragment));
        volunteerRecordButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_volunteerRecordFragment));
        allActivity.setOnClickListener(v->Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_activityAllActivityFragment));
        recordButton.setOnClickListener(v->Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_recordListFragment));
        return view;
    }


    private void setupActivityRecyclerViewForAdmin(RecyclerView recyclerView, TextView placeholderText,View view){
        List<Activity> activities = databaseHelper.selectActivitiesByState();
        if (activities != null && !activities.isEmpty()) {
            placeholderText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            ActivityAdapter adapter = new ActivityAdapter(activities, activity -> {
                Bundle bundle = new Bundle();
                bundle.putString("activity_name", activity.getName());
                bundle.putString("activity_location", activity.getArea());
                bundle.putString("activity_time", activity.getBeginTime());
                bundle.putString("activity_duration", String.valueOf(activity.getVolunteerTime()));
                bundle.putInt("activity_id", activity.getId());
                bundle.putString("activity_state", activity.getState());
                Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_activityDetailsFragment, bundle);
            });
            recyclerView.setAdapter(adapter);
        } else {
            placeholderText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        databaseHelper.close();
    }
    @Override
    public void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close(); // 确保关闭
        }
        super.onDestroy();
    }

//    这一段代码用来生成志愿者个人中心界面的活动列表
    private void setupActivityRecyclerView(String username, RecyclerView recyclerView, TextView placeholderText, View view) {
        List<Activity> activities = databaseHelper.getActivitiesByUsername(username);
        if (activities != null && !activities.isEmpty()) {
            placeholderText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            ActivityAdapter adapter = new ActivityAdapter(activities, activity -> {
                Bundle bundle = new Bundle();
                bundle.putString("activity_name", activity.getName());
                bundle.putString("activity_location", activity.getArea());
                bundle.putString("activity_time", activity.getBeginTime());
                bundle.putString("activity_duration", String.valueOf(activity.getVolunteerTime()));
                bundle.putInt("activity_id", activity.getId());
                bundle.putString("activity_state", activity.getState());
                Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_activityDetailsFragment, bundle);
            });
            recyclerView.setAdapter(adapter);
        } else {
            placeholderText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        databaseHelper.close();
    }
    private void setupRecordRecyclerView(String username, RecyclerView recyclerView, TextView placeholderText, View view) {
        List<ShowRecord> records = databaseHelper.selectRecordByState0();
        if (records != null && !records.isEmpty()) {
            placeholderText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            RecordAdapter adapter = new RecordAdapter(records, record -> {
                Bundle bundle = new Bundle();
                bundle.putString("username", record.getUserId());
                bundle.putString("activity_name", record.getActivityName());
                bundle.putString("date", record.getDate());
                bundle.putInt("record_id",record.getId());
                bundle.putString("volunteer_time",  record.getVolunteerTime().toString()+"（小时）");
                bundle.putInt("state",record.getState());
                Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_recordDetailsFragment, bundle);
            });
            recyclerView.setAdapter(adapter);
        } else {
            placeholderText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void setupRecordRecyclerViewByHostId(String username, RecyclerView recyclerView, TextView placeholderText, View view) {
        List<ShowRecord> records = databaseHelper.selectRecordByHostId(username);
        if (records != null && !records.isEmpty()) {
            placeholderText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            RecordAdapter adapter = new RecordAdapter(records, record -> {
                Bundle bundle = new Bundle();
                bundle.putString("username", record.getUserId());
                bundle.putString("activity_name", record.getActivityName());
                bundle.putString("date", record.getDate());
                bundle.putInt("record_id",record.getId());
                bundle.putString("volunteer_time",  record.getVolunteerTime().toString()+"（小时）");
                bundle.putInt("state",record.getState());
                Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_recordDetailsFragment, bundle);
            });
            recyclerView.setAdapter(adapter);
        } else {
            placeholderText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}
