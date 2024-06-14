package br.com.jjconsulting.mobile.jjlib.dao.entity.Action;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;

public class LegendAction extends BasicAction {
    /**
     * Nome padrão da ação
     */
    public static final String ACTION_NAME = "legend";

    public LegendAction() {
        //setName(ACTION_NAME);
        setToolTip("Legenda");
        setIcon(TIcon.INFO);
        setOrder(7);
        setVisible(false);
    }
}
