package com.example.myapplication.Entity;

import java.util.Date;

public class Record {
    private Integer id;
    private String picture;
    private Integer volunteerTime;
    private String date;
    private Integer state;
    private String userId;
    private Integer activityId;
    private String hostId;
    private String activityName;
    private String content;

    public Record() {
    }

    public Record(Integer id, String picture, Integer volunteerTime, String date, Integer state, String userId, Integer activityId, String hostId,String content) {
        this.id = id;
        this.picture = picture;
        this.volunteerTime = volunteerTime;
        this.date = date;
        this.state = state;
        this.userId = userId;
        this.activityId = activityId;
        this.hostId = hostId;
        this.content=content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getVolunteerTime() {
        return volunteerTime;
    }

    public void setVolunteerTime(Integer volunteerTime) {
        this.volunteerTime = volunteerTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", picture='" + picture + '\'' +
                ", volunteerTime=" + volunteerTime +
                ", date=" + date +
                ", state=" + state +
                ", userId=" + userId +
                ", activityId=" + activityId +
                ", hostId=" + hostId +
                ", activityName='" + activityName + '\'' +
                '}';
    }
    public String getContent()
    {
        return content;
    }
    public void setContent(String content) {
        this.content=content;

    }
}
