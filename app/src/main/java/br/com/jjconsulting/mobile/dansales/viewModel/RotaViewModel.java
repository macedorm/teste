package br.com.jjconsulting.mobile.dansales.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.RotaOrigem;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.model.TapDetail;
import br.com.jjconsulting.mobile.dansales.model.TapViewType;

public class RotaViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isFirstOpenPopup = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isVisitaPromotor = new MutableLiveData<>();

    private final MutableLiveData<String> promotor = new MutableLiveData<>();

    private final MutableLiveData<Rotas> rotas = new MutableLiveData<>();

    private final MutableLiveData<RotaOrigem> rotaOrigem = new MutableLiveData<>();

    public MutableLiveData<Rotas> getRotas() {
        return rotas;
    }

    public MutableLiveData<RotaOrigem> getRotaOrigem() {
        return rotaOrigem;
    }

    public MutableLiveData<Boolean> getIsVisitaPromotor() {
        return isVisitaPromotor;
    }

    public MutableLiveData<String> getPromotor() {
        return promotor;
    }

    public MutableLiveData<Boolean> getIsFirstOpenPopup() {
        return isFirstOpenPopup;
    }
}
