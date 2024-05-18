package edu.ucsb.cs156.organic.controllers;

import edu.ucsb.cs156.organic.entities.Course;
import edu.ucsb.cs156.organic.entities.Staff;
import edu.ucsb.cs156.organic.entities.Student;
import edu.ucsb.cs156.organic.entities.User;
import edu.ucsb.cs156.organic.repositories.CourseRepository;
import edu.ucsb.cs156.organic.repositories.SchoolRepository;
import edu.ucsb.cs156.organic.repositories.StaffRepository;
import edu.ucsb.cs156.organic.repositories.StudentRepository;
import edu.ucsb.cs156.organic.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import liquibase.pro.packaged.gh;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.organic.errors.EntityNotFoundException;
import edu.ucsb.cs156.organic.models.OrgStatus;
import edu.ucsb.cs156.organic.entities.School;

import org.springframework.security.access.AccessDeniedException;

import com.tianleyu.github.GitHubApp;
import com.tianleyu.github.GitHubAppOrg;
import com.tianleyu.github.GitHubToken;
import com.tianleyu.github.GitHubUserApi;
import com.tianleyu.github.JwtProvider;
import org.kohsuke.github.GitHub;

import java.time.LocalDateTime;

import javax.transaction.Transactional;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.Optional;

@Tag(name = "Courses")
@RequestMapping("/api/courses")
@RestController
@Slf4j
public class CoursesController extends ApiController {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StaffRepository courseStaffRepository;

    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    GitHubApp gitHubApp;

    @Autowired
    GitHubToken accessToken;

    @Operation(summary = "List all courses")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/all")
    public Iterable<Course> allCourses() {
        User u = getCurrentUser().getUser();
        log.info("u={}", u);
        // This is how you use it
        // log.warn("\u001B[33mTOKENTOTOKEN " + accessToken.getToken() + "\u001B[0m");
        // log.warn("\u001B[33mGetting User Emails\u001B[0m");
        // GitHubUserApi ghUser = new GitHubUserApi(accessToken);
        // log.warn("\u001B[33m"+ghUser.userEmails().toString()+"\u001B[0m");
        if (u.isAdmin()) {
            return courseRepository.findAll();
        } else {
            return courseRepository.findCoursesStaffedByUser(u.getGithubId());
        }
    }

