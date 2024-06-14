package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.Hashtable;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.SuperActivity;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TPageState;
import br.com.jjconsulting.mobile.jjlib.data.ValidationInfo;
import br.com.jjconsulting.mobile.jjlib.model.DataTable;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.JJValidationDialogFragment;

public abstract class SingleFragmentDataPainelActivity extends SuperActivity {

    public static final String DATA_KEY_ELEMENT_NAME = "key_element_name";
    public static final String DATA_KEY_ELEMENT_NAME_SAVE = "key_element_name_save";
    public static final String DATA_KEY_COD_USER = "key_cod_user";
    public static final String DATA_KEY_RELATION = "key_param_relation";
    public static final String DATA_KEY_PAGE_STATE = "key_page_state";
    public static final String DATA_KEY_DATA_TABLE = "key_page_data_table";
    public static final String DATA_KEY_SHOW_TITLE = "key_page_show_title";
    public static final String DATA_KEY_PARM = "key_param_title";
    public static final String DATA_KEY_REOPEN_FORM = "key_reopen_form";



    public DialogsCustom dialogCustom;

    protected abstract Fragment createFragment();


    /**
     * Returns a value indicating if the Activity must handle up/back navigation.
     * <br>
     * The default value is true - override only if you don't want to enable it, returning false.
     */
    protected boolean enableUpNavigation() {
        return true;
    }

    /**
     * You should override it and return true when the current activity has more than one parent.
     */
    protected boolean useOnBackPressedInUpNavigation() {
        return false;
    }

    private TPageState tPageState;

    private DataTable mDataTable;

    private ProgressDialog dialog;

    private Hashtable mRelationValues;

    private String mElementName;
    private String mElementNameSave;
    private String codUser;
    private String param;

    private boolean showTitle;
    private boolean reopenForm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        Toolbar mToolbar = findViewById(R.id.master_toolbar);
        setSupportActionBar(mToolbar);

        dialogCustom = new DialogsCustom(this);

        Intent intent = getIntent();
        if(intent.getStringExtra(DATA_KEY_ELEMENT_NAME)!= null) {
            mElementName = intent.getStringExtra(DATA_KEY_ELEMENT_NAME);
        }

        if(intent.getStringExtra(DATA_KEY_ELEMENT_NAME_SAVE)!= null) {
            mElementNameSave = intent.getStringExtra(DATA_KEY_ELEMENT_NAME_SAVE);
        }

        if(intent.getStringExtra(DATA_KEY_COD_USER)!= null) {
            codUser = intent.getStringExtra(DATA_KEY_COD_USER);
        }

        if(intent.getIntExtra(DATA_KEY_PAGE_STATE, 0)!= 0) {
            tPageState = TPageState.fromInteger(intent.getIntExtra(DATA_KEY_PAGE_STATE, 0));
        }

        if(intent.getSerializableExtra(DATA_KEY_DATA_TABLE)!= null) {
            mDataTable = (DataTable) intent.getSerializableExtra(DATA_KEY_DATA_TABLE);
        }

        if(intent.getSerializableExtra(DATA_KEY_SHOW_TITLE)!= null) {
            showTitle=  intent.getBooleanExtra(DATA_KEY_SHOW_TITLE, false);
        }


        if(intent.getSerializableExtra(DATA_KEY_REOPEN_FORM)!= null) {
            reopenForm = intent.getBooleanExtra(DATA_KEY_REOPEN_FORM, false);
        }

        if(intent.getStringExtra(DATA_KEY_PARM)!= null) {
            param =  intent.getStringExtra(DATA_KEY_PARM);
        }

        try {

            if (intent.getSerializableExtra(DATA_KEY_RELATION) != null) {
                mRelationValues = new Hashtable();
                HashMap hashMap = (HashMap) intent.getSerializableExtra(DATA_KEY_RELATION);
                mRelationValues.putAll(hashMap);
            }

        }catch (Exception ex){
            mRelationValues = null;
        }

        if( getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(enableUpNavigation());
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (getElementName() != null && tPageState != TPageState.VIEW) {
            getMenuInflater().inflate(R.menu.action_menu_painel, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                try{
                    if (useOnBackPressedInUpNavigation()) {
                        onBackPressed();
                    } else {
                        NavUtils.navigateUpFromSameTask(this);
                    }
                }catch (Exception ex){
                    finish();
                }

                return true;
            default:

                return false;
        }

        //return super.onOptionsItemSelected(item);
    }

    public void hideHomeUpButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);

        invalidateOptionsMenu();
    }

    public void showHomeUpButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        invalidateOptionsMenu();
    }

    public void showErros(Hashtable<String, String> errors){

        ValidationInfo validationInfo = new ValidationInfo();

        for (HashMap.Entry<String, String> entry : errors.entrySet()) {
            validationInfo.addError( entry.getValue());
        }

        android.app.FragmentManager fm = getFragmentManager();

        JJValidationDialogFragment jjValidationDialogFragment =
                JJValidationDialogFragment.newInstance(validationInfo, validationInfo.getMessages().isEmpty());

        jjValidationDialogFragment.setOnFinishValidation(success -> {
             if(success){
                 finish();
             }
        });

        jjValidationDialogFragment.show(getFragmentManager(), "");

        fm.executePendingTransactions();
    }

    public void showDialogMessage(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    public void showDialogLoading(Context context, boolean value){

        if(dialog == null){
            dialog = ProgressDialog.show(context, "",
                    getString(R.string.loading), true);
        }

        if(value){
            dialog.show();
        } else {
            dialog.dismiss();
        }

    }

    public String getElementName() {
        return mElementName;
    }

    public void setElementName(String mElementName) {
        this.mElementName = mElementName;
    }

    public TPageState getTPageState() {
        return tPageState;
    }

    public void setTPageState(TPageState tPageState) {
        this.tPageState = tPageState;
    }

    public DataTable getDataTable() {
        return mDataTable;
    }

    public void setDataTable(DataTable mDataTable) {
        this.mDataTable = mDataTable;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public boolean isReopenForm() {
        return reopenForm;
    }

    public void setReopenform(boolean reopenform) {
        this.reopenForm = reopenForm;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Hashtable getRelationValues() {
        return mRelationValues;
    }

    public void setRelationValues(Hashtable mRelationValues) {
        this.mRelationValues = mRelationValues;
    }

    public String getElementNameSave() {
        return mElementNameSave;
    }

    public void setElementNameSave(String mElementNameSave) {
        this.mElementNameSave = mElementNameSave;
    }

    public String getCodUser() {
        return codUser;
    }

    public void setCodUser(String codUser) {
        this.codUser = codUser;
    }
}
