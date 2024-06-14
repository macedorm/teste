package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;
import java.util.ArrayList;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class Perfil  implements Serializable {

    private int codigo;
    private String nome;

    /**
     * Filtro Usuário - Chat Isaac
     */
    private boolean permiteIsaac;

    /**
     * Filtro Cliente - Hierarquia Vendedor
     */
    private int posicaoHierarquia;

    /**
     * Relatório - Histórico de Notas
     */
    private boolean relatorioHistoricoNotasHabilitado;

    /**
     * Relatório - Carteira de Pedido
     */
    private boolean relatorioCarteiraPedidosHabilitado;

    /**
     * Relatório - Objetivo
     */
    private boolean relatorioObjetivoRafHabilitado;

    /**
     * Relatório de Positivação
     */
    private boolean permiteRelPositivacao; //PRF_TXT_HABRELPOSITIVACAO

    /**
     * Relatório - TAP - Saldo MasterContrato
     */
    private boolean relatorioETapSaldoMC;

    /**
     * Relatório - TAP - Lista TAP
     */
    private boolean relatorioETapLista;

    /**
     * TAP - Habilitar acesso ao Módulo
     */
    private boolean permiteModuloETap; //PRF_TXT_HABMODETAP

    /**
     * TAP - Habilitar inclusão
     */
    private boolean permiteEtapInc; //PRF_TXT_HABETAPINC

    /**
     * Habilitar Aprovação em Massa
     */
    private boolean permiteEtapAprovMassa; //PRF_TXT_HABETAPAPROVMASSA

    /**
     * TAP - Habilitar análise de finanças
     */
    private boolean permiteEtapAnFin; //PRF_TXT_HABETAPANFIN

    /**
     * TAP - Controles Internos - Habilitar consulta TAP
     */
    private boolean permiteEtapContrInt; //PRF_TXT_HABETAPCONTRINT

    /**
     * Rota Guiada - Opção Rota do Dia
     */
    private boolean permiteRotaGuiada; //PRF_TXT_HABROTADIA_RG

    /**
     * Rota Guiada - Check-in fora da área do Cliente (Alerta)
     */
    private boolean rotaCheckInForaArea; //PRF_TXT_HABCHECKFORAAREA_RG

    /**
     * Rota Guiada - Habilitar Justificativas - Visita não realizada
     */
    private boolean rotaJutificativaVisitaNaoRealizada; //PRF_TXT_HABJUSTVISITANAOREALI_RG

    /**
     * Rota Guiada - Habilitar Justificativas - Atividade obrigatória não realizada
     */
    private boolean rotaJutificativaAtividadeNaoRealizada; //PRF_TXT_HABJUSTATIVNREALI_RG

    /**
     * Rota Guiada - Habilitar Justificativas - Pedido não realizado
     */
    private boolean rotaJutificativaPedidoNaoRealizado; //PRF_TXT_HABJUSTPEDNREALI_RG

    /**
     * Rota Guiada - Habilitar Check-in/Check-out
     */
    private boolean rotaPermiteCheckinChekout; //PRF_TXT_CHECKIN_CHECKOUT_RG

    /**
     * Rota Guiada - Raio de aderência em metros
     */
    private int rotaRaioAderencia; //PRF_INT_CADRAIO_RG

    /**
     * CR - Habilita modulo CR
     */
    private boolean permiteCR;//PRF_TXT_HABCR

    /**
     * Pesquisa - Habilita modulo de pesquisa
     */
    private boolean permitePesquisa; //PRF_TXT_HABPESQUISA

    /**
     * Sincronismo - Habilita Auto Sincronismo
     */
    private boolean permiteAutoSync; //PRF_TXT_HABAUTOSINC

    /**
     * Sincronismo - Habilita Rastreio de Pedidos
     */
    private boolean permiteRastreioPedido; //PRF_TXT_HABRASPED

    /**
     * Relatorio - Habilita relátorio de notas do checklist
     */
    private boolean permiteRelatorioChecklist; //PRF_TXT_HABRELNOTACHECKLIST

    /**
     * Requisição - Permite vizualizar modulo de requisição
     */
    private boolean permiteRequisicao;//PRF_TXT_HABREQSOLICITANTE


    private boolean permitePlanejamentoRota;//PRF_TXT_HABPLANEJAMENTOROTA

    private long intervaloAutoSync; //PRF_INT_AUTOSINC_MINUTOS


    private ArrayList<PerfilVenda> perfisVenda;


    public Perfil() {
        perfisVenda = new ArrayList<>();
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getPosicaoHierarquia() {
        return posicaoHierarquia;
    }

    public void setPosicaoHierarquia(int posicaoHierarquia) {
        this.posicaoHierarquia = posicaoHierarquia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isRelatorioHistoricoNotasHabilitado() {
        return relatorioHistoricoNotasHabilitado;
    }
    public void setRelatorioHistoricoNotasHabilitado(boolean relatorioHistoricoNotasHabilitado) {
        this.relatorioHistoricoNotasHabilitado = relatorioHistoricoNotasHabilitado;
    }

    public boolean isRelatorioCarteiraPedidosHabilitado() {
        return relatorioCarteiraPedidosHabilitado;
    }

    public void setRelatorioCarteiraPedidosHabilitado(boolean relatorioCarteiraPedidosHabilitado) {
        this.relatorioCarteiraPedidosHabilitado = relatorioCarteiraPedidosHabilitado;
    }

    public boolean isRelatorioObjetivoRafHabilitado() {
        return relatorioObjetivoRafHabilitado;
    }

    public void setRelatorioObjetivoRafHabilitado(boolean relatorioObjetivoRafHabilitado) {
        this.relatorioObjetivoRafHabilitado = relatorioObjetivoRafHabilitado;
    }

    public boolean isRelatorioETapSaldoMC() {
        return relatorioETapSaldoMC;
    }

    public void setRelatorioETapSaldoMC(boolean relatorioETapSaldoMC) {
        this.relatorioETapSaldoMC = relatorioETapSaldoMC;
    }

    public boolean isRelatorioETapLista() {
        return relatorioETapLista;
    }

    public void setRelatorioETapLista(boolean relatorioETapLista) {
        this.relatorioETapLista = relatorioETapLista;
    }

    public boolean isPermiteModuloETap() {
        return permiteModuloETap;
    }

    public void setPermiteModuloETap(boolean habModuloETap) {
        this.permiteModuloETap = habModuloETap;
    }

    public boolean isPermiteEtapInc() {
        return permiteEtapInc;
    }

    public void setPermiteEtapInc(boolean permiteEtapInc) {
        this.permiteEtapInc = permiteEtapInc;
    }

    public boolean isPermiteEtapAnFin() {
        return permiteEtapAnFin;
    }

    public void setPermiteEtapAnFin(boolean permiteEtapAnFin) {
        this.permiteEtapAnFin = permiteEtapAnFin;
    }

    public boolean isPermiteEtapContrInt() {
        return permiteEtapContrInt;
    }

    public void setPermiteEtapContrInt(boolean permiteEtapContrInt) {
        this.permiteEtapContrInt = permiteEtapContrInt;
    }

    public boolean isPermiteEtapAprovMassa() {
        return permiteEtapAprovMassa;
    }

    public void setPermiteEtapAprovMassa(boolean permiteEtapAprovMassa) {
        this.permiteEtapAprovMassa = permiteEtapAprovMassa;
    }

    public ArrayList<PerfilVenda> getPerfisVenda() {
        return perfisVenda;
    }

    public void setPerfisVenda(ArrayList<PerfilVenda> perfisVenda) {
        this.perfisVenda = perfisVenda;
    }

    public PerfilVenda getPerfilVenda(Pedido pedido) {
        String codTpVend = pedido.getCodigoTipoVenda();
        String codUnNeg = pedido.getCodigoUnidadeNegocio();
        return getPerfilVenda(codTpVend, codUnNeg);
    }

    public PerfilVenda getPerfilVenda(String tipoVenda, String unidadeNegocio) {
        if (tipoVenda == null || unidadeNegocio == null)
            return new PerfilVenda();

        for (PerfilVenda perfilVenda : perfisVenda) {
            if (unidadeNegocio.equals(perfilVenda.getCodigoUnidadeNegocio())
                    && tipoVenda.equals(perfilVenda.getTipoVenda())) {
                return perfilVenda;
            }
        }

        return new PerfilVenda();
    }

    public boolean permiteVenda(String unidadeNegocio) {
        for (PerfilVenda perfilVenda : perfisVenda) {
            if (unidadeNegocio.equals(perfilVenda.getCodigoUnidadeNegocio())
                    && perfilVenda.isVendaHabilitada()) {
                return true;
            }
        }

        return false;
    }

    public boolean permiteVisualizarClientes() {
        return true;
    }

    public boolean permiteAprovacao(String unidadeNegocio) {
        for (PerfilVenda perfilVenda : perfisVenda) {
            if (unidadeNegocio.equals(perfilVenda.getCodigoUnidadeNegocio())
                    && perfilVenda.getAprov().isAprovacaoHabilitada()) {
                return true;
            }
        }

        return false;
    }

    public boolean permiteLiberacao(String unidadeNegocio) {
        for (PerfilVenda perfilVenda : perfisVenda) {
            if (unidadeNegocio.equals(perfilVenda.getCodigoUnidadeNegocio())
                    && perfilVenda.isLiberacaoHabilitada()) {
                return true;
            }
        }

        return false;
    }


    public boolean permiteVisualizarRotas() {
        return false;
    }

    public boolean permiteVisualizarRelatorios() {
        return (relatorioHistoricoNotasHabilitado ||
                relatorioCarteiraPedidosHabilitado ||
                relatorioObjetivoRafHabilitado);
    }

    public boolean permiteETap() {
        return (permiteModuloETap ||
                permiteEtapInc ||
                permiteEtapAnFin);
    }

    public boolean permiteCriarPedidoAgenda(String unidadeNegocio) {
        for (PerfilVenda perfilVenda : perfisVenda) {
            int tipoAgenda = perfilVenda.getTipoAgenda();
            if (unidadeNegocio.equals(perfilVenda.getCodigoUnidadeNegocio()) &&
                    PerfilVenda.TIPO_AGENDA_PERMITIR_AMBOS == tipoAgenda
                    || PerfilVenda.TIPO_AGENDA_PERMITIR_SOMENTE_AGENDA == tipoAgenda) {
                return true;
            }
        }

        return false;
    }

    public boolean possuiTipoVenda(String codigoTipoVenda, String unidadeNegocio) {
        for (PerfilVenda pf : perfisVenda) {
            if (unidadeNegocio.equals(pf.getCodigoUnidadeNegocio()) &&
                    codigoTipoVenda.equals(pf.getTipoVenda())) {
                return true;
            }
        }

        return false;
    }

    public boolean isHabPositivacao(){
         boolean habPositivacao = false;
        for(PerfilVenda perfilVenda: getPerfisVenda()) {
            if (perfilVenda.isPositivacaoHabilitado()) {
                habPositivacao = true;
                break;
            }
        }
        return habPositivacao;
    }

    public boolean isPermiteRelPositivacao() {
        return permiteRelPositivacao;
    }

    public void setPermiteRelPositivacao(boolean permiteRelPositivacao) {
        this.permiteRelPositivacao = permiteRelPositivacao;
    }

    public boolean isPermiteRotaGuiada() {
        return permiteRotaGuiada;
    }

    public void setPermiteRotaGuiada(boolean permiteRotaGuiada) {
        this.permiteRotaGuiada = permiteRotaGuiada;
    }

    public boolean isPermiteIsaac() {
        return permiteIsaac;
    }

    public void setChatIsaac(boolean permiteIsaac){
        this.permiteIsaac = permiteIsaac;
    }

    public boolean isRotaCheckInForaArea() {
        return rotaCheckInForaArea;
    }

    public void setRotaCheckInForaArea(boolean rotaCheckInForaArea) {
        this.rotaCheckInForaArea = rotaCheckInForaArea;
    }

    public boolean isRotaJutificativaVisitaNaoRealizada() {
        return rotaJutificativaVisitaNaoRealizada;
    }

    public void setRotaJutificativaVisitaNaoRealizada(boolean rotaJutificativaVisitaNaoRealizada) {
        this.rotaJutificativaVisitaNaoRealizada = rotaJutificativaVisitaNaoRealizada;
    }

    public boolean isRotaJutificativaAtividadeNaoRealizada() {
        return rotaJutificativaAtividadeNaoRealizada;
    }

    public void setRotaJutificativaAtividadeNaoRealizada(boolean rotaJutificativaAtividadeNaoRealizada) {
        this.rotaJutificativaAtividadeNaoRealizada = rotaJutificativaAtividadeNaoRealizada;
    }

    public boolean isRotaJutificativaPedidoNaoRealizado() {
        return rotaJutificativaPedidoNaoRealizado;
    }

    public void setRotaJutificativaPedidoNaoRealizado(boolean rotaJutificativaPedidoNaoRealizado) {
        this.rotaJutificativaPedidoNaoRealizado = rotaJutificativaPedidoNaoRealizado;
    }

    public boolean isRotaPermiteCheckinChekout() {
        return rotaPermiteCheckinChekout;
    }

    public void setRotaPermiteCheckinChekout(boolean rotaPermiteCheckinChokout) {
        this.rotaPermiteCheckinChekout = rotaPermiteCheckinChokout;
    }

    public int getRotaRaioAderencia() {
        return rotaRaioAderencia;
    }

    public void setRotaRaioAderencia(int rotaRaioAderencia) {
        this.rotaRaioAderencia = rotaRaioAderencia;
    }

    public boolean isPermiteCR() {
        return permiteCR;
    }

    public void setPermiteCR(boolean permiteCR) {
        this.permiteCR = permiteCR;
    }

    public void setPermiteIsaac(boolean permiteIsaac) {
        this.permiteIsaac = permiteIsaac;
    }

    public boolean permitePesquisas() {
        return permitePesquisa;
    }

    public void setPermitePesquisa(boolean permitePesquisa) {
        this.permitePesquisa = permitePesquisa;
    }

    public boolean isPermiteAutoSync() {
        return permiteAutoSync;
    }

    public void setPermiteAutoSync(boolean permiteAutoSync) {
        this.permiteAutoSync = permiteAutoSync;
    }

    public boolean permiteRastreioPedidos() {
        return permiteRastreioPedido;
    }

    public void setPermiteRastreioPedido(boolean permiteRastreioPedido) {
        this.permiteRastreioPedido = permiteRastreioPedido;
    }

    public boolean isPermiteRelatorioChecklist() {
        return permiteRelatorioChecklist;
    }

    public void setPermiteRelatorioChecklist(boolean permiteRelatorioChecklist) {
        this.permiteRelatorioChecklist = permiteRelatorioChecklist;
    }

    public boolean isPermitePesquisa() {
        return permitePesquisa;
    }

    public boolean isPermiteRastreioPedido() { return  permiteRastreioPedido;}

    public boolean isPermiteRequisicao() {
        return permiteRequisicao;
    }

    public void setPermiteRequisicao(boolean permiteRequisicao) {
        this.permiteRequisicao = permiteRequisicao;
    }

    public boolean isPermitePlanejamentoRota() {
        return permitePlanejamentoRota;
    }

    public void setPermitePlanejamentoRota(boolean permitePlanejamentoRota) {
        this.permitePlanejamentoRota = permitePlanejamentoRota;
    }

    public long getIntervaloAutoSync() {
        if(intervaloAutoSync > 0){
            return intervaloAutoSync * 60000;
        } else {
            return 0;
        }
    }

    public void setIntervaloAutoSync(long intervaloAutoSync) {
        this.intervaloAutoSync = intervaloAutoSync;
    }
}
