package br.com.jjconsulting.mobile.dansales.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ValidationDan implements Serializable {

    private Object origin;
    private ArrayList<ValidationMessage> message;

    public ValidationDan() {
        message = new ArrayList();
    }

    public ValidationDan(Object origin) {
        this.message = new ArrayList();
        this.origin = origin;
    }

    public Object getOrigin() {
        return origin;
    }

    public void setOrigin(Object origin) {
        this.origin = origin;
    }

    public ArrayList<ValidationMessage> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<ValidationMessage> message) {
        this.message = message;
    }

    public void addError(String message){
        this.message.add(new ValidationMessage(ValidationMessageType.ERROR, message));
    }

    public void addInfo(String message){
        this.message.add(new ValidationMessage(ValidationMessageType.SUCCESS, message));
    }

    public void addWarning(String message){
        this.message.add(new ValidationMessage(ValidationMessageType.ALERT, message));
    }

    public void addValidation(ValidationDan value){
        if (value != null) {
            for (ValidationMessage msg : value.getMessage()) {
                getMessage().add(msg);
            }
        }
    }

    public boolean isValid() {
        boolean isValid = true;
        for (ValidationMessage msg: message) {
            if(msg.getType() == ValidationMessageType.ERROR){
                isValid = false;
                break;
            }
        }

        return isValid;
    }

    public String GetAllErrorMessages(){
        StringBuilder sMessage = new StringBuilder();

        for (ValidationMessage msg: message) {
            if(msg.getType() == ValidationMessageType.ERROR){
                if (sMessage.length() > 0)
                    sMessage.append("\r\n");

                sMessage.append(msg.getMessage());
            }
        }
        return sMessage.toString();
    }

}
