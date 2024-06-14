package br.com.jjconsulting.mobile.dansales.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.widget.AppCompatEditText;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.MoneyTextWatcher;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class DialogsProductPrice {


    private Activity context;
    private AppCompatEditText mRespostaTextInputEditText;


    public void showDialog(String message, String hint, String preco, OnClickDialogMessage onClickDialogMessage) {
        try {

            Dialog mMessageDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
            mMessageDialog.setCancelable(false);
            mMessageDialog.setCanceledOnTouchOutside(true);

            mMessageDialog.setContentView(R.layout.dialog_edittext);

            TextView mMessageDialogTextView = mMessageDialog.findViewById(R.id.tv_dialog_message);
            mMessageDialogTextView.setText(message);

            mRespostaTextInputEditText = mMessageDialog.findViewById(R.id.price_edit_text);

            mRespostaTextInputEditText.setHint(hint);
            mRespostaTextInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            mRespostaTextInputEditText.addTextChangedListener(new MoneyTextWatcher(mRespostaTextInputEditText));

            if(TextUtils.isNullOrEmpty(preco)){
                mRespostaTextInputEditText.setText("0");
            } else {
                mRespostaTextInputEditText.setText(preco);
            }

            Button okButton = mMessageDialog.findViewById(R.id.ok_button);
            okButton.setOnClickListener((v)-> {
                onClickDialogMessage.onClick(mRespostaTextInputEditText.getText().toString());
                mMessageDialog.dismiss();
            });

            context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            mMessageDialog.show();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    public DialogsProductPrice(Activity context) {
        this.context = context;
    }

    public interface OnClickDialogMessage {
        void onClick(String values);
    }

}
