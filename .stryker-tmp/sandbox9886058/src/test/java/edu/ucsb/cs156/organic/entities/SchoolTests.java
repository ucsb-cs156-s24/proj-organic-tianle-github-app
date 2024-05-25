package edu.ucsb.cs156.organic.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class SchoolTests {

    @Test
    void test_noArgConstructor() {
        School school = new School();
        assertNotNull(school);
    }

    @Test 
    void test_allArgsConstructor() {
        School school = new School("ucsb", "UC Santa Barbara", "[WSMF]\\d\\d", "Enter quarter, e.g. F23, W24, S24, M24", "Quarter must be entered in the correct format");
        assertEquals("ucsb", school.getAbbrev());
        assertEquals("UC Santa Barbara", school.getName());
        assertEquals("[WSMF]\\d\\d", school.getTermRegex());
        assertEquals("Enter quarter, e.g. F23, W24, S24, M24", school.getTermDescription());
        assertEquals("Quarter must be entered in the correct format", school.getTermError());
    }
}
