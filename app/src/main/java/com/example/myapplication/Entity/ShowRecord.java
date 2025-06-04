package com.example.myapplication.Entity;


import java.util.Date;

public class ShowRecord extends Record {
    private String username; // 新增：用户名（关联用户）

    public ShowRecord() {
        super(); // 调用父类无参构造
    }

    /**
     * 全参构造方法（包含父类所有字段 + 新增 username）
     */
    public ShowRecord(
            Integer id,
            String picture,
            Integer volunteerTime,
            String date,
            Integer state,
            String userId,
            Integer activityId,
            String hostId,
            String activityName,
            String content,
            String username // 新增用户名参数
    ) {
        super(id, picture, volunteerTime, date, state, userId, activityId, hostId, content);
        this.username = username;
        this.setActivityName(activityName); // 复用父类方法设置活动名称
    }

    // ---------------------
    // Getter 和 Setter
    // ---------------------

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // ---------------------
    // 重写 toString 方法（可选）
    // ---------------------
    @Override
    public String toString() {
        return super.toString() +
                ", username='" + username + '\'';
    }
}