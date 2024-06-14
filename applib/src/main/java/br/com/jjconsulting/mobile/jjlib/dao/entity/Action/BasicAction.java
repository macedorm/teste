package br.com.jjconsulting.mobile.jjlib.dao.entity.Action;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;

public class BasicAction {

    /**
     * Id e nome do componente
     */
    private String name;

    /**
     *  Descrição do link
     */
    private String Text;

    /**
     * Texto exibido quando o ponteiro do mouse passa sobre o controle
     */
    private String toolTip;

    /**
     * Executar essa ação como padrão.
     * Ação será disparada ao clicar em qualquer lugar da linha.
     */
    private boolean isDefaultOption;

    /**
     * Exibe a ação em um grupo de menu
     * Default = false
     */
    private boolean isGroup;

    /**
     *  Faz um separador de menu antes dessa ação
     *  Default = false
     */
    private boolean dividerLine;

    /**
     * Icone do Link ou Button
     */
    private int icon;


    /**
     * Exibir titulo na grid
     */
    private boolean showTitle;

    /**
     * Se preenchido ao executar a ação essa mensagem será exibida com a opção de (Sim/Não)
     * Não = Cancela a ação
     * Sim = Ação será executado
     */
    private String confirmationMessage;

    /**
     * Expressão que retorna um boolean
     */
    private String enableExpression;

    /**
     * Expressão que retorna um boolean
     */
    private String visibleExpression;

    /**
     * Ordem em que a ação será exibida
     */
    private int order;

    /**
     * Exibir com estilo de um botão
     */
    private boolean showAsButton;


    public  BasicAction(){
        setVisible(true);
        setEnable(true);
    }

    /**
     * Id e nome do componente
     * @return Id
     */
    public String getName() {
        return name;
    }

    /**
     * Define Id e nome do componente
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Recupera a descrição do link
     * @return Texto a ser exibido
     */
    public String getText() {
        return Text;
    }

    /**
     * Define descrição do link
     * @param text
     */
    public void setText(String text) {
        Text = text;
    }

    /**
     * Recupera texto exibido quando o ponteiro do mouse passa sobre o controle
     */
    public String getToolTip() {
        return toolTip;
    }

    /**
     * Atribui texto exibido quando o ponteiro do mouse passa sobre o controle
     */
    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    /**
     * Definir essa ação como padrão
     */
    public boolean isDefaultOption() {
        return isDefaultOption;
    }

    /**
     * Executar essa ação como padrão.
     * Ação será disparada ao clicar em qualquer lugar da linha.
     */
    public void setDefaultOption(boolean defaultOption) {
        isDefaultOption = defaultOption;
    }

    /**
     * Exibe a ação em um grupo de menu
     */
    public boolean isGroup() {
        return isGroup;
    }

    /**
     * Define se exibe a ação em um grupo de menu
     */
    public void setGroup(boolean group) {
        isGroup = group;
    }

    /**
     * Faz um separador de menu antes dessa ação
     */
    public boolean isDividerLine() {
        return dividerLine;
    }

    /**
     * Atribui um separador de menu antes dessa ação
     * @param dividerLine
     */
    public void setDividerLine(boolean dividerLine) {
        this.dividerLine = dividerLine;
    }

    /**
     * Recupera o tipo de icone do Link ou Button
     * @return
     */
    public TIcon getIcon() {
        return TIcon.getTIcon(icon);
    }

    /**
     * Atribui Icone do Link ou Button
     * @param icon
     */
    public void setIcon(TIcon icon) {
        this.icon = icon.getValue();
    }

    /**
     * Exibir titulo na grid
     */
    public boolean isShowTitle() {
        return showTitle;
    }

    /**
     * Exibir titulo na grid
     */
    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    /**
     * Se preenchido ao executar a ação essa mensagem será exibida com a opção de (Sim/Não)
     * Não = Cancela a ação
     * Sim = Ação será executado
     */
    public String getConfirmationMessage() {
        return confirmationMessage;
    }

    /**
     * Atribui uma mensagem que será exibida com a opção de (Sim/Não)
     * Não = Cancela a ação
     * Sim = Ação será executado
     */
    public void setConfirmationMessage(String confirmationMessage) {
        this.confirmationMessage = confirmationMessage;
    }

    /**
     * Expressão que retorna um boolean
     */
    public String getEnableExpression() {
        return enableExpression;
    }

    /**
     * Atribui uma expressão
     */
    public void setEnableExpression(String enableExpression) {
        this.enableExpression = enableExpression;
    }

    /**
     * Expressão que retorna um boolean
     */
    public String getVisibleExpression() {
        return visibleExpression;
    }

    /**
     * Atribui uma expressão
     */
    public void setVisibleExpression(String visibleExpression) {
        this.visibleExpression = visibleExpression;
    }

    /**
     * Ordem em que a ação será exibida
     */
    public int getOrder() {
        return order;
    }

    /**
     * Atribui ordem em que a ação será exibida
     * @param order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Exibir com um botão
     */
    public boolean isShowAsButton() {
        return showAsButton;
    }

    /**
     * Exibir com estilo de um botão
     */
    public void setShowAsButton(boolean showAsButton) {
        this.showAsButton = showAsButton;
    }

    /**
     * Obtém ou define um valor que indica se o controle será ou não renderizado.
     */
    public void setVisible(boolean value){
        if (value)
            this.visibleExpression = "val:1";
        else
            this.visibleExpression = "val:0";
    }

    /**
     * Obtém ou define um valor que indica se o controle será ou não habilitado.
     */
    public void setEnable(boolean value){
        if (value)
            this.enableExpression = "val:1";
        else
            this.enableExpression = "val:0";
    }

    /**
     * Verifica se o controle é visivel, porém não aplica a expressao
     * @return true se o componente estiver habilitado
     */
    public boolean isVisible(){
        return !"val:0".equals(visibleExpression);
    }




}
