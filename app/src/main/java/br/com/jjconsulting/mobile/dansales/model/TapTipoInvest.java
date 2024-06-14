package br.com.jjconsulting.mobile.dansales.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class TapTipoInvest implements Serializable{

	private String descricao;
	private int idTipoInvest;
	private boolean ativo;
	private String cbuRegiao;
	private boolean valorEstimado;
	private String vlest;

	public void setDescricao(String descricao){
		this.descricao = descricao;
	}

	public String getDescricao(){
		return descricao;
	}

	public void setIdTipoInvest(int idTipoInvest){
		this.idTipoInvest = idTipoInvest;
	}

	public int getIdTipoInvest(){
		return idTipoInvest;
	}

	public void setAtivo(boolean ativo){
		this.ativo = ativo;
	}

	public boolean isAtivo(){
		return ativo;
	}

	public void setCbuRegiao(String cbuRegiao){
		this.cbuRegiao = cbuRegiao;
	}

	public String getCbuRegiao(){
		return cbuRegiao;
	}

	public void setValorEstimado(boolean valorEstimado){
		this.valorEstimado = valorEstimado;
	}

	public boolean isValorEstimado(){
		return valorEstimado;
	}

	public String getVlest() {
		return vlest;
	}

	public void setVlest(String vlest) {
		this.vlest = vlest;
	}
}