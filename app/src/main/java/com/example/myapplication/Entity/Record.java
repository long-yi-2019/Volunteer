package com.example.myapplication.Entity;

import java.util.Date;

public class Record {
    private Integer id;
    private String picture;
    private Double volunteerTime;
    private Date date;
    private Integer state;
    private Integer userId;
    private Integer activityId;
    private Integer hostId;

    public Record() {
    }

    public Record(Integer id, String picture, Double volunteerTime, Date date, Integer state, Integer userId, Integer activityId, Integer hostId) {
        this.id = id;
        this.picture = picture;
        this.volunteerTime = volunteerTime;
        this.date = date;
        this.state = state;
        this.userId = userId;
        this.activityId = activityId;
        this.hostId = hostId;
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

    public Double getVolunteerTime() {
        return volunteerTime;
    }

    public void setVolunteerTime(Double volunteerTime) {
        this.volunteerTime = volunteerTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
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
                '}';
    }
}
