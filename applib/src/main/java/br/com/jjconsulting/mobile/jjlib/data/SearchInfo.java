package br.com.jjconsulting.mobile.jjlib.data;

import java.io.Serializable;

public class SearchInfo implements Serializable {

    private String name;
    private int position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
