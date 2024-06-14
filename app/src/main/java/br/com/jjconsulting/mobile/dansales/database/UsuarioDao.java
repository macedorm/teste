package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.base.Tree;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class UsuarioDao extends BaseDansalesDao{

    private PerfilDao perfilDao;
    private UsuarioFuncaoDao funcaoDao;
    private boolean isCanceled;
    private int count;
    private  Context context;

    private StringBuilder valueTrue;

    public UsuarioDao(Context context) {
        super(context);
        this.context = context;
        perfilDao = new PerfilDao(context);
        funcaoDao = new UsuarioFuncaoDao(context);
        isCanceled = false;
    }

    public Usuario get(String codigoUsuario) {
        String whereClause = "where fun.COD_REG_FUNC = ?";
        String[] whereArgs = {codigoUsuario};
        ArrayList<Usuario> usuarios = query(whereClause, whereArgs, null);

        if (usuarios.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarios.get(0);
        return setAggregatedData(usuario);
    }

    private Tree<Usuario> getHierarquiaComercial(SQLiteDatabase database, Usuario currentUser, String codUnNeg, Tree<Usuario> root) {

        boolean isFirstLevel = false;
        if (root == null) {
            root = Tree.root();
            isFirstLevel = true;
        }

        if (!isCanceled) {
            StringBuilder sql = new StringBuilder();
            sql.append("select ");
            sql.append("fun.COD_REG_FUNC, ");
            sql.append("UPPER(fun.NOME_COMPLETO) NOME_COMPLETO, ");
            sql.append("fun.NOME_ABREV, ");
            sql.append("fun.EMAIL, ");
            sql.append("fun.COD_FUNCAO_PGV, ");
            sql.append("fun.COD_REGIONAL, ");
            sql.append("fun.CPF_CNPJ, ");
            sql.append("uac.USR_INT_PERFIL, ");
            sql.append("fuc.NOME NOME_FUNCAO ");
            sql.append("from TB_DEREGISTRO fun ");
            sql.append("left join TB_DEFUNCAO fuc ");
            sql.append("on fuc.COD_FUNCAO_PGV = fun.COD_FUNCAO_PGV ");
            sql.append("and fuc.DEL_FLAG = '0' ");
            sql.append("left join TBUSERACCESS uac ");
            sql.append("on uac.USR_INT_CODREGFUNC = fun.COD_REG_FUNC ");
            sql.append("inner join TB_DEREGISTROUN run ");
            sql.append("on run.COD_REG_FUNC = fun.COD_REG_FUNC ");
            sql.append("and run.DEL_FLAG = '0' ");
            sql.append("and run.COD_UNID_NEGOC = '");
            sql.append(codUnNeg);
            sql.append("' ");
            sql.append("where fun.DEL_FLAG = '0' ");

            if (isFirstLevel) {

                if (currentUser.getPerfil() == null || currentUser.getPerfil().getPosicaoHierarquia() == 0) {
                    sql.append("and fun.COD_REG_FUNC = '");
                    sql.append(currentUser.getCodigo());
                    sql.append("' ");
                } else {
                    sql.append("and fun.COD_FUNCAO_PGV IN ");
                    sql.append("(select distinct fc.COD_FUNCAO_PGV ");
                    sql.append("from TB_DEFUNCAO fc ");
                    sql.append("where fc.POSICAO = ");
                    sql.append(currentUser.getPerfil().getPosicaoHierarquia());
                    sql.append(")  ");
                }
            } else {
                if (currentUser.getCodigoFuncao().equals("1") ||
                        currentUser.getCodigoFuncao().equals("10") ||
                        currentUser.getCodigoFuncao().equals("18")) {  //Função AV
                    sql.append("and fun.COD_REGIONAL = '");
                    sql.append(currentUser.getCodigoRegional());
                    sql.append("' ");
                } else if (currentUser.getCodigoFuncao().equals("49")) { //Função RD
                    sql.append("and SUBSTR(fun.CPF_CNPJ,1,8) = '");
                    sql.append(currentUser.getCpfCnpj().substring(0, 8));
                    sql.append("' ");
                    sql.append("and fun.COD_REG_FUNC <> '");
                    sql.append(currentUser.getCodigo());
                    sql.append("' ");
                } else {
                    sql.append("and fun.COD_SUPERIOR_PGV = '");
                    sql.append(root.getData().getCodigo());
                    sql.append("' ");
                    sql.append("and fun.COD_REG_FUNC <> '");
                    sql.append(currentUser.getCodigo());
                    sql.append("' ");
                }
            }

            sql.append(" order by UPPER(fun.NOME_COMPLETO) ");

            count++;

            try (Cursor cursor = database.rawQuery(sql.toString(), null)) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Usuario u = new UsuarioDao.UsuarioCursorWrapper(cursor).getUsuario();
                    root.addChild(u);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                LogUser.log(Config.TAG, "query: " + e);
            }

            if (isFirstLevel) {

                for (Tree<Usuario> tree : root.getChildren()) {
                    if (!isCanceled) {
                        getHierarquiaComercial(database, currentUser, codUnNeg, tree);
                    } else {
                        break;
                    }

                }

            } else if (
                    !currentUser.getCodigoFuncao().equals("1") &&           //Função AV
                            !currentUser.getCodigoFuncao().equals("10") &&  //Função AV
                            !currentUser.getCodigoFuncao().equals("18") &&  //Função AV
                            !currentUser.getCodigoFuncao().equals("49")) {  //Função RD

                for (Tree<Usuario> tree : root.getChildren()) {
                    if (!isCanceled) {
                        getHierarquiaComercial(database, currentUser, codUnNeg, tree);
                    } else {
                        break;
                    }
                }
            }

        } else {
            isCanceled = false;
        }

        return root;
    }

    public Tree<Usuario> getHierarquiaComercial(Usuario currentUser, String codUnNeg) {
        SQLiteDatabase db = getDb();
        Tree<Usuario> root = getHierarquiaComercial(db, currentUser, codUnNeg, null);
        return root;
    }


    public String getAllUsersHierarquiaComercial(String codigoUsuario, String codUnNeg){
        StringBuilder query = null;
        query = new StringBuilder();
        Usuario currentUser = get(codigoUsuario);
        try{
            Tree<Usuario> usuarios =  getHierarquiaComercial(currentUser, codUnNeg);

            valueTrue = null;
            valueTrue = new StringBuilder();

            getValueTree(usuarios.getChildren());
            query.append(valueTrue.toString());
        }catch (Exception ex){
            return  codigoUsuario;
        }


        return  query.toString();

    }

    private void getValueTree(List<Tree<Usuario>> item){
        for (int i = 0; i < item.size(); i++) {
            Usuario usuario = item.get(i).getData();
            if (valueTrue.length() > 0) {
                valueTrue.append(",");
            }

            valueTrue.append("'" + usuario.getCodigo() + "'");

            if(item.get(i).getChildren().size() > 0){
                getValueTree(item.get(i).getChildren());
            }
        }
    }


    public  List<Usuario> getListUserTree(List<Tree<Usuario>> item){
        List<Usuario> usuarios = new ArrayList<>();
        return  convertTreeToList(usuarios, item);
    }

    public List<Usuario> convertTreeToList(List<Usuario> usuarios, List<Tree<Usuario>> item){

        for (int i = 0; i < item.size(); i++) {
            Usuario usuario = item.get(i).getData();
            usuarios.add(usuario);

            if(item.get(i).getChildren().size() > 0){
                convertTreeToList(usuarios, item.get(i).getChildren());
            }
        }

        return usuarios;
    }

    private Usuario setAggregatedData(Usuario usuario) {
        if (usuario != null) {
            usuario.setPerfil(perfilDao.get(usuario.getCodigoPerfil()));
        }

        return usuario;
    }

    private ArrayList<Usuario> query(String whereClause, String[] args, String orderBy) {
        ArrayList<Usuario> usuarios = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append("fun.COD_REG_FUNC, ");
        sql.append("fun.NOME_COMPLETO, ");
        sql.append("fun.NOME_ABREV, ");
        sql.append("uac.USR_TXT_EMAIL EMAIL, ");
        sql.append("fun.COD_FUNCAO_PGV, ");
        sql.append("fun.COD_REGIONAL, ");
        sql.append("fun.CPF_CNPJ, ");
        sql.append("uac.USR_INT_PERFIL, ");
        sql.append("fuc.NOME NOME_FUNCAO ");
        sql.append("from TB_DEREGISTRO fun ");
        sql.append("left join TB_DEFUNCAO fuc ");
        sql.append("on fuc.COD_FUNCAO_PGV = fun.COD_FUNCAO_PGV ");
        sql.append("and fuc.DEL_FLAG = '0' ");
        sql.append("inner join TBUSERACCESS uac ");
        sql.append("on uac.USR_INT_CODREGFUNC = fun.COD_REG_FUNC ");

        if (whereClause != null) {
            sql.append(whereClause);
        }

        if (orderBy != null) {
            sql.append(" ");
            sql.append(orderBy);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(sql.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                usuarios.add(new UsuarioDao.UsuarioCursorWrapper(cursor).getUsuario());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return usuarios;
    }

    public boolean isCancel() {
        return isCanceled;
    }

    public void setCancel(boolean cancel) {
        isCanceled = cancel;
    }

    public class UsuarioCursorWrapper extends CursorWrapper {

        public UsuarioCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Usuario getUsuario() {
            Usuario usuario = new Usuario();
            usuario.setCodigo(getString(getColumnIndex("COD_REG_FUNC")));
            usuario.setNome(getString(getColumnIndex("NOME_COMPLETO")));
            usuario.setNomeReduzido(getString(getColumnIndex("NOME_ABREV")));
            usuario.setEmail(getString(getColumnIndex("EMAIL")));
            usuario.setCodigoFuncao(getString(getColumnIndex("COD_FUNCAO_PGV")));
            usuario.setCodigoRegional(getString(getColumnIndex("COD_REGIONAL")));
            usuario.setCpfCnpj(getString(getColumnIndex("CPF_CNPJ")));
            usuario.setCodigoPerfil(getInt(getColumnIndex("USR_INT_PERFIL")));
            usuario.setNomeFuncao(getString(getColumnIndex("NOME_FUNCAO")));

            return usuario;
        }


    }
}
