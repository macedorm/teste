package br.com.jjconsulting.mobile.jjlib.dao.entity.Action;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;

public class EditAction extends BasicAction {
    /**
     * Nome padrão da ação
     */
    public static final String ACTION_NAME = "edit";

    public EditAction() {
        //setName(ACTION_NAME);
        setToolTip("Alterar");
        setIcon(TIcon.PENCIL);
        setOrder(2);
    }
}
