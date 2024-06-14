package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class RotaTarefas implements Serializable {

    @SerializedName("TSK_TXT_ID")
	private String id;
	@SerializedName("TSK_TXT_IDITEM")
	private String idItem;
	@SerializedName("TSK_INT_TYPE")
	private int status;
    @SerializedName("DT_ULT_ALT")
	private String dtUltAlt;
    @SerializedName("TSK_DAT_DIAPLANO")
    private String dataRota;
    @SerializedName("TSK_TXT_CODCLI")
    private String codClie;
    @SerializedName("COD_REG_FUNC")
    private String codRegFunc;
    @SerializedName("COD_UNID_NEGOC")
    private String codUnNeg;
    @SerializedName("TSK_INT_JUSTIF_ATIV")
    private int atividJust ;

    protected boolean isInvisible;

	protected Pedido pedido;
	protected Pesquisa pesquisa;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }


	public String getDtUltAlt() {
		return dtUltAlt;
	}

	public void setDtUltAlt(String dtUltAlt) {
		this.dtUltAlt = dtUltAlt;
	}

	public RotaGuiadaTaskType getStatus() {
		return RotaGuiadaTaskType.getRotaGuiadaTaskType(status);
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Pesquisa getPesquisa() {
		return pesquisa;
	}

	public void setPesquisa(Pesquisa pesquisa) {
		this.pesquisa = pesquisa;
	}

    public String getDataRota() {
        return dataRota;
    }

    public void setDataRota(String dataRota) {
        this.dataRota = dataRota;
    }

    public String getCodClie() {
        return codClie;
    }

    public void setCodClie(String codClie) {
        this.codClie = codClie;
    }

    public String getCodRegFunc() {
        return codRegFunc;
    }

    public void setCodRegFunc(String codRegFunc) {
        this.codRegFunc = codRegFunc;
    }

    public String getCodUnNeg() {
        return codUnNeg;
    }

    public void setCodUnNeg(String codUnNeg) {
        this.codUnNeg = codUnNeg;
    }

    public boolean isInvisible() {
        return isInvisible;
    }

    public void setInvisible(boolean invisible) {
        isInvisible = invisible;
    }

    public int getAtividJust() {
        return atividJust;
    }

    public void setAtividJust(int atividJust) {
        this.atividJust = atividJust;
    }


}