    @Operation(summary = "Get a single course by id")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/get")
    public Course getById(
            @Parameter(name = "id") @RequestParam Long id) {
        User u = getCurrentUser().getUser();

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Course.class, id));

        if (!u.isAdmin()) {
            courseStaffRepository.findByCourseIdAndGithubId(id, u.getGithubId())
                    .orElseThrow(() -> new AccessDeniedException(
                            String.format("User %s is not authorized to get course %d",
                                    u.getGithubLogin(), id)));
        }
        return course;
    }

    @Operation(summary = "Get GitHub App status")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/github")
    public OrgStatus getGithubOrgById(
            @Parameter(name = "id") @RequestParam Long id) {
        User u = getCurrentUser().getUser();

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Course.class, id));

        if (!u.isAdmin()) {
            courseStaffRepository.findByCourseIdAndGithubId(id, u.getGithubId())
                    .orElseThrow(() -> new AccessDeniedException(
                            String.format("User %s is not authorized to get course %d",
                                    u.getGithubLogin(), id)));
        }
        String githubOrg = course.getGithubOrg();
        try {
            GitHubAppOrg org = gitHubApp.org(githubOrg);
        } catch (Exception e) {
            return OrgStatus.builder()
                    .org(githubOrg)
                    .githubAppInstalled(false)
                    .build();
        }
        return OrgStatus.builder()
                .org(githubOrg)
                .githubAppInstalled(true)
                .build();
    }

    @Operation(summary = "Create a new course")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_INSTRUCTOR')")
    @PostMapping("/post")
    public Course postCourse(
            @Parameter(name = "name", description = "course name, e.g. CMPSC 156") @RequestParam String name,
            @Parameter(name = "school", description = "school abbreviation e.g. UCSB") @RequestParam String school,
            @Parameter(name = "term", description = "quarter or semester, e.g. F23") @RequestParam String term,
            @Parameter(name = "startDate", description = "in iso format, i.e. YYYY-mm-ddTHH:MM:SS; e.g. 2023-10-01T00:00:00 see https://en.wikipedia.org/wiki/ISO_8601") @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(name = "endDate", description = "in iso format, i.e. YYYY-mm-ddTHH:MM:SS; e.g. 2023-12-31T11:59:59 see https://en.wikipedia.org/wiki/ISO_8601") @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(name = "githubOrg", description = "for example ucsb-cs156-f23") @RequestParam String githubOrg)
            throws JsonProcessingException {

        Course course = Course.builder()
                .name(name)
                .school(school)
                .term(term)
                .startDate(startDate)
                .endDate(endDate)
                .githubOrg(githubOrg)
                .build();

        Course savedCourse = courseRepository.save(course);
        User u = getCurrentUser().getUser();

        Staff courseStaff = Staff.builder()
                .courseId(savedCourse.getId())
                .githubId(u.getGithubId())
                .build();

        log.info("courseStaff={}", courseStaff);
        courseStaffRepository.save(courseStaff);

        return savedCourse;
    }

    @Operation(summary = "Add a staff member to a course")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/addStaff")
    public Staff addStaff(
            @Parameter(name = "courseId") @RequestParam Long courseId,
            @Parameter(name = "githubLogin") @RequestParam String githubLogin)
            throws JsonProcessingException {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException(Course.class, courseId.toString()));

        User user = userRepository.findByGithubLogin(githubLogin)
                .orElseThrow(() -> new EntityNotFoundException(User.class, githubLogin.toString()));

        Staff courseStaff = Staff.builder()
                .courseId(course.getId())
                .githubId(user.getGithubId())
                .user(user)
                .build();

        courseStaff = courseStaffRepository.save(courseStaff);
        log.info("courseStaff={}", courseStaff);

        return courseStaff;
    }

    @Operation(summary = "Get Staff for course")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getStaff")
    public Iterable<Staff> getStaff(
            @Parameter(name = "courseId") @RequestParam Long courseId)
            throws JsonProcessingException {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException(Course.class, courseId.toString()));

        Iterable<Staff> courseStaff = courseStaffRepository.findByCourseId(course.getId());
        return courseStaff;
    }

    @Operation(summary = "Delete a Course Staff by id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/staff")
    public Object deleteStaff(
            @Parameter(name = "id") @RequestParam Long id) {
        Staff staff = courseStaffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Staff.class, id.toString()));

        courseStaffRepository.delete(staff);
        return genericMessage("Staff with id %s is deleted".formatted(id));
    }

    @Operation(summary = "Update information for a course")
    // allow for roles of ADMIN or INSTRUCTOR but only if the user is a staff member
    // for the course
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    @PutMapping("/update")
    public Course updateCourse(
            @Parameter(name = "id") @RequestParam Long id,
            @Parameter(name = "name", description = "course name, e.g. CMPSC 156") @RequestParam String name,
            @Parameter(name = "school", description = "school abbreviation e.g. UCSB") @RequestParam String school,
            @Parameter(name = "term", description = "quarter or semester, e.g. F23") @RequestParam String term,
            @Parameter(name = "startDate", description = "in iso format, i.e. YYYY-mm-ddTHH:MM:SS; e.g. 2023-10-01T00:00:00 see https://en.wikipedia.org/wiki/ISO_8601") @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(name = "endDate", description = "in iso format, i.e. YYYY-mm-ddTHH:MM:SS; e.g. 2023-12-31T11:59:59 see https://en.wikipedia.org/wiki/ISO_8601") @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(name = "githubOrg", description = "for example ucsb-cs156-f23") @RequestParam String githubOrg)
            throws JsonProcessingException {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Course.class, id.toString()));

        // Check if the current user is a staff member for this course or an admin. If
        // not, throw AccessDeniedException

        User u = getCurrentUser().getUser();
        if (!u.isAdmin()) {
            courseStaffRepository.findByCourseIdAndGithubId(course.getId(), u.getGithubId())
                    .orElseThrow(() -> new AccessDeniedException(
                            "User is not a staff member for this course"));
        }

        course.setName(name);
        course.setSchool(school);
        course.setTerm(term);
        course.setStartDate(startDate);
        course.setEndDate(endDate);
        course.setGithubOrg(githubOrg);

        course = courseRepository.save(course);
        log.info("course={}", course);

        return course;
    }

    // delete a course if the user is an admin or instructor for the course
    @Operation(summary = "Delete a course")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    @DeleteMapping("/delete")
    public Course deleteCourse(
            @Parameter(name = "id") @RequestParam Long id)
            throws JsonProcessingException {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Course.class, id.toString()));

        // Check if the current user is a staff member for this course or an admin. If
        // not, throw AccessDeniedException

        User u = getCurrentUser().getUser();
        if (!u.isAdmin()) {
            courseStaffRepository.findByCourseIdAndGithubId(course.getId(), u.getGithubId())
                    .orElseThrow(() -> new AccessDeniedException(
                            "User is not a staff member for this course"));
        }

        courseRepository.delete(course);
        return course;
    }

    @Operation(summary = "Join a course")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_INSTRUCTOR', 'ROLE_USER')")
    @PostMapping("/join")
    public String joinCourse(
            @Parameter(name = "id", description = "for example ucsb-cs156-f23") @RequestParam long courseId)
            throws JsonProcessingException {

        Course targetCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException(Course.class, courseId));
        User u = getCurrentUser().getUser();

        log.warn("\u001B[33m" + u.getGithubLogin() + "\u001B[0m");

        School s = schoolRepository.findById(targetCourse.getSchool())
                .orElseThrow(() -> new EntityNotFoundException(School.class, targetCourse.getSchool()));

        String emailSufix = s.getAbbrev() + ".edu";

        GitHubUserApi ghUser = new GitHubUserApi(accessToken);
        // log.warn("\u001B[33m"+ghUser.userEmails().toString()+"\u001B[0m");

        ArrayList<String> emails = ghUser.userEmails();

        boolean found = false;
        String schoolEmail = "";
        for (String email : emails) {
            if (email.endsWith(emailSufix)) {
                found = true;
                schoolEmail = email;
                break;
            }
        }

        if (!found) {
            return "User does not have a school email";
        }

        String netId = schoolEmail.split("@")[0];

        Student stu = studentRepository.findByCourseIdAndStudentId(courseId, netId)
                .orElse(null);

        if (stu != null) {
            return "User is already in the course";
        }

        // Check roster here

        // Send org Invitation
        GitHubAppOrg org = gitHubApp.org(targetCourse.getGithubOrg());
        org.inviteUserToThisOrg(u.getGithubLogin());

        // Store in db
        Student student = Student.builder()
                .courseId(courseId)
                .studentId(netId)
                .email(schoolEmail)
                .githubId(u.getGithubId())
                .build();

        studentRepository.save(student);
        return "OK";
    }

}
