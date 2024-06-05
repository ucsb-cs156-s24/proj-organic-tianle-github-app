package edu.ucsb.cs156.github;

public class OauthToken {
    private String token;
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public OauthToken(String token) {
        this.token = token;
    }
}
