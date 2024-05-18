package com.tianleyu.github;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import liquibase.pro.packaged.L;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GitHubUserApi {
    private GitHubToken token;
    HttpClient client;

    public GitHubUserApi(String ghToken) {
        token = new GitHubToken(ghToken);
        client = HttpClient.newHttpClient();
    }

    public GitHubUserApi(GitHubToken ghToken) {
        token = ghToken;
        client = HttpClient.newHttpClient();
    }

    public HttpResponse<String> get(String url)
            throws GitHubAppException {
        return Utils.get(url, token.getToken(), client);
    }

    public HttpResponse<String> post(String url, JSONObject body)
            throws GitHubAppException {
        return Utils.post(url, body.toString(), token.getToken(), client);

    }

    public HttpResponse<String> post(String url, String body)
            throws GitHubAppException {
        return Utils.post(url, body, token.getToken(), client);
    }

    public ArrayList<String> userEmails() {
        HttpResponse<String> response = get("/user/emails");
        if (response.statusCode() != 200) {
            throw new GitHubAppException("Failed to get user emails");
        }
        log.warn("\u001b[31m[com.tianleyu.github.GitHubUserApi] User emails: " + response.body() + "\u001b[0m");
        JSONArray emails = new JSONArray(response.body());
        ArrayList<String> verifiedEmails = new ArrayList<>();
        for (int i = 0; i < emails.length(); i++) {
            JSONObject email = emails.getJSONObject(i);
            if (email.getBoolean("verified")) {
                verifiedEmails.add(email.getString("email"));
            }
        }
        return verifiedEmails;
    }
}
