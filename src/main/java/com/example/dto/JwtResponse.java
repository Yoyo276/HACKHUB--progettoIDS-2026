package com.example.dto;

import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;

    public JwtResponse(String accessToken, String username, List<String> roles) {
        this.token = accessToken;
        this.username = username;
        this.roles = roles;
    }

    // Getter e Setter (Token, Username, Roles)
    public String getToken() { return token; }
    public List<String> getRoles() {return roles;}
    public String getUsername() {return username;}
    public String getType() {return type;}
}