package br.com.jjconsulting.mobile.jjlib.model;

import java.util.Hashtable;

public class ValidationLetter {

    public static int STATUS_EMPTY = 0;

    private int status;

    private String message;

    private Hashtable validationList;

    private Hashtable data;


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

    public Hashtable getValidationList() {
        return validationList;
    }

    public void setValidationList(Hashtable validationList) {
        this.validationList = validationList;
    }

    public Hashtable getData() {
        return data;
    }

    public void setData(Hashtable data) {
        this.data = data;
    }
}
