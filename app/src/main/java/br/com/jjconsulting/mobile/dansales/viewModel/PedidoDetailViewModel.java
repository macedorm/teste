package br.com.jjconsulting.mobile.dansales.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.PedidoViewType;
import br.com.jjconsulting.mobile.dansales.model.Pedido;

public class PedidoDetailViewModel extends ViewModel {

    private final MutableLiveData<Pedido> observablePedido = new MutableLiveData<>();
    private final MutableLiveData<PedidoViewType> viewTypePedido = new MutableLiveData<>();
    private final MutableLiveData<Boolean> viewEditMode = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<ItemPedido>> observableitens = new MutableLiveData<>();

    public MutableLiveData<Pedido> getObservablePedido() {
        return observablePedido;
    }

    public MutableLiveData<ArrayList<ItemPedido>> getObservableItens() {
        return observableitens;
    }

    public MutableLiveData<PedidoViewType> getObservableViewType() {
        return viewTypePedido;
    }

    public MutableLiveData<Boolean> getObservableEditMode() {
        return viewEditMode;
    }

}
