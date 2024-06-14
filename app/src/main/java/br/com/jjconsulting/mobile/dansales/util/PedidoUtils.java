package br.com.jjconsulting.mobile.dansales.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.ImageView;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.database.StatusPedidoDao;
import br.com.jjconsulting.mobile.dansales.model.OrigemPedido;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.model.StatusPedido;

public class PedidoUtils {

    private PedidoUtils() {
    }

    public static void getPedidoIconImageResourceId(Context context, ImageView imageView , int codigoStatus, int codigoOrigem) {
       try {
           int image;
           switch (codigoOrigem) {
               case OrigemPedido.MOBILE:
                   image = R.drawable.ic_mob_blue_trans;
                   break;
               case OrigemPedido.TEVEC:
                   image = R.drawable.ic_suj_blue_trans;
                   break;
               case OrigemPedido.EDI:
               case OrigemPedido.SUPORTE:
                   image = R.drawable.ic_edi_trans;
                   break;
               case OrigemPedido.CAC:
                   image = R.drawable.ic_cac_trans;
                   break;
               default:
                   image = R.drawable.ic_desk_blue_trans;
                   break;
           }

           StatusPedidoDao statusPedidoDao = new StatusPedidoDao(context);
           StatusPedido statusPedido = statusPedidoDao.get(codigoStatus);

           imageView.setImageResource(image);
           imageView.setColorFilter(statusPedido.getColor() != null ? Color.parseColor(statusPedido.getColor()) : R.color.formLabelTextColorDisable, PorterDuff.Mode.SRC_ATOP);
       }catch (Exception ex){
           imageView.setImageResource(R.drawable.ic_desk_blue_trans);
           imageView.setColorFilter(R.color.formLabelTextColorDisable, PorterDuff.Mode.SRC_ATOP);
       }

    }

    public static int getSortimentoColorResourceId(int tipoSortimento) {
        switch (tipoSortimento) {
            case Produto.SORTIMENTO_TIPO_OBRIGATORIO:
                return R.color.sortimentoObrigatorio;
            case Produto.SORTIMENTO_TIPO_RECOMENDADO:
                return R.color.sortimentoRecomendado;
            default:
                return R.color.formLabelTextColorDisable;
        }
    }
}
