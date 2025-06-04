package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.Entity.Activity;

import java.util.Objects;

public class RecordDetailsFragment extends Fragment {
    private String currentUser;
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_record_details, container, false);
        TextView activityNameTextView  = view.findViewById(R.id.activity_name_text);
        ImageView activityPictureImageView = view.findViewById(R.id.activity_picture);
        TextView userNameTextView = view.findViewById(R.id.user_name_text);
        TextView dateTextView = view.findViewById(R.id.record_time_text);
        TextView recordDurationTextView = view.findViewById(R.id.record_duration_text);
        TextView recordStateTextView = view.findViewById(R.id.record_state_text);
        Button approveButton = view.findViewById(R.id.approve_button);
        Button rejectButton = view.findViewById(R.id.reject_button);
        LinearLayout buttonContainer = view.findViewById(R.id.button_container);
        Bundle args = getArguments();
        int recordId;
        if (args != null) {
            activityNameTextView.setText(args.getString("activity_name", "未知活动"));
            userNameTextView.setText(args.getString("username", "未知用户"));
            dateTextView.setText(args.getString("date", "未知时间"));
            recordDurationTextView.setText(args.getString("volunteer_time", "未知时长"));
            recordId = args.getInt("record_id", 0);
            int state = args.getInt("state", 0);
            String stateText="待审核";
            int styleResId=R.style.StateTextPending;

            switch (state) {
                case 0:
                    stateText = "待审核";
                    styleResId = R.style.StateTextPending;
                    break;
                case 1:
                    stateText = "未通过";
                    styleResId = R.style.StateTextRejected;
                    break;
                case 2:
                    stateText = "已通过";
                    styleResId = R.style.StateTextApproved;
                    break;
                default:
                    break;
            }
            // 动态应用样式
            TextViewCompat.setTextAppearance(recordStateTextView, styleResId);
            recordStateTextView.setText(stateText);
        } else {
            recordId = 0;
        }
        VolunteerViewModel viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);
        viewModel.getUserRole().observe(getViewLifecycleOwner(), role -> {
            currentUser = role;
            Log.d("DEBUG","获取用户："+currentUser);
            DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());

            if (role.equals("Admin")){
                recordStateTextView.setVisibility(View.GONE);
                buttonContainer.setVisibility(View.VISIBLE);
                approveButton.setOnClickListener(v -> {
                    databaseHelper.updateRecordState(recordId,2);
                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                });
                rejectButton.setOnClickListener(v -> {
                    databaseHelper.updateRecordState(recordId, 1);
                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                });
            }
//            else{
//                int st = databaseHelper.selectRecordByRecordId(recordId).getState();
//                System.out.println(st);
//                if(st==0) .setVisibility(View.GONE);
//            }
            databaseHelper.close();
        });
        return view;
    }
}
