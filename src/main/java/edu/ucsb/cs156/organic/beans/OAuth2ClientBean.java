package edu.ucsb.cs156.organic.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.context.annotation.RequestScope;

import edu.ucsb.cs156.github.OauthToken;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class OAuth2ClientBean {
    // https://stackoverflow.com/questions/56533989/retrieving-the-access-token-of-a-oauth2-3-legged-authentication-using-spring-boo

    @Bean
    @RequestScope
    public OauthToken accessToken(OAuth2AuthorizedClientService clientService) {
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
        return new OauthToken(accessToken);
    }
}
