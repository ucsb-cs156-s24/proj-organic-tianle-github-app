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

public class GitHubAppExceptionTest {

    @Test
    void testNullConst() throws Exception {
        RuntimeException exception = new GitHubAppException();
        assertEquals(null, exception.getMessage());
    }

    @Test
    void testStringConst() throws Exception {
        RuntimeException exception = new GitHubAppException("This is a msg");
        assertEquals("This is a msg", exception.getMessage());
    }

    @Test
    void testThrowable() throws Exception {
        RuntimeException exception = new GitHubAppException(new Throwable("This is a throwable"));
        assertEquals("java.lang.Throwable: This is a throwable", exception.getMessage());
    }

    @Test
    void testStringThrowable() throws Exception {
        RuntimeException exception = new GitHubAppException("This is a msg", new Throwable("This is a throwable"));
        assertEquals("This is a msg", exception.getMessage());
    }
}
