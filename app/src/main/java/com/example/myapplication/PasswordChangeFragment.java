package com.example.myapplication;

import android.annotation.SuppressLint;
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

public class PasswordChangeFragment extends Fragment {
    String Username;
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_change, container, false);

        // 绑定视图
        EditText oldPasswordEditText = view.findViewById(R.id.old_password_edit_text);
        EditText newPasswordEditText = view.findViewById(R.id.new_password_edit_text);
        EditText confirmPasswordEditText = view.findViewById(R.id.confirm_password_edit_text);
        Button saveButton = view.findViewById(R.id.save_button);
        TextView errorText = view.findViewById(R.id.error_text);

        // 初始化ViewModel
        VolunteerViewModel viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);
        // 原来的名字
        viewModel.getUsername().observe(getViewLifecycleOwner(), name -> {
            Username = name;
            Log.d("DEBUG", "获取到用户名: " + Username);
            // 在这里更新UI或执行后续操作
        });
        // 保存按钮
        saveButton.setOnClickListener(v -> {
            String oldPassword = oldPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                errorText.setText("请填写所有字段");
                return;
            }
            if (newPassword.length() < 8 || newPassword.length() > 16 || !newPassword.matches("^[a-zA-Z0-9]+$")) {
                errorText.setText("新密码必须为8-16个字符，仅限字母和数字");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                errorText.setText("新密码与确认密码不一致");
                return;
            }

            Toast.makeText(requireContext(), "密码修改成功", Toast.LENGTH_SHORT).show();
            // TODO: 实现密码修改逻辑
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            boolean success = dbHelper.changePassword(Username, oldPassword, newPassword);
            if (success) {
                viewModel.setPassWord(newPassword);
                Toast.makeText(requireContext(), "密码修改成功", Toast.LENGTH_SHORT).show();      NavController navController = Navigation.findNavController(view);
                navController.popBackStack(); // 返回上一页
            } else {
                errorText.setText("旧密码不正确");
            }
        });

        return view;
    }
}
