
package edu.ucsb.cs156.organic.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.awaitility.Awaitility.await;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import edu.ucsb.cs156.organic.entities.Course;
import edu.ucsb.cs156.organic.entities.School;

import edu.ucsb.cs156.organic.errors.EntityNotFoundException;
import edu.ucsb.cs156.organic.entities.Staff;
import edu.ucsb.cs156.organic.entities.User;
import edu.ucsb.cs156.organic.entities.jobs.Job;
import edu.ucsb.cs156.organic.repositories.CourseRepository;
import edu.ucsb.cs156.organic.repositories.StaffRepository;
import edu.ucsb.cs156.organic.repositories.UserRepository;
import edu.ucsb.cs156.organic.repositories.SchoolRepository;
import edu.ucsb.cs156.organic.errors.EntityNotFoundException;

import edu.ucsb.cs156.organic.repositories.jobs.JobsRepository;
import edu.ucsb.cs156.organic.services.jobs.JobService;
import edu.ucsb.cs156.organic.services.CurrentUserService;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

@Slf4j
@WebMvcTest(controllers = SchoolController.class)
@Import(JobService.class)
@AutoConfigureDataJpa
public class SchoolControllerTests extends ControllerTestCase{
    
   @MockBean

    UserRepository userRepository;

    @MockBean
    SchoolRepository schoolRepository;

    @MockBean
    StaffRepository courseStaffRepository;

