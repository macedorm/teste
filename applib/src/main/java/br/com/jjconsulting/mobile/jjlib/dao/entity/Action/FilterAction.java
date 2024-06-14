package br.com.jjconsulting.mobile.jjlib.dao.entity.Action;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;

public class FilterAction extends BasicAction {
    /**
     * Nome padrão da ação
     */
    public static final String ACTION_NAME = "filter";


    public FilterAction() {
        //setName(ACTION_NAME);
        setToolTip("Filtrar");
        setIcon(TIcon.BINOCULARS);
        setOrder(5);
    }

}
