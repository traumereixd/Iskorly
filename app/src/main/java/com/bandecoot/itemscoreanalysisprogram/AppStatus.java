package com.bandecoot.itemscoreanalysisprogram;

/**
 * Data model for app status from remote kill-switch endpoint.
 */
public class AppStatus {
    private boolean disabled;
    private String message;

    public AppStatus() {
        this.disabled = false;
        this.message = "";
    }

    public AppStatus(boolean disabled, String message) {
        this.disabled = disabled;
        this.message = message;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
