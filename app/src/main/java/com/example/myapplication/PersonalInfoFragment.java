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

public class PersonalInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);

        // 绑定视图
        EditText usernameEditText = view.findViewById(R.id.username_edit_text);
        EditText nameEditText = view.findViewById(R.id.name_edit_text);
        Button saveButton = view.findViewById(R.id.save_button);

        // 保存按钮
        saveButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String name = nameEditText.getText().toString();
            if (username.isEmpty() || name.isEmpty()) {
                Toast.makeText(requireContext(), "请填写所有字段", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "已保存: " + username + ", " + name, Toast.LENGTH_SHORT).show();
                // TODO: 实现保存逻辑
            }
        });

        return view;
    }
}
