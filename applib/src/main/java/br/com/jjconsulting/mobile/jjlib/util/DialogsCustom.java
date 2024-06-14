package br.com.jjconsulting.mobile.jjlib.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import br.com.jjconsulting.mobile.jjlib.R;

public class DialogsCustom {

    public static final int DIALOG_TYPE_ERROR = 0;
    public static final int DIALOG_TYPE_WARNING = 1;
    public static final int DIALOG_TYPE_SUCESS = 2;
    public static final int DIALOG_TYPE_QUESTION = 3;

    private Context context;

    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogDefault;


    // Dialog com botões positivo/negativo
    public void showDialogQuestion(String message, int type, OnClickDialogQuestion onClickDialogQuestion) {
        if(((Activity)context).isFinishing()){
            return;
        }

        Dialog mQuestionDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mQuestionDialog.setCancelable(false);
        mQuestionDialog.setContentView(R.layout.dialog_two_buttons);

        TextView mQuestionDialogTwoButtonTextView = mQuestionDialog.findViewById(R.id.tv_dialog_message);
        mQuestionDialogTwoButtonTextView.setText(message);
        setIconDialog(mQuestionDialog, type, context);

        TextView mOkDialogQuestionButton = mQuestionDialog.findViewById(R.id.ok_dialog_two_buttons);
        mOkDialogQuestionButton.setOnClickListener(view -> {
            mQuestionDialog.dismiss();
            if (onClickDialogQuestion != null) {
                onClickDialogQuestion.onClickPositive();
            }
        });

        TextView mCancelDialogQuestionButton = mQuestionDialog.findViewById(R.id.cancel_dialog_two_buttons);
        mCancelDialogQuestionButton.setOnClickListener(view -> {
            mQuestionDialog.dismiss();
            if (onClickDialogQuestion != null) {
                onClickDialogQuestion.onClickNegative();
            }
        });

        mQuestionDialog.show();
    }


    // Dialog para mensagens de aviso ao usuário
    public void showDialogMessage(String message, int type, OnClickDialogMessage onClickDialogMessage) {

        if(((Activity)context).isFinishing()){
            return;
        }

        Activity activity = (Activity) context;

        Dialog mMessageDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mMessageDialog.setCancelable(false);
        mMessageDialog.setContentView(R.layout.dialog_error);

        TextView mMessageDialogTextView = mMessageDialog.findViewById(R.id.tv_dialog_message);
        mMessageDialogTextView.setText(message);

        setIconDialog(mMessageDialog, type, context);

        TextView tvOk = mMessageDialog.findViewById(R.id.ok_button);
        tvOk.setOnClickListener(view -> {
            try {
                mMessageDialog.dismiss();
                if (onClickDialogMessage != null) {
                    onClickDialogMessage.onClick();
                }
            }catch (Exception ex){
                LogUser.log(ex.toString());
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageDialog.show();
            }
        });

    }

    // Dialog para mensagens de aviso ao usuário
    public void showDialogLeftMessage(String message, int type, OnClickDialogMessage onClickDialogMessage) {

        if(((Activity)context).isFinishing()){
            return;
        }

        Activity activity = (Activity) context;

        Dialog mMessageDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mMessageDialog.setCancelable(false);
        mMessageDialog.setContentView(R.layout.dialog_message_left);

        TextView mMessageDialogTextView = mMessageDialog.findViewById(R.id.tv_dialog_message);
        mMessageDialogTextView.setText(message);

        setIconDialog(mMessageDialog, type, context);

        TextView tvOk = mMessageDialog.findViewById(R.id.ok_button);
        tvOk.setOnClickListener(view -> {
            mMessageDialog.dismiss();
            if (onClickDialogMessage != null) {
                onClickDialogMessage.onClick();
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageDialog.show();
            }
        });

    }


    // Dialog para mensagens de aviso ao usuário
    public void showDialogQuestionNote(String message, int type, OnClickDialogNote onClickDialogNote) {

        if(((Activity)context).isFinishing()){
            return;
        }

        Activity activity = (Activity) context;

        Dialog mMessageDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mMessageDialog.setCancelable(false);
        mMessageDialog.setContentView(R.layout.dialog_note_question);

        TextView mMessageDialogTextView = mMessageDialog.findViewById(R.id.tv_dialog_message);
        mMessageDialogTextView.setText(message);

        setIconDialog(mMessageDialog, type, context);

        TextInputEditText textInputEditText = mMessageDialog.findViewById(R.id.obs_text_view);

        Button yesButton = mMessageDialog.findViewById(R.id.yes_button);
        yesButton.setOnClickListener(view -> {
            mMessageDialog.dismiss();
            if (onClickDialogNote != null) {
                onClickDialogNote.onClickPositive(textInputEditText.getText().toString());
            }
        });

        Button noButton = mMessageDialog.findViewById(R.id.no_button);
        noButton.setOnClickListener(view -> {
            mMessageDialog.dismiss();
            if (onClickDialogNote != null) {
                onClickDialogNote.onClickNegative();
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageDialog.show();
            }
        });

    }

