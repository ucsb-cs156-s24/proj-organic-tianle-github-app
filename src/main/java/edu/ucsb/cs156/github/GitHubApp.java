package edu.ucsb.cs156.github;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

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
            String responseBody = resp.body();
            String message = String.format("""
                    Error getting installation for org %s: %s
                    Status Code: %d
                    """, org, responseBody, resp.statusCode());        
            throw new GitHubAppException(message);
        }
        JSONObject json = new JSONObject(resp.body());
        return new GitHubAppOrg(this, org, json.getInt("id") + "");
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
