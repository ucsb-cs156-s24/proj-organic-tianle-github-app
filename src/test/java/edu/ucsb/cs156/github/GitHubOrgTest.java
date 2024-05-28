package edu.ucsb.cs156.github;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class GitHubOrgTest {

    private JwtProvider jwtProvider;
    private HttpClient client;
    private GitHubApp gitHubApp;
    private GitHubAppOrg gitHubAppOrg;

    @BeforeEach
    void setUp() {
        client = mock(HttpClient.class);
        gitHubApp = mock(GitHubApp.class);
        HttpResponse<String> response2 = mock(HttpResponse.class);
        when(response2.statusCode()).thenReturn(200);
        when(response2.body()).thenReturn("{\"token\":\"1234\",\"expires_at\":\"9999-08-01T00:00:00Z\"}");

        when(gitHubApp.post(anyString(), anyString())).thenReturn(response2);
        gitHubAppOrg = new GitHubAppOrg(gitHubApp, "abc", "1234");
    }

    @Test
    void testTokenRenewalGet() throws Exception {
        HttpResponse<String> response2 = mock(HttpResponse.class);
        when(response2.statusCode()).thenReturn(200);
        when(response2.body()).thenReturn("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}");

        when(gitHubApp.post(anyString(), anyString())).thenReturn(response2);

        LocalDate expiration = LocalDate.parse("2021-08-01T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME);
        Field tokenExpiration = gitHubAppOrg.getClass().getDeclaredField("accessTokenExpiration");
        tokenExpiration.setAccessible(true);
        tokenExpiration.set(gitHubAppOrg, expiration);

        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.get(anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            mockedUtils.when(() -> Utils.post(anyString(), anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);

            gitHubAppOrg.get("/some/url");

            Field token = gitHubAppOrg.getClass().getDeclaredField("accessTokenExpiration");
            token.setAccessible(true);
            LocalDate newToken = (LocalDate) token.get(gitHubAppOrg);
            assertEquals(LocalDate.parse("2021-09-01T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME), newToken);

            Field nt = gitHubAppOrg.getClass().getDeclaredField("accessToken");
            nt.setAccessible(true);
            assertEquals("1232123", nt.get(gitHubAppOrg));
        }
    }

    @Test
    void testTokenRenewalPost() throws Exception {
        HttpResponse<String> response2 = mock(HttpResponse.class);
        when(response2.statusCode()).thenReturn(200);
        when(response2.body()).thenReturn("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}");

        when(gitHubApp.post(anyString(), anyString())).thenReturn(response2);

        LocalDate expiration = LocalDate.parse("2021-08-01T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME);
        Field tokenExpiration = gitHubAppOrg.getClass().getDeclaredField("accessTokenExpiration");
        tokenExpiration.setAccessible(true);
        tokenExpiration.set(gitHubAppOrg, expiration);

        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.get(anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            mockedUtils.when(() -> Utils.post(anyString(), anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);

            gitHubAppOrg.post("/some/url", new JSONObject());

            Field token = gitHubAppOrg.getClass().getDeclaredField("accessTokenExpiration");
            token.setAccessible(true);
            LocalDate newToken = (LocalDate) token.get(gitHubAppOrg);
            assertEquals(LocalDate.parse("2021-09-01T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME), newToken);

            Field nt = gitHubAppOrg.getClass().getDeclaredField("accessToken");
            nt.setAccessible(true);
            assertEquals("1232123", nt.get(gitHubAppOrg));
        }
    }

    @Test
    void testTokenRenewalPostStr() throws Exception {
        HttpResponse<String> response2 = mock(HttpResponse.class);
        when(response2.statusCode()).thenReturn(200);
        when(response2.body()).thenReturn("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}");

        when(gitHubApp.post(anyString(), anyString())).thenReturn(response2);

        LocalDate expiration = LocalDate.parse("2021-08-01T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME);
        Field tokenExpiration = gitHubAppOrg.getClass().getDeclaredField("accessTokenExpiration");
        tokenExpiration.setAccessible(true);
        tokenExpiration.set(gitHubAppOrg, expiration);

        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.get(anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            mockedUtils.when(() -> Utils.post(anyString(), anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);

            String res = gitHubAppOrg.post("/some/url", "This is some str").body();

            assertEquals("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}", res);

            Field token = gitHubAppOrg.getClass().getDeclaredField("accessTokenExpiration");
            token.setAccessible(true);
            LocalDate newToken = (LocalDate) token.get(gitHubAppOrg);
            assertEquals(LocalDate.parse("2021-09-01T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME), newToken);

            Field nt = gitHubAppOrg.getClass().getDeclaredField("accessToken");
            nt.setAccessible(true);
            assertEquals("1232123", nt.get(gitHubAppOrg));
        }
    }

    @Test
    void testTokenRenewalNoNeed() throws Exception {
        HttpResponse<String> response2 = mock(HttpResponse.class);
        when(response2.statusCode()).thenReturn(200);
        when(response2.body()).thenReturn("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}");

        when(gitHubApp.post(anyString(), anyString())).thenReturn(response2);

        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.get(anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            mockedUtils.when(() -> Utils.post(anyString(), anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            gitHubAppOrg.post("/some/url", "");

            Field token = gitHubAppOrg.getClass().getDeclaredField("accessTokenExpiration");
            token.setAccessible(true);
            LocalDate newToken = (LocalDate) token.get(gitHubAppOrg);
            assertEquals(LocalDate.parse("9999-08-01T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME), newToken);

            Field nt = gitHubAppOrg.getClass().getDeclaredField("accessToken");
            nt.setAccessible(true);
            assertEquals("1234", nt.get(gitHubAppOrg));
        }
    }

    @Test
    void testGet() throws Exception {
        HttpResponse<String> response2 = mock(HttpResponse.class);
        when(response2.statusCode()).thenReturn(200);
        when(response2.body()).thenReturn("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}");
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.get(anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            mockedUtils.when(() -> Utils.post(anyString(), anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            HttpResponse<String> res = gitHubAppOrg.get("/some/url");

            assertEquals(200, res.statusCode());
            assertEquals("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}", res.body());
        }
    }

    @Test
    void testInvite() throws Exception {
        HttpResponse<String> response2 = mock(HttpResponse.class);
        when(response2.statusCode()).thenReturn(201);
        when(response2.body()).thenReturn("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}");
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.get(anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            mockedUtils.when(() -> Utils.post(anyString(), anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);

            String res = gitHubAppOrg.inviteUserToThisOrg(123);

            assertEquals("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}", res);
        }
    }

    @Test
    void testInviteError() throws Exception {
        HttpResponse<String> response2 = mock(HttpResponse.class);
        when(response2.statusCode()).thenReturn(403);
        when(response2.body()).thenReturn("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}");
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.get(anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            mockedUtils.when(() -> Utils.post(anyString(), anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);

            assertThrows(GitHubAppException.class, () -> gitHubAppOrg.inviteUserToThisOrg(123));
        }
    }

}
