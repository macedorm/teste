package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class Rotas implements Serializable {

    private Cliente cliente;
	@SerializedName("RG_DAT_CHECKIN")
	private String checkin;
	@SerializedName("RG_DAT_CHECKOUT")
	private String checkout;
	@SerializedName("RG_FLO_DIFF_CHECKIN_OUT")
	private float diffCheck;
	@SerializedName("RG_TXT_CODCLI")
	private String codCliente;
	@SerializedName("RG_DAT_DIAPLANO")
	private String date;
	@SerializedName("RG_INT_STATUS")
	private int status;
	@SerializedName("RG_INT_ISROTA")
	private String isRota;
	@SerializedName("RG_TXT_CHECKIN_DENTRO")
	private String chekinDentro;
	@SerializedName("COD_REG_FUNC")
	private String codRegFunc;
	@SerializedName("RG_INT_JUSTIF_VISITA")
	private int justifVisita;
	@SerializedName("RG_INT_JUSTIF_PEDIDO")
	private int justifPedido;
	@SerializedName("RG_TXT_JUSTIF_ATIV_OBRIG")
	private String justifAtivObrig;
    @SerializedName("RG_FLO_DIFF_PAUSE")
    private float diffPause;
    @SerializedName("DT_ULT_ALT")
	private String dtUltAlt;
	@SerializedName("COD_UNID_NEGOC")
	private String codUnidNeg;
	private String nomeUser;

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isRota() {
		if(TextUtils.isNullOrEmpty(isRota)){
			 return false;
		} else {
			return isRota.equals("1");
		}
	}
	public void setRota(String rota) {
		isRota = rota;
	}

	public String getCheckin() {
		return checkin;
	}

	public void setCheckin(String checkin) {
		this.checkin = checkin;
	}

	public String getCheckout() {
		return checkout;
	}

	public void setCheckout(String checkout) {
		this.checkout = checkout;
	}

	public float getDiffCheck() {
		return diffCheck;
	}

	public void setDiffCheck(float diffCheck) {
		this.diffCheck = diffCheck;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCodCliente() {
		return codCliente;
	}

	public void setCodCliente(String codCliente) {
		this.codCliente = codCliente;
	}

	public boolean isCheckinDentro() {
		if(TextUtils.isNullOrEmpty(chekinDentro)){
			return false;
		} else {
			return chekinDentro.equals("1");
		}
	}

	public void setChekinDentro(String chekinDentro) {
		this.chekinDentro = chekinDentro;
	}

	public String getIsRota() {
		return isRota;
	}

	public void setIsRota(String isRota) {
		this.isRota = isRota;
	}

	public int getJustifVisita() {
		return justifVisita;
	}

	public void setJustifVisita(int justifVisita) {
		this.justifVisita = justifVisita;
	}

	public int getJustifPedido() {
		return justifPedido;
	}

	public void setJustifPedido(int justifPedido) {
		this.justifPedido = justifPedido;
	}

	public String getJustifAtivObrig() {
		return justifAtivObrig;
	}

	public void setJustifAtivObrig(String justifAtivObrig) {
		this.justifAtivObrig = justifAtivObrig;
	}

	public String getNomeUser() {
		return nomeUser;
	}

	public void setNomeUser(String nomeUser) {
		this.nomeUser = nomeUser;
	}

    public float getDiffPause() {
        return diffPause;
    }

    public void setDiffPause(float diffPause) {
        this.diffPause = diffPause;
    }


    public String getDtUltAlt() {
		return dtUltAlt;
	}

	public void setDtUltAlt(String dtUltAlt) {
		this.dtUltAlt = dtUltAlt;
	}

	public String getCodRegFunc() {
		return codRegFunc;
	}

	public void setCodRegFunc(String codRegFunc) {
		this.codRegFunc = codRegFunc;
	}

	public String getCodUnidNeg() {
		return codUnidNeg;
	}

	public void setCodUnidNeg(String codUnidNeg) {
		this.codUnidNeg = codUnidNeg;
	}

}
