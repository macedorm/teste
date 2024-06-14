package br.com.jjconsulting.mobile.dansales.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.util.Date;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaActionDao;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaTarefaDao;
import br.com.jjconsulting.mobile.dansales.model.MultiValues;
import br.com.jjconsulting.mobile.dansales.model.Pesquisa;
import br.com.jjconsulting.mobile.dansales.model.RotaGuiadaTaskType;
import br.com.jjconsulting.mobile.dansales.model.RotaOrigem;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.model.StatusPedido;
import br.com.jjconsulting.mobile.dansales.model.TActionRotaGuiada;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class RotaGuiadaUtils {

    public static final int STATUS_RG_NAO_INICIADO = 0;
    public static final int STATUS_RG_EM_ANDAMENTO = 1;
    public static final int STATUS_RG_FINALIZADO = 2;
    public static final int STATUS_RG_FORA_ROTA = 3;
    public static final int STATUS_RG_NAO_REALIZADO = 4;
    public static final int STATUS_RG_PAUSADO = 5;
    public static final int STATUS_RG_INCOMPLETO = 6;

    private static final int[] statuses = new int[] {
            RotaGuiadaUtils.STATUS_RG_NAO_INICIADO,
            RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO,
            RotaGuiadaUtils.STATUS_RG_FINALIZADO,
            RotaGuiadaUtils.STATUS_RG_FORA_ROTA,
            RotaGuiadaUtils.STATUS_RG_NAO_REALIZADO,
            RotaGuiadaUtils.STATUS_RG_INCOMPLETO,

    };

    private DialogsCustom dialogsDefault;

    private RotaGuiadaDao rotaGuiadaDao;
    private RotaGuiadaTarefaDao rotaGuiadaTarefaDao;
    private RotaGuiadaActionDao rotaGuiadaActionDao;

    public RotaGuiadaUtils(Context context){
        dialogsDefault = new DialogsCustom((Activity) context);
        rotaGuiadaDao = new RotaGuiadaDao(context);
        rotaGuiadaTarefaDao = new RotaGuiadaTarefaDao(context);
        rotaGuiadaActionDao = new RotaGuiadaActionDao(context);

    }

    public static int[] getStatuses() {
        return statuses;
    }

    public static int getStatusRGImageResourceId(boolean isRota, int status, boolean isUnrealized) {
        switch (status) {
            case RotaGuiadaUtils.STATUS_RG_NAO_INICIADO:
                if(isUnrealized){
                    return R.drawable.ic_rg_status_nao_realizado;
                } else {
                    return R.drawable.ic_rg_status_nao_iniciado;
                }
            case RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO:
                if(isUnrealized){
                    return R.drawable.ic_rg_status_incompleto;
                } else {
                    return R.drawable.ic_rg_status_em_andamento;
                }
            case RotaGuiadaUtils.STATUS_RG_INCOMPLETO:
                return R.drawable.ic_rg_status_incompleto;
            case RotaGuiadaUtils.STATUS_RG_FORA_ROTA:
                return R.drawable.ic_rg_status_cliente_fora_da_rota;
            case RotaGuiadaUtils.STATUS_RG_NAO_REALIZADO:
                return R.drawable.ic_rg_status_nao_realizado;
            case RotaGuiadaUtils.STATUS_RG_FINALIZADO:
                if(isRota){
                    return R.drawable.ic_rg_status_finalizado;
                } else {
                    return R.drawable.ic_rg_status_cliente_fora_da_rota;
                }
            case RotaGuiadaUtils.STATUS_RG_PAUSADO:
                if(isUnrealized){
                    return R.drawable.ic_rg_status_incompleto;
                } else {
                    return R.drawable.ic_rg_status_pausado;
                }
        }
        return -1;
    }

    public static int getStatusStringResourceId(int status) {
        switch (status) {
            case RotaGuiadaUtils.STATUS_RG_NAO_INICIADO:
                return R.string.rg_status_n_iniciado;
            case RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO:
                return R.string.rg_status_andamento;
            case RotaGuiadaUtils.STATUS_RG_FINALIZADO:
                return R.string.rg_status_finalizado;
            case RotaGuiadaUtils.STATUS_RG_FORA_ROTA:
                return R.string.rg_status_fora_rota;
            case RotaGuiadaUtils.STATUS_RG_NAO_REALIZADO:
                return R.string.rg_status_n_realizado;
            case RotaGuiadaUtils.STATUS_RG_INCOMPLETO:
                return R.string.rg_status_incompleto;
        }

        return R.string.rg_status_default;
    }

    public static String getStatusName(Context context, int status) {
            return context.getResources().getStringArray(R.array.array_status_rota)[status];
    }

    public static int getStatusRGTAKSImageResourceId(int type, int status, int just) {

        switch (RotaGuiadaTaskType.getRotaGuiadaTaskType(type)) {
            case PEDIDO:
                switch (status) {
                    case StatusPedido.ENVIADO_ADM:
                    case StatusPedido.PEDIDO_GERADO:
                    case StatusPedido.EXPORTADO:
                    case StatusPedido.ENVIADO_APROVACAO:
                    case StatusPedido.ENVIADO_CSP:
                    case StatusPedido.APROVADO_CSP:
                        if(just > 0){
                            return R.drawable.ic_rg_status_cliente_fora_da_rota;
                        } else {
                            return R.drawable.ic_rg_status_finalizado;
                        }
                        default:
                        return R.drawable.ic_rg_task_status_em_andamento;

                }

            case PESQUISA:
                if (status == Pesquisa.OBRIGATORIAS_RESPONDIDAS) {
                    return R.drawable.ic_rg_status_finalizado;
                } else {
                    if(just > 0){
                        return R.drawable.ic_rg_status_cliente_fora_da_rota;
                    } else {
                        return R.drawable.ic_rg_task_status_em_andamento;
                    }
                }

            default:
                return R.drawable.ic_rg_task_status_em_andamento;
        }

    }

    public static boolean isTaksFinish(int type, int status) {
        LogUser.log(Config.TAG, status + "");

        switch (RotaGuiadaTaskType.getRotaGuiadaTaskType(type)) {
            case PEDIDO:
                switch (status) {
                    case StatusPedido.ENVIADO_ADM:
                    case StatusPedido.PEDIDO_GERADO:
                    case StatusPedido.EXPORTADO:
                    case StatusPedido.ENVIADO_APROVACAO:
                    case StatusPedido.ENVIADO_CSP:
                    case StatusPedido.APROVADO_CSP:
                        return true;
                    default:
                        return false;
                }
            case PESQUISA:
                if(status == Pesquisa.OBRIGATORIAS_RESPONDIDAS){
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }


    public static boolean checkValidRota(Activity activity, Rotas rotas, boolean isEdit){
        DialogsCustom dialogsDefault = new DialogsCustom(activity);

        if (rotas == null) {
            dialogsDefault.showDialogMessage(activity.getString(R.string.rota_invalid), dialogsDefault.DIALOG_TYPE_WARNING, () -> {
                activity.finish();
            });

            return false;
        } else {
            if(!isEdit)
                return true;

            boolean isToday = false;

            try {
                isToday = FormatUtils.isSameDay(new Date(), FormatUtils.toDate(rotas.getDate()));
            } catch (Exception ex) {
                LogUser.log(ex.getMessage());
            }

            if (!isToday)
                dialogsDefault.showDialogMessage(activity.getString(R.string.rota_date_incorret), dialogsDefault.DIALOG_TYPE_WARNING, () -> {
                    activity.finish();
                });

            return isToday;
        }

    }

    public static boolean checkoutValidDate(Activity activity, Rotas rotas){
        DialogsCustom dialogsDefault = new DialogsCustom(activity);

        if (rotas == null) {
            dialogsDefault.showDialogMessage(activity.getString(R.string.rota_invalid), dialogsDefault.DIALOG_TYPE_WARNING, () -> {
                activity.finish();
            });

            return false;
        } else {
            boolean isToday = false;

            Date checkinDate = null;
            try{
                checkinDate = FormatUtils.toDate(rotas.getCheckin());
            }catch (Exception ex){
                LogUser.log(ex.getMessage());
            }

            if(checkinDate == null) {
                dialogsDefault.showDialogMessage(activity.getString(R.string.checkin_not_done), dialogsDefault.DIALOG_TYPE_WARNING, null);
                return false;
            }

            try {
                isToday = FormatUtils.isSameDay(new Date(), FormatUtils.toDate(rotas.getDate()));
            } catch (Exception ex) {
                LogUser.log(ex.getMessage());
            }

            if (!isToday)
                dialogsDefault.showDialogMessage(activity.getString(R.string.rota_date_checkin_checkout_error), dialogsDefault.DIALOG_TYPE_WARNING, () -> {
                    activity.finish();
                });

            return isToday;
        }
    }

    public static void manualActionRotaPause(Context context, Rotas rota, MultiValues multiValues) {
        RotaGuiadaUtils rotaGuiadaUtils = new RotaGuiadaUtils(context);
        Date checkout = rotaGuiadaUtils.rotaGuiadaActionDao.getLastDateAction(rota);

        if(checkout != null) {
            rota.setCheckout(FormatUtils.toTextToCompareDateInSQlite(checkout));
        }

        rotaGuiadaUtils.rotaGuiadaDao.setJustificaCheckoutManual(rota, multiValues);
        rotaGuiadaUtils.eventFirebase(context, "RG_CHECKOUT_MANUAL", rota);
    }

    public static void manualActionRotaIncomplete(Context context, Rotas rota, MultiValues multiValues) {
        RotaGuiadaUtils rotaGuiadaUtils = new RotaGuiadaUtils(context);
        String sqlDateAction = null;

        sqlDateAction = rota.getCheckout();
        Date lastDateAction = rotaGuiadaUtils.rotaGuiadaActionDao.getLastDateAction(rota);
        Date dateAction = null;

        try{
            dateAction = FormatUtils.toDateSeconds(sqlDateAction);

            if(lastDateAction == null)
                lastDateAction = FormatUtils.toDateSeconds(rota.getCheckin());

        }catch (Exception ex){
            LogUser.log(ex.getMessage());
        }

        rota = calcTimeDifference(rota, dateAction, lastDateAction, TActionRotaGuiada.CHECKOUT_MANUAL);
        rotaGuiadaUtils.rotaGuiadaActionDao.insertAction(TActionRotaGuiada.CHECKOUT_MANUAL, sqlDateAction, rota, null);

        rotaGuiadaUtils.rotaGuiadaDao.setJustificaCheckoutManual(rota, multiValues);
        rotaGuiadaUtils.eventFirebase(context, "RG_CHECKOUT_MANUAL", rota);
    }

    public static void actionRota(Context context, RotaOrigem rotasOrigem, Rotas rota, TActionRotaGuiada tActionRotaGuiada, OnSyncRota onSyncRota){
        RotaGuiadaUtils rotaGuiadaUtils = new RotaGuiadaUtils(context);

        String error = rotaGuiadaUtils.updateRota(context, rota, rotasOrigem, tActionRotaGuiada);

        if(!TextUtils.isNullOrEmpty(error)){
            rotaGuiadaUtils.dialogsDefault.showDialogMessage(error,
                    rotaGuiadaUtils.dialogsDefault.DIALOG_TYPE_WARNING, null);
            return;
        }

        onSyncRota.sync(rota);
    }

    private String updateRota(Context context, Rotas rota, RotaOrigem rotaOrigem, TActionRotaGuiada actionRotaGuiada){
        Date lastDateAction = rotaGuiadaActionDao.getLastDateAction(rota);
        Date dateAction = new Date();

        String sqlDateAction = FormatUtils.toTextToCompareDateInSQlite(dateAction);
        String tag = "";
        String error = null;

        int status;

        switch (actionRotaGuiada){
            case CHECKIN:

                if(rotaGuiadaDao.hasStatusInRoute(rota, RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO)){
                    return context.getString(R.string.message_error_visita_andamento);
                }

                tag = "RG_CHECKIN";
                rotaGuiadaTarefaDao.removeJusticativaAtividade(rota);

                if(rota.getCheckin() == null)
                    rota.setCheckin(sqlDateAction);

                rota.setJustifVisita(0);
                rota.setJustifPedido(0);
                rota.setJustifAtivObrig(null);
                rota.setCheckout(null);

                status = RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO;

                break;
            case CHECKOUT:
                tag = "RG_CHECKOUT";
                rota.setCheckout(sqlDateAction);
                status = RotaGuiadaUtils.STATUS_RG_FINALIZADO;

                break;
            case PAUSE:
                if(rotaGuiadaDao.hasStatusInRoute(rota, RotaGuiadaUtils.STATUS_RG_PAUSADO)){
                    return context.getString(R.string.message_error_visita_pausada);
                }

                tag = "RG_PAUSE";
                status = RotaGuiadaUtils.STATUS_RG_PAUSADO;
                break;
            case RESUME:
                if(rotaGuiadaDao.hasStatusInRoute(rota, RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO)){
                    return context.getString(R.string.message_error_visita_andamento);
                }

                tag = "RG_RESUME";
                status = RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO;
                break;
            default:
                status = rota.getStatus();
                break;
        }

        if(!rota.isCheckinDentro()){
            if(rotaOrigem != null){
                rota.setChekinDentro(rotaOrigem.isInRadius() ? "1" : "0");
            }
        }

        rota = calcTimeDifference(rota, dateAction, lastDateAction, actionRotaGuiada);
        rota.setStatus(status);

        rotaGuiadaDao.updateRota(rota, actionRotaGuiada);
        rotaGuiadaActionDao.insertAction(actionRotaGuiada, sqlDateAction, rota, rotaOrigem);
        eventFirebase(context, tag, rota);

        return error;
    }

    public static Rotas calcTimeDifference(Rotas rotas, Date dateAction, Date lastDateAction, TActionRotaGuiada actionRotaGuiada){
        try {

            long diff = FormatUtils.diffDateMin(lastDateAction, dateAction);

            switch (actionRotaGuiada){
                case PAUSE:
                case CHECKOUT:
                case CHECKOUT_MANUAL:
                    rotas.setDiffCheck((rotas.getDiffCheck() + diff));
                    break;
                case RESUME:
                    rotas.setDiffPause(rotas.getDiffPause() + diff);
                    break;
            }
        }catch (Exception ex){
            LogUser.log(ex.toString());
        }

        return rotas;
    }

    private void eventFirebase(Context context, String event, Rotas rota){
        Bundle params = new Bundle();

        String codCliente = "";

        if(rota.getCliente() == null){
            codCliente = rota.getCodCliente();
        } else{
            codCliente = rota.getCliente().getCodigo();
        }

        params.putString(event, rota.getCodRegFunc() + " - " + codCliente);
        FirebaseUtils.sendEvent(context, event, params);
    }

    public interface OnSyncRota{
        void sync(Rotas rota);
    }
}
