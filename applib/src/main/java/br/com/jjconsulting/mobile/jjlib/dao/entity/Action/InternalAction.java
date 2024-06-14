package br.com.jjconsulting.mobile.jjlib.dao.entity.Action;

import br.com.jjconsulting.mobile.jjlib.dao.entity.FormActionRedirect;

public class InternalAction extends BasicAction {

    private FormActionRedirect elementRedirect;

    public InternalAction() {
        //setIcon(TIcon.EXTERNAL_LINK_SQUARE);
        elementRedirect = new FormActionRedirect();
    }

    public FormActionRedirect getElementRedirect() {
        return elementRedirect;
    }

    public void setElementRedirect(FormActionRedirect elementRedirect) {
        this.elementRedirect = elementRedirect;
    }
}
