package br.com.jjconsulting.mobile.dansales.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.util.UserInfo;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;

public class BaseFragment extends Fragment {

    public DialogsCustom dialogsDefault;
    public UserInfo user;
    public Gson gson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogsDefault = new DialogsCustom(getActivity());

        user = new UserInfo();
        user.getUserInfo(getActivity());

        gson = new Gson();
    }
    public void showMessageError(String message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogsDefault.showDialogMessage(message, dialogsDefault.DIALOG_TYPE_ERROR, null);
            }
        });
    }


}
