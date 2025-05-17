package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.Entity.Activity;
import com.example.myapplication.Entity.TimeValidator;

public class ActivityPublishFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_publish, container, false);

        // 绑定视图
        EditText nameEditText = view.findViewById(R.id.activity_name_edit_text);
        EditText timeEditText = view.findViewById(R.id.activity_time_edit_text);
        EditText peopleEditText = view.findViewById(R.id.activity_people_edit_text);
        EditText durationEditText = view.findViewById(R.id.activity_duration_edit_text);
        EditText descriptionEditText = view.findViewById(R.id.activity_description_edit_text);
        Button publishButton = view.findViewById(R.id.publish_button);

        // 发布按钮
        publishButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String time = timeEditText.getText().toString();
            String people = peopleEditText.getText().toString();
            String duration = durationEditText.getText().toString();
            String description = descriptionEditText.getText().toString();


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
//                newActivity.setArea(area);
                newActivity.setBeginTime(time);
                newActivity.setCount(count);
                newActivity.setContent(description);
                newActivity.setVolunteerTime(volunteertime);


                // 5. 从ViewModel获取主办方ID
                VolunteerViewModel viewModel = new ViewModelProvider(requireActivity())
                        .get(VolunteerViewModel.class);
                newActivity.setHostId(String.valueOf(viewModel.getUsername()));
                // 6. 存入数据库
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
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(),
                        "人数和时长必须为数字",
                        Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}
