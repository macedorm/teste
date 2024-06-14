package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.TPerfilAgendaAprov;
import br.com.jjconsulting.mobile.dansales.model.TPerfilRegraAprov;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.MathUtils;

public class PerfilVendaDao extends BaseDansalesDao{

    public PerfilVendaDao(Context context) {
        super(context);
    }

    public ArrayList<PerfilVenda> getAll(int codigoPerfil) {
        String whereClause = "and tpv2.PRV_INT_CODPERFIL = " + String.valueOf(codigoPerfil);
        String query = "order by tpv.PRV_TXT_COD_UN, tpv.PRV_TXT_TPVEND";
        return query(whereClause, null, query);
    }

    private ArrayList<PerfilVenda> query(String whereClause, String[] args, String orderBy) {
        ArrayList<PerfilVendaRaw> raw = new ArrayList<>();

        String query = "select tpv.PRV_INT_CODPERFIL, tpv.PRV_TXT_COD_UN," +
                " tpv.PRV_TXT_TPVEND, tpv.PRV_TXT_KEY, tpv.PRV_TXT_VALUE" +
                " from TBPERFILTPVEND tpv" +
                " inner join TBPERFILTPVEND tpv2" +
                " on tpv2.PRV_INT_CODPERFIL = tpv.PRV_INT_CODPERFIL" +
                " and tpv2.PRV_TXT_COD_UN = tpv.PRV_TXT_COD_UN" +
                " and tpv2.PRV_TXT_TPVEND = tpv.PRV_TXT_TPVEND" +
                " where (" +
                "   (tpv2.PRV_TXT_KEY = 'ISENABLED' AND tpv2.PRV_TXT_VALUE = '1')" +
                "   or (tpv2.PRV_TXT_KEY = 'HABAPROVPED' AND tpv2.PRV_TXT_VALUE = '1')" +
                "   or (tpv2.PRV_TXT_KEY = 'HABLIBPED' AND tpv2.PRV_TXT_VALUE = '1')" +
                " )";

        if (whereClause != null) {
            query += " " + whereClause;
        }

        if (orderBy != null) {
            query += " " + orderBy;
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                raw.add(new PerfilVendaDao.PerfilVendaRawCursorWrapper(cursor)
                        .getPerfilVendaRaw());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return buildPerfisVenda(raw);
    }

    /**
     * Builds all perfis de venda using the provided raw.
     */
    private ArrayList<PerfilVenda> buildPerfisVenda(List<PerfilVendaRaw> raw) {
        HashMap<Integer, PerfilVenda> advancedRaw = new HashMap<>();

        for (int i = 0; i < raw.size(); i++) {
            PerfilVendaRaw r = raw.get(i);
            Integer key = (r.getCodigoUnidadeNegocio() + r.getTipoVenda()).hashCode();

            PerfilVenda perfilVenda = advancedRaw.get(key);

            if (perfilVenda == null) {
                perfilVenda = new PerfilVenda();
                perfilVenda.setCodigoPerfil(r.getCodigoPerfil());
                perfilVenda.setCodigoUnidadeNegocio(r.getCodigoUnidadeNegocio());
                perfilVenda.setTipoVenda(r.getTipoVenda());

                advancedRaw.put(key, perfilVenda);
            }

            switch (r.getChave()) {
                // flags
                case "ISENABLED":
                    perfilVenda.setVendaHabilitada("1".equals(r.getValor()));
                    break;
                case "HABLIBPED":
                    perfilVenda.setLiberacaoHabilitada("1".equals(r.getValor()));
                    break;
                case "EXIBEMPENHO":
                    perfilVenda.setExibicaoEmpenhoHabilitada("1".equals(r.getValor()));
                    break;
                case "EXIBPLANTA":
                    perfilVenda.setAleraPlanta("1".equals(r.getValor()));
                    break;
                case "EXIBLASTROPALLET":
                    perfilVenda.setExibicaoLastroPalletHabilitada("1".equals(r.getValor()));
                    break;
                case "EXIBENTREGAR":
                    perfilVenda.setExibicaoEntregarEm("1".equals(r.getValor()));
                        break;
                case "EXIBORDEMCOMPRA":
                    perfilVenda.setExibicaoOrdemCompra("1".equals(r.getValor()));
                    break;
                case "EXIBDTIMPSAP":
                    perfilVenda.setExibicaoDtImpSAP("1".equals(r.getValor()));
                    break;
                case "EXIBPEDSAP":
                    perfilVenda.setExibicaoPedSAP("1".equals(r.getValor()));
                    break;
                case "PERDATAENTREGA":
                    perfilVenda.setEdicaoDataEntregaHabilitada("1".equals(r.getValor()));
                    break;
                case "EXIBEVLLOTECALC":
                    perfilVenda.setExibicaoValorCalculadoLoteHabilitada("1".equals(r.getValor()));
                    break;
                case "HASANEXOOBS":
                    perfilVenda.setAnexoObrigatorio("1".equals(r.getValor()));
                    break;
                case "PERALTERARLOCALENTREGA":
                    perfilVenda.setEdicaoLocalEntregaHabilitada("1".equals(r.getValor()));
                    break;
                case "PERALTUM":
                    perfilVenda.setEdicaoUnidadeMedidaHabiltiada("1".equals(r.getValor()));
                    break;
                case "PEREDAVAN":
                    perfilVenda.setEdicaoAvancadaHabilitada("1".equals(r.getValor()));
                    break;
                case "PEREDUNIT":
                    perfilVenda.setEdicaoUnitariaHabilitada("1".equals(r.getValor()));
                    break;
                case "PERPEDIDOUGERNTE":
                    perfilVenda.setMarcacaoUrgenciaHabilitada("1".equals(r.getValor()));
                    break;
                case "PERPEDIDOREDE":
                    perfilVenda.setPedidoEmRedeHabilitado("1".equals(r.getValor()));
                    break;
                case "PERDIGMULT":
                    perfilVenda.setDigitacaoMultiploObrigatoria("1".equals(r.getValor()));
                    break;
                case "CONDPAGESPECIFIC":
                    perfilVenda.setCondicaoPagamentoEspecifica(r.getValor());
                    break;
                case "EXPORTSAPUN":
                    perfilVenda.setExportacaoSapEmUnidadeHabilitada("1".equals(r.getValor()));
                    break;
                case "VALIDPRODBLOQ":
                    perfilVenda.setValidacaoProdutoBloqueadoIgnorada("1".equals(r.getValor()));
                    break;
                case "HABSORTIMENTO":
                    perfilVenda.setSortimentoHabilitado("1".equals(r.getValor()));
                    break;
                case "HABPOSITIVACAO":
                    perfilVenda.setPositivacaoHabilitado("1".equals(r.getValor()));
                    break;
                case "PERAPENASPRODATV":
                    perfilVenda.setPermiteApenasCadastroProdutoAtivo("1".equals(r.getValor()));
                    break;
                case "IGNEXCLUSPRODATV":
                    perfilVenda.setIgnoraExclusividadeProdutoAtivo("1".equals(r.getValor()));
                    break;
                // types
                case "TIPOLIB":
                    perfilVenda.setTipoLiberacao(Integer.parseInt(r.getValor()));
                    break;
                case "OBSINT":
                    perfilVenda.setTipoObsInterna(Integer.parseInt(r.getValor()));
                    break;
                case "OBSNF":
                    perfilVenda.setTipoObsNf(Integer.parseInt(r.getValor()));
                    break;
                case "TIPODESCONTO":
                    perfilVenda.setTipoDesconto(Integer.parseInt(r.getValor()));
                    break;
                case "TIPOPESO":
                    perfilVenda.setTipoPeso(Integer.parseInt(r.getValor()));
                    break;
                case "TIPOPESOMAX":
                    perfilVenda.setTipoPesoMax(Integer.parseInt(r.getValor()));
                    break;
                case "TIPOAGENDA":
                    perfilVenda.setTipoAgenda(Integer.parseInt(r.getValor()));
                    break;
                case "TPVALORMIN":
                    perfilVenda.setTipoValorMinimo(Integer.parseInt(r.getValor()));
                    break;
                case "PERSELCONDPAG":
                    perfilVenda.setTipoCondPag(Integer.parseInt(r.getValor()));
                    break;
                // etc
                case "OBSINTINFO":
                    perfilVenda.setObsInternaInfo(r.getValor());
                    break;
                case "OBSNFINFO":
                    perfilVenda.setObsNfInfo(r.getValor());
                    break;
                case "PESOMIN":
                    perfilVenda.setPesoMinimo(
                            MathUtils.toDoubleOrDefaultUsingLocalePTBR(r.getValor()));
                    break;
                case "PESOMAX":
                    perfilVenda.setPesoMaximo(
                            MathUtils.toDoubleOrDefaultUsingLocalePTBR(r.getValor()));
                    break;
                case "VALORMAX":
                    perfilVenda.setValorMaximo(
                            MathUtils.toDoubleOrDefaultUsingLocalePTBR(r.getValor()));
                    break;
                case "VALORMIN":
                    perfilVenda.setValorMinimo(
                            MathUtils.toDoubleOrDefaultUsingLocalePTBR(r.getValor()));
                    break;
                case "UNMEDPADRAO":
                    perfilVenda.setUnMedidaPadrao(Integer.parseInt(r.getValor()));
                    break;
                case "TIPOJUSTSORT":
                    perfilVenda.setTipoJustificativaSortimento(Integer.parseInt(r.getValor()));
                    break;
                case "PERCAIXAFRAC":
                    perfilVenda.setPerCaixaFrac("1".equals(r.getValor()));
                    break;
                case "HABPEDCLIBLOQ":
                    perfilVenda.setPermitePedidoCliCredBloq("1".equals(r.getValor()));
                    break;
                case "HABAPROVPED":
                    perfilVenda.getAprov().setAprovacaoHabilitada("1".equals(r.getValor()));
                    break;
                case "PERMCOPEDIDO":
                    perfilVenda.getAprov().setPermiteMasterContratoPedido("1".equals(r.getValor()));
                    break;
                case "PERMAPROVMASSA":
                    perfilVenda.getAprov().setPermiteAprovMassa("1".equals(r.getValor()));
                    break;
                case "FILTRAPEDIDOREGRA":
                    int filtroRegra = Integer.parseInt(r.getValor());
                    perfilVenda.getAprov().setFiltroRegraAprov(TPerfilRegraAprov.fromInteger(filtroRegra));
                    break;
                case "FILTRAPEDIDOAPROV":
                    int filtroAgenda = Integer.parseInt(r.getValor());
                    perfilVenda.getAprov().setFiltroAgenda(TPerfilAgendaAprov.fromInteger(filtroAgenda));
                    break;
                default:
                    break;
            }
        }

        return new ArrayList<>(advancedRaw.values());
    }

    public class PerfilVendaRawCursorWrapper extends CursorWrapper {

        public PerfilVendaRawCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public PerfilVendaRaw getPerfilVendaRaw() {
            PerfilVendaRaw perfilVendaRaw = new PerfilVendaRaw();
            perfilVendaRaw.setCodigoPerfil(getInt(getColumnIndex("PRV_INT_CODPERFIL")));
            perfilVendaRaw.setCodigoUnidadeNegocio(getString(getColumnIndex(
                    "PRV_TXT_COD_UN")));
            perfilVendaRaw.setTipoVenda(getString(getColumnIndex(
                    "PRV_TXT_TPVEND")));
            perfilVendaRaw.setChave(getString(getColumnIndex("PRV_TXT_KEY")));
            perfilVendaRaw.setValor(getString(getColumnIndex("PRV_TXT_VALUE")));

            return perfilVendaRaw;
        }
    }

    private class PerfilVendaRaw {

        private int codigoPerfil;
        private String codigoUnidadeNegocio;
        private String tipoVenda;
        private String chave;
        private String valor;

        public PerfilVendaRaw() { }

        public int getCodigoPerfil() {
            return codigoPerfil;
        }

        public void setCodigoPerfil(int codigoPerfil) {
            this.codigoPerfil = codigoPerfil;
        }

        public String getCodigoUnidadeNegocio() {
            return codigoUnidadeNegocio;
        }

        public void setCodigoUnidadeNegocio(String codigoUnidadeNegocio) {
            this.codigoUnidadeNegocio = codigoUnidadeNegocio;
        }

        public String getTipoVenda() {
            return tipoVenda;
        }

        public void setTipoVenda(String tipoVenda) {
            this.tipoVenda = tipoVenda;
        }

        public String getChave() {
            return chave;
        }

        public void setChave(String chave) {
            this.chave = chave;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }
    }
}
