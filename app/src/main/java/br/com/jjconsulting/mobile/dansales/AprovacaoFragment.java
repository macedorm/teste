package br.com.jjconsulting.mobile.dansales;

import android.os.Bundle;

import br.com.jjconsulting.mobile.jjlib.UnderDevelopment;

public class AprovacaoFragment extends PedidosFragment implements UnderDevelopment {

    private static final String ARG_TYPE = "arg_type";

    public AprovacaoFragment() { }

    public static AprovacaoFragment newInstance(int type) {
        AprovacaoFragment fragment = new AprovacaoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }
}
