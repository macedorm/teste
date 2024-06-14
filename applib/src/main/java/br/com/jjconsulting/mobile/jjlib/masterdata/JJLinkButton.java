package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.content.Intent;
import android.view.View;

public class JJLinkButton extends JJBaseView {

    /// Obtém ou define um valor que indica se o controle está habilitado.
    /// Default (true)
    public boolean enable;

    public String toolTip;

    /// Descrição do link
    public String text;

    /// Exibir com estilo de um botão
    public boolean showAsButton;

    /// URL que será usada para link quando um usuário clicar no controle
    public Intent urlAction;

    /// <summary>
    /// Inicializa uma nova instância da classe JJButton
    /// </summary>
    public JJLinkButton()
    {
        this.enable = true;
    }


    public JJLinkButton getClone() {
        JJLinkButton c = new JJLinkButton();
        c.enable = enable;
        c.toolTip = toolTip;
        c.text = text;
        c.showAsButton = showAsButton;
        c.urlAction = urlAction;
        return c;
    }

    protected View renderView(){
        return  null;
    }

}
