package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.example.myapplication.Entity.TimeValidator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityPublishFragment extends Fragment {

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    String url = null;

    String state = "0";
    String username;

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        String fileName = saveImageToLocal(imageUri);
                        ImageView uploadImageView = getView().findViewById(R.id.upload_image_view);
                        uploadImageView.setImageURI(imageUri);
                        url = fileName;
                        // TODO: 这里你可以保存 imageUri 到变量用于后续上传或存储

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

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_publish, container, false);

        // 绑定视图
        EditText nameEditText = view.findViewById(R.id.activity_name_edit_text);
        EditText timeEditText = view.findViewById(R.id.activity_time_edit_text);
        EditText endTimeText = view.findViewById(R.id.activity_time_end_text);
        EditText positionText = view.findViewById(R.id.activity_position_edit_text);
        EditText peopleEditText = view.findViewById(R.id.activity_people_edit_text);
        EditText durationEditText = view.findViewById(R.id.activity_duration_edit_text);
        EditText descriptionEditText = view.findViewById(R.id.activity_description_edit_text);
        Button publishButton = view.findViewById(R.id.publish_button);
        FrameLayout uploadContainer = view.findViewById(R.id.upload_image_container);
        ImageView uploadImageView = view.findViewById(R.id.upload_image_view);
        setupImagePicker();
        timeEditText.setOnClickListener(v -> showDateTimePicker(timeEditText));
        endTimeText.setOnClickListener(v -> showDateTimePicker(endTimeText));
        uploadContainer.setOnClickListener(v -> openImageChooser());
        VolunteerViewModel viewModel = new ViewModelProvider(requireActivity())
                .get(VolunteerViewModel.class);


        // 发布按钮
        publishButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String time = timeEditText.getText().toString();
            String endTime = endTimeText.getText().toString();
            String area = positionText.getText().toString();
            String people = peopleEditText.getText().toString();
            String duration = durationEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String imageUrl = (url != null) ? url : null;


            if (name.isEmpty() || time.isEmpty() || people.isEmpty() || duration.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "请填写所有字段", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TimeValidator.isValidFormat(time)) {
                timeEditText.setError("时间格式必须为: YYYY-MM-DD HH:MM");
                timeEditText.requestFocus();
                return;
            }
            // TODO: 实现活动发布逻辑


            try {
                // 3. 转换数据类型
                int count = Integer.parseInt(people);
                int volunteertime = Integer.parseInt(duration);

                // 4. 创建Activity对象
                Activity newActivity = new Activity();
                newActivity.setName(name);
                newActivity.setBeginTime(time);
                newActivity.setEndTime(endTime);
                newActivity.setArea(area);
                newActivity.setCount(count);
                newActivity.setContent(description);
                newActivity.setVolunteerTime(volunteertime);
                newActivity.setPicture(imageUrl);
                newActivity.setState(state);
                viewModel.getUsername().observe(getViewLifecycleOwner(),username -> {
                    Log.d("DEBUG",username);
                    newActivity.setHostId(username);
                });

                // 5. 存入数据库
                DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                long result = dbHelper.addActivity(newActivity);

                if (result != -1) {
                    Toast.makeText(requireContext(),
                            "活动发布成功！ID: " + result,
                            Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(view);
                    navController.popBackStack(); // 返回上一页
                } else {
                    Toast.makeText(requireContext(),
                            "发布失败，请重试",
                            Toast.LENGTH_SHORT).show();
                }
                dbHelper.close();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(),
                        "人数和时长必须为数字",
                        Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void showDateTimePicker(EditText timeEditText) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            requireContext(),
                            (timePicker, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                timeEditText.setText(sdf.format(calendar.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }



    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

}
