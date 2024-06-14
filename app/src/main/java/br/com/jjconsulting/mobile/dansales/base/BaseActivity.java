package br.com.jjconsulting.mobile.dansales.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.util.UserInfo;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;

public class BaseActivity extends AppCompatActivity {

    public DialogsCustom dialogsDefault;
    public Gson gson;
    public UserInfo userInfo;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogsDefault = new DialogsCustom(this);
        gson = new Gson();
        userInfo = new UserInfo();
    }

    public void showMessageError(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogsDefault.showDialogMessage(message, dialogsDefault.DIALOG_TYPE_ERROR, null);
            }
        });
    }

    public void createProgress() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);
        String msg = getString(R.string.aguarde);
        loadingDialog.setMessage(msg);
    }

    public void showDialog(){
        createProgress();
        loadingDialog.show();
    }

    public void dismissDialog(){
        if(loadingDialog != null)
            loadingDialog.dismiss();
    }
}
