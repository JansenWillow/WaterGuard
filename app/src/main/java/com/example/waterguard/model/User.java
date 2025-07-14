package com.example.waterguard.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

// This is our User model class that will be stored in Realm database
public class User extends RealmObject {
    
    @PrimaryKey
    private long id;
    
    @Required
    private String email;
    
    @Required
    private String name;
    
    @Required
    private String password;
    
    private String profileImage; // Base64 string of profile image

    // Empty constructor required by Realm
    public User() {}

    // Constructor with all fields
    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getProfileImage() {
        return profileImage;
    }
    
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
} 