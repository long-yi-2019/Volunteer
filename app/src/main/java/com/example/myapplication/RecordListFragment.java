package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Entity.Activity;
import com.example.myapplication.Entity.Record;
import com.example.myapplication.Entity.ShowRecord;

import java.util.List;

public class RecordListFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecordAdapter adapter;
    private EditText searchEditText;
    private String userId;
//    private DatabaseHelper databaseHelper;
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        // 绑定视图
        searchEditText = view.findViewById(R.id.search_edit_text);
        recyclerView = view.findViewById(R.id.activities_recycler_view);
        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        VolunteerViewModel viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);
        viewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
            if (username != null) {
                System.out.println("username:" + username);
                userId = username;
            }
        });
        viewModel.getUserRole().observe(getViewLifecycleOwner(), role -> {
            if (role != null) {
                if (role.equals("Volunteer")) {
                    if (userId != null) {
                        System.out.println("username:" + userId);
                        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                        List<ShowRecord> records = dbHelper.getRecordsByUserId(userId);
                        RecordAdapter adapter = new RecordAdapter(records, record -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("username", record.getUserId());
                            bundle.putString("activity_name", record.getActivityName());
                            bundle.putString("date", record.getDate());
                            bundle.putInt("record_id", record.getId());
                            bundle.putString("volunteer_time", record.getVolunteerTime().toString() + "小时");
                            bundle.putInt("state", record.getState());
                            Navigation.findNavController(view).navigate(R.id.action_recordListFragment_to_recordDetailsFragment, bundle);
                        });
                        recyclerView.setAdapter(adapter);
                        dbHelper.close();
                    }

                } else if (role.equals("Organizer")) {
                    if (userId != null) {
                        System.out.println("username:" + userId);
                        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                        List<ShowRecord> records = dbHelper.getRecordsHostId(userId);
                        RecordAdapter adapter = new RecordAdapter(records, record -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("username", record.getUserId());
                            bundle.putString("activity_name", record.getActivityName());
                            bundle.putString("date", record.getDate());
                            bundle.putInt("record_id", record.getId());
                            bundle.putString("volunteer_time", record.getVolunteerTime().toString() + "小时");
                            bundle.putInt("state", record.getState());
                            Navigation.findNavController(view).navigate(R.id.action_recordListFragment_to_recordDetailsFragment, bundle);
                        });
                        recyclerView.setAdapter(adapter);
                        dbHelper.close();
                    }
                } else {
                    DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                    List<ShowRecord> records = dbHelper.getAllRecords();
                    RecordAdapter adapter = new RecordAdapter(records, record -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("username", record.getUserId());
                        bundle.putString("activity_name", record.getActivityName());
                        bundle.putString("date", record.getDate());
                        bundle.putInt("record_id", record.getId());
                        bundle.putString("volunteer_time", record.getVolunteerTime().toString() + "小时");
                        bundle.putInt("state", record.getState());
                        Navigation.findNavController(view).navigate(R.id.action_recordListFragment_to_recordDetailsFragment, bundle);
                    });
                    recyclerView.setAdapter(adapter);
                    dbHelper.close();
                }
            }
        });


        view.findViewById(R.id.search_button).setOnClickListener(v -> {
            // 获取搜索框中的文本
            String query = searchEditText.getText().toString();
            // 显示提示信息，反馈用户输入的搜索内容
            Toast.makeText(requireContext(), "搜索: " + query, Toast.LENGTH_SHORT).show();
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            // 实现搜索过滤逻辑
            List<ShowRecord> searchResults = dbHelper.selectRecordByActivityName(query);
            if (searchResults.isEmpty()) {
                // 若未找到相关记录，提示用户
                Toast.makeText(requireContext(), "未找到相关记录", Toast.LENGTH_SHORT).show();
            } else {
                // 找到记录时，提示用户找到的记录数量
                Toast.makeText(requireContext(), "找到" + searchResults.size() + "个活动", Toast.LENGTH_SHORT).show();

                // 获取RecyclerView
                RecyclerView recyclerView = view.findViewById(R.id.activities_recycler_view);
                // 创建RecordAdapter并传入搜索结果和点击事件处理逻辑
                adapter = new RecordAdapter(searchResults, record -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("username", record.getUserId());
                    bundle.putString("activity_name", record.getActivityName());
                    bundle.putString("date", record.getDate());
                    bundle.putInt("record_id", record.getId());
                    bundle.putString("volunteer_time", record.getVolunteerTime().toString() + "小时");
                    bundle.putInt("state", record.getState());

                    // 执行页面跳转逻辑，将相关数据传递到活动详情页面
                    try {
                        Navigation.findNavController(view).navigate(R.id.action_personalCenterFragment_to_recordDetailsFragment, bundle);
                    } catch (Exception e) {
                        // 捕获跳转过程中的异常，打印错误日志，方便排查问题
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "页面跳转失败，请检查设置", Toast.LENGTH_SHORT).show();
                    }
                });
                // 为RecyclerView设置新的适配器，更新显示内容

                recyclerView.setAdapter(adapter);
            }
            dbHelper.close();
        });


        return view;
    }
}