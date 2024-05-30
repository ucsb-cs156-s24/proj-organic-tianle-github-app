package edu.ucsb.cs156.organic.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.*;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import edu.ucsb.cs156.organic.entities.User;
import edu.ucsb.cs156.organic.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * This class is used to configure Spring Security.
 *
 * Among other things, this class is partially responsible for
 * the implementation of the ADMIN_GITHUB_LOGINS feature.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

  @Value("${app.admin.githubLogins}")
  private final List<String> adminGithubLogins = new ArrayList<String>();

  @Value("${spring.security.oauth2.client.registration.github.client-id}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.github.client-secret}")
  private String clientSecret;

  @Autowired
  UserRepository userRepository;

  // @Autowired
  // private LoginSuccessHandler loginSuccessHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .exceptionHandling(handling -> handling.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
            .oauth2Login(
                    oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.userAuthoritiesMapper(this.userAuthoritiesMapper())))
            .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/"));
    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring().requestMatchers("/h2-console/**");
  }

  private GrantedAuthoritiesMapper userAuthoritiesMapper() {
    return (authorities) -> {
      Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

      authorities.forEach(authority -> {
        log.trace("********** authority={}", authority);
        mappedAuthorities.add(authority);
        if (OAuth2UserAuthority.class.isInstance(authority)) {
          OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;

          Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();
          log.trace("********** userAttributes={}", userAttributes);
          mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
          String githubLogin = (String) userAttributes.get("login");
          boolean userIsAdmin = updateAdmin(githubLogin);
          if (userIsAdmin) {
            mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
          }
        }

      });
      return mappedAuthorities;
    };
  }

  public boolean updateAdmin(String githubLogin) {
    if (adminGithubLogins.contains(githubLogin)) {
      return true;
    }
    Optional<User> u = userRepository.findByGithubLogin(githubLogin);
    boolean result = u.isPresent() && u.get().isAdmin();
    if (result) {
      User user = u.get();
      user.setAdmin(true);
      userRepository.save(user);
    }
    return result;
  }
}

final class SpaCsrfTokenRequestHandler extends CsrfTokenRequestAttributeHandler {
  private final CsrfTokenRequestHandler delegate = new XorCsrfTokenRequestAttributeHandler();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
                     Supplier<CsrfToken> deferredCsrfToken) {
    /*
     * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection
     * of
     * the CsrfToken when it is rendered in the response body.
     */
    this.delegate.handle(request, response, deferredCsrfToken);
  }

  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
    /*
     * If the request contains a request header, use
     * CsrfTokenRequestAttributeHandler
     * to resolve the CsrfToken. This applies when a single-page application
     * includes
     * the header value automatically, which was obtained via a cookie containing
     * the
     * raw CsrfToken.
     */
    if (StringUtils.hasText(request.getHeader(csrfToken.getHeaderName()))) {
      return super.resolveCsrfTokenValue(request, csrfToken);
    }
    /*
     * In all other cases (e.g. if the request contains a request parameter), use
     * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
     * when a server-side rendered form includes the _csrf request parameter as a
     * hidden input.
     */
    return this.delegate.resolveCsrfTokenValue(request, csrfToken);
  }
}

final class CsrfCookieFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
    CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
    // Render the token value to a cookie by causing the deferred token to be loaded
    csrfToken.getToken();
    filterChain.doFilter(request, response);
  }
}