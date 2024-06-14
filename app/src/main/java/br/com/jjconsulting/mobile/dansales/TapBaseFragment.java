package br.com.jjconsulting.mobile.dansales;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;

import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.model.TapDetail;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.viewModel.TapDetailViewModel;

public class TapBaseFragment extends BaseFragment {

    private TapDetailViewModel mTapDetailViewModel;
    private UnidadeNegocio mCurrentUnidadeNegocio;
    private Usuario mCurrentUsuario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTapDetailViewModel = ViewModelProviders.of(getActivity())
                .get(TapDetailViewModel.class);

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

    protected TapDetail getCurrentTap() {
        return mTapDetailViewModel.getObservableTap().getValue();
    }

    protected void setCurrentTap(TapDetail tap) {
        mTapDetailViewModel.getObservableTap().setValue(tap);
    }


    protected boolean isEditMode() {
        return mTapDetailViewModel.getObservableEditMode().getValue();
    }
}
