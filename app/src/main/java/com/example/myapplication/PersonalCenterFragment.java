package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

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
        // 初始化ViewModel
        VolunteerViewModel viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);
        // 名字
        viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {
            Username = name;
            System.out.println("!!!!!!!!!!"+Username);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recordsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {
            Username = name;
            Log.d("PersonalCenter", "当前用户名: " + Username); // 使用Log代替System.out

            // 在这里执行依赖用户名的操作
            if (Username != null) {
                readyNumberLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 点击 "待参加" 区域执行的操作
//                        Toast.makeText(getActivity(), "待参加区域被点击", Toast.LENGTH_SHORT).show();
                        recordsRecyclerView.setVisibility(View.GONE);
                        recordTextView.setVisibility(View.GONE);
                        setupActivityRecyclerView(Username,  recyclerView, placeholderText, view);
                    }
                });

                verifyNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 点击 "待审核" 区域执行的操作
//                        Toast.makeText(getActivity(), "待审核区域被点击", Toast.LENGTH_SHORT).show();
                        recyclerView.setVisibility(View.GONE);
                        placeholderText.setVisibility(View.GONE);
                        setupRecordRecyclerView(Username, recordsRecyclerView, recordTextView, view);
                    }
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
                setupRecordRecyclerView(Username, recordsRecyclerView, recordTextView, view);
            }else {
                s="管理员";
                allActivity.setText("全部活动");
                recordButton.setText("全部记录");
                readyText.setText("待审核活动");
                setupRecordRecyclerView(Username, recordsRecyclerView, recordTextView, view);
            }
            roleTextView.setText("角色: " + s);
//            readyNumberLayout.setVisibility(role.equals("Organizer")?View.GONE:View.VISIBLE);
            volunteerRecordButton.setVisibility(role.equals("Volunteer")?View.VISIBLE:View.GONE);
            publishButton.setVisibility(role.equals("Organizer") || role.equals("Admin") ? View.VISIBLE : View.GONE);
            verifyNumber.setVisibility(role.equals("Admin") ? View.VISIBLE : View.GONE);
        });

        viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            userName.setText(name);
            System.out.println(dbHelper.selectAccount(name).toString());
            String real_name = dbHelper.selectAccount(name).getName();
            System.out.println(real_name);
            if(real_name != null && !real_name.isEmpty() && !real_name.equals(" ")){
                realName.setText("姓名："+ real_name);
            }else{
                realName.setText("姓名：暂无，请登记" );
            }

            System.out.println(dbHelper.getActivityNumberByUserName(name));
            readyPointNumber.setText(String.valueOf(dbHelper.getActivityNumberByUserName(name)));
            readyVerifyNumber.setText(String.valueOf(0));
        });


        // 导航按钮
        personalInfoButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_personalInfoFragment));
        publishButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_activityPublishFragment));
        volunteerRecordButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_volunteerRecordFragment));
        allActivity.setOnClickListener(v->Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_activityAllActivityFragment));
        recordButton.setOnClickListener(v->Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_recordListFragment));
        return view;
    }
    private void setupActivityRecyclerView(String username, RecyclerView recyclerView, TextView placeholderText, View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        List<Activity> activities = dbHelper.getActivitiesByUsername(username);
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
                Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_activityDetailsFragment, bundle);
            });
            recyclerView.setAdapter(adapter);
        } else {
            placeholderText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
    private void setupRecordRecyclerView(String username, RecyclerView recyclerView, TextView placeholderText, View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        List<ShowRecord> records = dbHelper.selectRecordByUserName(username);
        if (records != null && !records.isEmpty()) {
            placeholderText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            RecordAdapter adapter = new RecordAdapter(records, record -> {
                Bundle bundle = new Bundle();
                bundle.putString("activity_name", record.getActivityName());
                bundle.putString("activity_location", record.getActivityName());
                bundle.putString("activity_time", record.getDate().toString());
                bundle.putInt("record_id",record.getId());
            });
            recyclerView.setAdapter(adapter);
        } else {
            placeholderText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

}
