package edu.ucsb.cs156.organic.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@Tag(name = "Courses")
@RequestMapping("/githubwebhook")
@RestController
@Slf4j
public class GithubWebhookController {
    // @Autowired
    // CourseRepository courseRepository;
    // @Autowired
    // StaffRepository courseStaffRepository;
    // @Autowired
    // SchoolRepository schoolRepository;
    // @Autowired
    // UserRepository userRepository;
    // @Autowired
    // StudentRepository studentRepository;
    // @Autowired
    // GitHubApp gitHubApp;
    // @Autowired
    // GitHubToken accessToken;
    // @Autowired
    // GitHubUserApi gitHubUserApi;
    @Value("${edu.ucsb.cs156.github.webhookSecret}")
    String webhookSecret;

    @Operation(summary = "Process a GitHub webhook payload", description = "Process a GitHub webhook payload")
    // @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping(value = "/process", consumes = "application/json")
    public void allCourses(@Parameter(name = "id") @RequestBody String payload) {
        log.warn("\u001B[33m" + payload + "\u001B[0m", webhookSecret);
    }
}
