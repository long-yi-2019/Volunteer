package com.example.myapplication;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myapplication.Entity.Account;
import com.example.myapplication.Entity.Activity;
import com.example.myapplication.Entity.DateTimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "main.db";
    private static final int DB_VERSION = 12;
    private final Context context;
    private String dbPath;
//     Account表结构
    private static final String CREATE_ACCOUNT_TABLE =
            "CREATE TABLE account (" +
                    "id INTEGER PRIMARY KEY," +
                    "username TEXT NOT NULL UNIQUE," +
                    "name TEXT ," +
                    "password TEXT NOT NULL," +
                    "avatar TEXT," +
                    "role TEXT NOT NULL DEFAULT 'volunteer'," +
                    "phone TEXT," +
                    "email TEXT" +
                    ")";
    // Activity表结构
    private static final String CREATE_ACTIVITY_TABLE =
            "CREATE TABLE activity (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT ," +
                    "picture TEXT," +
                    "area TEXT ," +
                    "content TEXT," +
                    "count INTEGER DEFAULT 0," +
                    "actual_count INTEGER DEFAULT 0," +
                    "begin_time TEXT ," +
                    "end_time TEXT ," +
                    "volunteer_time INTEGER DEFAULT 0," +
                    "state TEXT DEFAULT 'pending'," +
                    "hostid TEXT " + ")";
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.dbPath = context.getDatabasePath(DB_NAME).getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 先创建account表（因为activity表有外键依赖）
        db.execSQL(CREATE_ACCOUNT_TABLE);
        db.execSQL(CREATE_ACTIVITY_TABLE);
        // 预约记录表
        db.execSQL("CREATE TABLE registration (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "activity_id INTEGER NOT NULL," +
                "username TEXT NOT NULL," +
                "register_time TEXT DEFAULT (datetime('now'))," +
                "UNIQUE(username, activity_id)," + // 防止重复预约
                "FOREIGN KEY(activity_id) REFERENCES activity(id))");
        getSampleActivities(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS activity");
        db.execSQL("DROP TABLE IF EXISTS account");
        db.execSQL("DROP TABLE IF EXISTS registration");
        onCreate(db);

    }
    // 检查用户名是否已存在
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                "account",
                new String[]{"username"},
                "username = ?",
                new String[]{username},
                null, null, null
        );
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }


    // 注册方法
    public long register(Account u) {
        // 先检查用户名是否已存在
        if (isUsernameExists(u.getUsername())) {
            return -2; // 用户名已存在
        }
        ContentValues cv = new ContentValues();
        cv.put("username", u.getUsername());
        cv.put("password", u.getPassword());
        cv.put("role", u.getRole());
        cv.put("name",u.getName());
        cv.put("avatar", u.getAvatar());
        cv.put("phone", u.getPhone());
        cv.put("email", u.getEmail());
        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert("account", null, cv);
        db.close();
        return result;
    }
