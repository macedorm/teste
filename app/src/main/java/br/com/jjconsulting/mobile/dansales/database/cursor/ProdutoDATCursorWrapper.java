package br.com.jjconsulting.mobile.dansales.database.cursor;

import android.database.Cursor;
import android.database.CursorWrapper;

import br.com.jjconsulting.mobile.dansales.model.EstoqueDAT;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.jjlib.util.MathUtils;

public class ProdutoDATCursorWrapper extends CursorWrapper {

    private Pedido pedido;

    public ProdutoDATCursorWrapper(Cursor cursor, Pedido pedido) {
        super(cursor);

        this.pedido = pedido;
    }

    public Produto getProduto() {
        Produto produto = new Produto();
        produto.setCodigo(getString(getColumnIndex("COD_SKU")));
        produto.setCodigoSimplificado(getString(getColumnIndex("COD_SIMPLIFICADO")));
        produto.setNome(getString(getColumnIndex("DESCRICAO")));
        produto.setFamilia(getString(getColumnIndex("FAMILIA")));
        produto.setMultiplo((int) MathUtils.toDoubleOrDefaultUsingLocalePTBR(getString(
                getColumnIndex("QTD_CAIXA"))));
        produto.setLastro(MathUtils.toDoubleOrDefaultUsingLocalePTBR(getString(
                getColumnIndex("CX_LASTRO"))));
        produto.setPallet(MathUtils.toDoubleOrDefaultUsingLocalePTBR(getString(
                getColumnIndex("CX_PALLET"))));
        produto.setPeso(MathUtils.toDoubleOrDefaultUsingLocalePTBR(getString(getColumnIndex(
                "PESO_BRUTO"))));
        produto.setPesoLiquido(MathUtils.toDoubleOrDefaultUsingLocalePTBR(getString(
                getColumnIndex("PESO_LIQUIDO"))));

        if (Pedido.UNIDADE_MEDIDA_UNIDADE.equals(pedido.getUnidadeMedida())) {
            produto.setPallet(produto.getPallet() * produto.getMultiplo());
            produto.setLastro(produto.getLastro() * produto.getMultiplo());
        }

        int estoque = getInt(getColumnIndex("QTDPROD"));
        if (Pedido.UNIDADE_MEDIDA_CAIXA.equals(pedido.getUnidadeMedida())
                && produto.getMultiplo() > 0) {
            estoque = estoque / produto.getMultiplo();
        }

        EstoqueDAT estoqueDAT = new EstoqueDAT();
        estoqueDAT.setQuantidadeDisponivel(estoque);
        estoqueDAT.setQuantidadeBatch(getInt(getColumnIndex("QTDBATCH")));
        estoqueDAT.setBloq(getString(getColumnIndex("B1_BLOQ_BATCH")));
        produto.setEstoqueDAT(estoqueDAT);

        return produto;
    }
}
