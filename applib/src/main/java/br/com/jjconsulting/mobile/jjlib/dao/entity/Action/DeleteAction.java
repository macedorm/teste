package br.com.jjconsulting.mobile.jjlib.dao.entity.Action;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;

public class DeleteAction extends BasicAction {
    /**
     * Nome padrão da ação
     */
    public static final String ACTION_NAME = "delete";

    public DeleteAction() {
        //setName(ACTION_NAME);
        setToolTip("Excluir");
        setIcon(TIcon.TRASH);
        setConfirmationMessage("Gostaria de excluir este registro?");
        setOrder(3);
    }
}
