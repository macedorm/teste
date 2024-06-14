package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResultadoSolicitacaoRecuperacaoSenha implements Serializable {

    @SerializedName("userId")
    private String codigoUsuario;

    @SerializedName("email")
    private String emailUsuario;

    @SerializedName("emailSent")
    private boolean emailEnviado;

    @SerializedName("enableRecoverPasswordInApp")
    private boolean liberadoParaRecuperarSenhaNoApp;

    @SerializedName("hasError")
    private boolean possuiErro;

    @SerializedName("errorMessage")
    private String erro;

    @SerializedName("hasAlert")
    private boolean possuiAviso;

    @SerializedName("alertMessage")
    private String aviso;

    public ResultadoSolicitacaoRecuperacaoSenha() {

    }

    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public boolean isEmailEnviado() {
        return emailEnviado;
    }

    public void setEmailEnviado(boolean emailEnviado) {
        this.emailEnviado = emailEnviado;
    }

    public boolean isLiberadoParaRecuperarSenhaNoApp() {
        return liberadoParaRecuperarSenhaNoApp;
    }

    public void setLiberadoParaRecuperarSenhaNoApp(boolean liberadoParaRecuperarSenhaNoApp) {
        this.liberadoParaRecuperarSenhaNoApp = liberadoParaRecuperarSenhaNoApp;
    }

    public boolean isPossuiErro() {
        return possuiErro;
    }

    public void setPossuiErro(boolean possuiErro) {
        this.possuiErro = possuiErro;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public boolean isPossuiAviso() {
        return possuiAviso;
    }

    public void setPossuiAviso(boolean possuiAviso) {
        this.possuiAviso = possuiAviso;
    }

    public String getAviso() {
        return aviso;
    }

    public void setAviso(String aviso) {
        this.aviso = aviso;
    }
}
