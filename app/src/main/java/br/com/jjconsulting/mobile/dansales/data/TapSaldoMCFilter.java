package br.com.jjconsulting.mobile.dansales.data;

import java.io.Serializable;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.model.TapComboRelSaldo;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class TapSaldoMCFilter implements Serializable {

    private TapComboRelSaldo codMC;
    private String dateStart;
    private String dateEnd;


    public TapSaldoMCFilter() {

    }

    public TapSaldoMCFilter(TapComboRelSaldo codMC, String dateStart, String dateEnd) {
        this.codMC = codMC;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public TapComboRelSaldo getCodMC() {
        return codMC;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public boolean isEmpty() {
        return codMC == null && dateStart == null && dateEnd == null;
    }
}