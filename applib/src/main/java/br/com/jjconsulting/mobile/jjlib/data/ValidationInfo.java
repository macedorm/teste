package br.com.jjconsulting.mobile.jjlib.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ValidationInfo implements Serializable {

    private Object origin;
    private ArrayList<ValidationMessage> messages;

    public ValidationInfo() {
        messages = new ArrayList();
    }

    public ValidationInfo(Object origin) {
        this.messages = new ArrayList();
        this.origin = origin;
    }

    public Object getOrigin() {
        return origin;
    }

    public void setOrigin(Object origin) {
        this.origin = origin;
    }

    public ArrayList<ValidationMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<ValidationMessage> messages) {
        this.messages = messages;
    }

    public void addError(String message){
        this.messages.add(new ValidationMessage(ValidationMessageType.ERROR, message));
    }

    public void addInfo(String message){
        this.messages.add(new ValidationMessage(ValidationMessageType.SUCCESS, message));
    }

    public void addWarning(String message){
        this.messages.add(new ValidationMessage(ValidationMessageType.ALERT, message));
    }

    public void addValidation(ValidationInfo value){
        if (value != null) {
            for (ValidationMessage msg : value.getMessages()) {
                getMessages().add(msg);
            }
        }
    }

    public boolean isValid() {
        boolean isValid = true;
        for (ValidationMessage msg:messages) {
            if(msg.getType() == ValidationMessageType.ERROR){
                isValid = false;
                break;
            }
        }

        return isValid;
    }

    public String GetAllErrorMessages(){
        StringBuilder sMessage = new StringBuilder();

        for (ValidationMessage msg:messages) {
            if(msg.getType() == ValidationMessageType.ERROR){
                if (sMessage.length() > 0)
                    sMessage.append("\r\n");

                sMessage.append(msg.getMessage());
            }
        }
        return sMessage.toString();
    }

}
