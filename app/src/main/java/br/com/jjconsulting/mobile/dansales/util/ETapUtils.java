package br.com.jjconsulting.mobile.dansales.util;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.TapListActivity;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.model.TapActionType;
import br.com.jjconsulting.mobile.dansales.model.TapItem;
import br.com.jjconsulting.mobile.dansales.model.TapStatus;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class ETapUtils {

    public static boolean isAddDeleteItem(ArrayList<TapItem> itens) {
        boolean isAddDelete = true;

        if (itens != null && itens.size() > 0) {
            if (itens.get(0).getTapItemRules() != null) {
                isAddDelete = itens.get(0).getTapItemRules().isDel();
            }
        }

        return isAddDelete;

    }

    public static Intent intentItemTap(Context context) {
        Intent it = null;

        Perfil perfil = Current.getInstance(context).getUsuario().getPerfil();

        if (perfil.isPermiteModuloETap() &&  !perfil.isPermiteEtapAnFin() && !perfil.isPermiteEtapContrInt()) {
            it = TapListActivity.newIntent(context, TapActionType.TAP_LIST);
        }

        if (!perfil.isPermiteModuloETap() && perfil.isPermiteEtapAnFin() && !perfil.isPermiteEtapContrInt()) {
            it = TapListActivity.newIntent(context, TapActionType.TAP_ANALISE_FINANCEIRA);
        }

        if (!perfil.isPermiteModuloETap() && !perfil.isPermiteEtapAnFin() && perfil.isPermiteEtapContrInt()) {
            it = TapListActivity.newIntent(context, TapActionType.TAP_CONSULTA);
        }

        return it;
    }

    public static boolean isOneItemTap(Context context) {
        Perfil perfil = Current.getInstance(context).getUsuario().getPerfil();

        if (perfil.isPermiteModuloETap() && !perfil.isPermiteEtapAnFin() && !perfil.isPermiteEtapContrInt()) {
            return true;
        }

        if (!perfil.isPermiteModuloETap() && perfil.isPermiteEtapAnFin() && !perfil.isPermiteEtapContrInt()) {
            return true;
        }

        if (!perfil.isPermiteModuloETap() && !perfil.isPermiteEtapAnFin() && perfil.isPermiteEtapContrInt()) {
            return true;
        }

        return false;
    }

    public static int getItemColorStatus(Context context, TapItem tapItem) {
        boolean hasAnexo = (tapItem.getAnexos() != null && !tapItem.getAnexos().isEmpty());

        if ((tapItem.getVlEst() == 0 && tapItem.getVlApur() == 0) || !hasAnexo) {
            return context.getResources().getColor(R.color.statusItemETap2);
        } else {
            return context.getResources().getColor(R.color.statusItemETap1);
        }
    }

    public static int getColorStatus(int codigoStatus) {
        switch (codigoStatus) {
            case TapStatus.EM_DIGITACAO:
                return R.drawable.ic_tap_blue_dark;
            case TapStatus.AGUARDANDO_APROVADOR:
                return R.drawable.ic_tap_yellow;
            case TapStatus.APROVACAO_CLIENTE:
                return R.drawable.ic_tap_pink;
            case TapStatus.APROVADO:
                return R.drawable.ic_tap_green_light;
            case TapStatus.REPROVADO:
                return R.drawable.ic_tap_red_dark;
            case TapStatus.AGUARDANDO_COMPROVACAO:
                return R.drawable.ic_tap_ocean;
            case TapStatus.VALIDACAO_COMPROVACAO:
                return R.drawable.ic_tap_blue_light;
            case TapStatus.AGUARDANDO_PROGRAMACAO_PGTO:
                return R.drawable.ic_tap_green_dark;
            case TapStatus.PAGO_PGTO_PROGRAMADO:
                return R.drawable.ic_tap_black;
            case TapStatus.CANCELADO:
                return R.drawable.ic_tap_gray;
            case TapStatus.BLOQUEADO_FINANCAS:
                return R.drawable.ic_tap_brown;
            case TapStatus.REPROVADO_CLIENTE:
                return R.drawable.ic_tap_red_dark;
            default:
                return R.drawable.ic_tap_blue_dark;
        }
    }

}
