package br.com.jjconsulting.mobile.dansales.database.cursor;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import br.com.jjconsulting.mobile.dansales.model.BatchDAT;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PrecoVenda;
import br.com.jjconsulting.mobile.jjlib.util.MathUtils;

public class BatchDATCursorWrapper extends CursorWrapper {

    private Pedido pedido;

    public BatchDATCursorWrapper(Cursor cursor, Pedido pedido) {
        super(cursor);
        this.pedido = pedido;
    }

    public BatchDAT getBatchDAT() {
        BatchDAT batchDAT = new BatchDAT();
        batchDAT.setLote(getString(0));
        batchDAT.setPercentualDesconto(getDouble(2));

        int estoque = getInt(3);
        int multiplo = (int) MathUtils.toDoubleOrDefaultUsingLocalePTBR(getString(6));

        if (Pedido.UNIDADE_MEDIDA_CAIXA.equals(pedido.getUnidadeMedida()) && multiplo > 0) {
            estoque = estoque / multiplo;
        }

        batchDAT.setQuantidadeDisponivel(estoque);

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
            batchDAT.setData(dateFormat.parse(
                    getString(1)));
        } catch (ParseException parseEx) {
        }

        PrecoVenda preco = new PrecoVenda();
        preco.setBloq(PrecoVenda.BLOQ_PRODUTO_DISPONIVEL);
        preco.setPreco(getDouble(4));
        preco.setDescontoMaximo(getDouble(5));
        batchDAT.setPrecoVenda(preco);

        return batchDAT;
    }
}
