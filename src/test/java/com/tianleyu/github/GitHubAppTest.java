package com.tianleyu.github;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.springframework.web.client.HttpClientErrorException;

public class GitHubAppTest {

    // @Mock
    // private HttpClient mockHttpClient;

    // @Mock
    // private HttpResponse<String> mockHttpResponse;

    // @Mock
    // private JwtProvider mockJwtProvider;

    // private GitHubApp gitHubApp;

    // @BeforeEach
    // public void setUp() {
    //     MockitoAnnotations.openMocks(this);
    //     when(mockJwtProvider.getJwt()).thenReturn("mockedJwtToken");
    //     gitHubApp = new GitHubApp("mockAppId", "mockPkFile") {
    //         {
    //             this.client = mockHttpClient;
    //         }
    //     };
    // }

    // @Test
    // public void testAppInfo_Success() throws Exception {
    //     // Arrange
    //     when(mockHttpResponse.body()).thenReturn("appInfoResponse");
    //     when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
    //             .thenReturn(mockHttpResponse);

    //     // Act
    //     String result = gitHubApp.appInfo();

    //     // Assert
    //     assertEquals("appInfoResponse", result);
    // }

    // @Test
    // public void testAppInfo_HttpClientErrorException() throws Exception {
    //     // Arrange
    //     when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
    //             .thenThrow(new HttpClientErrorException(null, "Error"));

    //     // Act
    //     String result = gitHubApp.appInfo();

    //     // Assert
    //     assertEquals("Error", result);
    // }

    // @Test
    // public void testOrg_Success() throws Exception {
    //     // Arrange
    //     when(mockHttpResponse.statusCode()).thenReturn(200);
    //     when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
    //             .thenReturn(mockHttpResponse);

    //     // Act
    //     GitHubAppOrg result = gitHubApp.org("testOrg");

    //     // Assert
    //     assertNotNull(result);
    //     assertEquals("testOrg", result);
    // }

    // @Test
    // public void testOrg_OrganizationNotFound() throws Exception {
    //     // Arrange
    //     when(mockHttpResponse.statusCode()).thenReturn(404);
    //     when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
    //             .thenReturn(mockHttpResponse);

    //     // Act & Assert
    //     assertThrows(GitHubAppException.class, () -> {
    //         gitHubApp.org("testOrg");
    //     });
    // }

    // @Test
    // public void testGet_Success() throws Exception {
    //     // Arrange
    //     when(mockHttpResponse.body()).thenReturn("getResponse");
    //     when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
    //             .thenReturn(mockHttpResponse);

    //     // Act
    //     HttpResponse<String> response = gitHubApp.get("/test");

    //     // Assert
    //     assertEquals("getResponse", response.body());
    // }

    // @Test
    // public void testGet_HttpClientErrorException() {
    //     // Arrange
    //     when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
    //             .thenThrow(new RuntimeException("Error"));

    //     // Act & Assert
    //     assertThrows(HttpClientErrorException.class, () -> {
    //         gitHubApp.get("/test");
    //     });
    // }
}