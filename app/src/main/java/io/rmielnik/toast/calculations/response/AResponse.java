package io.rmielnik.toast.calculations.response;

public class AResponse extends Response {

    public static AResponse success(int result) {
        return new AResponse(false, result);
    }

    public static AResponse inProgress() {
        return new AResponse(true, 0);
    }

    protected AResponse(boolean isProgress, int result) {
        super(isProgress, result);
    }
}
