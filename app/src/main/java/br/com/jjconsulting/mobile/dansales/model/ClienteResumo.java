package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public class ClienteResumo implements Serializable {

	private float nota;
	private float espaco;

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }

    public float getEspaco() {
        return espaco;
    }

    public void setEspaco(float espaco) {
        this.espaco = espaco;
    }
}