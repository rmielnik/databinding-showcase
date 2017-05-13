package io.rmielnik.toast.calculations.response;

public abstract class Response {

    private boolean isProgress;
    private int result;

    protected Response(boolean isProgress, int result) {
        this.isProgress = isProgress;
        this.result = result;
    }

    public boolean isProgress() {
        return isProgress;
    }

    public int getResult() {
        return result;
    }
}
