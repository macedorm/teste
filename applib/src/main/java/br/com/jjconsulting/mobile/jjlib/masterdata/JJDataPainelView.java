package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.connection.SyncFieldsConnection;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.BasicAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.CustomAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.InternalAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Factory;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormActionRelationField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElementField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TFormComponent;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TLoadingDataType;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TPageState;
import br.com.jjconsulting.mobile.jjlib.data.ValidationInfo;
import br.com.jjconsulting.mobile.jjlib.model.DataTable;
import br.com.jjconsulting.mobile.jjlib.model.RetFields;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.ImageCameraGallery;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.JJValidationDialogFragment;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.Mask.CurrencyMaskUtil;
import br.com.jjconsulting.mobile.jjlib.util.Mask.MaskUtil;
import br.com.jjconsulting.mobile.jjlib.util.Mask.NumberMaskUtil;
import br.com.jjconsulting.mobile.jjlib.util.Mask.PhoneMaskUtil;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;

public class JJDataPainelView extends Fragment {

    private static int KEY_TAG_COMPONENT = 99;
    private static int KEY_TAG_FIELD_NAME = 98;

    private SyncFieldsConnection syncFieldsConnection;
    private JJOnUpdate jjOnUpdate;
    private JJTabContentView.OnFinish onFinish;

    private ImageCameraGallery imageCameraGallery;

    private LinearLayout mListEmptyLinearLayout;
    private LinearLayout mContainerLinearLayout;

    private TextView mMessageTextView;

    private FormElement mFormElement;
    private FieldManager fieldManager;
    private TPageState tPageState;
    private HashMap<String, Object> fieldForms;

    private DataTable mDataTable;
    private DataTable mRowSelect;

    private boolean showTitle;
    private boolean blockChange;
    private boolean isChangeInfo;
    private boolean reopenForm;
    private boolean isTab;
    private boolean isLoading;

    private View viewForm;
    private LinearLayout view;

    private DialogsCustom dialogCustom;

    private HashMap mTrigger;

    private Hashtable relationValues;

    private DialogsCustom.OnClickDialogMessage onErrorTrigger;

    private JJButtonView.OnClickAction onClickAction;

    private String fieldNameOpenIntent;

    public static JJDataPainelView renderFragment(Context context, String elementName, DataTable values, TPageState tPageState, boolean reopenForm) {
        JJDataPainelView fragment = new JJDataPainelView();
        fragment.setElement(context, elementName);
        fragment.setTPageState(tPageState);
        fragment.setRowSelect(values);
        fragment.setReopenForm(reopenForm);

        try {
             if (!JJSDK.isInitialize()) {
                throw new Exception(context.getString(R.string.sdk_error), new Throwable(""));
            }
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }

        return fragment;
    }

