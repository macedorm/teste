package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

public class MessageChat {

    @SerializedName("text")
    private String message;
    private String date;
    private boolean typeUser;


    public MessageChat(){

    }

    public MessageChat(String message, String date, boolean typeUser) {
        this.message = message;
        this.date = date;
        this.typeUser = typeUser;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isTypeUser() {
        return typeUser;
    }

    public void setTypeUser(boolean typeUser) {
        this.typeUser = typeUser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}


