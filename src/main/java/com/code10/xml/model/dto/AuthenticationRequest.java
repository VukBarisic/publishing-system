package com.code10.xml.model.dto;

import org.hibernate.validator.constraints.NotEmpty;

public class AuthenticationRequest {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    public AuthenticationRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
