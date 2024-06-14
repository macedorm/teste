package br.com.jjconsulting.mobile.jjlib.dao.entity.Action;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;

public class InsertAction extends BasicAction {
    /**
     * Nome padrão da ação
     */
    public static final String ACTION_NAME = "insert";

    private String elementNameToSelect;
    private boolean reopenForm;

    public InsertAction() {
        //setName(ACTION_NAME);
        setToolTip("Adicionar");
        setIcon(TIcon.PLUS_CIRCLE);
        setOrder(1);
    }

    /**
     * Nome do dicionário utilizado para exibir a pré seleção de um registro (default= null)
     */
    public String getElementNameToSelect() {
        return elementNameToSelect;
    }

    /**
     * Define Nome do dicionário utilizado para exibir a pré seleção de um registro (default= null)
     */
    public void setElementNameToSelect(String elementNameToSelect) {
        this.elementNameToSelect = elementNameToSelect;
    }

    /**
     * Reabrir o formulário (default=false)
     */
    public boolean isReopenForm() {
        return reopenForm;
    }

    /**
     * Define se ao salvar o sistema irá reabrir o formulário (default=false)
     * @param reopenForm
     */
    public void setReopenForm(boolean reopenForm) {
        this.reopenForm = reopenForm;
    }
}
