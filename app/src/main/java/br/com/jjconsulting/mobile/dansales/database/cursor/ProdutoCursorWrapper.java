package br.com.jjconsulting.mobile.dansales.database.cursor;

import android.database.Cursor;
import android.database.CursorWrapper;

import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PrecoVenda;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.jjlib.util.MathUtils;

public class ProdutoCursorWrapper extends CursorWrapper {

    private Pedido pedido;
    private boolean usingPreco;

    public ProdutoCursorWrapper(Cursor cursor, Pedido pedido, boolean usingPreco) {
        super(cursor);

        this.pedido = pedido;
        this.usingPreco = usingPreco;
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

        if (usingPreco && 1 == getInt(getColumnIndex("POSSUI_PRECO"))) {
            PrecoVenda preco = new PrecoVenda();
            preco.setPreco(getDouble(getColumnIndex("DA1_PRCVEN")));
            preco.setBloq(getInt(getColumnIndex("B1_BLOQ")));
            preco.setDescontoMaximo(getDouble(getColumnIndex("DA1_DESCMAX")));
            produto.setPrecoVenda(preco);
        }

        if (Pedido.UNIDADE_MEDIDA_UNIDADE.equals(pedido.getUnidadeMedida())) {
            produto.setPallet(produto.getPallet() * produto.getMultiplo());
            produto.setLastro(produto.getLastro() * produto.getMultiplo());
        }

        return produto;
    }
}
