package br.com.jjconsulting.mobile.dansales.util;

import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.R;

public class ClienteUtils {

    private static final int[] statuses = new int[] {
            Cliente.STATUS_CREDITO_GREEN,
            Cliente.STATUS_CREDITO_YELLOW,
            Cliente.STATUS_CREDITO_RED,
            Cliente.STATUS_CREDITO_BLACK,
            Cliente.STATUS_CREDITO_BLUE

    };

    public static int[] getStatuses() {
        return statuses;
    }

    public static int getStatusCreditoColorResourceId(int statusCredito) {
        switch (statusCredito) {
            case Cliente.STATUS_CREDITO_GREEN:
                return R.color.cliente_credito_green;
            case Cliente.STATUS_CREDITO_YELLOW:
                return R.color.cliente_credito_yellow;
            case Cliente.STATUS_CREDITO_RED:
                return R.color.cliente_credito_red;
            case Cliente.STATUS_CREDITO_BLUE:
                return R.color.cliente_credito_blue;
            case Cliente.STATUS_CREDITO_BLACK:
                return R.color.cliente_credito_black;
        }

        return -1;
    }

    public static int getStatusCreditoStringResourceId(int statusCredito) {
        switch (statusCredito) {
            case Cliente.STATUS_CREDITO_GREEN:
                return R.string.cliente_status_credito_green;
            case Cliente.STATUS_CREDITO_YELLOW:
                return R.string.cliente_status_credito_yellow;
            case Cliente.STATUS_CREDITO_RED:
                return R.string.cliente_status_credito_red;
            case Cliente.STATUS_CREDITO_BLUE:
                return R.string.cliente_status_credito_blue;
            case Cliente.STATUS_CREDITO_BLACK:
                return R.string.cliente_status_credito_black;
        }

        return -1;
    }
}
