package com.assignment;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey
    private int id;

    private String firstName;
    private String lastName;
    private String email;
    private String avatar;

    // Field for storing the path to the locally uploaded image
    private String localAvatarPath;

    // Constructors
    public UserEntity(int id, String firstName, String lastName, String email, String avatar, String localAvatarPath) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.avatar = avatar;
        this.localAvatarPath = localAvatarPath;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getLocalAvatarPath() {
        return localAvatarPath;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setLocalAvatarPath(String localAvatarPath) {
        this.localAvatarPath = localAvatarPath;
    }

    // Combined getter for first and last name
    public String getFullName() {
        return firstName + " " + lastName;
    }
}