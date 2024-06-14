package br.com.jjconsulting.mobile.dansales;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PedidoViewType;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.viewModel.PedidoDetailViewModel;

public class PedidoBaseFragment extends BaseFragment {

    private PedidoDetailViewModel mPedidoDetailViewModel;
    private UnidadeNegocio mCurrentUnidadeNegocio;
    private Usuario mCurrentUsuario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPedidoDetailViewModel = ViewModelProviders.of(getActivity())
                .get(PedidoDetailViewModel.class);

        Current current = Current.getInstance(getActivity());
        mCurrentUsuario = current.getUsuario();
        mCurrentUnidadeNegocio = current.getUnidadeNegocio();
    }

    protected Usuario getCurrentUsuario() {
        return mCurrentUsuario;
    }

    protected UnidadeNegocio getCurrentUnidNeg() {
        return mCurrentUnidadeNegocio;
    }

    protected Pedido getCurrentPedido() {
        return mPedidoDetailViewModel.getObservablePedido().getValue();
    }

    protected void setCurrentPedido(Pedido pedido) {
        mPedidoDetailViewModel.getObservablePedido().setValue(pedido);
    }

    protected ArrayList<ItemPedido> getCurrentItens() {
        return mPedidoDetailViewModel.getObservableItens().getValue();
    }

    protected void setCurrentItens(ArrayList<ItemPedido> itens) {
        mPedidoDetailViewModel.getObservableItens().setValue(itens);
    }

    protected PedidoViewType getCurrentViewType() {
        return mPedidoDetailViewModel.getObservableViewType().getValue();
    }

    protected PerfilVenda getCurrentPerfilVenda() {
        try{
            Pedido pedido = getCurrentPedido();
            if (pedido == null)
                return null;

            return getCurrentUsuario().getPerfil().getPerfilVenda(pedido);
        }catch (Exception ex){
            return null;
        }
    }

    protected boolean isEditMode() {
        try{
            return mPedidoDetailViewModel.getObservableEditMode().getValue();
        }catch (Exception ex){
            return false;
        }
    }
}
