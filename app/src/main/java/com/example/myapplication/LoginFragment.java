package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class LoginFragment extends Fragment {

    private VolunteerViewModel viewModel;
    private boolean passwordVisible = false;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // 初始化ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);

        // 绑定视图
        EditText usernameEditText = view.findViewById(R.id.username_edit_text);
        EditText passwordEditText = view.findViewById(R.id.password_edit_text);
        ImageView passwordToggle = view.findViewById(R.id.password_toggle);
        Spinner identitySpinner = view.findViewById(R.id.identity_spinner);
        Button loginButton = view.findViewById(R.id.login_button);
        TextView registerLink = view.findViewById(R.id.register_link);
        TextView errorText = view.findViewById(R.id.error_text);
        // 设置身份选择下拉菜单
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"Volunteer", "Organizer", "Admin"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        identitySpinner.setAdapter(adapter);

        // 密码可见性切换
        passwordToggle.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_visibility);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_visibility_off);
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // 登录按钮
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String selectedIdentity = identitySpinner.getSelectedItem().toString();
            viewModel.setSelectedIdentity(selectedIdentity);
            viewModel.setUserRole(selectedIdentity);
            viewModel.setUsername(username);
            viewModel.setPassWord(password);
                    String role=selectedIdentity;
                    // 简单验证
                    if (username.isEmpty() || password.isEmpty()) {
                        errorText.setText("用户名和密码不能为空");
                        return;
                    }

                    // 验证登录
                    DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                    boolean loginSuccess = dbHelper.login(username, password,role);
//                    dbHelper.closeDatabase();
                    if (loginSuccess) {
                        // 登录成功，跳转到主界面
                        NavController navController = Navigation.findNavController(view);
                        navController.navigate(R.id.action_loginFragment_to_activitySearchFragment);;
                    } else {
                        errorText.setText("用户名或密码或角色错误");
                    }

            // 模拟登录，导航到活动搜索

        });

        // 注册链接
        registerLink.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });

        return view;
    }
}