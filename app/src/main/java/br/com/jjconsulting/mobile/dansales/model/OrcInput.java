package br.com.jjconsulting.mobile.dansales.model;

import java.util.ArrayList;
import java.util.Date;

public class OrcInput {

    private boolean isQuestionOk;
    private  Pedido orcCab;
    private ArrayList<ItemPedido> orcItens;

    public boolean isQuestionOk() {
        return isQuestionOk;
    }

    public void setQuestionOk(boolean questionOk) {
        isQuestionOk = questionOk;
    }

    public Pedido getOrcCab() {
        return orcCab;
    }

    public void setOrcCab(Pedido orcCab) {
        this.orcCab = orcCab;
    }

    public ArrayList<ItemPedido> getOrcItens() {
        return orcItens;
    }

    public void setOrcItens(ArrayList<ItemPedido> orcItens) {
        this.orcItens = orcItens;
    }
}