    // Dialog com botões positivo/negativo
    public void showDialogMessageLeftQuestion(String message, int type, OnClickDialogQuestion onClickDialogQuestion) {
        try {
            if(((Activity)context).isFinishing()){
                return;
            }

            Dialog mQuestionDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
            mQuestionDialog.setCancelable(false);
            mQuestionDialog.setContentView(R.layout.dialog_two_msg_left_buttons);

            TextView mQuestionDialogTwoButtonTextView = mQuestionDialog.findViewById(R.id.tv_dialog_message);
            mQuestionDialogTwoButtonTextView.setText(message);
            setIconDialog(mQuestionDialog, type, context);

            TextView mOkDialogQuestionButton = mQuestionDialog.findViewById(R.id.ok_dialog_two_buttons);
            mOkDialogQuestionButton.setOnClickListener(view -> {
                mQuestionDialog.dismiss();
                if (onClickDialogQuestion != null) {
                    onClickDialogQuestion.onClickPositive();
                }
            });

            TextView mCancelDialogQuestionButton = mQuestionDialog.findViewById(R.id.cancel_dialog_two_buttons);
            mCancelDialogQuestionButton.setOnClickListener(view -> {
                mQuestionDialog.dismiss();
                if (onClickDialogQuestion != null) {
                    onClickDialogQuestion.onClickNegative();
                }
            });

            mQuestionDialog.show();

            ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }




    public DialogsCustom(Context context) {
        this.context = context;
    }

    /**
     * @param dialog
     * @param type   0 - Error, 1 - Warning, 2 - Sucess
     */
    public void setIconDialog(Dialog dialog, int type, Context context) {

        ImageView icon;

        switch (type) {
            case 0:
                icon = dialog.findViewById(R.id.icon_image_view);
                icon.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_ATOP);
                icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cancel_black_24dp));
                break;
            case 1:
                icon = dialog.findViewById(R.id.icon_image_view);
                icon.setColorFilter(context.getResources().getColor(R.color.alertCollor), android.graphics.PorterDuff.Mode.SRC_ATOP);
                icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_error_black_24dp));
                break;
            case 2:
                icon = dialog.findViewById(R.id.icon_image_view);
                icon.setColorFilter(context.getResources().getColor(R.color.sucessCollor), android.graphics.PorterDuff.Mode.SRC_ATOP);
                icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_circle_black_24dp));
                break;
            case 3:
                icon = dialog.findViewById(R.id.icon_image_view);
                icon.setColorFilter(context.getResources().getColor(R.color.questionCollor), android.graphics.PorterDuff.Mode.SRC_ATOP);
               // icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_info_black_24dp));
                break;
        }

    }

    public void showDialogLoading(boolean value){
        if(((Activity)context).isFinishing()){
            return;
        }
        Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressDialog == null){
                    progressDialog = ProgressDialog.show(context, "",
                            context.getString(R.string.loading), true);
                }

                if(value){
                    progressDialog.show();
                } else {
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void showDialogSending(boolean value){
        if(((Activity)context).isFinishing()){
            return;
        }
        Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressDialog == null){
                    progressDialog = ProgressDialog.show(context, "",
                            context.getString(R.string.sending), true);
                }

                if(value){
                    progressDialog.show();
                } else {
                    progressDialog.dismiss();
                }
            }
        });
    }


    public void showDialogSaveLoading(boolean value){

        if(progressDialog == null){
            progressDialog = ProgressDialog.show(context, "",
                    context.getString(R.string.loading_save), true);
        }

        if(value){
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }

    }

    public void showDialogProgress(Context context, String value) {
        progressDialogDefault = new ProgressDialog(context);
        progressDialogDefault.setCancelable(false);
        progressDialogDefault.setMessage(value);
        progressDialogDefault.show();
    }

    public void dissmissDialogProgress(){
        if(progressDialogDefault != null)
            progressDialogDefault.dismiss();
    }

    public interface OnClickDialogMessage {
        void onClick();
    }

    public interface OnClickDialogNote {
        void onClickPositive(String note);
        void onClickNegative();
    }

    public interface OnClickDialogQuestion {
        void onClickPositive();
        void onClickNegative();
    }
}
