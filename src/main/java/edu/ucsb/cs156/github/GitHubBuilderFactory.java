package edu.ucsb.cs156.github;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

public class GitHubBuilderFactory {
    public GitHub build(JwtProvider jwtProvider) throws Exception{
        return new GitHubBuilder().withJwtToken(jwtProvider.getJwt()).build();
    }
    public GitHub build(String instToken) throws Exception{
        return new GitHubBuilder().withAppInstallationToken(instToken).build();
    }
    public GitHub buildOauth(String oauthToken) throws Exception{
        return new GitHubBuilder().withOAuthToken(oauthToken).build();
    }
}
