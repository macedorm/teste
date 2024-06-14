package br.com.jjconsulting.mobile.jjlib.dao.entity.Action;

import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;
import br.com.jjconsulting.mobile.jjlib.model.DataTable;

public class CustomAction extends BasicAction {

    private OnClick onClick;

    public CustomAction(){
        setIcon(TIcon.ANDROID);
    }

    public interface OnClick{
        void onClick(CustomAction sender, FormElement formElement, DataTable dataTable);
    }

    public OnClick getOnClick() {
        return onClick;
    }

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }
}
