package br.com.jjconsulting.mobile.dansales.data;

import java.util.Date;

import br.com.jjconsulting.mobile.dansales.database.SortimentoDao;
import br.com.jjconsulting.mobile.dansales.model.Pedido;

public class GetSortimentoAsyncTaskParameters {

    private SortimentoDao sortimentoDao;

    private Pedido pedido;
    private String planogramCode;
    private Date date;

    public GetSortimentoAsyncTaskParameters(SortimentoDao sortimentoDao, Pedido pedido,
                                            String planogramCode, Date date) {
        this.sortimentoDao = sortimentoDao;
        this.pedido = pedido;
        this.planogramCode = planogramCode;
        this.date = date;
    }

    public SortimentoDao getSortimentoDao() {
        return sortimentoDao;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public String getPlanogramCode() {
        return planogramCode;
    }

    public Date getDate() {
        return date;
    }
}
