package edu.ucsb.cs156.organic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.context.annotation.RequestScope;

import com.tianleyu.github.GitHubToken;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class OAuth2ClientConfig {

    @Bean
    @RequestScope
    public GitHubToken accessToken(OAuth2AuthorizedClientService clientService) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = null;
        if (authentication.getClass().isAssignableFrom(OAuth2AuthenticationToken.class)) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
            log.error(clientRegistrationId);
            log.error(oauthToken.getName());
            if (clientRegistrationId.equals("github")) {
                OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(clientRegistrationId,
                        oauthToken.getName());
                accessToken = client.getAccessToken().getTokenValue();

                log.error(accessToken);
            }
        }
        return new GitHubToken(accessToken);
    }
}
