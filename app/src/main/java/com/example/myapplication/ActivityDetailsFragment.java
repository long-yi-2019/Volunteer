package com.example.myapplication;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.Entity.Activity;

import java.util.Objects;

public class ActivityDetailsFragment extends Fragment {
    String currentUser;
    int activityId;
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
             activityId = getArguments().getInt("activity_id", -1); // -1是默认值
        }
        View view = inflater.inflate(R.layout.fragment_activity_details, container, false);
        // 初始化ViewModel
        VolunteerViewModel viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);
        // 绑定视图
        TextView nameTextView = view.findViewById(R.id.activity_name_text);
        TextView locationTextView = view.findViewById(R.id.activity_location_text);
        TextView timeTextView = view.findViewById(R.id.activity_time_text);
        TextView durationTextView = view.findViewById(R.id.activity_duration_text);
        TextView descriptionTextView = view.findViewById(R.id.activity_description_text);
        Button bookButton = view.findViewById(R.id.book_button);
        TextView errorText = view.findViewById(R.id.error_text);
        TextView userText = view.findViewById(R.id.activity_user_text);
        Button approveButton = view.findViewById(R.id.approve_button);
        Button rejectButton = view.findViewById(R.id.reject_button);
        LinearLayout buttonContainer = view.findViewById(R.id.button_container);
        Button deleteButton = view.findViewById(R.id.delete_activity_button);
        // 获取传递的数据
        Bundle args = getArguments();
        if (args != null) {
            nameTextView.setText(args.getString("activity_name", "未知活动"));
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            Activity activityName = dbHelper.selectActivityByName(args.getString("activity_name"));
            descriptionTextView.setText(activityName.getContent());
            locationTextView.setText(activityName.getArea());
            timeTextView.setText(activityName.getBeginTime());
            durationTextView.setText(activityName.getEndTime()+"小时");
            userText.setText(activityName.getActualCount()+" / "+ activityName.getCount());
            dbHelper.close();
            if (!Objects.equals(args.getString("activity_state"), "0")){
                approveButton.setVisibility(View.GONE);
                rejectButton.setVisibility(View.GONE);
            }
        }



        // 预约按钮
        bookButton.setOnClickListener(v -> {
            bookButton.setEnabled(false); // 防止重复点击

            viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {
                currentUser = name;

                DatabaseHelper db = new DatabaseHelper(requireContext());
                int result = db.registerActivityWithChecks(currentUser, activityId);

                switch (result) {
                    case 1:
                        Toast.makeText(requireContext(), "预约成功", Toast.LENGTH_SHORT).show();
                        bookButton.setVisibility(View.GONE);
                        NavController navController = Navigation.findNavController(v);
                        navController.popBackStack(); // 返回上一页
                        break;
                    case 0:
                        errorText.setText("您已预约过该活动");
                        bookButton.setEnabled(true);
                        return;
                    case -1:
                        errorText.setText("活动名额已满");
                        bookButton.setEnabled(true);
                        return;
                    case -2:
                        errorText.setText("活动已结束");
                        bookButton.setEnabled(true);
                        return;
                    default:
                        errorText.setText("预约失败");
                        bookButton.setEnabled(true);
                        return;
                }
                db.close();
            });
        });
//        deleteButton.setVisibility(View.VISIBLE);
        deleteButton.setOnClickListener(v -> {
            DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
            new AlertDialog.Builder(requireContext())
                    .setTitle("确认删除")
                    .setMessage("确定要删除该活动吗？此操作不可恢复。")
                    .setPositiveButton("删除", (dialog, which) -> {
                        int result = databaseHelper.deleteActivity(activityId);
                        if (result > 0) {
                            Toast.makeText(requireContext(), "活动删除成功", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(v).popBackStack();
                        } else {
                            Toast.makeText(requireContext(), "删除失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
            databaseHelper.close();
        });

        //如果是志愿者，预约活动后，则隐藏bookButton
        //如果是管理员，隐藏bookButton，显示buttonContainer，编写approveButton和rejectButton的点击事件
        viewModel.getUserRole().observe(getViewLifecycleOwner(), role -> {
            currentUser = role;
            Log.d("DEBUG","获取用户："+currentUser);
            DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());

            if (role.equals("Admin")){
                bookButton.setVisibility(View.GONE);
                buttonContainer.setVisibility(VISIBLE);
                approveButton.setOnClickListener(v -> {
                    databaseHelper.updateActivityState(activityId,"2");
                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                });
                rejectButton.setOnClickListener(v -> {
                    databaseHelper.updateActivityState(activityId, "1");
                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                });
                deleteButton.setVisibility(VISIBLE);
            }
            else if(role.equals("Organizer"))
            {
                bookButton.setVisibility(INVISIBLE);
            }
            else{
                int st = databaseHelper.ActivityWithChecks(currentUser,activityId);
                System.out.println(st);
                if(st==0) bookButton.setVisibility(View.GONE);

            }
            databaseHelper.close();
        });
        return view;
    }
}
