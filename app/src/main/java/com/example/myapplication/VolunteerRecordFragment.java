package com.example.myapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.Entity.Activity;
import com.example.myapplication.Entity.Record;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VolunteerRecordFragment extends Fragment {
    String UserName;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    String url = null;

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        String fileName = saveImageToLocal(imageUri);
                        ImageView uploadImageView = requireView().findViewById(R.id.upload_image_view);
                        uploadImageView.setImageURI(imageUri);
                        // TODO: 这里你可以保存 imageUri 到变量用于后续上传或存储
                        url = fileName;
                    }
                }
        );
    }
    private String saveImageToLocal(Uri uri) {
        try {
            ContentResolver resolver = requireContext().getContentResolver();
            InputStream inputStream = resolver.openInputStream(uri);

            if (inputStream == null) {
                Toast.makeText(requireContext(), "无法读取图片", Toast.LENGTH_SHORT).show();
                return null;
            }

            // 创建目标文件名（你可以用时间戳或 UUID）
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "image_" + timeStamp + ".jpg";

            // 获取应用私有目录下的图片文件夹
            File dir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "AppImages");
            if (!dir.exists()) {
                dir.mkdirs(); // 创建文件夹
            }

            File file = new File(dir, fileName);
            FileOutputStream outputStream = new FileOutputStream(file);

            // 复制图片数据到文件
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            // 返回文件名，用于存入数据库
            return fileName;

        } catch (IOException e) {
            Log.d("error", String.valueOf(e));
            Toast.makeText(requireContext(), "保存图片失败", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_record, container, false);
        Spinner activitySpinner = view.findViewById(R.id.activity_spinner);
        FrameLayout uploadContainer = view.findViewById(R.id.upload_image_container);
        EditText timeView = view.findViewById(R.id.activity_time_edit_text);
        String time = timeView.getText().toString();
        Button submitButton = view.findViewById(R.id.submit_button);
        setupImagePicker();
        uploadContainer.setOnClickListener(v -> openImageChooser());
        // 创建一个用于提示的列表
        List<String> activityList = new ArrayList<>();
        activityList.add("请选择活动");
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        VolunteerViewModel viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);
        viewModel.getUsername().observe(getViewLifecycleOwner(), name->{
            UserName = name;
            List<Activity> activitiesByUsername = dbHelper.getActivitiesByUsername(UserName);
            if (activitiesByUsername != null && !activitiesByUsername.isEmpty()) {
                for (Activity act : activitiesByUsername) {
                    activityList.add(act.getName());
                }
            } else {
                // 如果没有活动，添加“无活动可选”
                activityList.add("无活动");
            }
        });
// 创建适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, activityList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// 设置适配器
        activitySpinner.setAdapter(adapter);

        submitButton.setOnClickListener(v -> {
            String activityName = activitySpinner.getSelectedItem().toString();
            String volunteerTime = timeView.getText().toString();
            String imageUrl = (url != null) ? url : null;
            if (activityName.equals("请选择活动") || volunteerTime.isEmpty()) {
                Toast.makeText(requireContext(), "请填写完整信息", Toast.LENGTH_SHORT).show();
            } else {
                // 创建一个记录对象
                Record record = new Record();
                record.setActivityName(activityName);
                record.setVolunteerTime(Integer.parseInt(volunteerTime));
                record.setState(0);
                record.setUserId(UserName);
                record.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                record.setActivityId(dbHelper.selectActivityByName(activityName).getId());
                record.setPicture(url);
                record.setHostId(dbHelper.selectActivityByName(activityName).getHostId());
                try {
                    // 5. 存入数据库
                    long result = dbHelper.addRecord(record);

                    if (result != -1) {
                        Toast.makeText(requireContext(),
                                "记录提交成功！ID: " + result,
                                Toast.LENGTH_SHORT).show();
                        NavController navController = Navigation.findNavController(view);
                        navController.popBackStack(); // 返回上一页
                    } else {
                        Toast.makeText(requireContext(),
                                "提交失败，请重试",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(),
                            "时长必须为数字",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        dbHelper.close();
        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}
