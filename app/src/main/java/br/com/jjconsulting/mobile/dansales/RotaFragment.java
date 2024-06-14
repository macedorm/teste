package br.com.jjconsulting.mobile.dansales;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RotaFragment extends Fragment {

    public RotaFragment() {
        // Required empty public constructor
    }

    public static RotaFragment newInstance() {
        return new RotaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rota, container, false);
    }
}
