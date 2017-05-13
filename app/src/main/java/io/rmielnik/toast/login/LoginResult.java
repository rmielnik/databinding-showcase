package io.rmielnik.toast.login;

public class LoginResult {

    public static LoginResult success(String token) {
        return new LoginResult(false, true, token, null);
    }

    public static LoginResult inProgress() {
        return new LoginResult(true, false, null, null);
    }

    public static LoginResult error(Throwable t) {
        return new LoginResult(false, false, null, t);
    }

    private String token;
    private boolean inProgress;
    private boolean success;
    private Throwable error;

    private LoginResult(boolean inProgress, boolean success, String token, Throwable error) {
        this.inProgress = inProgress;
        this.success = success;
        this.token = token;
        this.error = error;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getToken() {
        return token;
    }

    public Throwable getError() {
        return error;
    }
}
