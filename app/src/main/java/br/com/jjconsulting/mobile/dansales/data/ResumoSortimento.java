package br.com.jjconsulting.mobile.dansales.data;

public class ResumoSortimento {

    private String planogramCode;
    private int quantidadeObrigatorioTotal;
    private int quantidadeObrigatorioInserida;
    private int quantidadeRecomendadaTotal;
    private int quantidadeRecomendadaInserida;
    private int quantidadeInovacaoTotal;
    private int quantidadeInovacaoInserida;

    public ResumoSortimento() { }

    public String getPlanogramCode() {
        return planogramCode;
    }

    public void setPlanogramCode(String planogramCode) {
        this.planogramCode = planogramCode;
    }

    public int getQuantidadeObrigatorioTotal() {
        return quantidadeObrigatorioTotal;
    }

    public void setQuantidadeObrigatorioTotal(int quantidadeObrigatorioTotal) {
        this.quantidadeObrigatorioTotal = quantidadeObrigatorioTotal;
    }

    public int getQuantidadeObrigatorioInserida() {
        return quantidadeObrigatorioInserida;
    }

    public void setQuantidadeObrigatorioInserida(int quantidadeObrigatorioInserida) {
        this.quantidadeObrigatorioInserida = quantidadeObrigatorioInserida;
    }

    public int getQuantidadeRecomendadaTotal() {
        return quantidadeRecomendadaTotal;
    }

    public void setQuantidadeRecomendadaTotal(int quantidadeRecomendadaTotal) {
        this.quantidadeRecomendadaTotal = quantidadeRecomendadaTotal;
    }

    public int getQuantidadeRecomendadaInserida() {
        return quantidadeRecomendadaInserida;
    }

    public void setQuantidadeRecomendadaInserida(int quantidadeRecomendadaInserida) {
        this.quantidadeRecomendadaInserida = quantidadeRecomendadaInserida;
    }

    public int getQuantidadeInovacaoTotal() {
        return quantidadeInovacaoTotal;
    }

    public void setQuantidadeInovacaoTotal(int quantidadeInovacaoTotal) {
        this.quantidadeInovacaoTotal = quantidadeInovacaoTotal;
    }

    public int getQuantidadeInovacaoInserida() {
        return quantidadeInovacaoInserida;
    }

    public void setQuantidadeInovacaoInserida(int quantidadeInovacaoInserida) {
        this.quantidadeInovacaoInserida = quantidadeInovacaoInserida;
    }

    public double getPercentualSortimentoObrigatorio() {
        double total = (double)quantidadeObrigatorioTotal;
        double inserido = (double)quantidadeObrigatorioInserida;

        if (total == 0) {
            return 0;
        }

        return inserido / total * 100;
    }

    public double getPercentualSortimentoRecomendado() {
        double total = (double)quantidadeRecomendadaTotal;
        double inserido = (double)quantidadeRecomendadaInserida;

        if (total == 0) {
            return 0;
        }

        return inserido / total * 100;
    }

    public double getPercentualSortimentoInovacao() {
        double total = (double)quantidadeInovacaoTotal;
        double inserido = (double)quantidadeInovacaoInserida;

        if (total == 0) {
            return 0;
        }

        return inserido / total * 100;
    }
}
