package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Entity.Activity;

import java.util.List;


public class AllActivityFragment extends Fragment {
    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private EditText searchEditText;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_activity, container, false);
        VolunteerViewModel viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);

        searchEditText = view.findViewById(R.id.search_edit_text);
        recyclerView = view.findViewById(R.id.activities_recycler_view);
        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        viewModel.getUserRole().observe(getViewLifecycleOwner(), role -> {
            List<Activity> activities=null;
            if (role.equals("Volunteer")){
                activities = dbHelper.getActivityList();
            } else if (role.equals("Organizer")) {
                activities = dbHelper.getActivityList();

            }else {
                activities = dbHelper.getActivityList();
            }
            adapter = new ActivityAdapter(activities, activity -> {
                // 导航到活动详情
                Bundle bundle = new Bundle();
                bundle.putString("activity_name", activity.getName());
                bundle.putString("activity_location", activity.getArea());
                bundle.putString("activity_time", activity.getBeginTime());
                bundle.putString("activity_duration", String.valueOf(activity.getVolunteerTime()));
                bundle.putInt("activity_id", activity.getId());
                Navigation.findNavController(view).navigate(R.id.action_allActivityFragment_to_activityDetailsFragment, bundle);
            });
            recyclerView.setAdapter(adapter);

        });

        // 搜索按钮
        view.findViewById(R.id.search_button).setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            Toast.makeText(requireContext(), "搜索: " + query, Toast.LENGTH_SHORT).show();

            // TODO: 实现搜索过滤逻辑
        });


        return view;
    }
}
