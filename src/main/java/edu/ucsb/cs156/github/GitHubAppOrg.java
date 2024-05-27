package edu.ucsb.cs156.github;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.json.*;

public class GitHubAppOrg {
    public String orgId;
    private GitHubApp app;
    private String instId;
    private String accessToken;
    private LocalDate accessTokenExpiration;

    private HttpClient client;

    public GitHubAppOrg(GitHubApp app, String org, String instId) {
        this.orgId = org;
        this.app = app;
        this.instId = instId;
        client = HttpClient.newHttpClient();
        getInstallationAccessToken();
    }

    private void getInstallationAccessToken() {
        String resp = app.post("/app/installations/" + instId + "/access_tokens", "{}").body();
        JSONObject json = new JSONObject(resp);
        this.accessToken = json.getString("token");
        this.accessTokenExpiration = LocalDate.parse(json.getString("expires_at"), DateTimeFormatter.ISO_DATE_TIME);
    }

    private void checkAccessTokenExpiration() {
        if (LocalDate.now().isAfter(accessTokenExpiration))
            getInstallationAccessToken();
    }

    public HttpResponse<String> get(String url)
            throws GitHubAppException {
        checkAccessTokenExpiration();
        return Utils.get(url, accessToken, client);
    }

    public HttpResponse<String> post(String url, JSONObject body)
            throws GitHubAppException {
        checkAccessTokenExpiration();
        return Utils.post(url, body.toString(), accessToken, client);

    }

    public HttpResponse<String> post(String url, String body)
            throws GitHubAppException {
        checkAccessTokenExpiration();
        return Utils.post(url, body, accessToken, client);
    }

    // Begin actual biz code

    public String inviteUserToThisOrg(Integer userId) {
        JSONObject body = new JSONObject();
        body.put("invitee_id", userId);
        HttpResponse<String> resp = post("/orgs/" + orgId + "/invitations", body);
        if (resp.statusCode() != 201) {
            throw new GitHubAppException("Failed to invite user to org: " + resp.body());
        }
        return resp.body();
    }
}
