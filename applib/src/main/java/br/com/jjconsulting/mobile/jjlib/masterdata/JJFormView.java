package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.dao.DictionaryDao;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.*;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Dictionary.DicParser;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Factory;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.UIOptions;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class JJFormView extends JJGridView {

    private Context context;

    public JJFormView() {
        toolBarActions = new ArrayList<>();
        toolBarActions.add(new InsertAction());
        toolBarActions.add(new LegendAction());
        toolBarActions.add(new RefreshAction());
        toolBarActions.add(new FilterAction());

        gridActions = new ArrayList<>();
        gridActions.add(new ViewAction());
        gridActions.add(new EditAction());
        gridActions.add(new DeleteAction());

    }

    public static JJFormView renderFragment(Context context, String elementName) {
        Factory factory = new Factory(context);
        FormElement e = factory.getFormElement(elementName);
        return renderFragment(context, e);
    }

    public static JJFormView renderFragment(Context context, FormElement element) {
        JJFormView fragment = new JJFormView();
        fragment.setElement(element);
        fragment.setShowMenuActionBar(true);
        fragment.setContext(context);

        DictionaryDao dictionaryDao = new DictionaryDao(context);
        DicParser dicParser = dictionaryDao.getDictionary(element.getName());
        UIOptions uiOptions = dicParser.uiOptions;
        fragment.setUiOptions(uiOptions);

        try {
            if (!JJSDK.isInitialize()) {
                throw new Exception(fragment.getContext().getString(R.string.sdk_error), new Throwable(""));
            }
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && requestCode == KEY_FORM_VIEW) {
            this.reloadData();
        }
    }

    public void setFragment(Fragment fr){
        FragmentManager childFragMan = getChildFragmentManager();
        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
        childFragTrans.replace(R.id.child_fragment_container, fr);
        childFragTrans.commit();
    }


    public void setUiOptions(UIOptions uiOptions) {
        setMessageEmpty(uiOptions.getGrid().getEmptyDataText());
        setShowTitle(uiOptions.getGrid().isShowTitle());
        gridActions = uiOptions.getGridActions().getAll();
        toolBarActions = uiOptions.getToolBarActions().getAll();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public InsertAction getInsertAction() {
        for (BasicAction action : getToolBarActions()) {
            if (action instanceof InsertAction)
                return (InsertAction)action;
        }
        return null;
    }

    public EditAction getEditAction() {
        for (BasicAction action : getGridActions()) {
            if (action instanceof EditAction)
                return (EditAction)action;
        }
        return null;
    }

    public DeleteAction getDeleteAction() {
        for (BasicAction action : getGridActions()) {
            if (action instanceof DeleteAction)
                return (DeleteAction)action;
        }
        return null;
    }

    public ViewAction getViewAction() {
        for (BasicAction action : getGridActions()) {
            if (action instanceof ViewAction)
                return (ViewAction)action;
        }
        return null;
    }

}
