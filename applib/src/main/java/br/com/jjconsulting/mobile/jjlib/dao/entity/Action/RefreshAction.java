package br.com.jjconsulting.mobile.jjlib.dao.entity.Action;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;

public class RefreshAction extends BasicAction {
    /**
     * Nome padrão da ação
     */
    public static final String ACTION_NAME = "refresh";

    public RefreshAction() {
        //setName(ACTION_NAME);
        setToolTip("Atualizar");
        setIcon(TIcon.REFRESH);
        setOrder(6);
    }
}
