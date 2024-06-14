package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.adapter.JJDialogSearchViewAdapter;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Factory;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElement;
import br.com.jjconsulting.mobile.jjlib.model.SpinnerDataItem;

public class JJSearchViewDialogFragment extends DialogFragment implements SearchView.OnQueryTextListener {

    private static final String ARG_LIST = "array_string_list";
    private static final String ARG_LEGEND = "show_legend_image";


    private JJDialogSearchViewAdapter jjDialogSearchViewAdapter;

    private FormElement mFormElement;

    private OnFinishValidation onFinishValidation;

    private ArrayList<SpinnerDataItem> searchInfoArray;

    private ListView mSearchListView;

    private boolean isShowImageLegend;


    public JJSearchViewDialogFragment() { }

    public static JJSearchViewDialogFragment newInstance(boolean isShowLegendImage, ArrayList<SpinnerDataItem> spinnerDataItem) {
        JJSearchViewDialogFragment fragment = new JJSearchViewDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LIST, spinnerDataItem);
        args.putBoolean(ARG_LEGEND, isShowLegendImage);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogValidationStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.jj_dialog_fragment_search_view, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        searchInfoArray = (ArrayList<SpinnerDataItem>) getArguments().getSerializable(ARG_LIST);
        isShowImageLegend = getArguments().getBoolean(ARG_LEGEND);

        mSearchListView = rootView.findViewById(R.id.listview);

        for (int i = 0; i < searchInfoArray.size(); i++) {
            searchInfoArray.get(i).setPosition(i);
        }

        jjDialogSearchViewAdapter = new JJDialogSearchViewAdapter(getActivity(), isShowImageLegend, searchInfoArray);
        mSearchListView.setAdapter(jjDialogSearchViewAdapter);

        SearchView editsearch = rootView.findViewById(R.id.search_view);
        editsearch.setOnQueryTextListener(this);

        mSearchListView.setOnItemClickListener((parent, view, position, id) ->{
            if(onFinishValidation != null){
                onFinishValidation.onFinish(searchInfoArray.get(position).getPosition());
            }
        });

        return rootView;
    }

    public FormElement getElement() {
        return mFormElement;
    }

    public void setElement(Context context, String elementName) {
        Factory factory = new Factory(context);
        this.mFormElement = factory.getFormElement(elementName);
    }

    public void setElement(FormElement element) {
        this.mFormElement = element;
    }

    public OnFinishValidation getOnFinishValidation() {
        return onFinishValidation;
    }

    public void setOnFinishValidation(OnFinishValidation onFinishValidation) {
        this.onFinishValidation = onFinishValidation;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String filter = newText;
        jjDialogSearchViewAdapter.filter(filter);
        return false;
    }

    public interface OnFinishValidation{
        void onFinish(int position);
    }
}
