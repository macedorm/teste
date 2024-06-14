package br.com.jjconsulting.mobile.dansales;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TapEmptyFragment extends TapBaseFragment {

    public TapEmptyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rota, container, false);
    }

    public static TapEmptyFragment newInstance() {
        TapEmptyFragment fragment = new TapEmptyFragment();
        return fragment;
    }
}

