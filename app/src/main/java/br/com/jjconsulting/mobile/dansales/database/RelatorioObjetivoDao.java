package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.ObjetivoFilter;
import br.com.jjconsulting.mobile.dansales.model.Familia;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotas;
import br.com.jjconsulting.mobile.dansales.model.RelatorioObjetivo;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.UnidadeNegocioUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioObjetivoDao extends BaseDansalesDao{

    public RelatorioObjetivoDao(Context context) {
        super(context);
    }

    public ArrayList<RelatorioObjetivo> findAll(String codigoUsuario,
                                                String codigoUnidadeNegocio,
                                                String nome,
                                                ObjetivoFilter filter,
                                                int indexOffSet) {


        ArrayList<RelatorioObjetivo> listRelatorioObjetivo = new ArrayList<>();


        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(codigoUnidadeNegocio);
        StringBuilder query = new StringBuilder();

        query.append("SELECT  ");
        query.append("raf.MARCA, ");

        if (filter != null && filter.getObjetivoUn() != null) {
            switch (filter.getObjetivoUn()) {
                case CAIXA:
                    query.append("SUM(raf.CAIXA_OBJETIVO) AS OBJ, ");
                    query.append("SUM(raf.CAIXA_COB) AS COB, ");
                    query.append("SUM(raf.CAIXA_RAF) AS RAF, ");
                    query.append("SUM(raf.CAIXA_REPORTADO) AS REPORTADO, ");

                    break;
                case FAT:
                    query.append("SUM(raf.FAT_OBJETIVO) AS OBJ, ");
                    query.append("SUM(raf.FAT_COB) AS COB, ");
                    query.append("SUM(raf.FAT_RAF) AS RAF, ");
                    query.append("SUM(raf.FAT_REPORTADO) AS REPORTADO, ");

                    break;
                case TON:
                    query.append("SUM(raf.TON_OBJETIVO) AS OBJ, ");
                    query.append("SUM(raf.TON_COB) AS COB, ");
                    query.append("SUM(raf.TON_RAF) AS RAF, ");
                    query.append("SUM(raf.TON_REPORTADO) AS REPORTADO, ");
                    break;
            }

        } else {
            query.append("SUM(raf.CAIXA_OBJETIVO) AS OBJ, ");
            query.append("SUM(raf.CAIXA_COB) AS COB, ");
            query.append("SUM(raf.CAIXA_RAF) AS RAF, ");
            query.append("SUM(raf.CAIXA_REPORTADO) AS REPORTADO, ");

        }

        query.append("SUM(raf.PERDA_ABAT) AS PERDA ");
        query.append("FROM TB_DERAF_DIARIO  raf ");
        query.append("INNER JOIN TB_DECLIENTE cli ");
        query.append("  ON  cli.COD_EMITENTE = raf.COD_CLIENTE ");
        query.append("  AND cli.DEL_FLAG = '0' ");
        query.append("INNER JOIN TB_DECLIENTEUN cun ");
        query.append("  ON  cun.COD_EMITENTE = cli.COD_EMITENTE ");
        query.append("  AND cun.COD_UNID_NEGOC = ? ");
        query.append("  AND cun.DEL_FLAG = '0' ");
        query.append("  AND cun.INATIVO <> '1' ");
        query.append("INNER JOIN TB_DECLIUNREG cur ");
        query.append("  ON  cur.COD_EMITENTE = cun.COD_EMITENTE ");
        query.append("  AND cur.COD_UNID_NEGOC = ? ");
        query.append("  AND cur.DEL_FLAG = '0' ");
        query.append("  AND cur.SEQUENCIA = '000' ");
        query.append("WHERE  raf.FAT_REPORTADO IS NOT NULL ");
        query.append(" AND raf.FAT_OBJETIVO IS NOT NULL ");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(codigoUnidadeNegocio);
        whereArgs.add(codigoUnidadeNegocio);

        if (nome != null) {
            query.append(" AND (cli.NOM_CLIENTE LIKE ? or raf.MARCA LIKE ?) ");
            whereArgs.add("%" + nome + "%");
            whereArgs.add("%" + nome + "%");
        }

        if (filter != null) {

            if (filter.getCliente() != null) {
                query.append(" AND cli.COD_EMITENTE = ? ");
                whereArgs.add(filter.getCliente().getCodigo());
            }

            if (filter.getOrganizacao() != null) {
                query.append(" and cli.COD_ORGANIZACAO = ? ");
                whereArgs.add(filter.getOrganizacao().getCodigo());
            }

            if (filter.getBandeira() != null) {
                query.append(" and cli.COD_BANDEIRA = ? ");
                whereArgs.add(filter.getBandeira().getCodigoBandeira());
            }

            if (filter.getFamilia() != null) {
                query.append(" and raf.MARCA = ? ");
                whereArgs.add(filter.getFamilia().getNome());
            }

            if (filter.getHierarquiaComercial() != null &&
                    filter.getHierarquiaComercial().size() > 0) {

                query.append(" and cur.COD_REG_FUNC in (");
                for (int i = 0; i < filter.getHierarquiaComercial().size(); i++) {
                    Usuario usuario = filter.getHierarquiaComercial().get(i);
                    if (i > 0) {
                        query.append(",");
                    }
                    query.append("'" + usuario.getCodigo() + "'");
                }
                query.append(") ");

            } else {
                query.append(" and cur.COD_REG_FUNC = ? ");
                whereArgs.add(codigoUsuario);
            }
        } else {
            query.append(" and cur.COD_REG_FUNC = ? ");
            whereArgs.add(codigoUsuario);
        }


        query.append(" GROUP BY raf.MARCA ");
        query.append(" ORDER BY raf.MARCA ");

        if (indexOffSet != -1) {
            query.append(" " + "LIMIT " + Config.SIZE_PAGE + " OFFSET " + indexOffSet);
        }

        ArrayList<RelatorioNotas> listNotas = new ArrayList<>();
        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                RelatorioObjetivo obj = new ObjetivoCursorWrapper(cursor).get();
                listRelatorioObjetivo.add(obj);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return listRelatorioObjetivo;
    }


    public ArrayList<Familia> getFamilia() {
        ArrayList<Familia> familias = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT");
        query.append(" DISTINCT(MARCA) ");
        query.append("FROM");
        query.append(" TB_DERAF_DIARIO ");
        query.append("WHERE");
        query.append(" FAT_REPORTADO IS NOT NULL ");
        query.append("AND");
        query.append(" FAT_OBJETIVO IS NOT NULL");

        int id = 1;
        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                familias.add(new RelatorioObjetivoDao.FamiliaCursorWrapper(cursor).getFamilia(id));
                id++;
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return familias;
    }

    public class ObjetivoCursorWrapper extends CursorWrapper {

        public ObjetivoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public RelatorioObjetivo get() {
            RelatorioObjetivo obj = new RelatorioObjetivo();
            obj.setNome(getString(getColumnIndex("MARCA")));
            //Campo Percentual
            obj.setPerda(getDouble(getColumnIndex("PERDA")) * 100);
            obj.setObj(getDouble(getColumnIndex("OBJ")));
            obj.setReal(getDouble(getColumnIndex("REPORTADO")));
            //Campo Percentual
            obj.setCob(getDouble(getColumnIndex("COB")) * 100);
            obj.setRaf(getDouble(getColumnIndex("RAF")));

            return obj;
        }
    }

    public class FamiliaCursorWrapper extends CursorWrapper {

        public FamiliaCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Familia getFamilia(int id) {
            Familia fam = new Familia();
            fam.setCodigo(String.valueOf(id));
            fam.setNome(getString(getColumnIndex("MARCA")));

            return fam;
        }
    }


}
