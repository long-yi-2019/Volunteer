package com.example.myapplication;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class PersonalInfoFragment extends Fragment {
    String oldUsername;
    TextView errorText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view= inflater.inflate(R.layout.fragment_personal_info, container, false);

        // 绑定视图
        EditText usernameEditText = view.findViewById(R.id.username_edit_text);
        EditText nameEditText = view.findViewById(R.id.name_edit_text);
        Button saveButton = view.findViewById(R.id.save_button);
        Button passWordChangeButton = view.findViewById(R.id.password_change_button);
        errorText= view.findViewById(R.id.error_text);
        // 初始化ViewModel
        VolunteerViewModel viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);
        // 原来的名字
        viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {
            oldUsername = name;
            Log.d("DEBUG", "获取到用户名: " + oldUsername);
            // 在这里更新UI或执行后续操作
        });
        // 保存按钮
        saveButton.setOnClickListener(v -> {

            String username = usernameEditText.getText().toString();
            String name = nameEditText.getText().toString();

            if (username.isEmpty() || name.isEmpty()) {
                Toast.makeText(requireContext(), "请填写所有字段", Toast.LENGTH_SHORT).show();
            } else {
                String newUsername = usernameEditText.getText().toString().trim();
                String newName = nameEditText.getText().toString().trim();
                // 验证输入
                if (newUsername.isEmpty()) {
                    errorText.setText("用户名不能为空");
                    return;
                }

                if (newName.isEmpty()) {
                    errorText.setText("姓名不能为空");
                    return;
                }

                // 检查用户名是否更改
                if (newUsername.equals(oldUsername)){
                    // 只更新姓名
                    if(updateNameOnly(newName))
                    {

                    }
                    else {
                        errorText.setText("更新失败");
                        return;
                    }
                } else {
                    // 更新用户名和姓名
                   if( updateUsernameAndName(newUsername, newName))
                   {
                       viewModel.setUsername(newUsername);


                   }
                   else {
                       errorText.setText("用户名已存在");
                       return;
                   }
                }
            }

            NavController navController = Navigation.findNavController(view);
            navController.popBackStack(); // 返回上一页
//
        });
        passWordChangeButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_personalInfoFragment_to_passwordChangeFragment));

        return view;
    }

    private boolean updateNameOnly(String newName) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        ContentValues values = new ContentValues();
        values.put("name", newName);
        int rowsAffected = dbHelper.getWritableDatabase().update(
                "account",
                values,
                "username = ?",
                new String[]{oldUsername}
        );

        dbHelper.close();

        if (rowsAffected > 0) {
            // 更新成功，返回上一页
            System.out.println(rowsAffected);
           return true;
        } else {
            return false;
        }
    }

    private boolean updateUsernameAndName(String newUsername, String newName) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        boolean success = dbHelper.updateUsernameAndName(oldUsername, newUsername, newName);
        dbHelper.close();
        if (success) {
            // 更新成功，保存新用户名并返回

            // 导航到登录界面
            return true;
                 }
        else {
            return false;
        }
    }
}
