package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AlteraMasterContrato implements Serializable {

    private TapMasterContrato masterContrato;
    private List<TapComboMesAcao> listAnoMes;

    public TapMasterContrato getMasterContrato() {
        return masterContrato;
    }

    public void setMasterContrato(TapMasterContrato masterContrato) {
        this.masterContrato = masterContrato;
    }

    public List<TapComboMesAcao> getListAnoMes() {
        return listAnoMes;
    }

    public void setListAnoMes(List<TapComboMesAcao> listAnoMes) {
        this.listAnoMes = listAnoMes;
    }
}