    @Autowired
    CurrentUserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/schools/all")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/schools/all")).andExpect(status().is(200));
    }

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/schools").param("abbrev", "1L")).andExpect(status().is(403));

    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {
        School school = School.builder()
                    .abbrev("ucsb")
                    .name("Ubarbara")
                    .termRegex("W24")
                    .termDescription("F24")
                    .termError("error")
                    .build();
        when(schoolRepository.findById(eq("ucsb"))).thenReturn(Optional.of(school));

        MvcResult response = mockMvc.perform(get("/api/schools").param("abbrev", "ucsb")).andExpect(status().isOk())

                .andReturn();

        verify(schoolRepository, times(1)).findById(eq("ucsb"));
        String expectedJson = objectMapper.writeValueAsString(school);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {
        when(schoolRepository.findById(eq("umn"))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(get("/api/schools").param("abbrev", "umn")).andExpect(status().isNotFound())

                .andReturn();

        verify(schoolRepository, times(1)).findById(eq("umn"));
        Map<String, Object> json = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>() {});
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("School with id umn not found", json.get("message"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_schools() throws Exception {
        School school1 = School.builder()
                    .abbrev("ucsb")
                    .name("Ubarbara")
                    .termRegex("W24")
                    .termDescription("F24")
                    .termError("error")
                    .build();        
        School school2 = School.builder()
                    .abbrev("umn")
                    .name("mich")
                    .termRegex("W24")
                    .termDescription("M24")
                    .termError("error1")
                    .build();  
        
        List<School> expectedSchools = Arrays.asList(school1, school2);

        when(schoolRepository.findAll()).thenReturn(expectedSchools);

        MvcResult response = mockMvc.perform(get("/api/schools/all")).andExpect(status().isOk()).andReturn();

        verify(schoolRepository, times(1)).findAll();
        String expectedJson = objectMapper.writeValueAsString(expectedSchools);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }


    // Tests for PUT /api/schools?id=... 

    @WithMockUser(roles = { "INSTRUCTOR", "USER" })
    @Test
    public void an_instructor_user_can_update_a_school_if_they_are_admin() throws Exception {
        // arrange


        School origSchool = School.builder()
                        .abbrev("ucsb")
                        .name("Ubarbara")
                        .termRegex("W24")
                        .termDescription("F24")
                        .termError("error")
                        .build();
        School editedSchool = School.builder()
                        .abbrev("ucsb")
                        .name("UBarbara")
                        .termRegex("M24")
                        .termDescription("S24")
                        .termError("error1")
                        .build();

        String requestBody = mapper.writeValueAsString(editedSchool);

        when(schoolRepository.findById(eq("ucsb"))).thenReturn(Optional.of(origSchool));
        when(schoolRepository.save(eq(origSchool))).thenReturn(origSchool);

        // act
        // get urlTemplate from courseAfter using string interpolation
        // String urlTemplate = String.format(
        //         "/api/schools/update?abbrev=%s&name=%s&termRegex=%s&termDescription=%s&termError=%s",
        //         editedSchool.getAbbrev(), editedSchool.getName(), editedSchool.getTermRegex(), editedSchool.getTermDescription(),
        //         editedSchool.getTermError());
        MvcResult response = mockMvc.perform(
                put("/api/schools/update?abbrev=ucsb")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(schoolRepository, times(1)).findById("ucsb");
        verify(schoolRepository, times(1)).save(editedSchool);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseString);
    }
        
    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_school_that_does_not_exist() throws Exception {
            // arrange
            School editedSchool = School.builder()
                            .abbrev("ucsb")
                            .name("Ubarbara")
                            .termRegex("W24")
                            .build();}


// Tests for DELETE /api/schools?id=... 


    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_school() throws Exception {
            // arrange


            School school1 = School.builder()
                            .abbrev("UCSB")
                            .name("University of California Santa Barbara")
                            .termRegex("W")
                            .termDescription("W24")
                            .termError("term error??")
                            .build();
                            

            when(schoolRepository.findById(eq("UCSB"))).thenReturn(Optional.of(school1));

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/schools?abbrev=UCSB")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(schoolRepository, times(1)).findById("UCSB");
            verify(schoolRepository, times(1)).delete(any());

            Map<String, Object> json = responseToJson(response);
            assertEquals("School with id UCSB deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_school_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(schoolRepository.findById(eq("UCSB"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/schools?abbrev=UCSB")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(schoolRepository, times(1)).findById("UCSB");
                Map<String, Object> json = responseToJson(response);
                assertEquals("School with id UCSB not found", json.get("message"));
        }

    
    // Tests for POST /api/schools...

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/schools/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" }) 
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/schools/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_school() throws Exception {
            // arrange

            School school = School.builder()
                            .abbrev("ucsb")
                            .name("Ubarbara")
                            .termRegex("[WSMF]\\d\\d")

                            .termDescription("F24")
                            .termError("error")
                            .build();

            when(schoolRepository.save(eq(school))).thenReturn(school);  


            // act
            MvcResult response = mockMvc.perform(
                post("/api/schools/post?abbrev=ucsb&name=Ubarbara&termRegex=[WSMF]\\d\\d&termDescription=F24&termError=error")
                                .with(csrf()))
                .andExpect(status().isOk()).andReturn();
                

            // assert
            verify(schoolRepository, times(1)).save(school);
            String expectedJson = mapper.writeValueAsString(school);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
            }

    
    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_school_bad_format_termRegex() throws Exception {
            // arrange

            School school = School.builder()
                            .abbrev("ucsb")
                            .name("Ubarbara")
                            .termRegex("[WSMF]\\d\\d")
                            .termDescription("q24")
                            .termError("error")
                            .build();

            when(schoolRepository.save(eq(school))).thenReturn(school);  


            // act
            MvcResult response = mockMvc.perform(post("/api/schools/post?abbrev=UCSB&name=Ubarbara&termRegex=[WSMF]\\d\\d&termDescription=q24&termError=error")
                                                                .with(csrf()))
                            .andExpect(status().is(400)).andReturn(); // only admins can post
                

            // assert
            Map<String, Object> json = responseToJson(response);
            assertEquals("IllegalArgumentException", json.get("type"));
            assertEquals("Invalid termDescription format. It must follow the pattern [WSMF]\\d\\d", json.get("message"));            
            }

    
    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_school_bad_format_abbrev() throws Exception {
            // arrange

            School school = School.builder()
                            .abbrev("UCSB")
                            .name("Ubarbara")
                            .termRegex("[WSMF]\\d\\d")
                            .termDescription("F24")
                            .termError("error")
                            .build();

            when(schoolRepository.save(eq(school))).thenReturn(school);  


            // act
            MvcResult response = mockMvc.perform(post("/api/schools/post?abbrev=UCSB&name=Ubarbara&termRegex=[WSMF]\\d\\d&termDescription=F24&termError=error")
                                                                .with(csrf()))
                            .andExpect(status().is(400)).andReturn(); // only admins can post
                

            // assert
            Map<String, Object> json = responseToJson(response);
            assertEquals("IllegalArgumentException", json.get("type"));
            assertEquals("Invalid abbrev format. Abbrev must be all lowercase", json.get("message"));            
            }

            @WithMockUser(roles = { "ADMIN", "USER" })
            @Test
            public void updateSchool_fails_whenSchoolDoesNotExist() throws Exception {
                // arrange
                String nonExistentAbbrev = "nonexistent";
                School editedSchool = School.builder()
                                    .abbrev(nonExistentAbbrev)
                                    .name("Nonexistent University")
                                    .termRegex("W24")
                                    .build();

            
                String requestBody = objectMapper.writeValueAsString(editedSchool);
            
                when(schoolRepository.findById(nonExistentAbbrev)).thenReturn(Optional.empty());
            
                // act  assert
                mockMvc.perform(put("/api/schools/update?abbrev=" + nonExistentAbbrev)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf()))
                        .andExpect(status().isNotFound())
                        .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                        .andExpect(result -> assertEquals("School with id nonexistent not found", result.getResolvedException().getMessage()));
            }


}

