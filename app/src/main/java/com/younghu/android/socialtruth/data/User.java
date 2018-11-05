package com.younghu.android.socialtruth.data;

public class User {
    private String id;
    private String username;
    private String email;
    private String photoUrl;

    public User(String id, String username, String email, String photoUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public User() {
    }

    public User(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
