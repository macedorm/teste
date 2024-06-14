package br.com.jjconsulting.mobile.dansales.data;

public class SyncResult {

    private boolean isValid;
    private String message;

    public SyncResult() { }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
