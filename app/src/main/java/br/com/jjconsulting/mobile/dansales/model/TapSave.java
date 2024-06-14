package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TapSave implements Serializable {

    private TapCabec cab;
    private List<TapItem> item;

    public TapCabec getTapcab() {
        return cab;
    }

    public void setTapcab(TapCabec tapcab) {
        this.cab = tapcab;
    }

    public List<TapItem> getTapitem() {
        return item;
    }

    public void setTapitem(List<TapItem> tapitem) {
        this.item = tapitem;
    }
}