//登录
public boolean login(String username, String password,String role) {
    SQLiteDatabase db = getReadableDatabase();
    // 查询用户名和密码是否匹配
    Cursor cursor = db.query(
            "account",
            new String[]{"username"}, // 只需要验证是否存在，不需要返回全部字段
            "username = ? AND password = ? AND role = ?",
            new String[]{username, password, role},
            null, null, null
    );
    boolean loginSuccess = cursor != null && cursor.getCount() > 0;
    if (cursor != null) {
        cursor.close();
    }
//    db.close();
    return loginSuccess;
}


    /**
     * 更新用户名和姓名
     * @param oldUsername 原用户名
     * @param newUsername 新用户名
     * @param newName 新姓名
     * @return 是否更新成功
     */
    public boolean updateUsernameAndName(String oldUsername, String newUsername, String newName) {
        SQLiteDatabase db = getWritableDatabase();

        // 1. 检查新用户名是否已存在（排除当前用户自己的记录）
        Cursor cursor = db.query(
                "account",
                new String[]{"username"},
                "username = ? AND username != ?",
                new String[]{newUsername, oldUsername},
                null, null, null
        );

        boolean usernameExists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }

        if (usernameExists) {
            db.close();
            return false; // 用户名已存在
        }

        // 2. 更新数据
        ContentValues values = new ContentValues();
        values.put("username", newUsername);
        values.put("name", newName);

        int rowsAffected = db.update(
                "account",
                values,
                "username = ?",
                new String[]{oldUsername}
        );

        db.close();
        return rowsAffected > 0;
    }
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            // 1. 验证旧密码是否正确
            Cursor cursor = db.query(
                    "account",
                    new String[]{"password"},
                    "username = ?",
                    new String[]{username},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String correctPassword = cursor.getString(0);
                cursor.close();

                if (!correctPassword.equals(oldPassword)) {
                    return false; // 旧密码不匹配
                }

                // 2. 更新密码
                ContentValues values = new ContentValues();
                values.put("password", newPassword);

                int rowsAffected = db.update(
                        "account",
                        values,
                        "username = ?",
                        new String[]{username}
                );

                return rowsAffected > 0;
            }
            return false;
        } finally {
            db.close();
        }

    }

    public void closeDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
    // ==================== Activity表操作 ====================

    /**
     * 添加活动
     */
    public long addActivity(Activity activity,SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("name", activity.getName());
        values.put("picture", activity.getPicture());
        values.put("area", activity.getArea());
        values.put("content", activity.getContent());
        values.put("count", activity.getCount());
        values.put("actual_count", activity.getActualCount());
        values.put("begin_time", activity.getBeginTime());
        values.put("end_time", activity.getEndTime());
        values.put("volunteer_time", activity.getVolunteerTime());
        values.put("state", activity.getState());
        values.put("hostid", activity.getHostId());
        db.insert("activity", null, values);
//        db.close();
        return 1;
    }

    public long addActivity(Activity activity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", activity.getName());
        values.put("picture", activity.getPicture());
        values.put("area", activity.getArea());
        values.put("content", activity.getContent());
        values.put("count", activity.getCount());
        values.put("actual_count", activity.getActualCount());
        values.put("begin_time", activity.getBeginTime());
        values.put("end_time", activity.getEndTime());
        values.put("volunteer_time", activity.getVolunteerTime());
        values.put("state", activity.getState());
        values.put("hostid", activity.getHostId());
        db.insert("activity", null, values);
        db.close();
        return 1;
    }
    private List<Activity> getSampleActivities(SQLiteDatabase db) {
        List<Activity> activities = new ArrayList<>();
        // 1. 校园环境维护类
        activities.add(new Activity(0, "学堂新居·家蒸护航", "", "校园社区",
                "为教职工宿舍区提供搬家协助和环境整理服务",
                10, 0, "2025-05-10 08:00", "2025-05-10 10:00", 2, "", "小黑蚁志愿队"));

        // 2. 开学迎新活动
        activities.add(new Activity(1, "新生导航·温暖启程", "", "校门口广场",
                "帮助新生搬运行李、引导报到流程、校园导览",
                50, 32, "2025-09-01 07:00", "2025-09-01 17:00", 6, "", "小黑蚁志愿队"));

        // 3. 图书馆志愿服务
        activities.add(new Activity(2, "书香守护者", "", "校图书馆",
                "图书整理、读者引导、阅读区秩序维护",
                15, 8, "2025-05-15 13:30", "2025-05-15 16:30", 3, "", "小黑蚁志愿队"));

        // 4. 环保回收行动
        activities.add(new Activity(3, "绿色校园·废品回收", "", "全校各宿舍楼",
                "可回收物分类收集、环保知识宣传",
                20, 12, "2025-05-20 09:00", "2025-05-20 11:30", 2, "", "小黑蚁志愿队"));

        // 5. 支教帮扶活动
        activities.add(new Activity(4, "周末课堂·爱心支教", "", "农民工子弟学校",
                "为周边社区儿童提供学科辅导和兴趣课程",
                30, 25, "2025-05-16 08:30", "2025-05-16 11:30", 3, "", "小黑蚁志愿队"));

        // 6. 运动会志愿服务
        activities.add(new Activity(6, "校运会后勤保障", "", "学校操场",
                "担任裁判助理、场地维护、医疗点支持",
                40, 40, "2025-10-12 08:00", "2025-10-12 17:00", 8, "","小黑蚁志愿队" ));

        // 7. 食堂文明督导
        activities.add(new Activity(7, "光盘行动监督员", "", "学生食堂",
                "倡导节约粮食、维持排队秩序",
                10, 6, "2025-05-11 11:00", "2025-05-11 12:30", 1, "", "小黑蚁志愿队"));

        // 8. 防疫志愿服务
        activities.add(new Activity(8, "校园防疫先锋队", "", "校医院",
                "协助体温检测、防疫物资分发",
                8, 8, "2025-05-09 07:30", "2025-05-09 18:00", 5, "", "小黑蚁志愿队"));

        for (Activity activity : activities) {
            long result = addActivity(activity,db);
            if (result == -1) {
                Log.w("DB", "插入失败: " + activity.getName());
            }
        }

        return activities;

    }

    /**
     * 获取活动列表
     */
    /**
     * 获取所有活动（按开始时间降序）
     * @return 包含Activity对象的Cursor
     */
    public Cursor getAllActivitiesOrderByTime() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(
                "activity",          // 表名
                null,               // 返回所有列
                null,               // WHERE条件（无）
                null,               // WHERE参数
                null,               // GROUP BY
                null,               // HAVING
                "begin_time DESC"   // ORDER BY 按开始时间降序
        );
    }

    /**
     * 获取活动列表（封装成List）
     */
    public List<Activity> getActivityList() {
        List<Activity> activities = new ArrayList<>();
        Cursor cursor = getAllActivitiesOrderByTime();
        // Get column indices once
        int idIndex = cursor.getColumnIndex("id");
        int nameIndex = cursor.getColumnIndex("name");
        int beginTimeIndex = cursor.getColumnIndex("begin_time");
        while (cursor.moveToNext()) {
            Activity activity = new Activity();

            // Only set values if columns exist
            if (idIndex != -1) {
                activity.setId(cursor.getInt(idIndex));
            }
            if (nameIndex != -1) {
                activity.setName(cursor.getString(nameIndex));
            }
            if (beginTimeIndex != -1) {
                activity.setBeginTime(cursor.getString(beginTimeIndex));
            }

            activities.add(activity);
        }
        cursor.close();
        return activities;
    }
    /**
     * 更新活动状态
     */
    public int updateActivityState(int id, String newState) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("state", newState);
        int count = db.update("activity", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return count;
    }

    /**
     * 删除活动
     */
    public int deleteActivity(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int count = db.delete("activity", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return count;
    }



    // ==================== 预约相关方法 ====================

    /**
     * 检查活动是否已结束
     * @param activityId 活动ID
     * @return true=已结束，false=未结束
     */
    public boolean isActivityEnded(int activityId) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("activity",
                    new String[]{"end_time"},
                    "id=?",
                    new String[]{String.valueOf(activityId)},
                    null, null, null);
            System.out.println("!!!!!!!"+c);
            Log.d("DB_DEBUG", "Cursor count: " + c.getCount()); // 结果数量
            Log.d("DB_DEBUG", "Columns: " + Arrays.toString(c.getColumnNames())); // 列名
            if (c.moveToFirst()) {
                String endTime = c.getString(0);
                // 比较当前时间与结束时间
                System.out.println("!!!!!!!"+System.currentTimeMillis());
                System.out.println("!!!!!!!"+DateTimeUtils.parseToMillis(endTime));
                boolean isEnded = System.currentTimeMillis() > DateTimeUtils.parseToMillis(endTime);
                c.close();
                return isEnded;
            }
            System.out.println("???????");
            return true; // 活动不存在视为已结束
        } finally {
            db.close();
        }
    }

    /**
     * 检查活动名额是否已满
     * @param activityId 活动ID
     * @return true=已满，false=仍有名额
     */
    public boolean isActivityFull(int activityId) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("activity",
                    new String[]{"count", "actual_count"},
                    "id=?",
                    new String[]{String.valueOf(activityId)},
                    null, null, null);

            if (c.moveToFirst()) {
                boolean isFull = c.getInt(1) >= c.getInt(0);
                c.close();
                return isFull;
            }
            return true; // 活动不存在视为已满
        } finally {
            db.close();
        }
    }

    /**
     * 检查用户是否已预约过该活动
     * @param username 用户名
     * @param activityId 活动ID
     * @return true=已预约，false=未预约
     */
    public boolean hasUserRegistered(String username, int activityId) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("registration",
                    null,
                    "username=? AND activity_id=?",
                    new String[]{username, String.valueOf(activityId)},
                    null, null, null);
            boolean exists = c.getCount() > 0;
            c.close();
            return exists;
        } finally {
            db.close();
        }
    }

    /**
     * 预约活动（整合所有检查）
     * @return 1=成功, 0=已预约过, -1=活动名额已满, -2=活动已结束, -3=错误
     */
    public int registerActivityWithChecks(String username, int activityId) {
        // 前置检查
        if (isActivityEnded(activityId)) return -2;
        if (isActivityFull(activityId)) return -1;
        if (hasUserRegistered(username, activityId)) return 0;


        try {
            // 1. 更新活动人数

//            System.out.println("!!!!!!!1");
            ContentValues cv = new ContentValues();
            cv.put("actual_count", getCurrentCount(activityId) + 1);
//            System.out.println("!!!!!!!2");
            SQLiteDatabase db = getWritableDatabase();
            db.update("activity", cv, "id=?", new String[]{String.valueOf(activityId)});
//            System.out.println("!!!!!!!3");
            // 2. 添加预约记录
            ContentValues reg = new ContentValues();
//            System.out.println("!!!!!!!4");
            reg.put("username", username);
            reg.put("activity_id", activityId);
            db.insert("registration", null, reg);
            db.close();
            return 1;
        } catch (Exception e) {
            Log.e("DB", "预约失败", e);
            return -3;
        }
    }

    /**
     * 获取当前参与人数（辅助方法）
     */
    private int getCurrentCount(int activityId) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("activity",
                    new String[]{"actual_count"},
                    "id=?",
                    new String[]{String.valueOf(activityId)},
                    null, null, null);
            return c.moveToFirst() ? c.getInt(0) : 0;
        } finally {
            db.close();
        }


    }
    /**
     * 根据用户名获取该用户报名的所有活动
     */
    public List<Activity> getActivitiesByUsername(String username) {
        List<Activity> activities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // 两表联查：通过registration找activity_id，再关联activity表获取详情
        String query = "SELECT a.* FROM  activity"  + " a " +
                "INNER JOIN registration" +  " r " +
                "ON a.id" + " = r.activity_id" + " " +
                "WHERE r.username"  + " = ?";
        System.out.println(username);
        Cursor cursor = db.rawQuery(query, new String[]{username});

        try {
            if (cursor.moveToFirst()) {
                do {
                    Activity activity = new Activity();
                    activity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    activity.setName(cursor.getString(cursor.getColumnIndex("name")));
                    activity.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
                    activity.setArea(cursor.getString(cursor.getColumnIndex("area")));
                    activity.setContent(cursor.getString(cursor.getColumnIndex("content")));
                    activity.setCount(cursor.getInt(cursor.getColumnIndex("count")));
                    activity.setActualCount(cursor.getInt(cursor.getColumnIndex("actual_count")));
                    activity.setBeginTime(cursor.getString(cursor.getColumnIndex("begin_time")));
                    activity.setEndTime(cursor.getString(cursor.getColumnIndex("end_time")));
                    activity.setVolunteerTime(cursor.getInt(cursor.getColumnIndex("volunteer_time")));
                    activity.setState(cursor.getString(cursor.getColumnIndex("state")));
                    activity.setHostId(cursor.getString(cursor.getColumnIndex("hostid")));
                    activities.add(activity);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return activities;
    }


//模糊查询
    public List<Activity> searchActivitiesByName(String keyword) {
        List<Activity> activities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // 使用LIKE实现模糊查询，%表示任意字符
        String query = "SELECT * FROM activity WHERE name LIKE ?";
        String[] selectionArgs = new String[]{"%" + keyword + "%"};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        try {
            while (cursor.moveToNext()) {
                Activity activity = new Activity();
                activity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                activity.setName(cursor.getString(cursor.getColumnIndex("name")));
                activity.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
                activity.setArea(cursor.getString(cursor.getColumnIndex("area")));
                activity.setContent(cursor.getString(cursor.getColumnIndex("content")));
                activity.setCount(cursor.getInt(cursor.getColumnIndex("count")));
                activity.setActualCount(cursor.getInt(cursor.getColumnIndex("actual_count")));
                activity.setBeginTime(cursor.getString(cursor.getColumnIndex("begin_time")));
                activity.setEndTime(cursor.getString(cursor.getColumnIndex("end_time")));
                activity.setVolunteerTime(cursor.getInt(cursor.getColumnIndex("volunteer_time")));
                activity.setState(cursor.getString(cursor.getColumnIndex("state")));
                activity.setHostId(cursor.getString(cursor.getColumnIndex("hostid")));
                activities.add(activity);
            }
        } finally {
            cursor.close();
            db.close();
        }

        return activities;
    }
}







