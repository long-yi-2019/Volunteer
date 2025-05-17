package com.example.myapplication;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VolunteerViewModel extends ViewModel {
    private final MutableLiveData<String> selectedIdentity = new MutableLiveData<>("Volunteer");
    private final MutableLiveData<String> userRole = new MutableLiveData<>("Volunteer");

    private final MutableLiveData<String> userName = new MutableLiveData<>("");
    private final MutableLiveData<String> passWord = new MutableLiveData<>("");
    public void setSelectedIdentity(String identity) {
        selectedIdentity.setValue(identity);
    }

    public MutableLiveData<String> getSelectedIdentity() {
        return selectedIdentity;
    }

    public void setUserRole(String role) {
        userRole.setValue(role);
    }

    public MutableLiveData<String> getUserRole() {
        return userRole;
    }
    // 新增username相关方法
    public void setUsername(String username) {
        userName.setValue(username);
    }

    public MutableLiveData<String> getUsername() {
        return userName;
    }
    public void setPassWord(String password) {
        passWord.setValue(password);
    }

    public MutableLiveData<String> getPassWord() {
        return passWord;
    }
}