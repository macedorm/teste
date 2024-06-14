package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public class PerfilVenda implements Serializable     {

    public static final int TIPO_LIBERACAO_SEM_LIBERACAO = 1;
    public static final int TIPO_LIBERACAO_SEMPRE_ENVIAR_LIBERACAO = 2;

    public static final int TIPO_OBS_INTERNA_NAO_EXIBIR = 1;
    public static final int TIPO_OBS_INTERNA_EXIBIR_OPCIONAL = 2;
    public static final int TIPO_OBS_INTERNA_EXBIGIR_OBRIGATORIO = 3;
    public static final int TIPO_OBS_INTERNA_EXIBIR_OBRIGATORIO_URGENTE = 4;

    public static final int TIPO_OBS_NF_NAO_EXIBIR = 1;
    public static final int TIPO_OBS_NF_EXIBIR_OPCIONAL = 2;
    public static final int TIPO_OBS_NF_EXBIGIR_OBRIGATORIO = 3;

    public static final int TIPO_DESCONTO_SEM_PERMISSAO = 1;
    public static final int TIPO_DESCONTO_PADRAO = 2;
    public static final int TIPO_DESCONTO_MAX_PERMITIDO = 3;

    public static final int TIPO_PESO_NAO_VALIDAR = 1;
    public static final int TIPO_PESO_ESPECIFICO = 2;
    public static final int TIPO_PESO_CLIENTE = 3;
    public static final int TIPO_WARNING_PESO_CLIENTE = 4;

    public static final int TIPO_AGENDA_NAO_PERMITIR = 1;
    public static final int TIPO_AGENDA_PERMITIR_SOMENTE_AGENDA = 2;
    public static final int TIPO_AGENDA_PERMITIR_AMBOS = 3;

    public static final int TIPO_VALOR_MINIMO_NAO_VALIDA = 1;
    public static final int TIPO_VALOR_MINIMO_VALIDA_CADASTRO_CLIENTE = 2;
    public static final int TIPO_VALOR_MINIMO_VALIDA_PERFIL = 3;

    public static final int TIPO_COND_PAG_PADRAO_CLIENTE = 1;
    public static final int TIPO_COND_PAG_PERMITE_ALTERAR = 2;
    public static final int TIPO_COND_PAG_NAO_EXIBE = 3;

    public static final int UN_MEDIDA_INDISPONIVEL = 0;
    public static final int UN_MEDIDA_CAIXA = 1;
    public static final int UN_MEDIDA_UNIDADE = 2;
    public static final int UN_MEDIDA_PADRAO_CLIENTE = 3;

    public static final int SORTIMENTO_NAOJUSTIFICA = 1;
    public static final int SORTIMENTO_JUTIFICAITEM = 2;
    public static final int SORTIMENTO_JUSTIFICAPEDIDO = 3;
    public static final int ESCOLHA_USUARIO = 4;


    private int codigoPerfil;

    private String codigoUnidadeNegocio;
    private String tipoVenda;

    private boolean vendaHabilitada;
    private boolean liberacaoHabilitada;
    private boolean exibicaoEmpenhoHabilitada;
    private boolean exibicaoPlantaHabilitada;
    private boolean exibicaoLastroPalletHabilitada;
    private boolean exibicaoEntregarEm;
    private boolean exibicaoOrdemCompra;
    private boolean exibicaoDtImpSAP;
    private boolean exibicaoPedSAP;
    private boolean edicaoDataEntregaHabilitada;
    private boolean exibicaoValorCalculadoLoteHabilitada;
    private boolean anexoObrigatorio;
    private boolean edicaoLocalEntregaHabilitada;
    private boolean edicaoUnidadeMedidaHabiltiada;
    private boolean edicaoAvancadaHabilitada;
    private boolean edicaoUnitariaHabilitada;
    private boolean marcacaoUrgenciaHabilitada;
    private boolean pedidoEmRedeHabilitado;
    private boolean digitacaoMultiploObrigatoria;
    private boolean selecaoCondicaoPagamentoHabilitada;
    private String condicaoPagamentoEspecifica;
    private boolean exportacaoSapEmUnidadeHabilitada;
    private boolean validacaoProdutoBloqueadoIgnorada;
    private boolean sortimentoHabilitado;
    private boolean positivacaoHabilitado;

    private int tipoLiberacao;
    private int tipoObsInterna;
    private int tipoObsNf;
    private int tipoDesconto;
    private int tipoPeso;
    private int tipoPesoMax;
    private int tipoAgenda;
    private int tipoValorMinimo;
    private int tipoCondPag;
    private int tipoJustificativaSortimento;

    private String obsInternaInfo;
    private String obsNfInfo;
    private double pesoMinimo;
    private double pesoMaximo;
    private double valorMaximo;
    private double valorMinimo;

    private int unMedidaPadrao;
    private boolean permiteApenasCadastroProdutoAtivo;
    private boolean ignoraExclusividadeProdutoAtivo;
    private boolean isPerCaixaFrac;

    private boolean permitePedidoCliCredBloq; //PRF_TXT_HABPEDCLIBLOQ
    private PerfilVendaAprov aprov;

    public PerfilVenda() {
        setTipoLiberacao(TIPO_LIBERACAO_SEM_LIBERACAO);
        setTipoDesconto(TIPO_DESCONTO_SEM_PERMISSAO);
        setTipoPeso(TIPO_PESO_NAO_VALIDAR);
        setTipoPesoMax(TIPO_PESO_NAO_VALIDAR);
        setTipoAgenda(TIPO_AGENDA_NAO_PERMITIR);
        setTipoObsInterna(TIPO_OBS_INTERNA_NAO_EXIBIR);
        setTipoObsNf(TIPO_OBS_NF_EXIBIR_OPCIONAL);
        setTipoCondPag(TIPO_COND_PAG_NAO_EXIBE);
        setTipoJustificativaSortimento(SORTIMENTO_NAOJUSTIFICA);
        setExibicaoEntregarEm(true);
        setExibicaoOrdemCompra(true);
        setExibicaoDtImpSAP(true);
        setExibicaoPedSAP(true);
        setPerCaixaFrac(false);
        aprov = new PerfilVendaAprov();
    }

    public int getCodigoPerfil() {
        return codigoPerfil;
    }

    public void setCodigoPerfil(int codigoPerfil) {
        this.codigoPerfil = codigoPerfil;
    }

    public String getCodigoUnidadeNegocio() {
        return codigoUnidadeNegocio;
    }

    public void setCodigoUnidadeNegocio(String codigoUnidadeNegocio) {
        this.codigoUnidadeNegocio = codigoUnidadeNegocio;
    }

    public String getTipoVenda() {
        return tipoVenda;
    }

    public void setTipoVenda(String tipoVenda) {
        this.tipoVenda = tipoVenda;
    }

    public boolean isVendaHabilitada() {
        return vendaHabilitada;
    }

    public void setVendaHabilitada(boolean vendaHabilitada) {
        this.vendaHabilitada = vendaHabilitada;
    }

    public boolean isLiberacaoHabilitada() {
        return liberacaoHabilitada;
    }

    public void setLiberacaoHabilitada(boolean liberacaoHabilitada) {
        this.liberacaoHabilitada = liberacaoHabilitada;
    }

    public boolean isExibicaoEmpenhoHabilitada() {
        return exibicaoEmpenhoHabilitada;
    }

    public void setExibicaoEmpenhoHabilitada(boolean exibicaoEmpenhoHabilitada) {
        this.exibicaoEmpenhoHabilitada = exibicaoEmpenhoHabilitada;
    }

    public boolean isAlteraPlanta() {
        return exibicaoPlantaHabilitada;
    }

    public void setAleraPlanta(boolean exibicaoPlantaHabilitada) {
        this.exibicaoPlantaHabilitada = exibicaoPlantaHabilitada;
    }

    public boolean isExibicaoLastroPalletHabilitada() {
        return exibicaoLastroPalletHabilitada;
    }

    public void setExibicaoLastroPalletHabilitada(boolean exibicaoLastroPalletHabilitada) {
        this.exibicaoLastroPalletHabilitada = exibicaoLastroPalletHabilitada;
    }


    public boolean isExibicaoEntregarEm() {
        return exibicaoEntregarEm;
    }

    public void setExibicaoEntregarEm(boolean exibicaoEntregarEm) {
        this.exibicaoEntregarEm = exibicaoEntregarEm;
    }

    public boolean isExibicaoOrdemCompra() {
        return exibicaoOrdemCompra;
    }

    public void setExibicaoOrdemCompra(boolean exibicaoOrdemCompra) {
        this.exibicaoOrdemCompra = exibicaoOrdemCompra;
    }

    public boolean isExibicaoDtImpSAP() {
        return exibicaoDtImpSAP;
    }

    public void setExibicaoDtImpSAP(boolean exibicaoDtImpSAP) {
        this.exibicaoDtImpSAP = exibicaoDtImpSAP;
    }

    public boolean isExibicaoPedSAP() {
        return exibicaoPedSAP;
    }

    public void setExibicaoPedSAP(boolean exibicaoPedSAP) {
        this.exibicaoPedSAP = exibicaoPedSAP;
    }

    public boolean isEdicaoDataEntregaHabilitada() {
        return edicaoDataEntregaHabilitada;
    }

    public void setEdicaoDataEntregaHabilitada(boolean edicaoDataEntregaHabilitada) {
        this.edicaoDataEntregaHabilitada = edicaoDataEntregaHabilitada;
    }

    public boolean isExibicaoValorCalculadoLoteHabilitada() {
        return exibicaoValorCalculadoLoteHabilitada;
    }

    public void setExibicaoValorCalculadoLoteHabilitada(boolean exibicaoValorCalculadoLoteHabilitada) {
        this.exibicaoValorCalculadoLoteHabilitada = exibicaoValorCalculadoLoteHabilitada;
    }

    public boolean isAnexoObrigatorio() {
        return anexoObrigatorio;
    }

    public void setAnexoObrigatorio(boolean anexoObrigatorio) {
        this.anexoObrigatorio = anexoObrigatorio;
    }

    public boolean isEdicaoLocalEntregaHabilitada() {
        return edicaoLocalEntregaHabilitada;
    }

    public void setEdicaoLocalEntregaHabilitada(boolean edicaoLocalEntregaHabilitada) {
        this.edicaoLocalEntregaHabilitada = edicaoLocalEntregaHabilitada;
    }

    public boolean isEdicaoUnidadeMedidaHabiltiada() {
        return edicaoUnidadeMedidaHabiltiada;
    }

    public void setEdicaoUnidadeMedidaHabiltiada(boolean edicaoUnidadeMedidaHabiltiada) {
        this.edicaoUnidadeMedidaHabiltiada = edicaoUnidadeMedidaHabiltiada;
    }

    public boolean isEdicaoAvancadaHabilitada() {
        return edicaoAvancadaHabilitada;
    }

    public void setEdicaoAvancadaHabilitada(boolean edicaoAvancadaHabilitada) {
        this.edicaoAvancadaHabilitada = edicaoAvancadaHabilitada;
    }

    public boolean isEdicaoUnitariaHabilitada() {
        return edicaoUnitariaHabilitada;
    }

    public void setEdicaoUnitariaHabilitada(boolean edicaoUnitariaHabilitada) {
        this.edicaoUnitariaHabilitada = edicaoUnitariaHabilitada;
    }

    public boolean isMarcacaoUrgenciaHabilitada() {
        return marcacaoUrgenciaHabilitada;
    }

    public void setMarcacaoUrgenciaHabilitada(boolean marcacaoUrgenciaHabilitada) {
        this.marcacaoUrgenciaHabilitada = marcacaoUrgenciaHabilitada;
    }

    public boolean isPedidoEmRedeHabilitado() {
        return pedidoEmRedeHabilitado;
    }

    public void setPedidoEmRedeHabilitado(boolean pedidoEmRedeHabilitado) {
        this.pedidoEmRedeHabilitado = pedidoEmRedeHabilitado;
    }

    public boolean isDigitacaoMultiploObrigatoria() {
        return digitacaoMultiploObrigatoria;
    }

    public void setDigitacaoMultiploObrigatoria(boolean digitacaoMultiploObrigatoria) {
        this.digitacaoMultiploObrigatoria = digitacaoMultiploObrigatoria;
    }

    public boolean isSelecaoCondicaoPagamentoHabilitada() {
        return selecaoCondicaoPagamentoHabilitada;
    }

    public void setSelecaoCondicaoPagamentoHabilitada(boolean selecaoCondicaoPagamentoHabilitada) {
        this.selecaoCondicaoPagamentoHabilitada = selecaoCondicaoPagamentoHabilitada;
    }

    public String getCondicaoPagamentoEspecifica() {
        return condicaoPagamentoEspecifica;
    }

    public void setCondicaoPagamentoEspecifica(String condicaoPagamentoEspecifica) {
        this.condicaoPagamentoEspecifica = condicaoPagamentoEspecifica;
    }

    public boolean isExportacaoSapEmUnidadeHabilitada() {
        return exportacaoSapEmUnidadeHabilitada;
    }

    public void setExportacaoSapEmUnidadeHabilitada(boolean exportacaoSapEmUnidadeHabilitada) {
        this.exportacaoSapEmUnidadeHabilitada = exportacaoSapEmUnidadeHabilitada;
    }

    public boolean isValidacaoProdutoBloqueadoIgnorada() {
        return validacaoProdutoBloqueadoIgnorada;
    }

    public void setValidacaoProdutoBloqueadoIgnorada(boolean validacaoProdutoBloqueadoIgnorada) {
        this.validacaoProdutoBloqueadoIgnorada = validacaoProdutoBloqueadoIgnorada;
    }

    public boolean isSortimentoHabilitado() {
        return sortimentoHabilitado;
    }

    public void setSortimentoHabilitado(boolean sortimentoHabilitado) {
        this.sortimentoHabilitado = sortimentoHabilitado;
    }

    public boolean isPositivacaoHabilitado() {
        return positivacaoHabilitado;
    }

    public void setPositivacaoHabilitado(boolean positivacaoHabilitado) {
        this.positivacaoHabilitado = positivacaoHabilitado;
    }

    public int getTipoLiberacao() {
        return tipoLiberacao;
    }

    public void setTipoLiberacao(int tipoLiberacao) {
        this.tipoLiberacao = tipoLiberacao;
    }

    public int getTipoObsInterna() {
        return tipoObsInterna;
    }

    public void setTipoObsInterna(int tipoObsInterna) {
        this.tipoObsInterna = tipoObsInterna;
    }

    public int getTipoObsNf() {
        return tipoObsNf;
    }

    public void setTipoObsNf(int tipoObsNf) {
        this.tipoObsNf = tipoObsNf;
    }

    public int getTipoDesconto() {
        return tipoDesconto;
    }

    public void setTipoDesconto(int tipoDesconto) {
        this.tipoDesconto = tipoDesconto;
    }

    public int getTipoPeso() {
        return tipoPeso;
    }

    public void setTipoPeso(int tipoPeso) {
        this.tipoPeso = tipoPeso;
    }

    public int getTipoPesoMax() {
        return tipoPesoMax;
    }

    public void setTipoPesoMax(int tipoPesoMax) {
        this.tipoPesoMax = tipoPesoMax;
    }

    public int getTipoAgenda() {
        return tipoAgenda;
    }

    public void setTipoAgenda(int tipoAgenda) {
        this.tipoAgenda = tipoAgenda;
    }

    public int getTipoValorMinimo() {
        return tipoValorMinimo;
    }

    public void setTipoValorMinimo(int tipoValorMinimo) {
        this.tipoValorMinimo = tipoValorMinimo;
    }

    public int getTipoCondPag() {
        return tipoCondPag;
    }

    public void setTipoCondPag(int tipoCondPag) {
        this.tipoCondPag = tipoCondPag;
    }

    public String getObsInternaInfo() {
        return obsInternaInfo;
    }

    public void setObsInternaInfo(String obsInternaInfo) {
        this.obsInternaInfo = obsInternaInfo;
    }

    public String getObsNfInfo() {
        return obsNfInfo;
    }

    public void setObsNfInfo(String obsNfInfo) {
        this.obsNfInfo = obsNfInfo;
    }

    public double getPesoMinimo() {
        return pesoMinimo;
    }

    public void setPesoMinimo(double pesoMinimo) {
        this.pesoMinimo = pesoMinimo;
    }

    public double getPesoMaximo() {
        return pesoMaximo;
    }

    public void setPesoMaximo(double pesoMaximo) {
        this.pesoMaximo = pesoMaximo;
    }

    public double getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(double valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public double getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(double valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public int getUnMedidaPadrao() {
        return unMedidaPadrao;
    }

    public void setUnMedidaPadrao(int unMedidaPadrao) {
        this.unMedidaPadrao = unMedidaPadrao;
    }

    public boolean isPermiteApenasCadastroProdutoAtivo() {
        return permiteApenasCadastroProdutoAtivo;
    }

    public void setPermiteApenasCadastroProdutoAtivo(boolean permiteApenasCadastroProdutoAtivo) {
        this.permiteApenasCadastroProdutoAtivo = permiteApenasCadastroProdutoAtivo;
    }

    public boolean isIgnoraExclusividadeProdutoAtivo() {
        return ignoraExclusividadeProdutoAtivo;
    }

    public void setIgnoraExclusividadeProdutoAtivo(boolean ignoraExclusividadeProdutoAtivo) {
        this.ignoraExclusividadeProdutoAtivo = ignoraExclusividadeProdutoAtivo;
    }

    public int getTipoJustificativaSortimento() {
        return tipoJustificativaSortimento;
    }

    public void setTipoJustificativaSortimento(int tipoJustificativaSortimento) {
        this.tipoJustificativaSortimento = tipoJustificativaSortimento;
    }

    public boolean isPerCaixaFrac() {
        return isPerCaixaFrac;
    }

    public void setPerCaixaFrac(boolean perCaixaFrac) {
        isPerCaixaFrac = perCaixaFrac;
    }

    public boolean isPermitePedidoCliCredBloq() {
        return permitePedidoCliCredBloq;
    }

    public void setPermitePedidoCliCredBloq(boolean permitePedidoCliCredBloq) {
        this.permitePedidoCliCredBloq = permitePedidoCliCredBloq;
    }

    public PerfilVendaAprov getAprov() {
        return aprov;
    }
}
