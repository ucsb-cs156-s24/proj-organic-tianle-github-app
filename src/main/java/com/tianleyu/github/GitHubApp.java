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

    public String appInfo() {
        try {
            HttpResponse<String> response = get("/app");
            return response.body();
        } catch (HttpClientErrorException e) {
            return e.getMessage();
        }
    }

    public GitHubAppOrg org(String org) throws GitHubAppException {
        HttpResponse<String> resp = get("/orgs/" + org + "/installation");
        if (resp.statusCode() != 200) {
            throw new GitHubAppException("Organization not found");
        }
        return new GitHubAppOrg(this, org);
    }

    public HttpResponse<String> get(String url)
            throws HttpClientErrorException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com" + url))
                    .timeout(Duration.of(10L, ChronoUnit.SECONDS)) // Change 10 to 10L
                    .header("Authorization", "Bearer " + jwtProvider.getJwt())
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .GET()
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new HttpClientErrorException(null, e.getMessage());
        }
    }
}
