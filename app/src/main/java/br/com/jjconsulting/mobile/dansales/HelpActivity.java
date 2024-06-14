package br.com.jjconsulting.mobile.dansales;

import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class HelpActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return HelpFragment.newInstance();
    }

    @Override
    protected boolean useOnBackPressedInUpNavigation() {
        return true;
    }
}
