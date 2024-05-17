package edu.ucsb.cs156.organic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tianleyu.github.GitHubApp;

@Configuration
public class GitHubAppConfig {

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @Value("${com.tianleyu.github.pkfile}")
    private String clientPkPath;

    @Bean
    public GitHubApp gitHubApp() {
        return new GitHubApp(this.clientId, this.clientPkPath);
    }

}
