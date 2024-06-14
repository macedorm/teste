package br.com.jjconsulting.mobile.jjlib.model;

import java.util.ArrayList;
import java.util.HashMap;


public class RetError {

    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
