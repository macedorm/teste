package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.connection.SyncFieldsConnection;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.CustomAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Factory;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElementField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TLoadingDataType;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TPageState;
import br.com.jjconsulting.mobile.jjlib.data.ValidationInfo;
import br.com.jjconsulting.mobile.jjlib.model.DataTable;
import br.com.jjconsulting.mobile.jjlib.model.RetFields;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.JJValidationDialogFragment;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class JJPickItemActivity extends SingleFragmentDataPainelActivity {

    public static final int KEY_FORM_VIEW = 99;

    private DialogsCustom dialogCustom;

    public static Intent newIntent(Context context, String elementName, String saveElementName) {
        Intent it = new Intent(context, JJPickItemActivity.class);
        it.putExtra(DATA_KEY_ELEMENT_NAME, elementName);
        it.putExtra(DATA_KEY_ELEMENT_NAME_SAVE, saveElementName);
        it.putExtra(DATA_KEY_PAGE_STATE, TPageState.VIEW.getValue());
        return it;
    }

    private JJGridView buildJJGridView() {
        JJGridView jjView = JJGridView.renderFragment(this, getElementName());

        CustomAction action = new CustomAction();
        action.setName("AC_SELECT");
        action.setIcon(TIcon.CHEVRON_RIGHT);
        action.setToolTip("Selecionar");
        action.setDefaultOption(true);
        action.setOnClick((sender, formElement, dataTable) -> {
            selectedItem(formElement, dataTable);
        });

        jjView.addGridAction(action);

        return jjView;
    }

    @Override
    protected Fragment createFragment() {
        dialogCustom = new DialogsCustom(this);
        invalidateOptionsMenu();
        return buildJJGridView();
    }

    public int getIndexElement(String fieldName, FormElement element) {
        int index = -1;

        for (int ind = 0; ind < element.getFormFields().size(); ind++) {
            if (element.getFormFields().get(ind).getFieldname().equals(fieldName)) {
                index = ind;
            }
        }

        return index;
    }

    private void selectedItem(FormElement formElement, DataTable dataTable) {
        Factory factory = new Factory(JJPickItemActivity.this);
        FormElement formElementCab = factory.getFormElement(getElementNameSave());
        Hashtable values = new Hashtable();

        for (FormElementField elementField : formElementCab.getFormFields()) {
            boolean ignore = false;

            if (elementField.getDefaultvalue() != null && elementField.getDefaultvalue().contains("exp")) {
                try {
                    ExpressionManager expressionManager = new ExpressionManager();
                    String exp = expressionManager.parseExpression(elementField.getDefaultvalue(), TPageState.INSERT);
                    values.put(elementField.getFieldname(), dataTable.getDataItens().get(getIndexElement(exp, formElement)).getValue().toString());
                    ignore = true;
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, ex.toString());
                }

            }

            if (!ignore) {
                if (elementField.getIspk()) {
                    values.put(elementField.getFieldname(), FormatUtils.createIDTemp(getCodUser()));
                }
            }
        }

            /*
            if(uiOptions != null){
                for (FormAction formAction : uiOptions.getActions()) {
                    switch (formAction.getType()){
                        default:
                            break;
                        case VIEW_ONFORM:
                            grid.addAction(selectedAction);
                            break;
                        case UPDATE_ONFORM:
                            grid.addAction(new GridAction(this, TGridAction.UPDATE_ONFORM,  (actionUpdate, formElementUpdate, dataTableUpdate) -> {
                                startActivityForResult(JJTabActivity.newIntent(this, dataTable, formElement.getName(), TPageState.UPDATE), KEY_FORM_VIEW);
                            }, formAction));
                            break;
                        case DELETE_ONGRID:
                            grid.addAction(new GridAction(this, TGridAction.DELETE_ONGRID, null, formAction));
                            break;
                    }
                }

            }*/

        SyncFieldsConnection syncFieldsConnection = new SyncFieldsConnection(JJPickItemActivity.this, new SyncFieldsConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader) {
                try {
                    Factory factory = new Factory(JJPickItemActivity.this);

                    Gson gson = new Gson();
                    List<ValidationLetter> validationLetter = gson.fromJson(response, new TypeToken<List<ValidationLetter>>() {
                    }.getType());

                    Hashtable dataServer = validationLetter.get(0).getData();
                    Hashtable newItem = new Hashtable();

                    Set<Map.Entry<Object, Object>> entrySet = values.entrySet();
                    for (Map.Entry<Object, Object> entry : entrySet) {

                        if (dataServer.containsKey(entry.getKey())) {
                            newItem.put(entry.getKey(), dataServer.get(entry.getKey()));
                        } else {
                            newItem.put(entry.getKey(), entry.getValue());
                        }
                    }

                    if (TLoadingDataType.fromInteger(formElementCab.getMode()) == TLoadingDataType.OFFLINE) {
                        if (validationLetter != null && validationLetter.size() > 0) {
                            factory.insert(formElement, newItem, validationLetter.get(0).getData());
                        } else {
                            factory.insert(formElement, newItem, null);
                        }
                    }

                    if (formElement.getRelations().size() == 0) {
                        getItem(newItem);
                    } else {
                        dialogCustom.showDialogLoading(false);
                        setResult(RESULT_OK);
                        finish();
                    }

                } catch (Exception ex) {
                    LogUser.log(Config.TAG, ex.toString());
                    dialogCustom.showDialogMessage(getString(br.com.jjconsulting.mobile.jjlib.R.string.sync_connection_error), DialogsCustom.DIALOG_TYPE_ERROR, null);
                }
            }

            @Override
            public void onError(int code, VolleyError volleyError, int typeConnection, String response) {
                dialogCustom.showDialogLoading(false);

                if (code == Connection.NO_CONNECTION) {
                    dialogCustom.showDialogMessage(getString(br.com.jjconsulting.mobile.jjlib.R.string.sync_connection_error), DialogsCustom.DIALOG_TYPE_ERROR, null);
                } else {
                    try {
                        Gson gson = new Gson();
                        ArrayList<ValidationLetter> validation = gson.fromJson(response, new TypeToken<List<ValidationLetter>>() {
                        }.getType());
                        if (validation != null && validation.size() > 0) {
                            showErros(validation.get(0).getValidationList());
                        }
                    } catch (Exception ex) {
                        dialogCustom.showDialogMessage(getString(br.com.jjconsulting.mobile.jjlib.R.string.sync_connection_error), DialogsCustom.DIALOG_TYPE_ERROR, null);
                    }
                }
            }
        });

        dialogCustom.showDialogLoading(true);
        syncFieldsConnection.insertField(formElementCab.getName(), values);

    }

    public void showErros(Hashtable<String, String> errors) {

        ValidationInfo validationInfo = new ValidationInfo();

        for (HashMap.Entry<String, String> entry : errors.entrySet()) {
            validationInfo.addError(entry.getValue());
        }

        android.app.FragmentManager fm = getFragmentManager();
        JJValidationDialogFragment jjValidationDialogFragment =
                JJValidationDialogFragment.newInstance(validationInfo, validationInfo.getMessages().isEmpty());

        jjValidationDialogFragment.setOnFinishValidation(success -> {
            if (success) {
                finish();
            }
        });

        jjValidationDialogFragment.show(getFragmentManager(), "");
        fm.executePendingTransactions();
    }


    private void getItem(Hashtable filter) {

        Factory factory = new Factory(this);
        FormElement formElement = factory.getFormElement(getElementNameSave());

        SyncFieldsConnection syncFieldsConnection = new SyncFieldsConnection(this, new SyncFieldsConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader) {
                dialogCustom.showDialogLoading(false);
                Gson gson = new Gson();
                Factory factory = new Factory(JJPickItemActivity.this);

                try {
                    RetFields retFields = gson.fromJson(response, RetFields.class);
                    DataTable mDataTable = factory.convertResponseRetFields(retFields, formElement).get(0);
                    startActivityForResult(JJTabActivity.newIntent(JJPickItemActivity.this, mDataTable, getElementNameSave(), TPageState.INSERT), JJFormView.KEY_FORM_VIEW);

                    setResult(RESULT_OK);
                    finish();
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, ex.toString());
                }
            }

            @Override
            public void onError(int code, VolleyError volleyError, int typeConnection, String response) {
                dialogCustom.showDialogLoading(false);
                Toast.makeText(JJPickItemActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        });

        dialogCustom.showDialogLoading(true);
        syncFieldsConnection.syncField(getElementNameSave(), filter, 1, 1, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == KEY_FORM_VIEW) {
            createFragment();
        }
    }
}
