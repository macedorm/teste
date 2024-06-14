package br.com.jjconsulting.mobile.dansales.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class TapAcao implements Serializable {

	private String descricao;
	private boolean ativo;
	private boolean anexo;
	private boolean semCateg;
	private int idtpInvest;
	private String cbuAcao;
	private int idAcao;
	private boolean apFin;

	public void setDescricao(String descricao){
		this.descricao = descricao;
	}

	public String getDescricao(){
		return descricao;
	}

	public void setAtivo(boolean ativo){
		this.ativo = ativo;
	}

	public boolean isAtivo(){
		return ativo;
	}

	public void setAnexo(boolean anexo){
		this.anexo = anexo;
	}

	public boolean isAnexo(){
		return anexo;
	}

	public void setSemCateg(boolean semCateg){
		this.semCateg = semCateg;
	}

	public boolean isSemCateg(){
		return semCateg;
	}

	public void setIdtpInvest(int idtpInvest){
		this.idtpInvest = idtpInvest;
	}

	public int getIdtpInvest(){
		return idtpInvest;
	}

	public void setCbuAcao(String cbuAcao){
		this.cbuAcao = cbuAcao;
	}

	public String getCbuAcao(){
		return cbuAcao;
	}

	public void setIdAcao(int idAcao){
		this.idAcao = idAcao;
	}

	public int getIdAcao(){
		return idAcao;
	}

	public void setApFin(boolean apFin){
		this.apFin = apFin;
	}

	public boolean isApFin(){
		return apFin;
	}
}