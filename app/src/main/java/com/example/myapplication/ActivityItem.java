package com.example.myapplication;

public class ActivityItem {
    private String name;
    private String location;
    private String time;
    private String duration;

    public ActivityItem(String name, String location, String time, String duration) {
        this.name = name;
        this.location = location;
        this.time = time;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public String getDuration() {
        return duration;
    }
}