package br.com.jjconsulting.mobile.dansales.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SyncPesquisa implements Serializable {

    private boolean success;
    private String message;
    private float notaFinal;
    private ArrayList<SyncPesquisaPilar> listPilar;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public float getNotaFiscal() {
        return notaFinal;
    }

    public void setNotaFiscal(float notaFiscal) {
        this.notaFinal = notaFiscal;
    }

    public ArrayList<SyncPesquisaPilar> getListPilar() {
        return listPilar;
    }

    public void setListPilar(ArrayList<SyncPesquisaPilar> listPilar) {
        this.listPilar = listPilar;
    }
}
