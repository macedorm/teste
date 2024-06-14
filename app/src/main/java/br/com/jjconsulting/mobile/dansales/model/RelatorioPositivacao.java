package br.com.jjconsulting.mobile.dansales.model;

import java.util.Date;

public class RelatorioPositivacao {

    private String codigo;

    private String nome;

    private int planejadoQtd;
    private int aderenciaQtd;
    private int foraPlanoQtd;


    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPlanejadoQtd() {
        return planejadoQtd;
    }

    public int getAderenciaQtd() {
        return aderenciaQtd;
    }

    public int getForaPlanoQtd() {
        return foraPlanoQtd;
    }

    public void setPlanejadoQtd(int planejadoQtd) {
        this.planejadoQtd = planejadoQtd;
    }

    public void setAderenciaQtd(int aderenciaQtd) {
        this.aderenciaQtd = aderenciaQtd;
    }

    public void setForaPlanoQtd(int foraPlanoQtd) {
        this.foraPlanoQtd = foraPlanoQtd;
    }

    public int getAderenciaPerc() {
        float calc = (aderenciaQtd * 100.00f) / planejadoQtd;

        float min = Float.NEGATIVE_INFINITY;
        float max = Float.POSITIVE_INFINITY;

        if(min == calc|| max == calc){
            return 0;
        } else {
            return Math.round(calc);
        }
    }

    public int getForaPlanoPerc() {
        float calc = (foraPlanoQtd / (float)(aderenciaQtd+foraPlanoQtd)) * 100.00f;

        float min = Float.NEGATIVE_INFINITY;
        float max = Float.POSITIVE_INFINITY;

        if(min == calc|| max == calc){
            return 0;
        } else {
            return Math.round(calc);
        }
    }

    public int getProdutivoPerc() {

        try{
            float calc = ((aderenciaQtd + foraPlanoQtd) * 100.00f) / planejadoQtd;
            float min = Float.NEGATIVE_INFINITY;
            float max = Float.POSITIVE_INFINITY;

            if(min == calc|| max == calc){
                return 0;
            } else {
                return Math.round(calc);
            }
        } catch (Exception ex){
            return 0;
        }

    }

    public int getPerdidoPerc() {
        try{
            return Math.round(100.00f - getAderenciaPerc());
        } catch (Exception ex){
            return 0;
        }
    }

}
