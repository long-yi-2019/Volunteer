package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.Entity.Account;

public class RegisterFragment extends Fragment {

    private VolunteerViewModel viewModel;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // 初始化ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);

        // 绑定视图
        EditText usernameEditText = view.findViewById(R.id.username_edit_text);
        EditText passwordEditText = view.findViewById(R.id.password_edit_text);
        EditText confirmPasswordEditText = view.findViewById(R.id.confirm_password_edit_text);
        Spinner identitySpinner = view.findViewById(R.id.identity_spinner);
        Button registerButton = view.findViewById(R.id.register_button);
        TextView errorText = view.findViewById(R.id.error_text);

        // 设置身份选择下拉菜单
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"Volunteer", "Organizer", "Admin"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        identitySpinner.setAdapter(adapter);

        // 注册按钮
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            String selectedIdentity = identitySpinner.getSelectedItem().toString();

            // 验证输入
            if (username.length() < 6 || username.length() > 20 || !username.matches("^[a-zA-Z0-9]+$")) {
                errorText.setText("用户名必须为6-20个字符，仅限字母和数字");
                return;
            }
            if (password.length() < 8 || password.length() > 16 || !password.matches("^[a-zA-Z0-9]+$")) {
                errorText.setText("密码必须为8-16个字符，仅限字母和数字");
                return;
            }
            if (!password.equals(confirmPassword)) {
                errorText.setText("两次输入的密码不一致");
                return;
            }


            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            if (dbHelper.isUsernameExists(username)) {
                errorText.setText("用户名已存在");
                dbHelper.closeDatabase();
                return;
            }
            dbHelper.closeDatabase();
            Account new_volunteer=new Account();
            new_volunteer.setUsername(username);
            new_volunteer.setPassword(password);
            new_volunteer.setRole(selectedIdentity);
            long result=dbHelper.register(new_volunteer);
            if (result != -1) {
                // 注册成功，更新UI和导航
            }
            else if(result == -2)
            {
                errorText.setText("账号已被注册");
                return;
            }
            else
            {
                errorText.setText("注册失败");
                return;
            }

            // 保存身份并模拟注册
            viewModel.setSelectedIdentity(selectedIdentity);
            viewModel.setUserRole(selectedIdentity);

            // 导航到登录界面
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_registerFragment_to_loginFragment);
        });

        return view;
    }
}
