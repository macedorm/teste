package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public class PerfilVendaAprov implements Serializable {

    private boolean aprovacaoHabilitada;

    private boolean permiteMasterContratoPedido;

    private boolean permiteAprovMassa;

    private TPerfilAgendaAprov filtroAgenda;

    private TPerfilRegraAprov filtroRegraAprov;

    public PerfilVendaAprov() {
        setFiltroAgenda(TPerfilAgendaAprov.APROVA_AMBOS);
        setFiltroRegraAprov(TPerfilRegraAprov.APROVA_AMBOS);
    }

    public boolean isAprovacaoHabilitada() {
        return aprovacaoHabilitada;
    }

    public void setAprovacaoHabilitada(boolean aprovacaoHabilitada) {
        this.aprovacaoHabilitada = aprovacaoHabilitada;
    }

    public boolean isPermiteMasterContratoPedido() {
        return permiteMasterContratoPedido;
    }

    public void setPermiteMasterContratoPedido(boolean permiteMasterContratoPedido) {
        this.permiteMasterContratoPedido = permiteMasterContratoPedido;
    }

    public boolean isPermiteAprovMassa() {
        return permiteAprovMassa;
    }

    public void setPermiteAprovMassa(boolean permiteAprovMassa) {
        this.permiteAprovMassa = permiteAprovMassa;
    }

    public TPerfilAgendaAprov getFiltroAgenda() {
        return filtroAgenda;
    }

    public void setFiltroAgenda(TPerfilAgendaAprov filtroAgenda) {
        this.filtroAgenda = filtroAgenda;
    }

    public TPerfilRegraAprov getFiltroRegraAprov() {
        return filtroRegraAprov;
    }

    public void setFiltroRegraAprov(TPerfilRegraAprov filtroRegraAprov) {
        this.filtroRegraAprov = filtroRegraAprov;
    }
}
