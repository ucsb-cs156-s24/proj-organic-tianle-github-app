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
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;

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
import edu.ucsb.cs156.organic.models.GeneralOperationResp;
import edu.ucsb.cs156.organic.models.OrgStatus;
import edu.ucsb.cs156.organic.entities.School;

import org.springframework.security.access.AccessDeniedException;

import edu.ucsb.cs156.github.GitHubBuilderFactory;
import edu.ucsb.cs156.github.JwtProvider;
import edu.ucsb.cs156.github.OauthToken;

import org.kohsuke.github.GHApp;
import org.kohsuke.github.GHAppInstallation;
import org.kohsuke.github.GHEmail;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.time.LocalDateTime;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CoursesController
 *
 * This class is a REST controller for the Course entity. It provides endpoints
 * for creating, reading, updating, and deleting courses. It also provides an
 * endpoint for listing all courses.
 *
 * The class is annotated with {@code @RestController @code} to indicate that it
 * is a
 * controller class that handles REST requests. It is also annotated with
 * {@code @RequestMapping("/api/courses") @code} to indicate that it handles
 * requests to the
 * <code>/api/courses</code> path.
 * 
 * The class has an autowired field of type GithubToken called accessToken. This
 * field can be used to get a handle to the Github API via a call
 * like this one:
 * 
 * <pre>
 * GitHubUserApi ghUser = new GitHubUserApi(accessToken);
 * String emails = ghUser.userEmails().toString()
 * </pre>
 */

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
    JwtProvider jwtProvider;

    @Autowired
    GitHubBuilderFactory gitHubBuilderFactory;

    @Autowired
    OauthToken oauthToken;

    @Operation(summary = "List all courses")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/all")
    public Iterable<Course> allCourses() {
        User u = getCurrentUser().getUser();
        log.info("u={}", u);
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
        String githubOrg = course.getGithubOrg();

        if (!u.isAdmin()) {
            Optional<Staff> s = courseStaffRepository.findByCourseIdAndGithubId(id, u.getGithubId());
            if (s.isEmpty()) {
                throw new AccessDeniedException(
                        String.format("User %s is not authorized to get course %d",
                                u.getGithubLogin(), id));
            }
        }
        log.info("\u001b[33m****************************************");
        log.info("About to call gitHubApp.appInfo()...");
        log.warn("\u001b[33m" + jwtProvider.getJwt());

        GitHub gitHub;
        GHApp currApp;
        try {
            gitHub = gitHubBuilderFactory.build(jwtProvider);
            currApp = gitHub.getApp();
        } catch (Exception e) {
            log.error("EXCEPTION: ", e);
            course.setGithubAppInstallationId(0);
            courseRepository.save(course);
            return OrgStatus.builder()
                    .org(githubOrg)
                    .githubAppInstalled(false)
                    .name("")
                    .exceptionThrown(true)
                    .exceptionMessage(e.toString())
                    .build();
        }

        log.info("****************************************");
        log.info("appInfo={}", currApp.toString());
        log.info("****************************************");

        GHAppInstallation currOrg = null;
        try {
            currOrg = currApp.getInstallationByOrganization(githubOrg);
            course.setGithubAppInstallationId(currOrg.getId());
            courseRepository.save(course);
        } catch (Exception e) {
            log.error("CAUGHT EXCEPTION \u001b[31m" + e.toString() + "\u001b[0m");
            course.setGithubAppInstallationId(0);
            courseRepository.save(course);
            return OrgStatus.builder()
                    .org(githubOrg)
                    .githubAppInstalled(false)
                    .name(currApp.getSlug())
                    .exceptionThrown(true)
                    .exceptionMessage(e.toString())
                    .build();
        }
        return OrgStatus.builder()
                .org(githubOrg)
                .githubAppInstalled(true)
                .name(currApp.getSlug())
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
                .githubAppInstallationId(0)
                .build();
        try {
            GHAppInstallation inst = gitHubBuilderFactory.build(jwtProvider).getApp()
                    .getInstallationByOrganization(githubOrg);
            course.setGithubAppInstallationId(inst.getId());
        } catch (Exception e) {
            log.error("EXCEPTION: ", e);
            courseRepository.save(course);
            User u = getCurrentUser().getUser();

            Staff courseStaff = Staff.builder()
                    .courseId(course.getId())
                    .githubId(u.getGithubId())
                    .build();

            log.info("courseStaff={}", courseStaff);
            courseStaffRepository.save(courseStaff);
            return course;
        }

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
        try {
            GHAppInstallation inst = gitHubBuilderFactory.build(jwtProvider).getApp()
                    .getInstallationByOrganization(githubOrg);
            course.setGithubAppInstallationId(inst.getId());
        } catch (Exception e) {
            log.error("EXCEPTION: ", e);
            course.setGithubAppInstallationId(0);
            courseRepository.save(course);
            return course;
        }

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

    @Operation(summary = "Get a joinable info for a course")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/join")
    public Course joinById(
            @Parameter(name = "id") @RequestParam Long id) {
        User u = getCurrentUser().getUser();

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Course.class, id));

        return course;
    }

    @Operation(summary = "Join a course")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_INSTRUCTOR', 'ROLE_USER')")
    @PostMapping("/join")
    public GeneralOperationResp joinCourse(
            @Parameter(name = "id", description = "for example ucsb-cs156-f23") @RequestParam long courseId)
            throws JsonProcessingException {
        Course targetCourse;

        Optional<Course> tempCourse = courseRepository.findById(courseId);
        if (tempCourse.isPresent()) {
            targetCourse = tempCourse.get();
        } else {
            throw new EntityNotFoundException(Course.class, courseId);
        }
        User u = getCurrentUser().getUser();

        log.warn("\u001B[33mUSER JOINING THE COURSE\u001B[0m");
        log.warn("\u001B[33m" + u.getGithubLogin() + "\u001B[0m");
        log.warn("\u001B[33m" + oauthToken.getToken() + "\u001B[0m");
        School s;
        Optional<School> tempSchool = schoolRepository.findByName(targetCourse.getSchool());
        if (tempSchool.isPresent()) {
            s = tempSchool.get();
        } else {
            throw new EntityNotFoundException(School.class, targetCourse.getSchool());
        }

        String emailSufix = s.getAbbrev() + ".edu";
        GHMyself currUser;
        List<GHEmail> emails;

        try {
            
            currUser = gitHubBuilderFactory.buildOauth(oauthToken.getToken()).getMyself();
            emails = currUser.getEmails2();
        } catch (Exception e) {
            log.warn(
                    "\u001B[33m-----------------------------USER EMAIL ERROR -----------------------------------\u001B[0m");
            log.error(e.toString());
            throw new AccessDeniedException("Failed to get user email.");
        }

        boolean found = false;
        String schoolEmail = "";
        for (GHEmail email : emails) {
            if (email.getEmail().endsWith(emailSufix)) {
                found = true;
                schoolEmail = email.getEmail();
                break;
            }
        }

        if (!found) {
            throw new AccessDeniedException("User does not have a school email");
        }

        Student stu = studentRepository.findByCourseIdAndEmail(courseId, schoolEmail)
                .orElse(null);

        if (stu == null) {
            throw new AccessDeniedException("User is not in the roster");
        }

        if (stu.getGithubId() != null && stu.getGithubId() != 0) {
            throw new AccessDeniedException("User is already in the org");
        }
        // Check roster here
        // Send org Invitation
        try {
            GitHub hub = gitHubBuilderFactory.build(jwtProvider);
            GHAppInstallation inst = hub.getApp()
                    .getInstallationByOrganization(targetCourse.getGithubOrg());

            GHOrganization org = gitHubBuilderFactory.build(inst.createToken().create().getToken())
                    .getOrganization(targetCourse.getGithubOrg());
            log.warn("\u001B[33m--------- GOING TO INVITE ----------\u001B[0m");
            org.add(currUser, GHOrganization.Role.MEMBER);
        } catch (Exception e) {
            log.error(e.toString());
            throw new AccessDeniedException("Failed to invite user to org. Is this user already in the org?");
        }

        // Store in db
        stu.setGithubId(u.getGithubId());
        stu.setUser(u);

        studentRepository.save(stu);
        return GeneralOperationResp.builder().success(true).message("Joined Successfully").build();
    }

}
