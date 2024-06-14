package br.com.jjconsulting.mobile.dansales.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.TapDetail;
import br.com.jjconsulting.mobile.dansales.model.TapViewType;

public class TapDetailViewModel extends ViewModel {

    private final MutableLiveData<TapDetail> observableTap = new MutableLiveData<>();
    private final MutableLiveData<TapViewType> viewTypeTap = new MutableLiveData<>();
    private final MutableLiveData<Boolean> viewEditMode = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<ItemPedido>> observableitens = new MutableLiveData<>();

    public MutableLiveData<TapDetail> getObservableTap() {
        return observableTap;
    }

    public MutableLiveData<ArrayList<ItemPedido>> getObservableItens() {
        return observableitens;
    }

    public MutableLiveData<TapViewType> getObservableViewType() {
        return viewTypeTap;
    }

    public MutableLiveData<Boolean> getObservableEditMode() {
        return viewEditMode;
    }

}
