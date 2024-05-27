package edu.ucsb.cs156.github;

public class GitHubAppException extends RuntimeException {
    public GitHubAppException(String message) {
        super(message);
    }

    public GitHubAppException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitHubAppException(Throwable cause) {
        super(cause);
    }

    public GitHubAppException() {
        super();
    }
}
