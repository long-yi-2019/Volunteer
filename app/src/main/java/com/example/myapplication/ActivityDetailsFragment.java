package com.example.myapplication;

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

public class ActivityDetailsFragment extends Fragment {
    String currentUser;
    int activityId;
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
        Button approveButton = view.findViewById(R.id.approve_button);
        Button rejectButton = view.findViewById(R.id.reject_button);
        LinearLayout buttonContainer = view.findViewById(R.id.button_container);
        // 获取传递的数据
        Bundle args = getArguments();
        if (args != null) {
            nameTextView.setText(args.getString("activity_name", "未知活动"));
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            Activity activityName = dbHelper.selectActivityByName(args.getString("activity_name"));
            descriptionTextView.setText(activityName.getContent());
            locationTextView.setText(activityName.getArea());
            timeTextView.setText(activityName.getBeginTime());
            durationTextView.setText(activityName.getEndTime());
            dbHelper.close();
        }



        // 预约按钮
        bookButton.setOnClickListener(v -> {
//            Toast.makeText(requireContext(), "已预约: " + nameTextView.getText(), Toast.LENGTH_SHORT).show();
            // TODO: 实现预约逻辑

            // 名字
            viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {
                currentUser = name;
                Log.d("DEBUG", "获取到用户名: " + currentUser);
                // 在这里更新UI或执行后续操作
            });
            DatabaseHelper db = new DatabaseHelper(requireContext());
            int result = db.registerActivityWithChecks(currentUser, activityId);
            switch (result) {
                case 1:
                    Toast.makeText(requireContext(), "预约成功" , Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_activitySearchFragment_to_activityDetailsFragment); break;
                case 0:  errorText.setText("您已预约过该活动");return;
                case -1:  errorText.setText("活动名额已满"); return;
                case -2:  errorText.setText("活动已结束"); return;
                default:  errorText.setText("预约失败");return;
            }
            db.close();

        });


        //如果是志愿者，预约活动后，则隐藏bookButton
        //如果是管理员，隐藏bookButton，显示buttonContainer，编写approveButton和rejectButton的点击事件
        viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {

        });
        return view;
    }
}
