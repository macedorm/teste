package br.com.jjconsulting.mobile.jjlib;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UnderDevelopmentFragment extends SuperFragment {

    public static UnderDevelopmentFragment newInstance() {
        return new UnderDevelopmentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.not_implemented, container, false);
    }
}
