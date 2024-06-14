package br.com.jjconsulting.mobile.dansales.model;

public class RelatorioObjetivo {

    private String nome;
    private double perda;
    private double obj;
    private double real;
    private double cob;
    private double raf;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPerda() {
        return perda;
    }

    public void setPerda(double perda) {
        this.perda = perda;
    }

    public double getObj() {
        return obj;
    }

    public void setObj(double obj) {
        this.obj = obj;
    }

    public double getReal() {
        return real;
    }

    public void setReal(double real) {
        this.real = real;
    }

    public double getCob() {
        return cob;
    }

    public void setCob(double cob) {
        this.cob = cob;
    }

    public double getRaf() {
        return raf;
    }

    public void setRaf(double raf) {
        this.raf = raf;
    }
}
