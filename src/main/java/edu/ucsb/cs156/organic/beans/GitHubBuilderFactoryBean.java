package edu.ucsb.cs156.organic.beans;

import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.ucsb.cs156.github.GitHubBuilderFactory;
import edu.ucsb.cs156.github.JwtProvider;

@Configuration
public class GitHubBuilderFactoryBean {

    @Bean
    public GitHubBuilderFactory jwtProvider() {
        return new GitHubBuilderFactory();
    }
}

