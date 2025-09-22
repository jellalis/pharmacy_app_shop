package com.panagou.pms.android.project.pharmacyapp;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class User {
    private String name;
    private String email;
    private String phone;
    private String role;

    @ServerTimestamp
    private Date createdTime;

    @ServerTimestamp
    private Date updatedTime;

    public User() { }

    public User(String name, String email, String phone, String role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public Date getCreatedTime() { return createdTime; }
    public Date getUpdatedTime() { return updatedTime; }


}
