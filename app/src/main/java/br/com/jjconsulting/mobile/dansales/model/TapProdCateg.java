package br.com.jjconsulting.mobile.dansales.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class TapProdCateg implements Serializable {

	private String descricao;
	private int idCategProd;
	private boolean ativo;
	private String cbuCategProd;

	public void setDescricao(String descricao){
		this.descricao = descricao;
	}

	public String getDescricao(){
		return descricao;
	}

	public void setIdCategProd(int idCategProd){
		this.idCategProd = idCategProd;
	}

	public int getIdCategProd(){
		return idCategProd;
	}

	public void setAtivo(boolean ativo){
		this.ativo = ativo;
	}

	public boolean isAtivo(){
		return ativo;
	}

	public void setCbuCategProd(String cbuCategProd){
		this.cbuCategProd = cbuCategProd;
	}

	public String getCbuCategProd(){
		return cbuCategProd;
	}
}