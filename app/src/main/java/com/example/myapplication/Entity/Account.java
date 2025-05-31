package com.example.myapplication.Entity;
public class Account {
    private int id;
    private String username;
    private String password;
    private String name;
    private String avatar;
    private String role;
    private String phone;
    private String email;
//    private String state;
    // 无参构造函数
    public Account() {
        this.username = " ";
        this.password = " ";
        this.avatar = " ";
        this.role = " ";
        this.name=" ";
        this.phone = " ";
        this.email = " ";
//        this.state = " ";

    }

    // 全参构造函数
    public Account(int id, String username, String password,
                   String avatar, String role, String phone, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.avatar = avatar;
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.name = name;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name=name;
    }
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
         this.username=username;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }

    // toString 方法
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", avatar='" + avatar + '\'' +
                ", role='" + role + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", name='"+ name +'\'' +
                '}';
    }

    // equals 和 hashCode 方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id &&
                username.equals(account.username) &&
                password.equals(account.password) &&
                avatar.equals(account.avatar) &&
                role.equals(account.role) &&
                phone.equals(account.phone) &&
                email.equals(account.email) &&
                name.equals(account.name);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, username, password, avatar, role, phone, email,name);
    }
}