package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.Date;

import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RotaGuiadaPickClienteActivity extends SingleFragmentActivity
        implements ClientesFragment.OnClienteClickListener {

    public static String KEY_RESULT_CLIENTE =  "key_result_cliente";

    public static Intent newIntent(Context packageContext, String date) {
        Intent it = new Intent(packageContext, RotaGuiadaPickClienteActivity.class);
        it.putExtra(KEY_DATA_PAR, date);
        return it;
    }

    @Override
    protected Fragment createFragment() {
        return ClientesFragment.newInstance(true);
    }

    @Override
    public void onClienteClick(Cliente cliente) {
        RotaGuiadaDao rotaGuiadaDao = new RotaGuiadaDao(this);

        Date date = null;
        try{
            date = FormatUtils.toDate(getData());
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }

        if(cliente.getStatusCredito() != Cliente.STATUS_CREDITO_BLACK){
            String userId = Current.getInstance(this).getUsuario().getCodigo();
            String unNeg = Current.getInstance(this).getUnidadeNegocio().getCodigo();
            if(!rotaGuiadaDao.hasClienteInRoute(userId, unNeg, cliente.getCodigo(), date)){
                Intent returnIntent = new Intent();
                returnIntent.putExtra(KEY_RESULT_CLIENTE,cliente);
                setResult(RESULT_OK,returnIntent);
                finish();
            } else {
                DialogsCustom dialogsDefault = new DialogsCustom(this);
                dialogsDefault.showDialogMessage(getString(R.string.has_rota_cliente), dialogsDefault.DIALOG_TYPE_WARNING, null);
            }
        } else {
            DialogsCustom dialogsDefault = new DialogsCustom(this);
            dialogsDefault.showDialogMessage(getString(R.string.cliente_not_available), dialogsDefault.DIALOG_TYPE_WARNING, null);
        }
    }
}
