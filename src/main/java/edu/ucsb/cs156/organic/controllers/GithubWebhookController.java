package edu.ucsb.cs156.organic.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import java.net.http.HttpResponse;
import java.nio.file.AccessDeniedException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletResponse;

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

    private final String ALGORITHM = "HmacSHA256";

    @Operation(summary = "Process a GitHub webhook payload", description = "Process a GitHub webhook payload")
    // @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping(value = "/process", consumes = "application/json")

    public void allCourses(@RequestHeader(value = "X-Hub-Signature-256") String signature,
            @Parameter(name = "id") @RequestBody String payload, HttpServletResponse response) throws Exception {
        // Sample json on INSTALL (this is valuable for testing) :
        // {"action":"created","installation":{"id":51336299,"account":{"login":"ucsb-cs-dev","id":132534027,"node_id":"O_kgDOB-ZPCw","avatar_url":"https://avatars.githubusercontent.com/u/132534027?v=4","gravatar_id":"","url":"https://api.github.com/users/ucsb-cs-dev","html_url":"https://github.com/ucsb-cs-dev","followers_url":"https://api.github.com/users/ucsb-cs-dev/followers","following_url":"https://api.github.com/users/ucsb-cs-dev/following{/other_user}","gists_url":"https://api.github.com/users/ucsb-cs-dev/gists{/gist_id}","starred_url":"https://api.github.com/users/ucsb-cs-dev/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/ucsb-cs-dev/subscriptions","organizations_url":"https://api.github.com/users/ucsb-cs-dev/orgs","repos_url":"https://api.github.com/users/ucsb-cs-dev/repos","events_url":"https://api.github.com/users/ucsb-cs-dev/events{/privacy}","received_events_url":"https://api.github.com/users/ucsb-cs-dev/received_events","type":"Organization","site_admin":false},"repository_selection":"selected","access_tokens_url":"https://api.github.com/app/installations/51336299/access_tokens","repositories_url":"https://api.github.com/installation/repositories","html_url":"https://github.com/organizations/ucsb-cs-dev/settings/installations/51336299","app_id":866461,"app_slug":"ucsb-cs-linker","target_id":132534027,"target_type":"Organization","permissions":{"members":"write","organization_administration":"write"},"events":["member","membership","organization"],"created_at":"2024-05-29T13:46:04.000-07:00","updated_at":"2024-05-29T13:46:04.000-07:00","single_file_name":null,"has_multiple_single_files":false,"single_file_paths":[],"suspended_by":null,"suspended_at":null},"repositories":[],"requester":null,"sender":{"login":"yuxiaolejs","id":43783877,"node_id":"MDQ6VXNlcjQzNzgzODc3","avatar_url":"https://avatars.githubusercontent.com/u/43783877?v=4","gravatar_id":"","url":"https://api.github.com/users/yuxiaolejs","html_url":"https://github.com/yuxiaolejs","followers_url":"https://api.github.com/users/yuxiaolejs/followers","following_url":"https://api.github.com/users/yuxiaolejs/following{/other_user}","gists_url":"https://api.github.com/users/yuxiaolejs/gists{/gist_id}","starred_url":"https://api.github.com/users/yuxiaolejs/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/yuxiaolejs/subscriptions","organizations_url":"https://api.github.com/users/yuxiaolejs/orgs","repos_url":"https://api.github.com/users/yuxiaolejs/repos","events_url":"https://api.github.com/users/yuxiaolejs/events{/privacy}","received_events_url":"https://api.github.com/users/yuxiaolejs/received_events","type":"User","site_admin":false}}
        // signature header:
        // sha256=38433304c66fa9ebac84f11964b9035dfc923893ed49a8dfb82d4f0fd3da9e23

        // Verify signature
        final SecretKeySpec secret_key = new SecretKeySpec(webhookSecret.getBytes("UTF-8"), ALGORITHM);
        final Mac sha256_HMAC = Mac.getInstance(ALGORITHM);
        sha256_HMAC.init(secret_key);
        final String trusted = "sha256=" + byteArrayToHex(sha256_HMAC.doFinal(payload.getBytes("UTF-8")));
        if (!trusted.equals(signature)) {
            log.error("\u001B[33mVALIDATION FAILED\u001B[0m: Sec " + webhookSecret + " - Sig " + signature + " - Exp "
                    + trusted);
            // throw new AccessDeniedException("Invalid signature");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        log.warn("\u001B[33m" + payload + "\u001B[0m:" + webhookSecret + " - " + signature);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // https://gist.github.com/lesstif/655f6b295a619306405621e02634a08d
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
