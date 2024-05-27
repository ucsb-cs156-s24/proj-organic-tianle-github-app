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
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class GitHubUserApiTest {

    private GitHubToken gitHubToken;
    private HttpClient client;
    private GitHubUserApi userApi;

    @BeforeEach
    void setUp() {
        client = mock(HttpClient.class);
        gitHubToken = mock(GitHubToken.class);
        when(gitHubToken.getToken()).thenReturn("FakeToken");
        userApi = new GitHubUserApi(gitHubToken);
    }

    @Test
    void testStringConst() throws Exception {
        userApi = new GitHubUserApi("ababab");
        Field token = userApi.getClass().getDeclaredField("token");
        token.setAccessible(true);
        assertEquals("ababab", ((GitHubToken) token.get(userApi)).getToken());
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
            HttpResponse<String> res = userApi.get("/some/url");

            assertEquals(200, res.statusCode());
            assertEquals("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}", res.body());
        }
    }

    @Test
    void testPost1() throws Exception {
        HttpResponse<String> response2 = mock(HttpResponse.class);
        when(response2.statusCode()).thenReturn(200);
        when(response2.body()).thenReturn("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}");
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.get(anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            mockedUtils.when(() -> Utils.post(anyString(), anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            HttpResponse<String> res = userApi.post("/some/url", "body");

            assertEquals(200, res.statusCode());
            assertEquals("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}", res.body());
        }
    }

    @Test
    void testPost2() throws Exception {
        HttpResponse<String> response2 = mock(HttpResponse.class);
        when(response2.statusCode()).thenReturn(200);
        when(response2.body()).thenReturn("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}");
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.get(anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            mockedUtils.when(() -> Utils.post(anyString(), anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            HttpResponse<String> res = userApi.post("/some/url", new JSONObject());

            assertEquals(200, res.statusCode());
            assertEquals("{\"token\":\"1232123\",\"expires_at\":\"2021-09-01T00:00:00Z\"}", res.body());
        }
    }

    @Test
    void userEmailsFails() throws Exception {
        HttpResponse<String> response2 = mock(HttpResponse.class);
        when(response2.statusCode()).thenReturn(200);
        JSONObject email1 = new JSONObject();
        email1.put("email", "test@test.com");
        email1.put("verified", true);
        JSONObject email2 = new JSONObject();
        email2.put("email", "test2@test.com");
        email2.put("verified", false);
        JSONArray emails = new JSONArray();
        emails.put(email1);
        emails.put(email2);
        when(response2.body()).thenReturn(emails.toString());
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.get(anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            mockedUtils.when(() -> Utils.post(anyString(), anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            ArrayList<String> res = userApi.userEmails();
            assertEquals(1, res.size());
            assertEquals("test@test.com", res.get(0));
        }
    }

    @Test
    void userEmailsOK() throws Exception {
        HttpResponse<String> response2 = mock(HttpResponse.class);
        when(response2.statusCode()).thenReturn(404);
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.get(anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            mockedUtils.when(() -> Utils.post(anyString(), anyString(), anyString(), any(HttpClient.class)))
                    .thenReturn(response2);
            assertThrows(GitHubAppException.class, () -> userApi.userEmails());
        }
    }
}
