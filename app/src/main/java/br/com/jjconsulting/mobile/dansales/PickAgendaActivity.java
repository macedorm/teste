package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.model.Agenda;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PedidoViewType;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class PickAgendaActivity extends SingleFragmentActivity
        implements AgendasFragment.OnAgendaClickListener {

    private static final String ARG_CODIGO_PEDIDO = "codigo_pedido";

    private String mCodigoPedido;

    public static Intent newIntent(Context packageContext, String codigoPedido) {
        Intent intent = new Intent(packageContext, PickAgendaActivity.class);
        intent.putExtra(ARG_CODIGO_PEDIDO, codigoPedido);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCodigoPedido = getIntent().getStringExtra(ARG_CODIGO_PEDIDO);
    }

    @Override
    protected Fragment createFragment() {
        return AgendasFragment.newInstance(getIntent().getStringExtra(ARG_CODIGO_PEDIDO));
    }

    @Override
    protected boolean useOnBackPressedInUpNavigation() {
        return true;
    }

    @Override
    public void onAgendaClick(Agenda agenda, int tipoAgenda) {
        PedidoDao pedidoDao = new PedidoDao(this);
        Pedido pedido = pedidoDao.get(mCodigoPedido);

        switch (tipoAgenda) {
            case Pedido.TIPO_AGENDA_NAO_POSSUI:
                pedido.setCodigoAgenda(null);
                pedido.setTipoAgenda(Pedido.TIPO_AGENDA_NAO_POSSUI);
                break;
            case Pedido.TIPO_AGENDA_AGENDA:
                pedido.setCodigoAgenda(agenda.getCodigo());
                pedido.setTipoAgenda(Pedido.TIPO_AGENDA_AGENDA);
                pedido.setCodigoTipoVenda(TipoVenda.VENDA);
                break;
            case Pedido.TIPO_AGENDA_AGENDA_DISTRIBUIDOR:
                pedido.setCodigoAgenda(null);
                pedido.setTipoAgenda(Pedido.TIPO_AGENDA_AGENDA_DISTRIBUIDOR);
                pedido.setCodigoTipoVenda(TipoVenda.VENDA);
                break;
        }

        PedidoBusiness pedidoBusiness = new PedidoBusiness();
        pedidoBusiness.updatePedido(getApplicationContext(), pedido, null, null);
        startActivity(PedidoDetailActivity.newIntent(this, pedido.getCodigo(), PedidoViewType.PEDIDO, false, false));
        finish();
    }
}
