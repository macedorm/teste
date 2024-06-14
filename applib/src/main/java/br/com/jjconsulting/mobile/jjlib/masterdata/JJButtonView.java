package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.BasicAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.InternalAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.UrlRedirectAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormActionRedirect;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TPageState;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class JJButtonView {

    private OnClickAction onClickAction;
    private Context mContext;
    private boolean enable;
    private int width;
    private List<BasicAction> actions;

    private FrameLayout containerTextInputLayout;
    private LinearLayout clickDefaultLinearLayout;
    private TextInputLayout textInputLayout;
    private String fieldName;
    private boolean isTextArea;


    public JJButtonView(Context context, List<BasicAction> formAction) {
        this(context, formAction, false);
    }

    public JJButtonView(Context context, List<BasicAction> formAction, boolean isTextArea)  {
        this.mContext = context;
        this.actions = formAction;

        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        width = size.x;
        this.isTextArea = isTextArea;
    }

    public View renderView() {

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View layout;
        if(isTextArea){
            layout = inflater.inflate(R.layout.jj_item_multiline_button_view, null);
        } else{
            layout = inflater.inflate(R.layout.jj_item_button_view, null);
        }

        textInputLayout = layout.findViewById(R.id.jj_button_text_input_layout);
        containerTextInputLayout = layout.findViewById(R.id.jj_container_input_relative_layout);
        clickDefaultLinearLayout = layout.findViewById(R.id.click_default_linear_layout);

        LinearLayout containerJJIcon = layout.findViewById(R.id.container_jj_icon_view);

        int iconSize = 0;
        boolean isGroup = false;

        if (actions != null) {

            List<BasicAction> actionsGroup = getActions(true);

            for (BasicAction item : actions) {
                //102
                if (item.isGroup()) {
                    actionsGroup.add(item);
                    isGroup = true;
                } else {
                    String color = String.format("#%06x", mContext.getResources().getColor(R.color.action_icon_menu) & 0xffffff);
                    JJIcon jjIcon = new JJIcon(mContext, item.getIcon(), color);
                    jjIcon.setOnClickListener(v->{
                        onClickAction.onClickAction(JJButtonView.this, item, 0);
                    });

                    containerJJIcon.addView(jjIcon.createJJIcon(40));
                    iconSize += jjIcon.getWidth();
                }
            }

            if (isGroup) {
                TIcon icon = (TIcon.values()[102]);
                JJIcon jjIcon = new JJIcon(mContext, icon, String.format("#%06x", mContext.getResources().getColor(R.color.action_icon_menu) & 0xffffff));
                jjIcon.setOnClickListener(v->{
                    createPopupMenu(v, 0);
                });

                containerJJIcon.addView(jjIcon.renderView());
                iconSize += jjIcon.getWidth();
            }
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((width - 50 ) - iconSize, LinearLayout.LayoutParams.WRAP_CONTENT);
        containerTextInputLayout.setLayoutParams(params);

        containerTextInputLayout.setVisibility(View.VISIBLE);
        clickDefaultLinearLayout.setOnClickListener((v)-> {
            if(actions != null) {
                for (BasicAction item : actions) {
                    if (item.isDefaultOption()) {
                        onClickAction.onClickAction(JJButtonView.this, item, 0);
                        break;
                    }
                }
            }
        });

        return layout;
    }

    private void createPopupMenu(View view, int position) {
        List<BasicAction> actionsGroup = getActions(true);
        if (actionsGroup.size() > 0) {
            PopupMenu popup = new PopupMenu(mContext, view, Gravity.END);
            int index = 0;
            for (BasicAction item: actionsGroup) {
                popup.getMenu().add(position, index, index, item.getName());
                index++;
            }

            popup.setOnMenuItemClickListener(item -> {
                onClickAction.onClickAction(this, actionsGroup.get(item.getItemId()), position);
                return false;
            });

            popup.show();
        }
    }

    public void executeAction(final BasicAction action, int position, Hashtable formValues){
        if(!TextUtils.isNullOrEmpty(action.getConfirmationMessage())){
            DialogsCustom dialogsCustom = new DialogsCustom(mContext);
            dialogsCustom.showDialogQuestion(action.getConfirmationMessage(), DialogsCustom.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                @Override
                public void onClickPositive() {
                    eventAction(action, position, formValues);
                }

                @Override
                public void onClickNegative() {

                }
            });
        } else {
            eventAction(action, position, formValues);
        }
    }

    private void eventAction(BasicAction action, int position, Hashtable formValues){
        if (action instanceof UrlRedirectAction){
            UrlRedirectAction urlAction = (UrlRedirectAction)action;
            if(urlAction.isUrlAsPopUp()){
                mContext.startActivity(JJWebViewActivity.newIntent(mContext, urlAction.getUrlRedirect(), formValues));
            } else {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(urlAction.getUrlRedirect()));
                mContext.startActivity(i);
            }
        } else if (action instanceof InternalAction){
            InternalAction internalAction = (InternalAction)action;
            FormActionRedirect redirect = internalAction.getElementRedirect();
            switch (redirect.getViewType()) {
                case VIEW:
                    mContext.startActivity(JJDataPainelActivity.newIntent(mContext, redirect.getElementNameRedirect(), TPageState.VIEW, formValues, true, false, null));
                    break;
                case LIST:
                    mContext.startActivity(JJFormActivity.newIntent(mContext, redirect.getElementNameRedirect(), TPageState.VIEW, formValues, true));
                    break;
            }
        }
    }


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean value) {
        enable = value;
        textInputLayout.getEditText().setEnabled(enable);
        clickDefaultLinearLayout.setVisibility(enable? View.GONE: View.VISIBLE);
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void addActons(BasicAction action){
        if(actions == null){
            actions =new ArrayList<>();
        }

        actions.add(action);
    }

    public OnClickAction getOnClickAction() {
        return onClickAction;
    }

    public void setOnClickAction(OnClickAction onClickAction) {
        this.onClickAction = onClickAction;
    }

    public interface OnClickAction{
        void onClickAction(JJButtonView jjButtonView, BasicAction action, int position);
    }

    //pan

    public List<BasicAction> getActions() {
        return actions;
    }

    public List<BasicAction> getActions(Boolean isGroup){
        List<BasicAction> list = new ArrayList<>();
        for (BasicAction action : actions) {
            if(action.isGroup() == isGroup)
                list.add(action);
        }
        return list;
    }




}

