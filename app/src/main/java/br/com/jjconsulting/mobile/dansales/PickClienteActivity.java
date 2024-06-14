package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PedidoViewType;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionPedido;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;

public class PickClienteActivity extends SingleFragmentActivity
        implements ClientesFragment.OnClienteClickListener {

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, PickClienteActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return ClientesFragment.newInstance();
    }

    @Override
    public void onClienteClick(Cliente cliente) {
        PedidoDao pedidoDao = new PedidoDao(this);
        Current current = Current.getInstance(this);

        DialogsCustom dialogsDefault = new DialogsCustom(this);

        if (cliente.getStatusCredito() != Cliente.STATUS_CREDITO_BLACK) {
            CurrentActionPedido.getInstance().setUpdateListPedido(true);
            PedidoBusiness pedidoBusiness = new PedidoBusiness();
            Pedido pedido = pedidoBusiness.createNewPedido(pedidoDao, current.getUnidadeNegocio(),
                    current.getUsuario(), cliente);
            if (pedidoBusiness.isAgenda(current.getUsuario(), current.getUnidadeNegocio(), cliente)) {
                startActivity(PickAgendaActivity.newIntent(this, pedido.getCodigo()));
            } else {
                startActivity(PedidoDetailActivity.newIntent(this, pedido.getCodigo(),
                        PedidoViewType.PEDIDO, false, true));
            }

            finish();

        } else {
            dialogsDefault.showDialogMessage(getString(R.string.message_error_cliente_bloaquedo), dialogsDefault.DIALOG_TYPE_WARNING, null);
        }
    }
}
