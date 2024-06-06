package edu.ucsb.cs156.organic.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiControllerTest {

    public static ObjectMapper mapperThatIgnoresMockitoMocks() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public boolean hasIgnoreMarker(final AnnotatedMember m) {
                return super.hasIgnoreMarker(m) || m.getName().contains("Mockito");
            }
        });
        return mapper;
    }

    static class TestClass {
        public String name = "test";
        public String MockitoField = "should be ignored";

        @JsonIgnore
        public String ignoredField = "ignored";
    }

    @Test
    public void testMapperIgnoresMockitoMocks() throws Exception {
        ObjectMapper mapper = mapperThatIgnoresMockitoMocks();
        TestClass testClass = new TestClass();

        String json = mapper.writeValueAsString(testClass);

        // Check that "MockitoField" is ignored
        assertFalse(json.contains("MockitoField"));

        // Check that "ignoredField" is ignored
        assertFalse(json.contains("ignoredField"));

        // Check that "name" is not ignored
        assertTrue(json.contains("name"));
    }
}
