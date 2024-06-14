package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.BasicAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.CustomAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.InternalAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.DataItemValue;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Factory;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormActionRelationField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElementDataItem;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElementField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TFilter;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TFormComponent;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TPageState;
import br.com.jjconsulting.mobile.jjlib.model.SpinnerDataItem;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.ImageCameraGallery;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.Mask.CurrencyMaskUtil;
import br.com.jjconsulting.mobile.jjlib.util.Mask.MaskUtil;
import br.com.jjconsulting.mobile.jjlib.util.Mask.NumberMaskUtil;
import br.com.jjconsulting.mobile.jjlib.util.Mask.PhoneMaskUtil;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class JJFilterDialogFragment extends DialogFragment {

    private static final String ARG_ELEMENT_NAME = "array_string_element_name";
    private static final String ARG_FILTER= "array_string_filter";
    private static int KEY_TAG_COMPONENT = 99;

    private String fieldNameOpenIntent;

    private ImageCameraGallery imageCameraGallery;

    private Button mDialogFragPedOkButton;

    private FormElement mFormElement;

    private OnFinishValidation onFinishValidation;

    private String elementName;

    private Hashtable mFilter;

    private HashMap<String, Object> fieldForms;

    private SpinnerDataItem spinnerDataItemFirst;

    private FieldManager fieldManager;

    private JJButtonView.OnClickAction onClickAction;

    public JJFilterDialogFragment() { }

    public static JJFilterDialogFragment newInstance(String elementName, Hashtable filter) {
        JJFilterDialogFragment fragment = new JJFilterDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ELEMENT_NAME, elementName);

        if(filter != null)
            args.putSerializable(ARG_FILTER, filter);


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogValidationStyle);
        fieldForms = new LinkedHashMap<>();
        fieldManager = new FieldManager();
        imageCameraGallery = new ImageCameraGallery();

        onClickAction = (jjButtonView, action, position)-> {
            jjButtonView.executeAction(action, position, createRelationValues(action, getFormValue()));
        };

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            try {
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                String result = data.getExtras().getString(JJBarcodeScannerActivity.DATA_KEY_BARCODE);
                String fieldName = data.getExtras().getString(JJBarcodeScannerActivity.DATA_KEY_FIELD_NAME);
                setValueBarcode(fieldName, result);
            } catch (Exception ex) {
                LogUser.log(Config.TAG, ex.toString());

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.jj_dialog_fragment_filter, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        elementName = getArguments().getString(ARG_ELEMENT_NAME);
        setElement(getActivity(), elementName);

        try {
            mFilter = (Hashtable) getArguments().getSerializable(ARG_FILTER);
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }

        LinearLayout linearLayout = rootView.findViewById(R.id.container_linear_layout);
        linearLayout.addView(renderView());

        mDialogFragPedOkButton = rootView.findViewById(R.id.dialog_frag_ped_ok_button);

        mDialogFragPedOkButton.setOnClickListener(view -> {
            dismiss();

            if(onFinishValidation != null){
                onFinishValidation.onFinish(getFormValue());
            }
        });

        return rootView;
    }

    /**
     * Cria Forma na tela
     */
    public View createForms(){

        LayoutInflater inflater =  getActivity().getLayoutInflater();

        LinearLayout view = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setLayoutParams(lp);
        View viewForm = null;
        EditText editText;
        CheckBox checkBox;
        Spinner spinner;

        int id = 0;

        if (getElement() == null) {
            throw new IllegalArgumentException(getString(R.string.error_element));
        }

        int index = 0;

        for(FormElementField item: getElement().getFormfields()){

            boolean visible = fieldManager.isVisible(item, TPageState.FILTER, null);

            if(visible){
                viewForm = null;

                TFormComponent tFormComponent = TFormComponent.fromInteger(item.getComponent());

                if(item.getElementFilter() != null) {

                    if (item.getElementFilter().getType() == TFilter.RANGE) {
                        LinearLayout viewRange = (LinearLayout) inflater.inflate(R.layout.jj_item_input_text_range_view, view, false);

                        TextInputLayout textInputLayoutEnd = viewRange.findViewById(R.id.end_text_input_layout);
                        EditText editTextEnd = viewRange.findViewById(R.id.end_edit_text);
                        editTextEnd.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editTextEnd.addTextChangedListener(MaskUtil.insert(editTextEnd, tFormComponent));

                        TextInputLayout textInputLayoutStart = viewRange.findViewById(R.id.text_input_start_layout);
                        EditText editTexStart = viewRange.findViewById(R.id.start_edit_text);
                        editTexStart.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editTexStart.addTextChangedListener(MaskUtil.insert(editTexStart, tFormComponent));
                        // viewFormEnd.setId((index * 10) + 1);

                        LinearLayout viewLabel = new LinearLayout(getActivity());
                        LinearLayout.LayoutParams lpLabel = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                        lpLabel.setMargins(0, 40, 0, 0);

                        TextView label = viewRange.findViewById(R.id.description_text_view);
                        label.setText(item.getLabel());
                        label.setLayoutParams(lpLabel);

                        viewForm = viewRange;

                        textInputLayoutStart.setHint("");
                        textInputLayoutEnd.setHint("");

                        editTexStart.setHint(getString(R.string.start));
                        editTextEnd.setHint(getString(R.string.end));
                        addFormField(item.getFieldname(), viewRange);

                        id++;

                        viewForm.setTag(item.getLabel());
                        view.addView(viewForm);

                        index++;
                    }else if (item.getElementFilter().getType() != TFilter.NONE) {
                        switch (tFormComponent) {
                            default:
                                JJButtonView jjButtonView = null;

                                if (tFormComponent == TFormComponent.TEXTAREA) {
                                    jjButtonView = new JJButtonView(getActivity(), item.getActions() == null ? null : item.getActions().getAll(), true);
                                } else {
                                    jjButtonView = new JJButtonView(getActivity(), item.getActions() == null ? null : item.getActions().getAll());
                                }


                                if (tFormComponent == TFormComponent.QRCODE) {
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

                                } else {
                                    jjButtonView.setOnClickAction(onClickAction);
                                }

                                viewForm = jjButtonView.renderView();

                                if (tFormComponent != TFormComponent.QRCODE) {
                                    jjButtonView.setEnable(true);
                                }

                                viewForm.clearFocus();
                                editText = viewForm.findViewById(R.id.jj_button_edit_text);


                                ((TextInputLayout) viewForm.findViewById(R.id.jj_button_text_input_layout)).setHint(item.getLabel());
                                editText.setTag(item.getComponent() + "|" + item.getFieldname());

                                switch (tFormComponent) {
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
                                        if (item.getNumberOfDecimalPlaces() > 0) {
                                            editText.addTextChangedListener(NumberMaskUtil.decimal(editText, item.getNumberOfDecimalPlaces()));
                                        }
                                        break;
                                    case CURRENCY:
                                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                        editText.addTextChangedListener(CurrencyMaskUtil.monetario(editText));
                                        break;
                                    case TEXTAREA:
                                        if (item.getSize() != null && item.getSize() > 0) {
                                            int size = item.getSize();
                                            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(size)});
                                        }
                                        break;
                                }


                                addFormField(item.getFieldname(), editText);

                                break;
                            case PASSWORD:
                                viewForm = inflater.inflate(R.layout.jj_item_password_input_text_view, view, false);
                                editText = viewForm.findViewById(R.id.password_edit_text);
                                if (item.getSize() != null && item.getSize() > 0) {
                                    if (item.getSize() != null && item.getSize() > 0) {
                                        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(item.getSize())});
                                    }
                                }

                                editText.setTag(item.getComponent() + "|" + item.getFieldname());

                                ((TextInputLayout) viewForm).setHint(item.getLabel());

                                addFormField(item.getFieldname(), editText);

                                break;
                            case UPLOAD:
                                JJPickerFileView jjPickerFileView = new JJPickerFileView(getActivity(), new JJPickerFileView.OnOpenIntent() {
                                    @Override
                                    public void onOpen(String filedName) {
                                        fieldNameOpenIntent = filedName;
                                    }
                                });

                                jjPickerFileView.setFieldName(item.getFieldname());

                                viewForm = jjPickerFileView.renderView();

                                addFormField(item.getFieldname(), jjPickerFileView);
                                editText = viewForm.findViewById(R.id.jj_button_edit_text);
                                ((TextInputLayout) viewForm.findViewById(R.id.jj_button_text_input_layout)).setHint(item.getLabel());
                                editText.setTag(item.getComponent() + "|" + item.getFieldname());
                                break;
                            case PHOTO:
                                viewForm = inflater.inflate(R.layout.jj_item_photo, view, false);
                                TextView titlePhoto = (viewForm.findViewById(R.id.title_text_view));
                                titlePhoto.setText(item.getLabel());

                                ImageView photoImageView = viewForm.findViewById(R.id.photo_image_view);
                                photoImageView.setTag(item.getComponent() + "|" + item.getFieldname());


                                addFormField(item.getFieldname(), photoImageView);

                                photoImageView.setAlpha(1.0f);
                                photoImageView.setOnClickListener(viewPhoto -> {
                                    openCamera(item.getFieldname());
                                });

                                viewForm.setVisibility(View.GONE);

                                break;
                            case RADIOBUTTON:
                            case CHECKBOX:
                                viewForm = inflater.inflate(R.layout.jj_item_check_box, view, false);
                                checkBox = viewForm.findViewById(R.id.check_box);
                                checkBox.setTag(item.getComponent() + "|" + item.getFieldname());
                                TextView title = (viewForm.findViewById(R.id.title_text_view));
                                title.setText(item.getLabel());

                                addFormField(item.getFieldname(), checkBox);

                                title.setVisibility(View.GONE);
                                checkBox.setVisibility(View.GONE);
                                viewForm.setVisibility(View.GONE);
                                break;
                            case LOCATION:
                                JJLocationView jjLocationView = new JJLocationView(getActivity());
                                addFormField(item.getFieldname(), jjLocationView);
                                viewForm = null;
                                break;
                            case COMBOBOX:
                            case SEARCH:
                                JJSpinnerSearch jjSpinnerSearch = new JJSpinnerSearch(getActivity(), item.getLabel(), item.getFieldname(), String.valueOf(item.getComponent()));

                                try {
                                    if (getFormValue() != null) {
                                        jjSpinnerSearch.setValueTrigger(getFormValue().get(item.getFieldname()).toString());
                                    }
                                } catch (Exception ex) {
                                    LogUser.log(Config.TAG, ex.toString());
                                }

                                if (tFormComponent == TFormComponent.SEARCH) {
                                    jjSpinnerSearch.setSearchView(true);
                                }

                                viewForm = jjSpinnerSearch.renderView(null,
                                        "", item.getDataItem(), null);

                                jjSpinnerSearch.getSpinner().setTag(item.getComponent() + "|" + item.getFieldname());
                                addFormField(item.getFieldname(), jjSpinnerSearch);


                                break;
                        }
                    }


                    id++;

                    if (viewForm != null) {
                        viewForm.setTag(item.getLabel());
                        viewForm.setPadding(50, 0, 50, 0);
                        view.addView(viewForm);


                        view.setPadding(0, 50, 0, 30);
                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            }
                        }, 1000);
                    }

                    index++;

                }
            }



        }

        return view;
    }

    @SuppressLint("ResourceType")
    protected View renderView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
      /*  LayoutInflater inflater =  getActivity().getLayoutInflater();

        LinearLayout view = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setLayoutParams(lp);
        View viewForm = null;
        EditText editText;
        CheckBox checkBox;
        Spinner spinner;

        int id = 0;

        if (getElement() == null) {
            throw new IllegalArgumentException(getString(R.string.error_element));
        }

        int index = 0;

        for(FormElementField item: getElement().getFormfields()) {
            TFormComponent tFormComponent = TFormComponent.fromInteger(item.getComponent());

            if(item.getElementFilter() != null) {

                boolean visible = fieldManager.isVisible(item, TPageState.FILTER, null);
                if(visible) {
                    if (item.getElementFilter().getType() == TFilter.RANGE) {
                        LinearLayout viewRange = (LinearLayout) inflater.inflate(R.layout.jj_item_input_text_range_view, view, false);

                        TextInputLayout textInputLayoutEnd = viewRange.findViewById(R.id.end_text_input_layout);
                        EditText editTextEnd = viewRange.findViewById(R.id.end_edit_text);
                        editTextEnd.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editTextEnd.addTextChangedListener(MaskUtil.insert(editTextEnd, tFormComponent));

                        TextInputLayout textInputLayoutStart = viewRange.findViewById(R.id.text_input_start_layout);
                        EditText editTexStart = viewRange.findViewById(R.id.start_edit_text);
                        editTexStart.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editTexStart.addTextChangedListener(MaskUtil.insert(editTexStart, tFormComponent));
                        // viewFormEnd.setId((index * 10) + 1);

                        LinearLayout viewLabel = new LinearLayout(getActivity());
                        LinearLayout.LayoutParams lpLabel = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                        lpLabel.setMargins(0, 40, 0, 0);

                        TextView label = viewRange.findViewById(R.id.description_text_view);
                        label.setText(item.getLabel());
                        label.setLayoutParams(lpLabel);

                        viewForm = viewRange;

                        textInputLayoutStart.setHint("");
                        textInputLayoutEnd.setHint("");

                        editTexStart.setHint(getString(R.string.start));
                        editTextEnd.setHint(getString(R.string.end));
                        addFormField(item.getFieldname(), viewRange);

                        id++;

                        viewForm.setTag(item.getLabel());
                        view.addView(viewForm);

                        index++;
                    } else if (item.getElementFilter().getType() != TFilter.NONE) {
                        switch (tFormComponent) {
                            default:
                                viewForm = inflater.inflate(R.layout.jj_item_input_text_view, view, false);
                                ((TextInputLayout) viewForm).setHint(item.getLabel());
                                editText = viewForm.findViewById(R.id.edit_text);
                                editText.setTag(item.getComponent());
                                switch (tFormComponent) {
                                    case CNPJ_CPF:
                                    case CPF:
                                    case CNPJ:
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
                                        break;
                                    case CURRENCY:
                                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                        editText.addTextChangedListener(CurrencyMaskUtil.monetario(editText));
                                        break;
                                }

                                if (mFilter != null && mFilter.get(item.getFieldname()) != null) {
                                    editText.setText(mFilter.get(item.getFieldname()).toString());
                                }

                                addFormField(item.getFieldname(), editText);
                                break;
                            case PASSWORD:
                                viewForm = inflater.inflate(R.layout.jj_item_password_input_text_view, view, false);
                                editText = viewForm.findViewById(R.id.password_edit_text);
                                ((TextInputLayout) viewForm).setHint(item.getLabel());
                                editText.setTag(item.getComponent());

                                if (mFilter != null && mFilter.get(item.getFieldname()) != null) {
                                    editText.setText(mFilter.get(item.getFieldname()).toString());
                                }

                                addFormField(item.getFieldname(), editText);
                                break;
                            case RADIOBUTTON:
                            case CHECKBOX:
                                viewForm = inflater.inflate(R.layout.jj_item_check_box, view, false);
                                checkBox = viewForm.findViewById(R.id.check_box);
                                checkBox.setTag(item.getComponent());
                                checkBox.requestFocus();
                                ((TextView) viewForm.findViewById(R.id.title_text_view)).setText(item.getLabel());
                                addFormField(item.getFieldname(), checkBox);
                                break;

                            case SEARCH:
                            case COMBOBOX:
                                /*viewForm = inflater.inflate(R.layout.jj_item_combo_box, view, false);
                                spinner = viewForm.findViewById(R.id.spinner);
                                spinner.setTag(item.getComponent());
                                ((TextView) viewForm.findViewById(R.id.title_text_view)).setText(item.getLabel());
                                ArrayList<SpinnerDataItem> spinnerArray = setupSpinner(spinner, item.getDataItem());
                                setValeuSpinner(spinner, index, spinnerArray, item.getFieldname());
                                addFormField(item.getFieldname(), spinner);



                                JJSpinnerSearch jjSpinnerSearch = new JJSpinnerSearch(getActivity(), item.getLabel(), item.getFieldname(), String.valueOf(item.getComponent()));
                                jjSpinnerSearch.setEnable(true);
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

                                DataItem dataItem = null;
                                if (mFilter != null && mFilter.get(item.getFieldname()) != null) {
                                    dataItem = new DataItem();
                                    dataItem.setValue(mFilter.get(item.getFieldname()).toString());
                                }

                                viewForm = jjSpinnerSearch.renderView(dataItem,
                                        null, item.getDataItem(), null);

                                jjSpinnerSearch.getSpinner().setTag(item.getComponent() + "|" + item.getFieldname());



                                addFormField(item.getFieldname(), jjSpinnerSearch);



                                if(!visible){
                                    viewForm.setVisibility(View.GONE);
                                }
                                break;
                        }

                        id++;

                        viewForm.setTag(item.getLabel());
                        view.addView(viewForm);

                        index++;

                    }

                }
            }
        }*/

        ScrollView scrollView = new ScrollView(getActivity());
        scrollView.setLayoutParams(lp);
        scrollView.addView(createForms());

        return scrollView;
    }

    public String getTag(View view, int TAG){
        String[] data = view.getTag().toString().split("\\|");

        if(TAG == KEY_TAG_COMPONENT){
            return data[0];
        } else {
            return data[1];
        }
    }

    public Hashtable getFormValue(){
        Hashtable values = null;

        int index= 0;

        for (HashMap.Entry<String, Object> entry : fieldForms.entrySet()) {


            try {
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

                switch (tFormComponent) {
                    default:

                        String text;

                        EditText editText = (EditText) entry.getValue();
                        if (editText.getText().toString().length() > 0) {
                            if (values == null) {
                                values = new Hashtable();
                            }

                            switch (tFormComponent) {
                                case CNPJ_CPF:
                                case CPF:
                                case CNPJ:
                                    values.put(entry.getKey(), editText.getText().toString().replaceAll("[^0-9]*", ""));
                                    break;
                                default:
                                    values.put(entry.getKey(), editText.getText().toString());
                                    break;
                            }
                        }
                        break;
                    case RADIOBUTTON:
                    case CHECKBOX:
                        if (values == null) {
                            values = new Hashtable();
                        }
                        values.put(entry.getKey(), (((CheckBox) entry.getValue()).isChecked()));
                        break;
                    case COMBOBOX:
                    case SEARCH:
                        if (values == null) {
                            values = new Hashtable();
                        }
                        try {
                            String value = ((JJSpinnerSearch) entry.getValue()).getValue();
                            if(!TextUtils.isNullOrEmpty(value)){
                                values.put(entry.getKey(), value);
                            }

                        } catch (Exception ex){
                            LogUser.log(Config.TAG, ex.toString());
                        }
                        break;
                    case CURRENCY:
                        if (values == null) {
                            values = new Hashtable();
                        }
                        text = ((EditText) entry.getValue()).getText().toString();
                        text = text.replace(" ", "");
                        text = text.replace(".", "");
                        text = text.replace(",",".");


                        if(!TextUtils.isNullOrEmpty(text)){
                            values.put(entry.getKey(), Float.parseFloat(text));
                        }

                        break;
                    case NUMBER:
                        if (values == null) {
                            values = new Hashtable();
                        }
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

            }catch (Exception ex){
                LogUser.log(Config.TAG, ex.toString());
            }

            index++;
        }

        return values;

    }

    private ArrayList<SpinnerDataItem> setupSpinner(Spinner mSpinner, FormElementDataItem formElementDataItem) {

        ArrayList<SpinnerDataItem> spinnerArray = new ArrayList<>();

        spinnerDataItemFirst = new SpinnerDataItem();

        switch (formElementDataItem.getFirstOption()){
            case ALL:
                spinnerDataItemFirst.setName(getActivity().getString(R.string.all));
                spinnerDataItemFirst.setValue(String.valueOf(formElementDataItem.getFirstOption().getValue()));
                break;
            case CHOOSE:
                spinnerDataItemFirst.setName(getActivity().getString(R.string.choose));
                spinnerDataItemFirst.setValue(String.valueOf(formElementDataItem.getFirstOption().getValue()));
                break;
            case NONE:
                spinnerDataItemFirst.setName(getActivity().getString(R.string.none));
                spinnerDataItemFirst.setValue(String.valueOf(formElementDataItem.getFirstOption().getValue()));
                break;
        }

        if(formElementDataItem == null || formElementDataItem.getItens() == null){
            SpinnerDataItem spinnerDataItemTeste = new SpinnerDataItem();
            spinnerDataItemTeste.setName("teste");
            spinnerDataItemTeste.setValue("1");
            spinnerArray.add(spinnerDataItemTeste);
        } else {
            for (DataItemValue item : formElementDataItem.getItens()) {
                SpinnerDataItem spinnerDataItem = new SpinnerDataItem();
                spinnerDataItem.setName(item.description);
                spinnerDataItem.setValue(item.getId());
                spinnerArray.add(spinnerDataItem);

            }
        }

        SpinnerArrayAdapter spinnerArrayAdapter;

        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(spinnerArray.toArray(),
                spinnerDataItemFirst.getName());

        if (spinnerArray.size() > 0) {

            spinnerArrayAdapter = new SpinnerArrayAdapter<SpinnerDataItem>(
                    getActivity(), objects, true) {
                @Override
                public String getItemDescription(SpinnerDataItem item) {
                    return item.getName();
                }
            };

            mSpinner.setAdapter(spinnerArrayAdapter);
        }

        return spinnerArray;
    }

    private void openCamera(String nameImage) {
        fieldNameOpenIntent = nameImage;
        getActivity().startActivityForResult(imageCameraGallery.getPhotoIntent(getActivity().getString(R.string.title_intent_photo),nameImage,  getActivity()), ImageCameraGallery.OPEN_CAMERA);
    }

    public void openBarcode(String fieldName){
        Intent it = new Intent(getActivity(), JJBarcodeScannerActivity.class);
        it.putExtra(JJBarcodeScannerActivity.DATA_KEY_FIELD_NAME, fieldName);
        startActivityForResult(it, JJBarcodeScannerActivity.BARCODE_REQUEST);
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

    private void setValeuSpinner(Spinner spinner, int position, ArrayList<SpinnerDataItem> spinnerArray, String fieldName){
        if(mFilter != null && mFilter.get(fieldName) != null){
            for(int ind = 0; ind < spinnerArray.size(); ind++){
                if(spinnerArray.get(ind).getValue().equals(mFilter.get(fieldName).toString())){
                    spinner.setSelection(ind + 1);
                }
            }
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

    private void addFormField(String name, Object view){
        fieldForms.put(name,view);
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

    public interface OnFinishValidation{
        void onFinish(Hashtable filter);
    }
}
