package br.com.jjconsulting.mobile.dansales;

import android.os.Bundle;

import br.com.jjconsulting.mobile.jjlib.UnderDevelopment;

public class LiberacaoFragment extends PedidosFragment implements UnderDevelopment {

    private static final String ARG_TYPE = "arg_type";

    public LiberacaoFragment() { }

    public static LiberacaoFragment newInstance(int type) {
        LiberacaoFragment fragment = new LiberacaoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }
}
