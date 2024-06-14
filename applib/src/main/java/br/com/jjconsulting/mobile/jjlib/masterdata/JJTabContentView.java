package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Hashtable;

import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.dao.DictionaryDao;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Dictionary.DicParser;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Factory;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TPageState;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TRelationView;
import br.com.jjconsulting.mobile.jjlib.dao.entity.UIOptions;
import br.com.jjconsulting.mobile.jjlib.model.DataTable;

public class JJTabContentView extends Fragment implements OnPageSelected {

    private JJOnUpdate jjOnUpdate;

    private DataTable mDataTable;

    private FormElement mFormElement;

    private Hashtable mRelationValues;

    private TPageState pageState;

    private TRelationView tRelationView;

    private JJGridView jjGridView;

    private JJDataPainelView jjDataPainelView;

    private OnFinish onFinish;

    private Fragment fragment;

    private boolean reCreate;
    public boolean isLoading;

    private int positionTab;


    public static JJTabContentView newInstance(Context context, DataTable dataTable, String elementName, TPageState pageState, TRelationView objectType, int position, JJOnUpdate jjOnUpdate) {
        JJTabContentView JJTabContentView = new JJTabContentView();
        JJTabContentView.setDataTable(dataTable);
        JJTabContentView.setElement(context, elementName);
        JJTabContentView.setPageState(pageState);
        JJTabContentView.setTRelationView(objectType);
        JJTabContentView.setRelationValues(null);
        JJTabContentView.setPositionTab(position);
        JJTabContentView.setJJOnUpdate(jjOnUpdate);
        return JJTabContentView;
    }

    public static JJTabContentView newInstance(Context context, String elementName, Hashtable relationValues, TPageState pageState, TRelationView objectType, int position, JJOnUpdate jjOnUpdate) {
        JJTabContentView JJTabContentView = new JJTabContentView();
        JJTabContentView.setDataTable(null);
        JJTabContentView.setElement(context, elementName);
        JJTabContentView.setPageState(pageState);
        JJTabContentView.setTRelationView(objectType);
        JJTabContentView.setRelationValues(relationValues);
        JJTabContentView.setPositionTab(position);
        JJTabContentView.setJJOnUpdate(jjOnUpdate);
        return JJTabContentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFinish = new OnFinish() {
            @Override
            public void finish(boolean isNewAdd) {
                getActivity().finish();

                if(isNewAdd) {
                    JJTabContentView tabContentView = ((JJTabActivity) getActivity()).getJjTabContentView()[0];

                    if (tabContentView.mRelationValues != null) {
                        startActivity(JJDataPainelActivity.newIntent(getContext(), tabContentView.mFormElement.getName(), TPageState.INSERT, tabContentView.mRelationValues, true, false, null));
                    } else {
                        startActivity(JJDataPainelActivity.newIntent(getContext(), tabContentView.mFormElement.getName(), null, TPageState.INSERT, true, false, null));
                    }
                }
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.tab_view, container,
                false);

        return view;
    }

    @Override
    public void onPageSelected(int position) {
        if (position == positionTab) {

            //Corrigindo o problema de atualização de tabs
            //if(fragment == null //|| isReCreate()){
                getActivity().invalidateOptionsMenu();
                setReCreate(false);

                if (getTRelationView() == TRelationView.LIST) {
                    fragment = buildJJGridView();
                } else {
                   fragment = buildJJPainelView();
                }
            }
            setFragment(fragment);
       // }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getTRelationView() != TRelationView.LIST) {
            jjDataPainelView.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setFragment(Fragment fr) {
        FragmentManager fragmentManager = getChildFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fr)
                .commitAllowingStateLoss();
    }

    private Fragment buildJJGridView() {
        jjGridView = JJGridView.renderFragment(getContext(), getFormElement().getName());
        jjGridView.setJJOnUpdate(jjOnUpdate);
        jjGridView.setTab(true);

        if (pageState == TPageState.INSERT || pageState == TPageState.UPDATE) {
            DictionaryDao dictionaryDao = new DictionaryDao(getContext());
            DicParser dicParser = dictionaryDao.getDictionary(getFormElement().getName());
            UIOptions uiOptions = dicParser.uiOptions;
            jjGridView.gridActions= uiOptions.getGridActions().getAll();
            jjGridView.toolBarActions= uiOptions.getToolBarActions().getAll();
        }

        jjGridView.setRelationValues(mRelationValues);
        jjGridView.setShowMenuActionBar(false);
        jjGridView.setShowTitle(true);
        jjGridView.setOnFinish(onFinish);

        return jjGridView;
    }

    private Fragment buildJJPainelView() {
        jjDataPainelView = JJDataPainelView.renderFragment(getContext(), getFormElement().getName(), getDataTable(), tRelationView != TRelationView.UPDATE ? TPageState.VIEW : pageState, false);
        jjDataPainelView.setRelationValues(mRelationValues);
        jjDataPainelView.setShowTitle(true);
        jjDataPainelView.setTab(true);
        jjDataPainelView.setLoading(isLoading);

        return jjDataPainelView;
    }

    public Hashtable getFormValues() {
        return jjDataPainelView.getFormValue();
    }

    public boolean isUpdate() {
        try {
            return (getTRelationView() == TRelationView.UPDATE && jjDataPainelView.isChangeInfo());
        } catch (Exception ex) {
            return false;
        }
    }

    public void setUpdate(boolean update) {
        if (jjDataPainelView != null) {
            jjDataPainelView.setChangeInfo(update);
        }
    }


    public boolean isReCreate() {
        return reCreate;
    }

    public void setReCreate(boolean reCreate) {
        this.reCreate = reCreate;
    }

    public void showErros(Hashtable<String, String> errors) {
        if (jjDataPainelView != null) {
            jjDataPainelView.showErros(errors);
        }
    }

    public DataTable getDataTable() {
        return mDataTable;
    }

    public void setDataTable(DataTable mDataTable) {
        this.mDataTable = mDataTable;
    }

    public TRelationView getTRelationView() {
        return tRelationView;
    }

    public void setTRelationView(TRelationView tRelationView) {
        this.tRelationView = tRelationView;
    }

    public void setPageState(TPageState pageState) {
        this.pageState = pageState;
    }

    public void setRelationValues(Hashtable relationValues) {
        this.mRelationValues = relationValues;
    }

    public void setJJOnUpdate(JJOnUpdate jjOnUpdate) {
        this.jjOnUpdate = jjOnUpdate;
    }

    public void setPositionTab(int positionTab) {
        this.positionTab = positionTab;
    }

    public FormElement getFormElement() {
        return mFormElement;
    }

    public void setElement(Context context, String elementName) {
        Factory factory = new Factory(context);
        mFormElement = factory.getFormElement(elementName);
    }

    public Fragment getFragment() {
        return fragment;
    }

    public interface OnFinish{
        void finish(boolean isNewAdd);
    }
}
