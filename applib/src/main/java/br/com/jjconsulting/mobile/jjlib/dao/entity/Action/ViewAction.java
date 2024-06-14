package br.com.jjconsulting.mobile.jjlib.dao.entity.Action;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;

public class ViewAction extends BasicAction {
    /**
     * Nome padrão da ação
     */
    public static final String ACTION_NAME = "view";

    public ViewAction() {
       // setName(ACTION_NAME);
        setToolTip("Visualizar");
        setIcon(TIcon.EYE);
        setOrder(1);
    }
}
