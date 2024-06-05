package edu.ucsb.cs156.github;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

public class GitHubBuilderFactory {
    public GitHub build(JwtProvider jwtProvider) throws Exception{
        return new GitHubBuilder().withJwtToken(jwtProvider.getJwt()).build();
    }
}
