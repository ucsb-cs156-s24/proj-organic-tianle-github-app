package com.tianleyu.github;

public class GitHubAppOrg {
    public String orgId;
    private GitHubApp app;

    public GitHubAppOrg(GitHubApp app, String org) {
        this.orgId = org;
        this.app = app;
    }
}
