package br.com.jjconsulting.mobile.dansales.model;

public class SolicitacaoAlteracaoSenha {

    private String user;
    private String appImei;
    private String appId;
    private String pwdNew;
    private String pwdConfirm;

    public SolicitacaoAlteracaoSenha() {

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAppImei() {
        return appImei;
    }

    public void setAppImei(String appImei) {
        this.appImei = appImei;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPwdNew() {
        return pwdNew;
    }

    public void setPwdNew(String pwdNew) {
        this.pwdNew = pwdNew;
    }

    public String getPwdConfirm() {
        return pwdConfirm;
    }

    public void setPwdConfirm(String pwdConfirm) {
        this.pwdConfirm = pwdConfirm;
    }
}
