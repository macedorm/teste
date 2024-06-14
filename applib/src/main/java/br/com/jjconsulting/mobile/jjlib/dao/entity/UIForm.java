package br.com.jjconsulting.mobile.jjlib.dao.entity;

public class UIForm {
    private boolean showAdd;
    private boolean showViewModeAsStatic;


    public UIForm() {

    }

    /**
     * Exibir botão adicionar (Default = true)
     */
    public boolean isShowAdd() {
        return showAdd;
    }

    /**
     * Exibir botão adicionar (Default = true)
     */
    public void setShowAdd(boolean showAdd) {
        this.showAdd = showAdd;
    }

    /**
     * Quando o painel estiver no modo de visualização
     * remover as bordas dos campos exibindo como texto.
     * (Default = false)
     */
    public boolean isShowViewModeAsStatic() {
        return showViewModeAsStatic;
    }

    /**
     * Quando o painel estiver no modo de visualização
     * remover as bordas dos campos exibindo como texto.
     * (Default = false)
     */
    public void setShowViewModeAsStatic(boolean showViewModeAsStatic) {
        this.showViewModeAsStatic = showViewModeAsStatic;
    }

}
