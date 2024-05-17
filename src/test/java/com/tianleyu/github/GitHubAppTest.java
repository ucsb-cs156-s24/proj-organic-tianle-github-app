package com.tianleyu.github;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.json.*;

public class GitHubAppTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    @Mock
    private JwtProvider mockJwtProvider;

    private GitHubApp gitHubApp;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockJwtProvider.getJwt()).thenReturn("mockedJwtToken");
        gitHubApp = new GitHubApp("mockAppId", "mockPkFile") {
            {
                this.client = mockHttpClient;
            }
        };
    }

    @Test
    public void testAppInfo_Success() throws Exception {
        // Arrange
        when(mockHttpResponse.body()).thenReturn("{\"key\":\"appInfoResponse\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        // Act
        JSONObject result = gitHubApp.appInfo();

        // Assert
        assertEquals("{\"key\":\"appInfoResponse\"}", result.toString());
    }

    @Test
    public void testOrg_Success() throws Exception {
        // Arrange
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        // Act
        GitHubAppOrg result = gitHubApp.org("testOrg");

        // Assert
        assertNotNull(result);
        assertEquals("testOrg", result.orgId);
    }

    @Test
    public void testOrg_OrganizationNotFound() throws Exception {
        // Arrange
        when(mockHttpResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        // Act & Assert
        assertThrows(GitHubAppException.class, () -> {
            gitHubApp.org("testOrg");
        });
    }

    @Test
    public void testGet_Success() throws Exception {
        // Arrange
        when(mockHttpResponse.body()).thenReturn("getResponse");
        when(mockHttpClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        // Act
        HttpResponse<String> response = gitHubApp.get("/test");

        // Assert
        assertEquals("getResponse", response.body());
    }

    @Test
    public void testGet_HttpClientErrorException() throws Exception {
        // Arrange
        when(mockHttpResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException());

        // Act & Assert
        assertThrows(GitHubAppException.class, () -> {
            gitHubApp.get("/test");
        });
    }
}