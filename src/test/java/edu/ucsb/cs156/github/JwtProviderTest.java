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

public class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider("FakeID", "FakeFile");
    }

    @Test
    void getJwtNoExpire() throws Exception {
        Field jwt = jwtProvider.getClass().getDeclaredField("jwt");
        jwt.setAccessible(true);
        jwt.set(jwtProvider, "FakeJWT");
        Field jwtCreatedAt = jwtProvider.getClass().getDeclaredField("jwtCreatedAt");
        jwtCreatedAt.setAccessible(true);
        jwtCreatedAt.set(jwtProvider, System.currentTimeMillis());
        assertEquals("FakeJWT", jwtProvider.getJwt());
    }

    @Test
    void getJwtExpired() throws Exception {
        Field jwt = jwtProvider.getClass().getDeclaredField("jwt");
        jwt.setAccessible(true);
        jwt.set(jwtProvider, "FakeJWT");
        Field jwtCreatedAt = jwtProvider.getClass().getDeclaredField("jwtCreatedAt");
        jwtCreatedAt.setAccessible(true);
        jwtCreatedAt.set(jwtProvider, System.currentTimeMillis() - 1000000);
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.createJWT(anyString(), anyLong(), anyString()))
                    .thenReturn("SecondFakeJWT");
            assertEquals("SecondFakeJWT", jwtProvider.getJwt());
        }
    }

    @Test
    void getJwtFirst() throws Exception {
        Field jwtCreatedAt = jwtProvider.getClass().getDeclaredField("jwtCreatedAt");
        jwtCreatedAt.setAccessible(true);
        jwtCreatedAt.set(jwtProvider, System.currentTimeMillis());
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.createJWT(anyString(), anyLong(), anyString()))
                    .thenReturn("SecondFakeJWT");
            assertEquals("SecondFakeJWT", jwtProvider.getJwt());
        }
    }

    @Test
    void getJwtError() throws Exception {
        Field jwtCreatedAt = jwtProvider.getClass().getDeclaredField("jwtCreatedAt");
        jwtCreatedAt.setAccessible(true);
        jwtCreatedAt.set(jwtProvider, System.currentTimeMillis());
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.createJWT(anyString(), anyLong(), anyString()))
                    .thenThrow(new RuntimeException("Error"));
            assertEquals(null, jwtProvider.getJwt());
        }
    }
}