    public static JJDataPainelView renderFragment(FormElement element, DataTable values, TPageState tPageState, boolean reopenForm) {
        JJDataPainelView fragment = new JJDataPainelView();
        fragment.setElement(element);
        fragment.setTPageState(tPageState);
        fragment.setRowSelect(values);
        fragment.setReopenForm(reopenForm);

        try {
            if (!JJSDK.isInitialize()) {
                throw new Exception(fragment.getContext().getString(R.string.sdk_error), new Throwable(""));
            }
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
        return fragment;

    }

    public JJDataPainelView() {
        fieldForms = new LinkedHashMap<>();
        fieldManager = new FieldManager();
        imageCameraGallery = new ImageCameraGallery();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogCustom = new DialogsCustom(getContext());
        requestPermissions(
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                10);

        onErrorTrigger = ()-> {
            LogUser.log(Config.TAG, "onErrorTrigger");
        };

        onClickAction = (jjButtonView, action, position)-> {
            jjButtonView.executeAction(action, position, createRelationValues(action, getFormValue()));
        };

        createTitle();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setRetainInstance(true);
        getActivity().invalidateOptionsMenu();
        return renderView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == getActivity().RESULT_OK && requestCode == ImageCameraGallery.OPEN_CAMERA) {
            String path = null;

            Bitmap bitmap = null;

            if (data != null) {
                Uri uriImage = data.getData();
                if (uriImage == null) {
                    try {
                        uriImage = imageCameraGallery.getOutPutfileUri();
                        path = imageCameraGallery.getRealPathFromURI(uriImage, getActivity());
                        bitmap = BitmapFactory.decodeFile(path);
                    } catch (Exception e) {
                        LogUser.log(Config.TAG, e.toString());
                    }

                } else {
                    try {
                        path = imageCameraGallery.getRealPathFromURI(uriImage, getActivity());
                        bitmap = BitmapFactory.decodeFile(path);

                    } catch (Exception e) {
                        LogUser.log(Config.TAG, e.toString());
                    }
                }
            } else {
                Uri uriImage = imageCameraGallery.getOutPutfileUri();
                try {
                    path = imageCameraGallery.getRealPathFromURI(uriImage, getActivity());
                    bitmap = BitmapFactory.decodeFile(path);
                } catch (Exception e) {
                    LogUser.log(Config.TAG, e.toString());
                }
            }

            if (bitmap != null) {
                bitmap = imageCameraGallery.imageOrientationValidator(bitmap, path);
                ImageView image = (ImageView) fieldForms.get(fieldNameOpenIntent);
                image.setImageBitmap(bitmap);
            }
        }  else if (resultCode == getActivity().RESULT_OK && requestCode == ImageCameraGallery.OPEN_FILE) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                JJPickerFileView jjPickerFileView = (JJPickerFileView) fieldForms.get(fieldNameOpenIntent);
                jjPickerFileView.onActivityResult(result);
            }
        }  else if (resultCode == getActivity().RESULT_OK) {
            try {
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                String result = data.getExtras().getString(JJBarcodeScannerActivity.DATA_KEY_BARCODE);
                String fieldName = data.getExtras().getString(JJBarcodeScannerActivity.DATA_KEY_FIELD_NAME);
                setValueBarcode(fieldName, result);
            }catch (Exception ex){
                LogUser.log(Config.TAG, ex.toString());

            }
        }
    }

    private void bind(){
        if(isLoading || (getRowSelect() != null && tPageState != TPageState.INSERT)) {
            if (TLoadingDataType.fromInteger(mFormElement.getMode()) == TLoadingDataType.OFFLINE) {
                Factory factory = new Factory(getContext());
                ArrayList<DataTable> list = factory.getDataTable(mFormElement,  factory.convertDataTableInHastable(getRowSelect(), mFormElement), null, false,0,0,0);
                if(list.size() > 0){
                    mDataTable = list.get(0);
                    createForms(false);
                }else {
                   showErrorRegister();
                }
            } else {
                syncFieldsConnection = new SyncFieldsConnection(getActivity(), new SyncFieldsConnection.ConnectionListener() {
                    @Override
                    public void onSucess(String response, int typeConnection, InputStreamReader reader) {

                        dialogCustom.showDialogLoading(false);
                        Gson gson = new Gson();
                        Factory factory = new Factory(getContext());

                        try {
                            if (getFilter() == null) {
                                Type type = new TypeToken<Hashtable>() {
                                }.getType();
                                Hashtable retField = gson.fromJson(response, type);
                                mDataTable = factory.convertResponseRetField(retField, mFormElement);
                            } else {
                                RetFields retFields = gson.fromJson(response, RetFields.class);
                                mDataTable = factory.convertResponseRetFields(retFields, mFormElement).get(0);
                            }

                            createForms(false);
                        } catch (Exception ex) {
                            LogUser.log(Config.TAG, ex.toString());
                        }
                    }

                    @Override
                    public void onError(int code, VolleyError volleyError, int typeConnection, String response) {
                        dialogCustom.showDialogLoading(false);
                        hideMessageError(true);
                        mMessageTextView.setText(getString(R.string.sync_connection_error));
                    }
                });

                dialogCustom.showDialogLoading(true);

                Factory factory = new Factory(getContext());

                if(getFilter() != null){
                    syncFieldsConnection.syncField(mFormElement.getName(), factory.convertDataTableInHastable(getRowSelect(), mFormElement), 1,1,1);
                } else {
                    syncFieldsConnection.getField(null, mFormElement);
                }
            }

        } else {
            mDataTable = getRowSelect();
            if (TLoadingDataType.fromInteger(mFormElement.getMode()) == TLoadingDataType.ONLINE) {
                triggerForm(null);
            } else {
                createForms(false);
            }
        }
    }


    private void reopenForm(){
        Toast.makeText(getContext(), getString(R.string.dialog_validation_sucess), Toast.LENGTH_SHORT).show();
        Intent reopenForm = JJDataPainelActivity.newIntent(getContext(), getElement().getName(), getTPageState(),relationValues, isShowTitle(),  isReopenForm(), onFinish);
        startActivity(reopenForm);
        getActivity().finish();
    }

    protected View renderView() {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.jj_item_data_painel_view, null);

        mContainerLinearLayout = layout.findViewById(R.id.content_linear_layout);
        mListEmptyLinearLayout = layout.findViewById(R.id.list_empty_text_view);
        mMessageTextView = mListEmptyLinearLayout.findViewById(R.id.message_text_view);

        view = new LinearLayout(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setLayoutParams(lp);

        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setLayoutParams(lp);
        scrollView.addView(view);

        scrollView.post(()-> {
            bind();
        });

        mContainerLinearLayout.addView(scrollView);

        return layout;
    }

    /**
     * Cria Forma na tela
     */
    public void createForms(boolean isFirstTrigger){

        Hashtable formValues = getFormValue();
        if(formValues == null || formValues.size() == 0){
            Factory factory = new Factory(getContext());
            formValues = factory.convertDataTableInHastable(mDataTable, getElement());
        }

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view.removeAllViews();
        blockChange = true;
        EditText editText;
        CheckBox checkBox;

        int id = 0;

        if (getElement() == null) {
            throw new IllegalArgumentException(getString(R.string.error_element));
        }

        int index = 0;

        for(FormElementField item: getElement().getFormfields()){
            TFormComponent tFormComponent = TFormComponent.fromInteger(item.getComponent());

            boolean enable = isEnableView(item, formValues, isFirstTrigger);
            boolean visible = isVisibleView(item, formValues, isFirstTrigger);

            switch (tFormComponent){
                default:
                    JJButtonView jjButtonView = null;

                    if(tFormComponent == TFormComponent.TEXTAREA){
                        jjButtonView = new JJButtonView(getContext(),  item.getActions() == null ? null:item.getActions().getAll(), true);
                    } else {
                        jjButtonView = new JJButtonView(getContext(), item.getActions() == null ? null:item.getActions().getAll());
                    }

                    if(tFormComponent == TFormComponent.QRCODE ){
                        if(enable) {
                            CustomAction formAction = new CustomAction();
                            formAction.setDefaultOption(true);
                            formAction.setIcon(TIcon.BARCODE);
                            jjButtonView.addActons(formAction);

                            jjButtonView.setOnClickAction(new JJButtonView.OnClickAction() {
                                @Override
                                public void onClickAction(JJButtonView jjButtonView, BasicAction action, int position) {
                                    openBarcode(jjButtonView.getFieldName());
                                }
                            });
                            jjButtonView.setFieldName(item.getFieldname());
                        }

                    } else {
                        jjButtonView.setOnClickAction(onClickAction);
                    }

                    viewForm = jjButtonView.renderView();
                    viewForm.clearFocus();

                    editText = viewForm.findViewById(R.id.jj_button_edit_text);

                    ((TextInputLayout)viewForm.findViewById(R.id.jj_button_text_input_layout)).setHint(item.getLabel());
                    editText.setTag(item.getComponent() + "|" + item.getFieldname());

                    switch (tFormComponent){
                        case CNPJ_CPF:
                        case CNPJ:
                        case CPF:
                        case CEP:
                        case HOUR:
                        case DATE:
                        case DATETIME:
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            editText.addTextChangedListener(MaskUtil.insert(editText, tFormComponent));
                            break;
                        case TEL:
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            editText.addTextChangedListener(PhoneMaskUtil.insert(editText));
                            break;
                        case NUMBER:
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            if(item.getNumberOfDecimalPlaces() > 0){
                                editText.addTextChangedListener(NumberMaskUtil.decimal(editText, item.getNumberOfDecimalPlaces()));
                            }
                            break;
                        case CURRENCY:
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            editText.addTextChangedListener(CurrencyMaskUtil.monetario(editText));
                            break;
                        case TEXTAREA:
                            if(item.getSize() != null && item.getSize() > 0){
                                int size = item.getSize();
                                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(size)});
                            }
                            break;
                    }

                    jjButtonView.setEnable(enable);

                    setValueText(tFormComponent, editText, index, item.getFieldname(), item.getAutonum(), item.getNumberOfDecimalPlaces());
                    addFormField(item.getFieldname(), editText);

                    if(item.isAutoPostBack()){
                        addListenerAutoPostBack(tFormComponent, editText);
                    } else {
                        addListener(tFormComponent, editText);
                    }

                    if(!visible){
                        viewForm.setVisibility(View.GONE);
                    }

                    break;
                case PASSWORD:
                    viewForm = inflater.inflate(R.layout.jj_item_password_input_text_view, view, false);
                    editText = viewForm.findViewById(R.id.password_edit_text);
                    if(item.getSize() != null && item.getSize() > 0){
                        if(item.getSize() != null && item.getSize() > 0){
                            editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(item.getSize())});
                        }
                    }

                    editText.setTag(item.getComponent() + "|" + item.getFieldname());

                    ((TextInputLayout)viewForm).setHint(item.getLabel());


                    editText.setEnabled(enable);
                    if(mDataTable != null && mDataTable.getDataItens().get(index).getValue() != null){
                        editText.setText(mDataTable.getDataItens().get(index).getValue().toString());
                    }

                    setValueText(tFormComponent, editText, index, item.getFieldname(), item.getAutonum(), item.getNumberOfDecimalPlaces());
                    addFormField(item.getFieldname(), editText);
                    if(item.isAutoPostBack()){
                        addListenerAutoPostBack(tFormComponent, editText);
                    } else {
                        addListener(tFormComponent, editText);
                    }

                    if(!visible){
                        viewForm.setVisibility(View.GONE);
                    }
                    break;
                case UPLOAD:
                    JJPickerFileView jjPickerFileView = new JJPickerFileView(getContext(), new JJPickerFileView.OnOpenIntent() {
                        @Override
                        public void onOpen(String filedName) {
                            fieldNameOpenIntent = filedName;
                        }
                    });

                    jjPickerFileView.setFieldName(item.getFieldname());

                    viewForm = jjPickerFileView.renderView();

                    addFormField(item.getFieldname(), jjPickerFileView);
                    editText = viewForm.findViewById(R.id.jj_button_edit_text);
                    ((TextInputLayout)viewForm.findViewById(R.id.jj_button_text_input_layout)).setHint(item.getLabel());
                    editText.setTag(item.getComponent() + "|" + item.getFieldname());
                    break;
                case PHOTO:
                    viewForm = inflater.inflate(R.layout.jj_item_photo, view, false);
                    TextView titlePhoto = (viewForm.findViewById(R.id.title_text_view));
                    titlePhoto.setText(item.getLabel());

                    ImageView photoImageView = viewForm.findViewById(R.id.photo_image_view);
                    photoImageView.setTag(item.getComponent() + "|" + item.getFieldname());


                    addFormField(item.getFieldname(), photoImageView);
                    setValueImage(photoImageView, item.getFieldname());

                    if (enable) {
                        photoImageView.setAlpha(1.0f);
                        photoImageView.setOnClickListener(view -> {
                            openCamera(item.getFieldname());
                        });
                    } else {
                        photoImageView.setAlpha(0.5f);
                    }

                    if(!visible) {
                        viewForm.setVisibility(View.GONE);
                    }

                    break;
                case RADIOBUTTON:
                case CHECKBOX:
                    viewForm = inflater.inflate(R.layout.jj_item_check_box, view, false);
                    checkBox = viewForm.findViewById(R.id.check_box);
                    checkBox.setTag(item.getComponent() + "|" + item.getFieldname());
                    TextView title = (viewForm.findViewById(R.id.title_text_view));
                    title.setText(item.getLabel());


                    checkBox.setEnabled(enable);
                    setValueCheckBox(checkBox, index, item.getFieldname());
                    addFormField(item.getFieldname(), checkBox);
                    if(item.isAutoPostBack()){
                        addListenerAutoPostBack(tFormComponent, checkBox);
                    } else {
                        addListener(tFormComponent, checkBox);
                    }

                    if(!visible){
                        title.setVisibility(View.GONE);
                        checkBox.setVisibility(View.GONE);
                        viewForm.setVisibility(View.GONE);
                    }
                    break;
                case LOCATION:
                    JJLocationView jjLocationView =  new JJLocationView(getActivity());
                    addFormField(item.getFieldname(), jjLocationView);
                    addListenerLocation(jjLocationView, enable);
                    viewForm = null;
                    break;
                case COMBOBOX:
                case SEARCH:
                    JJSpinnerSearch jjSpinnerSearch = new JJSpinnerSearch(getContext(), item.getLabel(), item.getFieldname(), String.valueOf(item.getComponent()));

                    try{
                        if( getFormValue() != null){
                            jjSpinnerSearch.setValueTrigger(getFormValue().get(item.getFieldname()).toString());
                        }
                    } catch (Exception ex){
                        LogUser.log(Config.TAG, ex.toString());
                    }

                    if(tFormComponent == TFormComponent.SEARCH){
                        jjSpinnerSearch.setSearchView(true);
                    }

                    viewForm = jjSpinnerSearch.renderView(mDataTable == null ? null:mDataTable.getDataItens().get(index),
                            (String) getTriggerInfo(item.getFieldname(), "Value"), item.getDataItem(), (List<LinkedTreeMap>) getTriggerInfo(item.getFieldname(), "DataItems"));

                    jjSpinnerSearch.setEnable(enable);
                    jjSpinnerSearch.getSpinner().setTag(item.getComponent() + "|" + item.getFieldname());
                    addFormField(item.getFieldname(), jjSpinnerSearch);

                    if(item.isAutoPostBack()){
                        addListenerAutoPostBack(tFormComponent, jjSpinnerSearch.getSpinner());
                    } else {
                        addListener(tFormComponent, jjSpinnerSearch.getSpinner());
                    }

                    if(!visible){
                        viewForm.setVisibility(View.GONE);
                    }
                    break;
            }

            id++;

            if(viewForm != null){
                viewForm.setTag(item.getLabel());
                viewForm.setPadding(50,0,50,0);
                view.addView(viewForm);


                view.setPadding(0,50,0,30);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        blockChange = false;
                    }
                }, 1000);
            }

            index++;

        }

    }

    public void addListenerLocation(JJLocationView view, boolean enable){

        if(enable && (tPageState == TPageState.INSERT || tPageState == TPageState.UPDATE)){
            view.getLocation(new JJLocationView.OnChangeLocation() {
                @Override
                public void onChange() {
                    isChangeInfo = true;
                }
            });
        }

    }

    public void addListener(TFormComponent tFormComponent, View view){

        if(TLoadingDataType.fromInteger(getElement().getMode()) == TLoadingDataType.ONLINE) {
            switch (tFormComponent) {
                case RADIOBUTTON:
                case CHECKBOX:
                    ((CheckBox) view).setOnCheckedChangeListener(((buttonView, isChecked) ->{
                        if (!blockChange) {
                            isChangeInfo = true;
                        }
                    }));
                    break;

                case COMBOBOX:
                case SEARCH:

                    ((Spinner) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            if (!blockChange) {
                                isChangeInfo = true;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });
                    break;
                default:
                    ((EditText) view).addTextChangedListener(new TextWatcher() {

                        public void afterTextChanged(Editable s) {}

                        public void beforeTextChanged(CharSequence s, int start,
                                                      int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {
                            if (!blockChange) {
                                isChangeInfo = true;
                            }
                        }
                    });

                    break;
            }
        }
    }

    public void addListenerAutoPostBack(TFormComponent tFormComponent, View view){

        if(TLoadingDataType.fromInteger(getElement().getMode()) == TLoadingDataType.ONLINE) {
            switch (tFormComponent) {
                case RADIOBUTTON:
                case CHECKBOX:
                    ((CheckBox) view).setOnCheckedChangeListener(((buttonView, isChecked) ->{
                        if (!blockChange) {
                            isChangeInfo = true;
                            triggerForm( getTag(buttonView,KEY_TAG_FIELD_NAME ));
                        }
                    }));
                    break;
                case COMBOBOX:
                case SEARCH:
                    ((Spinner) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            if (!blockChange) {
                                isChangeInfo = true;
                                triggerForm( getTag((view) ,KEY_TAG_FIELD_NAME ));
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }

                    });
                    break;
                default:
                    (view).setOnFocusChangeListener((v, hasFocus)->{
                        if (!blockChange) {
                            if (!hasFocus) {
                                triggerForm(getTag(view,KEY_TAG_FIELD_NAME ));
                            }
                        }
                    });
                    ((EditText) view).addTextChangedListener(new TextWatcher() {

                        public void afterTextChanged(Editable s) {}

                        public void beforeTextChanged(CharSequence s, int start,
                                                      int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (!blockChange) {
                                isChangeInfo = true;
                            }
                        }
                    });
                    break;
            }
        }
    }

    public Hashtable getFormValue(){
        Hashtable values = new Hashtable();

        int index= 0;
        for (HashMap.Entry<String, Object> entry : fieldForms.entrySet()) {
            TFormComponent tFormComponent;

            try{
                tFormComponent = TFormComponent.fromInteger(Integer.parseInt(getTag((View)entry.getValue(), KEY_TAG_COMPONENT)));
            } catch (Exception ex){
                try{
                    tFormComponent = TFormComponent.fromInteger(Integer.parseInt((getTag(((JJSpinnerSearch)entry.getValue()).getSpinner(), KEY_TAG_COMPONENT))));
                }catch (Exception e){
                    tFormComponent = TFormComponent.LOCATION;
                }
            }

            switch (tFormComponent){
                default:
                    String text;
                    switch (tFormComponent) {
                        case CNPJ_CPF:
                        case CPF:
                        case CNPJ:
                            text  = ((EditText) entry.getValue()).getText().toString().replaceAll("[^0-9]*", "");
                            break;
                        default:
                            text = ((EditText) entry.getValue()).getText().toString();
                            break;
                    }

                    if(!TextUtils.isNullOrEmpty(text)){
                        values.put(entry.getKey(), text);
                    }
                    break;
                case PHOTO:
                    ImageView imageView = (((ImageView)entry.getValue()));
                    Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    values.put(entry.getKey(), imageCameraGallery.convertBase64(bitmap));
                    break;
                case RADIOBUTTON:
                case CHECKBOX:
                    values.put(entry.getKey(), (((CheckBox) entry.getValue()).isChecked()));
                    break;
                case LOCATION:
                    JJLocationView jjLocationView = ((JJLocationView) entry.getValue());
                    values.put(entry.getKey(), jjLocationView.getLatlong() == null ? "":jjLocationView.getLatlong());
                    break;
                case COMBOBOX:
                case SEARCH:
                    try {
                        String value = ((JJSpinnerSearch) entry.getValue()).getValue();
                        values.put(entry.getKey(), value);
                    } catch (Exception ex){
                        LogUser.log(Config.TAG, ex.toString());
                    }
                    break;
                case CURRENCY:
                    text = ((EditText) entry.getValue()).getText().toString();
                    text = text.replace(" ", "");
                    text = text.replace(".", "");
                    text = text.replace(",",".");


                    if(!TextUtils.isNullOrEmpty(text)){
                        values.put(entry.getKey(), Float.parseFloat(text));
                    }

                    break;
                case NUMBER:
                    text = ((EditText) entry.getValue()).getText().toString();
                    text = text.replace(" ", "");
                    if(!TextUtils.isNullOrEmpty(text)) {

                        try {
                            switch (getElement().getFormFields().get(index).getDatatype()) {
                                case INT:
                                    values.put(entry.getKey(), Integer.parseInt(text));
                                    break;
                                case FLOAT:
                                    values.put(entry.getKey(), Float.parseFloat(text));
                                    break;

                            }
                        } catch (Exception ex) {
                            LogUser.log(Config.TAG, ex.toString());
                        }
                    }
            }

            index++;
        }


        if(relationValues != null){
            for (Object key : relationValues.keySet()) {
                values.put(key, relationValues.get(key));
            }
        }

        return values;

    }

    public void triggerForm(String objname){
        SyncFieldsConnection syncFieldsConnection = new SyncFieldsConnection(getContext(), new SyncFieldsConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader) {
                try {

                    dialogCustom.showDialogLoading(false);

                    Object object = null;
                    if(objname != null){
                        object = mTrigger.get(objname.toLowerCase());
                    }

                    mTrigger = new Gson().fromJson(response, HashMap.class);

                    if(object != null){
                        mTrigger.put(objname.toLowerCase(), object);
                    }

                    createForms(objname == null);
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, ex.toString());
                    dialogCustom.showDialogMessage(getString(R.string.sync_connection_error), DialogsCustom.DIALOG_TYPE_ERROR, onErrorTrigger);
                }
            }

            @Override
            public void onError(int code, VolleyError volleyError, int typeConnection, String response) {
                try {
                    dialogCustom.showDialogLoading(false);
                    if (code == Connection.NO_CONNECTION) {
                        dialogCustom.showDialogMessage(getString(R.string.sync_connection_error), DialogsCustom.DIALOG_TYPE_ERROR, onErrorTrigger);
                    } else {
                        try {
                            Gson gson = new Gson();
                            ValidationLetter[] retProcess = gson.fromJson(response, ValidationLetter[].class);
                            showErros(retProcess);

                        } catch (Exception ex) {
                            dialogCustom.showDialogMessage(getString(R.string.sync_connection_error), DialogsCustom.DIALOG_TYPE_ERROR, onErrorTrigger);
                        }
                    }
                }catch (Exception ex){
                    LogUser.log(Config.TAG, ex.toString());
                }
            }
        });

        dialogCustom.showDialogLoading(true);
        syncFieldsConnection.triggerForm(getElement().getName(), objname, getFormValue(), getTPageState());
    }

    public void addItens(){
        Hashtable errors = validateFields(getFormValue(), getTPageState());
        if (errors.size() == 0) {

            SyncFieldsConnection syncFieldsConnection = new SyncFieldsConnection(getContext(), new SyncFieldsConnection.ConnectionListener() {
                @Override
                public void onSucess(String response, int typeConnection, InputStreamReader reader) {
                    try {

                        dialogCustom.showDialogLoading(false);

                        Gson gson = new Gson();
                        List<ValidationLetter> validationLetter = gson.fromJson(response, new TypeToken<List<ValidationLetter>>() {}.getType());

                        if (TLoadingDataType.fromInteger(getElement().getMode()) == TLoadingDataType.OFFLINE) {
                            Factory factory = new Factory(getContext());
                            if(validationLetter != null && validationLetter.size() > 0){
                                factory.insert(getElement(), getFormValue(), validationLetter.get(0).getData());
                            } else {
                                factory.insert(getElement(), getFormValue(), null);
                            }
                        }

                        try {
                            //MESSAGE - implementação LNG - TB_DESPACHO-ITEM
                            //ACTION_MESSAGE: 1 - Sair da tela, 0 - Continuar fluxo normal
                            if (validationLetter != null && validationLetter.size() > 0) {

                                if (validationLetter.get(0).getData().containsKey("message")) {
                                    dialogCustom.showDialogMessage(validationLetter.get(0).getData().get("message").toString(), DialogsCustom.DIALOG_TYPE_SUCESS, new DialogsCustom.OnClickDialogMessage() {
                                        @Override
                                        public void onClick() {

                                            if (validationLetter.get(0).getData().containsKey("action_message")) {
                                                if (validationLetter.get(0).getData().get("action_message").equals("0")) {
                                                    finishAddItens(validationLetter, reopenForm);
                                                } else  if (validationLetter.get(0).getData().get("action_message").equals("1")) { getActivity().finish();
                                                    if(onFinish != null){
                                                        onFinish.finish(false);
                                                    } else {
                                                        getActivity().finish();
                                                    }
                                                } else if (validationLetter.get(0).getData().get("action_message").equals("2")) {
                                                    if(onFinish != null){
                                                        onFinish.finish(true);
                                                    } else {
                                                        finishAddItens(validationLetter, true);
                                                    }
                                                }
                                            } else {
                                                finishAddItens(validationLetter, reopenForm);
                                            }
                                        }
                                    });

                                    return;
                                }
                            }
                        }catch (Exception ex){
                        }

                        finishAddItens(validationLetter, reopenForm);


                    } catch (Exception ex) {
                        LogUser.log(Config.TAG, ex.toString());
                        dialogCustom.showDialogMessage(getString(R.string.sync_connection_error), DialogsCustom.DIALOG_TYPE_ERROR, null);
                    }
                }

                @Override
                public void onError(int code, VolleyError volleyError, int typeConnection, String response) {
                    dialogCustom.showDialogLoading(false);

                    if(code == Connection.NO_CONNECTION){
                        dialogCustom.showDialogMessage(getString(R.string.sync_connection_error), DialogsCustom.DIALOG_TYPE_ERROR, null);
                    } else {
                        try {
                            Gson gson = new Gson();
                            ValidationLetter[] retProcess = gson.fromJson(response, ValidationLetter[].class);
                            showErros(retProcess);

                        }catch (Exception ex){
                            dialogCustom.showDialogMessage(getString(R.string.sync_connection_error), DialogsCustom.DIALOG_TYPE_ERROR, null);
                        }
                    }
                }
            });

            dialogCustom.showDialogLoading(true);
            syncFieldsConnection.insertField(getElement().getName(), getFormValue());

        } else {
            showErros(errors);
        }
    }

    public void finishAddItens(List<ValidationLetter> validationLetter, boolean reopen){
        if(reopen){
            reopenForm();
        } else {

            if(getElement().getRelations().size() > 0){
                RetFields retFields = new RetFields();
                ArrayList<HashMap<String, Object>> fields = new ArrayList<>();
                Hashtable formValue = getFormValue();
                HashMap<String, Object> hash = new HashMap<>();

                for(FormElementField item: getElement().getFormfields()) {

                    if(validationLetter.get(0).getData().containsKey(item.getFieldname())){
                        hash.put(item.getFieldname(), validationLetter.get(0).getData().get(item.getFieldname()));
                    } else {
                        hash.put(item.getFieldname(), formValue.get(item.getFieldname()));
                    }
                }

                fields.add(hash);
                retFields.setFields(fields);

                Factory factory = new Factory(getContext());

                ArrayList<DataTable> dataTables = factory.convertResponseRetFields(retFields, mFormElement);
                startActivity(JJTabActivity.newIntent(getContext(), dataTables.get(0), mFormElement.getName(), TPageState.UPDATE, true));

                getActivity().finish();

            } else {
                blockChange = true;
                getActivity().finish();

                updateScreen();
            }

        }
    }

    public void updateScreen(){
        if(jjOnUpdate != null){
            jjOnUpdate.onUpdate();
        }
    }

    private boolean isVisibleView(FormElementField item, Hashtable formValues, boolean isFirstTrigger){
        boolean visible;
        Object valueTrigger =  getTriggerInfo(item.getFieldname(), "Visible");

        if(valueTrigger == null || isFirstTrigger){
            visible = fieldManager.isVisible(item, tPageState, formValues);
        } else {
            visible = (boolean) valueTrigger;
        }

        return visible;
    }

    private boolean isEnableView(FormElementField item, Hashtable formValues, boolean isFirstTrigger){
        boolean enable;

        Object valueTrigger =  getTriggerInfo(item.getFieldname(), "Enable");

        if(valueTrigger == null || isFirstTrigger == true){
            enable = fieldManager.isEnable(item, tPageState, formValues);
        } else {
            enable = (boolean) valueTrigger;
        }

        if(getTPageState() == TPageState.UPDATE){
            if(item.getIspk()){
                enable = false;
            }
        }

        return enable;
    }

    private Object getTriggerInfo(String fieldName, String key){
        if(mTrigger != null){
            LinkedTreeMap infoField = (LinkedTreeMap) mTrigger.get(fieldName.toLowerCase());

            Object info = null;
            if(infoField != null){
                info =  infoField.get(key);
            }

            return info;
        } else {
            return null;
        }
    }

    private void addFormField(String name, Object view){
        fieldForms.put(name,view);
    }

    private void setValueCheckBox(CheckBox checkBox, int index, String fieldName){
        if(getTriggerInfo(fieldName, "Value") == null){
            if(mDataTable != null && mDataTable.getDataItens().get(index).getValue() != null){
                checkBox.setChecked((boolean)mDataTable.getDataItens().get(index).getValue());
            }
        } else {
            checkBox.setChecked((boolean) getTriggerInfo(fieldName, "Value"));
        }
    }

    private void setValueImage(ImageView imageView, String fieldName){
        if(getTriggerInfo(fieldName, "Value") != null){
            try{
                imageView.setImageBitmap(imageCameraGallery.decodeBase64Bitmap(getTriggerInfo(fieldName, "Value").toString()));
            }catch (Exception ex){
                LogUser.log(Config.TAG, ex.toString());
            }
        }
    }

    private String createValueText(TFormComponent tFormComponent, String fieldName, int position, int decimalSize){
        String valueText = "";

        Object valueTrigger;
        valueTrigger = getTriggerInfo(fieldName, "Value");


        String valueForm = null;

        try{
            if( getFormValue() != null){
                valueForm =  getFormValue().get(fieldName).toString();
            }
        } catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }

        if(valueForm != null){
            valueText = valueForm;
        } else {
            if(valueTrigger != null) {
                valueText = (valueTrigger.toString());
            }else if(mDataTable != null && mDataTable.getDataItens().get(position).getValue() != null) {
                valueText = mDataTable.getDataItens().get(position).getValue().toString();

                switch (tFormComponent) {
                    case NUMBER:
                    case CURRENCY:
                        valueText = NumberMaskUtil.adjustDecimalSize(decimalSize, valueText);
                        break;
                }
            }
        }

        return valueText;
    }

    private void setValueText(TFormComponent tFormComponent, EditText editText, int position, String fieldName, boolean isAutoNum, int decimalSize){
        String value = createValueText(tFormComponent, fieldName, position, decimalSize);

        if(value != null) {
            editText.setText(value);
        } else if(isAutoNum && TLoadingDataType.fromInteger(getElement().getMode()) == TLoadingDataType.OFFLINE){
            Factory factory = new Factory(getContext());
            int first = factory.getFirstRowAutoNum(getElement().getName(), fieldName);

            if(first  >= 0){
                editText.setText(String.valueOf(-1));
            } else {
                editText.setText(String.valueOf(first-1));
            }
        } else if(isAutoNum) {
            editText.setText(String.valueOf(-1));
        }

    }


    public String getTag(View view, int TAG){
        String[] data = view.getTag().toString().split("\\|");

        if(TAG == KEY_TAG_COMPONENT){
            return data[0];
        } else {
            return data[1];
        }
    }


    private void openCamera(String nameImage) {
        fieldNameOpenIntent = nameImage;
        getActivity().startActivityForResult(imageCameraGallery.getPhotoIntent(getActivity().getString(R.string.title_intent_photo),nameImage, getActivity()), ImageCameraGallery.OPEN_CAMERA);
    }

    public Hashtable validateFields(Hashtable values, TPageState pageState)
    {
        if (values == null)
            throw new IllegalArgumentException(getString(R.string.error_values));

        Hashtable errors = new Hashtable();
        String val = "";
        String err = "";
        FieldManager fieldManager = new FieldManager();

        for (FormElementField item:getElement().getFormfields()) {

            if (fieldManager.isVisible(item, pageState, values) &&
                    fieldManager.isVisible(item, pageState, values)) {
                if (values.containsKey(item.getFieldname()) && values.get(item.getFieldname()) != null )
                    val = values.get(item.getFieldname()).toString();
                else
                    val = "";

                err = fieldManager.validateField(item, item.getLabel(), val);
                if (!TextUtils.isNullOrEmpty(err))
                    errors.put(item.getLabel(), err);
            }
        }
        return errors;
    }

    private void createTitle(){
        try {

            if(!isTab){
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

                if (actionBar != null) {
                    if (showTitle) {
                        actionBar.setTitle(mFormElement.getTitle());
                    } else {
                        actionBar.setTitle("");
                    }
                }
            }

        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }
    }


    public void hideMessageError(boolean isHide){
        mContainerLinearLayout.setVisibility(isHide ? View.GONE: View.VISIBLE);
        mListEmptyLinearLayout.setVisibility(isHide ? View.VISIBLE: View.GONE);
    }

    public void showErrorRegister(){
        hideMessageError(true);
    }

    public void showErros(Hashtable<String, String> errors){

        ValidationInfo validationInfo = new ValidationInfo();

        for (HashMap.Entry<String, String> entry : errors.entrySet()) {
            validationInfo.addError( entry.getValue());
        }

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        JJValidationDialogFragment jjValidationDialogFragment =
                JJValidationDialogFragment.newInstance(validationInfo, validationInfo.getMessages().isEmpty());

        jjValidationDialogFragment.setOnFinishValidation(success -> {
            if(success){
                getActivity().finish();
            }
        });

        jjValidationDialogFragment.show(getActivity().getFragmentManager(), "");
        fm.executePendingTransactions();
    }


    public boolean showErros(ValidationLetter[] retProcess){

        ValidationInfo validationInfo = new ValidationInfo();

        for(int ind = 0; ind < retProcess.length; ind++){

            if(retProcess[ind].getValidationList() != null && retProcess[ind].getValidationList().size() > 0){
                Set<Map.Entry<String, String>> entrySet = retProcess[ind].getValidationList().entrySet();
                for (Map.Entry entry : entrySet) {
                    validationInfo.addError(entry.getValue().toString());
                }
            } else {
                validationInfo.addError(retProcess[ind].getMessage());

            }
        }

        if(validationInfo.getMessages() != null &&  validationInfo.getMessages().size() > 0){
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            JJValidationDialogFragment jjValidationDialogFragment =
                    JJValidationDialogFragment.newInstance(validationInfo, validationInfo.getMessages().isEmpty());

            jjValidationDialogFragment.setOnFinishValidation(success -> {
                if(success){
                    getActivity().finish();
                }
            });

            jjValidationDialogFragment.show(getActivity().getFragmentManager(), "");
            fm.executePendingTransactions();

            return true;
        } else {
            return false;
        }
    }

    public Hashtable createRelationValues(BasicAction action, Hashtable formValues) {
        Hashtable filters = new Hashtable<>();

        if (action instanceof InternalAction) {
            InternalAction internalAction = (InternalAction)action;
            for(FormActionRelationField item : internalAction.getElementRedirect().getRelationFields()){
                filters.put(item.getRedirectField(), formValues.get(item.internalField));
            }
        }

        return filters;
    }

    private Hashtable getFilter(){
        if(relationValues != null){
            return  relationValues;
        } else {
            return new Hashtable<>();
        }
    }

    public void setValueBarcode(String fieldName, String value){
        EditText editText = (EditText) fieldForms.get(fieldName);
        editText.setFocusableInTouchMode(false);
        editText.setFocusable(false);

        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.setFocusableInTouchMode(true);
                editText.setFocusable(true);
            }
        });


        editText.setText(value);
    }

    public void openBarcode(String fieldName){
        Intent it = new Intent(getContext(), JJBarcodeScannerActivity.class);
        it.putExtra(JJBarcodeScannerActivity.DATA_KEY_FIELD_NAME, fieldName);
        startActivityForResult(it, JJBarcodeScannerActivity.BARCODE_REQUEST);
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

    public TPageState getTPageState() {
        return tPageState;
    }

    public void setTPageState(int value) {
        this.tPageState = TPageState.fromInteger(value);
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

    public DataTable getRowSelect() {
        return mRowSelect;
    }

    public void setRowSelect(DataTable mRowSelect) {
        this.mRowSelect = mRowSelect;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public Hashtable getRelationValues() {
        return relationValues;
    }

    public void setRelationValues(Hashtable relationValues) {
        this.relationValues = relationValues;
    }

    public boolean isChangeInfo() {
        return isChangeInfo;
    }

    public void setChangeInfo(boolean changeInfo) {
        isChangeInfo = changeInfo;
    }

    public JJOnUpdate getJjOnUpdate() {
        return jjOnUpdate;
    }

    public void setJjOnUpdate(JJOnUpdate jjOnUpdate) {
        this.jjOnUpdate = jjOnUpdate;
    }

    public boolean isReopenForm() {
        return reopenForm;
    }

    public void setReopenForm(boolean reopenForm) {
        this.reopenForm = reopenForm;
    }

    public boolean isTab() {
        return isTab;
    }

    public void setTab(boolean tab) {
        isTab = tab;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public JJTabContentView.OnFinish getOnFinish() {
        return onFinish;
    }

    public void setOnFinish(JJTabContentView.OnFinish onFinish) {
        this.onFinish = onFinish;
    }
}
