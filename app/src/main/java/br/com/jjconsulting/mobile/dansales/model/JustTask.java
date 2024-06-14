package br.com.jjconsulting.mobile.dansales.model;


import br.com.jjconsulting.mobile.dansales.util.TJustTask;

public class JustTask {

    private TJustTask tipo;
    private Pesquisa pesquisa;
    private int indexTarefa;

    public TJustTask getTipo() {
        return tipo;
    }

    public void setTipo(br.com.jjconsulting.mobile.dansales.util.TJustTask tipo) {
        this.tipo = tipo;
    }

    public Pesquisa getPesquisa() {
        return pesquisa;
    }

    public void setPesquisa(Pesquisa pesquisa) {
        this.pesquisa = pesquisa;
    }

    public int getIndexTarefa() {
        return indexTarefa;
    }

    public void setIndexTarefa(int indexTarefa) {
        this.indexTarefa = indexTarefa;
    }

    public static JustTask getInstance(TJustTask tJustTask, Pesquisa pesquisa){
        JustTask justTask = new JustTask();
        justTask.pesquisa = pesquisa;
        justTask.tipo = tJustTask;

        return justTask;
    }
}
