package com.vveed.permissions.responses;

public class LoginResponse {
    private String token;

    public LoginResponse(String token){
        this.token = token;
    }

    public String getToken(){
        return this.token;
    }

    public void setToken(String token){
        this.token = token;
    }
}
