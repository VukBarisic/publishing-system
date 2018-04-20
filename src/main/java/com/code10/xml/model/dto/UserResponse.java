package com.code10.xml.model.dto;

public class UserResponse {

    private String username;

    private int score;

    private String email;

    private String reviewerStatus;

    public UserResponse() {
    }

    public UserResponse(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public UserResponse(String username, int score, String email) {
        this.username = username;
        this.score = score;
        this.email = email;
    }

    public UserResponse(String username, String email, String reviewerStatus) {
        this.username = username;
        this.email = email;
        this.reviewerStatus = reviewerStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReviewerStatus() {
        return reviewerStatus;
    }

    public void setReviewerStatus(String reviewerStatus) {
        this.reviewerStatus = reviewerStatus;
    }
}
