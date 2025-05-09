package com.example.myapplication;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VolunteerViewModel extends ViewModel {
    private final MutableLiveData<String> selectedIdentity = new MutableLiveData<>("Volunteer");
    private final MutableLiveData<String> userRole = new MutableLiveData<>("Volunteer");

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
}