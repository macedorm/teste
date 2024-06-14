package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public class AttachMessage {

    private int idAttach;
    private int idMessage;
    private int type;

    private String name;

    public int getIdAttach() {
        return idAttach;
    }

    public void setIdAttach(int idAttach) {
        this.idAttach = idAttach;
    }

    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
