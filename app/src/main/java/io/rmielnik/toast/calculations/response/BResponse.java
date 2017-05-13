package io.rmielnik.toast.calculations.response;

public class BResponse extends Response {

    public static BResponse success(int result) {
        return new BResponse(false, result);
    }

    public static BResponse inProgress() {
        return new BResponse(true, 0);
    }

    protected BResponse(boolean isProgress, int result) {
        super(isProgress, result);
    }
}
