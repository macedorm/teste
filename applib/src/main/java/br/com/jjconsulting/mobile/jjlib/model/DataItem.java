package br.com.jjconsulting.mobile.jjlib.model;

import java.io.Serializable;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TField;

public class DataItem  implements Serializable {
    private Object value;
    private TField dataType;
    private boolean enable;
    private boolean visible;
    private boolean setVisibleEnable;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public TField getDataType() {
        return dataType;
    }

    public void setDataType(TField dataType) {
        this.dataType = dataType;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isSetVisibleEnable() {
        return setVisibleEnable;
    }

    public void setSetVisibleEnable(boolean setVisibleEnable) {
        this.setVisibleEnable = setVisibleEnable;
    }
}
