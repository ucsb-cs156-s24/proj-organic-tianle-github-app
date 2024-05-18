package com.tianleyu.github;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.web.client.HttpClientErrorException;

import java.time.temporal.*;
import java.time.Duration; // Add this import statement
import org.json.*;

public class GitHubApp {
    private JwtProvider jwtProvider;
    HttpClient client;

    public GitHubApp(String appId, String pkFile) {
        jwtProvider = new JwtProvider(appId, pkFile);
        client = HttpClient.newHttpClient();
    }

    public JSONObject appInfo() {
        HttpResponse<String> response = get("/app");
        return new JSONObject(response.body());
    }

    public GitHubAppOrg org(String org) throws GitHubAppException {
        HttpResponse<String> resp = get("/orgs/" + org + "/installation");
        if (resp.statusCode() != 200) {
            throw new GitHubAppException("Organization not found");
        }
        JSONObject json = new JSONObject(resp.body());
        return new GitHubAppOrg(this, org, json.getString("id"));
    }

    public HttpResponse<String> get(String url)
            throws GitHubAppException {
        return Utils.get(url, jwtProvider.getJwt(), client);
    }

    public HttpResponse<String> post(String url, JSONObject body)
            throws GitHubAppException {
        return Utils.post(url, body.toString(), jwtProvider.getJwt(), client);

    }

    public HttpResponse<String> post(String url, String body)
            throws GitHubAppException {
        return Utils.post(url, body, jwtProvider.getJwt(), client);
    }
}
