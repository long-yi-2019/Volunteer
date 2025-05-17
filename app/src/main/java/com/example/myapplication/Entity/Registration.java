package com.example.myapplication.Entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Registration {
    private int id;
    private int activityId;
    private String username;
    private String registerTime; // 格式：yyyy-MM-dd HH:mm:ss

    // 构造方法
    public Registration() {}

    public Registration(int activityId, String username) {
        this.activityId = activityId;
        this.username = username;
        this.registerTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getActivityId() { return activityId; }
    public void setActivityId(int activityId) { this.activityId = activityId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRegisterTime() { return registerTime; }
    public void setRegisterTime(String registerTime) { this.registerTime = registerTime; }

    @Override
    public String toString() {
        return "Registration{" +
                "id=" + id +
                ", activityId=" + activityId +
                ", username='" + username + '\'' +
                ", registerTime='" + registerTime + '\'' +
                '}';
    }
}