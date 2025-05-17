package com.example.myapplication.Entity;

public class Activity {
    private int id;
    private String name;
    private String picture;
    private String area;
    private String content;
    private int count;
    private int actualCount;
    private String beginTime;
    private String endTime;
    private int volunteerTime;
    private String state;
    private String hostId;

    // 无参构造方法
    public Activity() {
    }

    // 全参构造方法
    public Activity(int id, String name, String picture, String area, String content,
                    int count, int actualCount, String beginTime, String endTime,
                    int volunteerTime, String state, String hostId) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.area = area;
        this.content = content;
        this.count = count;
        this.actualCount = actualCount;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.volunteerTime = volunteerTime;
        this.state = state;
        this.hostId = hostId;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getActualCount() {
        return actualCount;
    }

    public void setActualCount(int actualCount) {
        this.actualCount = actualCount;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getVolunteerTime() {
        return volunteerTime;
    }

    public void setVolunteerTime(int volunteerTime) {
        this.volunteerTime = volunteerTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", area='" + area + '\'' +
                ", content='" + content + '\'' +
                ", count=" + count +
                ", actualCount=" + actualCount +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", volunteerTime=" + volunteerTime +
                ", state='" + state + '\'' +
                ", hostId=" + hostId +
                '}';
    }
}