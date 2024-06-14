package br.com.jjconsulting.mobile.dansales.model;

public class Regiao{
	private String descricao;
	private boolean ativo;
	private String cbuRegiao;
	private int idRegiao;

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

	public void setCbuRegiao(String cbuRegiao){
		this.cbuRegiao = cbuRegiao;
	}

	public String getCbuRegiao(){
		return cbuRegiao;
	}

	public void setIdRegiao(int idRegiao){
		this.idRegiao = idRegiao;
	}

	public int getIdRegiao(){
		return idRegiao;
	}

	@Override
 	public String toString(){
		return 
			"TapRegiao{" +
			"descricao = '" + descricao + '\'' + 
			",ativo = '" + ativo + '\'' + 
			",cbuRegiao = '" + cbuRegiao + '\'' + 
			",idRegiao = '" + idRegiao + '\'' + 
			"}";
		}
}
