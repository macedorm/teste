package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.dao.entity.DataItemValue;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Factory;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElementDataItem;
import br.com.jjconsulting.mobile.jjlib.model.DataItem;
import br.com.jjconsulting.mobile.jjlib.model.SpinnerDataItem;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class JJSpinnerSearch {

    private Context mContext;

    private Factory factory;

    private SpinnerDataItem spinnerDataItemFirst;

    private List<LinkedTreeMap> mItensTrigger;

    private FormElementDataItem mFormElementDataItem;

    private ArrayList<SpinnerDataItem> spinnerArray;

    private DataItem mItem;

    private Spinner mSpinner;

    private View mLayout;

    private LinearLayout mContainerClick;

    private String valueTrigger;
    private String fieldName;
    private String title;
    private String tag;

    private boolean isSearchView;
    private boolean isEnable;

    public JJSpinnerSearch(Context context, String title, String fieldName, String tag){
        this.mContext = context;
        this.title = title;
        this.tag = tag;
        this.fieldName = fieldName;
    }


    public View renderView(DataItem item, String valueTrigger, FormElementDataItem formElementDataItem, List<LinkedTreeMap> itensTrigger) {

        this.mItensTrigger = itensTrigger;
        this.mFormElementDataItem = formElementDataItem;
        this.mItem = item;
        this.valueTrigger = valueTrigger;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        mLayout = inflater.inflate(R.layout.jj_item_combo_box, null);

        mContainerClick = mLayout.findViewById(R.id.container_click);
        mContainerClick.setVisibility(View.GONE);

        mSpinner = mLayout.findViewById(R.id.spinner);
        mSpinner.setTag(tag);

        setEnable(isEnable);

        TextView titleCombo = (mLayout.findViewById(R.id.title_text_view));
        titleCombo.setText(title);

        spinnerArray = setupSpinner();
        setValueSpinner(spinnerArray);

        if(isSearchView){
            mSpinner.setEnabled(false);
            mContainerClick.setVisibility(View.VISIBLE);
            mContainerClick.setOnClickListener((v)-> {
                if(isEnable){

                    android.app.FragmentManager fm = ((Activity)mContext).getFragmentManager();

                    JJSearchViewDialogFragment jjSearchViewDialogFragment = JJSearchViewDialogFragment.newInstance(mFormElementDataItem.showImageLegend, spinnerArray);

                    jjSearchViewDialogFragment.setOnFinishValidation((position) -> {
                        jjSearchViewDialogFragment.dismiss();
                        mSpinner.setSelection(position + 1);
                    });

                    jjSearchViewDialogFragment.show(((Activity)mContext).getFragmentManager(), "");
                    fm.executePendingTransactions();
                }
            });
        } else {
            setEnable(isEnable);
            mContainerClick.setVisibility(View.GONE);
        }

        return mLayout;
    }

    private void setValueSpinner(ArrayList<SpinnerDataItem> spinnerArray){
        String value = null;

        if(valueTrigger != null){
            value = valueTrigger;
        } else {
            if (mItem != null && mItem.getValue() != null) {
                value = mItem.getValue().toString();
            }
        }

        if(value != null){
            for(int ind = 0; ind < spinnerArray.size(); ind++){
                if(value.equals(spinnerArray.get(ind).getValue())){
                   if(isSearchView){
                       mSpinner.setSelection(ind + 1);
                   } else {
                       mSpinner.setSelection(ind);
                   }
                }
            }
        }
    }

    private ArrayList<SpinnerDataItem> setupSpinner() {

        ArrayList<SpinnerDataItem> spinnerArray = new ArrayList<>();

        spinnerDataItemFirst = new SpinnerDataItem();

        switch (mFormElementDataItem.getFirstOption()){
            case ALL:
                spinnerDataItemFirst.setName(mContext.getString(R.string.all));
                spinnerDataItemFirst.setValue(String.valueOf(mFormElementDataItem.getFirstOption().getValue()));
                break;
            case CHOOSE:
                spinnerDataItemFirst.setName(mContext.getString(R.string.choose));
                spinnerDataItemFirst.setValue(String.valueOf(mFormElementDataItem.getFirstOption().getValue()));
                break;
            case NONE:
                spinnerDataItemFirst.setName(mContext.getString(R.string.none));
                spinnerDataItemFirst.setValue(String.valueOf(mFormElementDataItem.getFirstOption().getValue()));
                break;
        }

        if(mFormElementDataItem.showImageLegend){
            spinnerDataItemFirst.setIcon(321);
            spinnerDataItemFirst.setColorIcon("#" + Integer.toHexString(Color.GRAY));
        }

        if(!isSearchView){
            spinnerArray.add(spinnerDataItemFirst);
        }

        if(mItensTrigger != null){
            for(LinkedTreeMap hashMap: mItensTrigger) {
                SpinnerDataItem spinnerDataItem = new SpinnerDataItem();
                spinnerDataItem.setName(hashMap.get("description").toString());
                spinnerDataItem.setValue(hashMap.get("id").toString());

                if(mFormElementDataItem.showImageLegend){
                    if(hashMap.get("icon").toString() != null){
                        spinnerDataItem.setIcon(Integer.parseInt(hashMap.get("icon").toString().replace(".0", "")));
                    }

                    if(hashMap.get("imagecolor").toString() != null){
                        spinnerDataItem.setColorIcon(hashMap.get("imagecolor").toString());
                    }
                }

                spinnerArray.add(spinnerDataItem);
            }
        } else {
            if (mFormElementDataItem != null && (mFormElementDataItem.getItens() == null || mFormElementDataItem.getItens().isEmpty())) {
                if(mFormElementDataItem.getCommand() != null){
                    factory = new Factory(mContext);

                    for(SpinnerDataItem item: factory.getDataCombo(mFormElementDataItem.getCommand().getSql())){
                        spinnerArray.add(item);
                    }

                } else {
                    spinnerArray.add(new SpinnerDataItem());
                }
            } else {
                for (DataItemValue item : mFormElementDataItem.getItens()) {
                    SpinnerDataItem spinnerDataItem = new SpinnerDataItem();
                    spinnerDataItem.setName(item.description);
                    spinnerDataItem.setValue(item.getId());

                    if(mFormElementDataItem.showImageLegend){
                        spinnerDataItem.setIcon(item.getIcon());
                        spinnerDataItem.setColorIcon(item.getImageColor());
                    }

                    spinnerArray.add(spinnerDataItem);
                }
            }
        }

        SpinnerArrayAdapter spinnerArrayAdapter;

        Object[] objects;

        if(isSearchView){
            objects =  SpinnerArrayAdapter.makeObjectsWithHint(spinnerArray.toArray(),
                    spinnerDataItemFirst.getName());
        } else {
            objects = spinnerArray.toArray();
        }

        if (spinnerArray.size() > 0) {
            spinnerArrayAdapter = new SpinnerArrayAdapter<SpinnerDataItem>(
                    mContext, objects, isSearchView) {
                @Override
                public String getItemDescription(SpinnerDataItem item) {
                    return item.getName();
                }
            };

            mSpinner.setAdapter(spinnerArrayAdapter);
        }

        return spinnerArray;
    }

    public boolean isSearchView() {
        return isSearchView;
    }

    public String getValue(){

        String text = ((SpinnerDataItem) mSpinner.getSelectedItem()).getValue();

        if(!TextUtils.isNullOrEmpty(text)){

            if(text.trim().equals("1")){
                switch (mFormElementDataItem.getFirstOption()){
                    case NONE:
                        text = "";
                        break;
                }
            }

        }



        return text;
    }

    public void setSearchView(boolean searchView) {
        isSearchView = searchView;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;

        if(mSpinner != null){
            mSpinner.setEnabled(isEnable);
        }
    }

    public void setVisible(boolean isVisible){
        mLayout.setVisibility(isVisible ? View.VISIBLE: View.GONE);
    }

    public String getValueTrigger() {
        return valueTrigger;
    }

    public void setValueTrigger(String valueTrigger) {
        this.valueTrigger = valueTrigger;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Spinner getSpinner() {
        return mSpinner;
    }

    public void setSpinner(Spinner spinner) {
        this.mSpinner = spinner;
    }

    public SpinnerDataItem getSpinnerDataItemFirst() {
        return spinnerDataItemFirst;
    }

    public void setSpinnerDataItemFirst(SpinnerDataItem spinnerDataItemFirst) {
        this.spinnerDataItemFirst = spinnerDataItemFirst;
    }

}

