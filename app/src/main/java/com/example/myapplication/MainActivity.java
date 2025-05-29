package com.example.myapplication;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取导航控制器和底部导航栏
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        bottomNav = findViewById(R.id.bottom_navigation);

        // 设置导航联动
        NavigationUI.setupWithNavController(bottomNav, navController);

        // 监听页面切换事件
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // 如果当前页面是登录页或注册页，则隐藏底部导航栏
            if (destination.getId() == R.id.loginFragment ||
                    destination.getId() == R.id.registerFragment) {
                bottomNav.setVisibility(View.GONE);
            } else {
                // 其他页面显示底部导航栏
                bottomNav.setVisibility(View.VISIBLE);
            }
        });
    }
}