package com.tianleyu.github;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    public void setUp() {
        jwtProvider = new JwtProvider("mockAppId", "mockKeyFile");
    }

    @Test
    public void test_jwt_auto_refresh() throws Exception {
        // Arrange
        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.createJWT(anyString(), anyLong(), anyString())).thenReturn("JWT1");
            // Act & Assert
            Field jwtField = JwtProvider.class.getDeclaredField("jwt");
            jwtField.setAccessible(true);
            jwtField.set(jwtProvider, "mockedJwtToken");

            // Set the private variable 'jwtCreatedAt' using reflection
            Field jwtCreatedAtField = JwtProvider.class.getDeclaredField("jwtCreatedAt");
            jwtCreatedAtField.setAccessible(true);
            jwtCreatedAtField.set(jwtProvider, 0);

            jwtProvider.getJwt();
            assertEquals("JWT1", jwtProvider.getJwt());
        }
        // assertThrows(GitHubAppException.class, () -> {
        // gitHubApp.get("/test");
        // });
    }

    @Test
    public void test_jwt_auto_create() throws Exception {
        // Arrange
        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.createJWT(anyString(), anyLong(), anyString())).thenReturn("JWT1");
            // Act & Assert
            Field jwtField = JwtProvider.class.getDeclaredField("jwt");
            jwtField.setAccessible(true);
            jwtField.set(jwtProvider, null);

            // Set the private variable 'jwtCreatedAt' using reflection
            Field jwtCreatedAtField = JwtProvider.class.getDeclaredField("jwtCreatedAt");
            jwtCreatedAtField.setAccessible(true);
            jwtCreatedAtField.set(jwtProvider, 0);

            jwtProvider.getJwt();
            assertEquals("JWT1", jwtProvider.getJwt());
        }
        // assertThrows(GitHubAppException.class, () -> {
        // gitHubApp.get("/test");
        // });
    }
}
