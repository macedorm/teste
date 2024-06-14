package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

;

public class JJHomeFragment extends Fragment {

    private TextView mtitle;

    private JJMenuView jjMenuView;

    public JJHomeFragment() {
        // Required empty public constructor
    }

    public static JJHomeFragment newInstance(JJMenuView jjMenuViewCurrent) {
        JJHomeFragment jjHomeFragment = new JJHomeFragment();

        JJMenuView jjMenu = new JJMenuView(jjMenuViewCurrent.getContext(), jjMenuViewCurrent.getIdFragment(), jjMenuViewCurrent.getPrimaryColor());
        jjMenu.setHashModule(jjMenuViewCurrent.getModules());
        jjMenu.setStyleButton(true);
        jjMenu.setTitleHome(jjMenuViewCurrent.getTitleHome());
        jjMenu.setAppName(jjMenuViewCurrent.getAppName());
        jjMenu.setShowButtonAdd(jjMenuViewCurrent.isShowButtonAdd());

        jjMenu.setOnClickList((view, position)-> {
            int index = 0;

            if(position > -1 ){
                index = position + 1;
            }

            jjMenuViewCurrent.changeColor(jjMenuViewCurrent.getPositionView().get(index), position);
            jjMenuViewCurrent.setSelectedPosition(position);
        });


        jjHomeFragment.jjMenuView = jjMenu;

        return jjHomeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.jj_fragment_home, container, false);

        LinearLayout containerLienarLayout = view.findViewById(R.id.content_linear_layout);
        containerLienarLayout.addView(jjMenuView.renderView());

        if(!TextUtils.isNullOrEmpty(jjMenuView.getAppName())){
            getActivity().setTitle(jjMenuView.getAppName());
        }

        mtitle = view.findViewById(R.id.home_title);

        if(TextUtils.isNullOrEmpty(jjMenuView.getTitleHome())){
            String hom = "";
            if (getContext().getPackageName().contains("hlm")) {
                hom = getString(R.string.title_hom);
            }
            mtitle.setText(String.format(getString(R.string.title_page_home), "", hom));
        } else {
            mtitle.setText(jjMenuView.getTitleHome());
        }

        return view;
    }
}
