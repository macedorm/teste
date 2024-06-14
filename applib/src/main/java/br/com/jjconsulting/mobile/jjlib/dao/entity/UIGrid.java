package br.com.jjconsulting.mobile.jjlib.dao.entity;


public class UIGrid {

    private boolean showConfig;
    private boolean showRowStriped;
    private boolean showIconLegend;
    private boolean showRefresh;
    private boolean showTitle;
    private boolean showToolbar;
    private boolean showFilter;
    private boolean enableSorting;
    private boolean enableMultSelect;
    private boolean maintainValuesOnLoad;
    private boolean showHeaderWhenEmpty;
    private boolean showPagging;
    private String emptyDataText;

    public  UIGrid(){
        this.setShowRowStriped(true);
        this.setShowConfig(true);
        this.setShowTitle(true);
        this.setShowToolbar(true);
        this.setShowFilter(true);
        this.setShowRefresh(true);
        this.setEnableSorting(true);
        this.setShowHeaderWhenEmpty(true);
        this.setShowPagging(true);
        this.setEmptyDataText("Não existe registro para ser exibido");
    }

    /**
     * Exibir o botão de configuração de layout (Default = true)
     */
    public boolean isShowConfig() {
        return showConfig;
    }

    /**
     * Exibir o botão de configuração de layout (Default = true)
     */
    public void setShowConfig(boolean showConfig) {
        this.showConfig = showConfig;
    }


    /**
     * Exibir colunas zebradas (Default = true)
     */
    public boolean isShowRowStriped() {
        return showRowStriped;
    }

    /**
     * Exibir colunas zebradas (Default = true)
     */
    public void setShowRowStriped(boolean showRowStriped) {
        this.showRowStriped = showRowStriped;
    }

    /**
     * Exibir botão de legenda (Default = false)
     * Somente componentes do tipo ComboBox é possivel exibir imagens como legenda.
     */
    public boolean isShowIconLegend() {
        return showIconLegend;
    }

    /**
     * Exibir botão de legenda (Default = false)
     * Somente componentes do tipo ComboBox é possivel exibir imagens como legenda.
     */
    public void setShowIconLegend(boolean showIconLegend) {
        this.showIconLegend = showIconLegend;
    }

    /**
     * Exibir botão atualizar (Default = true)
     */
    public boolean isShowRefresh() {
        return showRefresh;
    }

    /**
     * Exibir botão atualizar (Default = true)
     */
    public void setShowRefresh(boolean showRefresh) {
        this.showRefresh = showRefresh;
    }

    /**
     * Exibir título no cabeçalho da página
     */
    public boolean isShowTitle() {
        return showTitle;
    }

    /**
     * Exibir título no cabeçalho da página
     */
    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    /**
     * Exibir toolbar (Default = true)
     */
    public boolean isShowToolbar() {
        return showToolbar;
    }

    /**
     * Exibir toolbar (Default = true)
     */
    public void setShowToolbar(boolean showToolbar) {
        this.showToolbar = showToolbar;
    }

    /**
     * Exibir filtros (Default = true)
     */
    public boolean isShowFilter() {
        return showFilter;
    }

    /**
     * Exibir filtros (Default = true)
     */
    public void setShowFilter(boolean showFilter) {
        this.showFilter = showFilter;
    }

    /**
     * Habilita Ordenação das colunas (Default = true)
     */
    public boolean isEnableSorting() {
        return enableSorting;
    }

    /**
     * Habilita Ordenação das colunas (Default = true)
     */
    public void setEnableSorting(boolean enableSorting) {
        this.enableSorting = enableSorting;
    }

    /**
     * Permite selecionar multiplas linhas na Grid
     * habilitando um checkbox na primeira coluna. (Defaut = false)
     */
    public boolean isEnableMultSelect() {
        return enableMultSelect;
    }

    /**
     * Permite selecionar multiplas linhas na Grid
     * habilitando um checkbox na primeira coluna. (Defaut = false)
     */
    public void setEnableMultSelect(boolean enableMultSelect) {
        this.enableMultSelect = enableMultSelect;
    }

    /**
     * Mantem os filtros, ordem e paginação da grid na sessão,
     * e recupera na primeira carga da pagina. (Default = false)
     */
    public boolean isMaintainValuesOnLoad() {
        return maintainValuesOnLoad;
    }

    /**
     * Mantem os filtros, ordem e paginação da grid na sessão,
     * e recupera na primeira carga da pagina. (Default = false)
     */
    public void setMaintainValuesOnLoad(boolean maintainValuesOnLoad) {
        this.maintainValuesOnLoad = maintainValuesOnLoad;
    }

    /**
     * Obtém ou define um valor que indica se o cabeçalho da gridview
     * ficará visível quando não existir dados.
     * (Default = true)
     */
    public boolean isShowHeaderWhenEmpty() {
        return showHeaderWhenEmpty;
    }

    /**
     * Obtém ou define um valor que indica se o cabeçalho da gridview
     * ficará visível quando não existir dados.
     * (Default = true)
     */
    public void setShowHeaderWhenEmpty(boolean showHeaderWhenEmpty) {
        this.showHeaderWhenEmpty = showHeaderWhenEmpty;
    }

    /**
     * Obtém ou define o texto a ser exibido na linha de dados vazia
     * quando um controle JJGridView não contém registros.
     * Valor padrão = (Não existe registro para ser exibido).
     */
    public String getEmptyDataText() {
        return emptyDataText;
    }

    /**
     * Obtém ou define o texto a ser exibido na linha de dados vazia
     * quando um controle JJGridView não contém registros.
     * Valor padrão = (Não existe registro para ser exibido).
     */
    public void setEmptyDataText(String emptyDataText) {
        this.emptyDataText = emptyDataText;
    }

    /**
     * Exibe os controles de paginação (Default = true)
     */
    public boolean isShowPagging() {
        return showPagging;
    }

    /**
     * Exibe os controles de paginação (Default = true)
     */
    public void setShowPagging(boolean showPagging) {
        this.showPagging = showPagging;
    }
}
