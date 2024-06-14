package br.com.jjconsulting.mobile.dansales.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.TapStatus;
import br.com.jjconsulting.mobile.dansales.model.Usuario;

public class TapFilter implements Serializable {

    private TapStatus status;
    private List<Usuario> hierarquiaComercial;
    private Date dateStart;
    private Date dateEnd;
    private boolean isPendingApproval;

    public TapFilter() {
        this.hierarquiaComercial = new ArrayList<>();
    }

    public TapFilter(TapStatus status, Date dateStart, Date dateEnd, boolean isPendingApproval) {
        this.status = status;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.isPendingApproval = isPendingApproval;

    }

    public TapStatus getStatus() {
        return status;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public List<Usuario> getHierarquiaComercial() {
        return hierarquiaComercial;
    }

    public void setStatus(TapStatus status) {
        this.status = status;
    }

    public void setHierarquiaComercial(List<Usuario> hierarquiaComercial) {
        this.hierarquiaComercial = hierarquiaComercial;
    }

    public boolean isPendingApproval() {
        return isPendingApproval;
    }

    public void setPendingApproval(boolean pendingApproval) {
        isPendingApproval = pendingApproval;
    }

    public boolean isEmpty() {
        return !isPendingApproval && status == null && dateStart == null && dateEnd == null;
    }
}