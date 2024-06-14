package br.com.jjconsulting.mobile.jjlib.data;


import java.io.Serializable;

public class ValidationMessage  implements Serializable {
    private ValidationMessageType type;
    private String message;

    public ValidationMessageType getType() {
        return type;
    }

    public void setType(ValidationMessageType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ValidationMessage(){

    }

    public ValidationMessage(ValidationMessageType type, String message){
        setType(type);
        setMessage(message);
    }

    public ValidationMessage(String message){
        setType(ValidationMessageType.ERROR);
        setMessage(message);
    }
}
