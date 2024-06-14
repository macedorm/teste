package br.com.jjconsulting.mobile.jjlib.dao.entity.Action;

public class  SqlCommandAction extends BasicAction {
    private String commandSQL;
    private  boolean applyOnSelected;

    public SqlCommandAction(){
       // setIcon(TIcon.PLAY);
    }

    /**
     *  Recupera Comando SQL a ser executado, sem parsear expression
     */
    public String getCommandSQL() {
        return commandSQL;
    }

    /**
     *  Define Comando SQL a ser executado, aceita expression
     */
    public void setCommandSQL(String commandSQL) {
        this.commandSQL = commandSQL;
    }

    /**
     *  Aplicar somenter as linhas selecionadas (default=false)
     */
    public boolean isApplyOnSelected() {
        return applyOnSelected;
    }

    /**
     * Aplicar somenter as linhas selecionadas (default=false)
     */
    public void setApplyOnSelected(boolean applyOnSelected) {
        this.applyOnSelected = applyOnSelected;
    }
}
