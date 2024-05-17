package com.tianleyu.github;

import org.json.*;

public class GitHubAppOrg {
    public String orgId;
    private GitHubApp app;
    private String instId;
    private String accessToken;

    public GitHubAppOrg(GitHubApp app, String org, String instId) {
        this.orgId = org;
        this.app = app;
        this.instId = instId;
        getInstallationAccessToken();
    }

    private void getInstallationAccessToken() {
        String resp = app.post("/app/installations/" + instId + "/access_tokens", "{}").body();
        JSONObject json = new JSONObject(resp);
        this.accessToken = json.getString("token");
    }
}
