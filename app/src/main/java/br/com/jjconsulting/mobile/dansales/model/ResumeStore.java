package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResumeStore implements Serializable {



    private String message;

    private boolean success;

    private double visitaMedia;

    private double ultimaNota;

    private String tempoMedio;

    private List<ResumePilar> pilares;

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

    public String getTempoMedio() {
        return tempoMedio;
    }

    public void setTempoMedio(String tempoMedio) {
        this.tempoMedio = tempoMedio;
    }

    public double getVisitaMedia() {
        return visitaMedia;
    }

    public void setVisitaMedia(int visitaMedia) {
        this.visitaMedia = visitaMedia;
    }

    public double getUltimaNota() {
        return ultimaNota;
    }

    public void setUltimaNota(double ultimaNota) {
        this.ultimaNota = ultimaNota;
    }

    public List<ResumePilar> getPilares() {
        return pilares;
    }

    public void setPilares(List<ResumePilar> pilares) {
        this.pilares = pilares;
    }

    public static class ResumePilar {
        private String pilar;
        private double media_dia;
        private double media_x_dias;
        private double media_promotor;


        public String getPilar() {
            return pilar;
        }

        public void setPilar(String pilar) {
            this.pilar = pilar;
        }

        public double getMedia_dia() {
            return media_dia;
        }

        public void setMedia_dia(double media_dia) {
            this.media_dia = media_dia;
        }

        public double getMedia_x_dias() {
            return media_x_dias;
        }

        public void setMedia_x_dias(double media_x_dias) {
            this.media_x_dias = media_x_dias;
        }

        public double getMedia_promotor() {
            return media_promotor;
        }

        public void setMedia_promotor(double media_promotor) {
            this.media_promotor = media_promotor;
        }
    }

}
