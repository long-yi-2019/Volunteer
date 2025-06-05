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
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.myapplication.Entity.Record;
import com.example.myapplication.Entity.ShowRecord;

import java.io.File;

public class RecordDetailsFragment extends Fragment {
    private String currentUser;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_details, container, false);
        TextView activityNameTextView = view.findViewById(R.id.activity_name_text);
        ImageView activityPictureImageView = view.findViewById(R.id.activity_picture);
        TextView userNameTextView = view.findViewById(R.id.user_name_text);
        TextView dateTextView = view.findViewById(R.id.record_time_text);
        TextView recordDurationTextView = view.findViewById(R.id.record_duration_text);
        TextView recordStateTextView = view.findViewById(R.id.record_state_text);
        Button approveButton = view.findViewById(R.id.approve_button);
        Button rejectButton = view.findViewById(R.id.reject_button);
        LinearLayout buttonContainer = view.findViewById(R.id.button_container);
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        Bundle args = getArguments();
        System.out.println(args);
        int recordId;
        if (args != null) {
            ShowRecord recode = null;
            try {
                recode = databaseHelper.selectRecordByRecordId(args.getInt("record_id"));
                System.out.println(recode);
                if (recode == null) {
                    throw new IllegalArgumentException("记录不存在");
                }
            } catch (Exception e) {
                Log.e("RecordLoader", "获取记录失败: " + e.getMessage());
                // 跳过图片加载，继续执行后续代码
//                return;
            }

            String imagePathFromDb = recode.getPicture();

            if (imagePathFromDb == null || imagePathFromDb.isEmpty()) {
                Log.e("ImageLoader", "图片路径为空");
                // 可以选择跳过图片加载或显示默认图片
//                activityPictureImageView.setImageResource(R.drawable.ic_no_image);
                // 继续执行后续代码，不返回
            } else {
                try {
                    // 检查外部存储状态
                    String state1 = Environment.getExternalStorageState();
                    if (!Environment.MEDIA_MOUNTED.equals(state1)) {
                        Log.e("ImageLoader", "外部存储不可用: " + state1);
                        Toast.makeText(requireContext(), "存储不可用，请检查设置", Toast.LENGTH_SHORT).show();
                        // 继续执行后续代码，不返回
                    } else {
                        // 构建完整文件路径
                        File appImagesDir = new File(
                                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                "AppImages"
                        );
                        File imageFile = new File(appImagesDir, imagePathFromDb);

                        // 打印调试信息
                        Log.d("ImageLoader", "数据库路径: " + imagePathFromDb);
                        Log.d("ImageLoader", "完整路径: " + imageFile.getAbsolutePath());
                        Log.d("ImageLoader", "文件是否存在: " + imageFile.exists());

                        if (imageFile.exists()) {
                            Glide.with(requireContext())
                                    .load(imageFile)
                                    .into(activityPictureImageView);
                        } else {
                            Log.e("ImageLoader", "文件不存在，可能已被删除或移动");
//                            activityPictureImageView.setImageResource(R.drawable.ic_image_not_found);
                            Toast.makeText(requireContext(), "图片已丢失，请重新上传", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("ImageLoader", "加载图片时发生异常: " + e.getMessage());
                    // 显示默认图片
//                    activityPictureImageView.setImageResource(R.drawable.ic_error);
                    // 继续执行后续代码
                }
            }

            userNameTextView.setText(args.getString("username", "未知用户"));
            activityNameTextView.setText(recode.getActivityName());
            dateTextView.setText(args.getString("date", "未知时间"));
            recordDurationTextView.setText(args.getString("volunteer_time", "未知时长"));
            recordId = args.getInt("record_id", 0);
            int state = args.getInt("state", 0);
            String stateText = "待审核";
            int styleResId = R.style.StateTextPending;

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
            Log.d("DEBUG", "获取用户：" + currentUser);


            if (role.equals("Admin")) {
                recordStateTextView.setVisibility(View.GONE);
                buttonContainer.setVisibility(View.VISIBLE);
                approveButton.setOnClickListener(v -> {
                    databaseHelper.updateRecordState(recordId, 2);
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

        });
        databaseHelper.close();
        return view;
    }
}