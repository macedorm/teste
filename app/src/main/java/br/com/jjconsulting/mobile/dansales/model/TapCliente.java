package br.com.jjconsulting.mobile.dansales.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class TapCliente implements Serializable {

	private String nomeClie;
	private long pesoMaxOrc;
	private String empresa;
	private int pesoMinimo;
	private String unidMed;
	private String codCanal;
	private int vlrMinOrc;
	private boolean onlyAgenda;
	private boolean isMultiplo;
	private String planta;
	private String condicaoPagPadrao;
	private String condicaoPagSupply;
	private String codBandeira;
	private String cnpj;
	private String filial;
	private String bairro;
	private String endereco;
	private String cep;
	private boolean isSupply;
	private String municipio;
	private String codClie;
	private int saldoSupply;
	private String uf;
	private String nomeReduz;
	private boolean hasDescBaby;
	private String loja;

	public void setNomeClie(String nomeClie){
		this.nomeClie = nomeClie;
	}

	public String getNomeClie(){
		return nomeClie;
	}

	public void setPesoMaxOrc(long pesoMaxOrc){
		this.pesoMaxOrc = pesoMaxOrc;
	}

	public long getPesoMaxOrc(){
		return pesoMaxOrc;
	}

	public void setEmpresa(String empresa){
		this.empresa = empresa;
	}

	public String getEmpresa(){
		return empresa;
	}

	public void setPesoMinimo(int pesoMinimo){
		this.pesoMinimo = pesoMinimo;
	}

	public int getPesoMinimo(){
		return pesoMinimo;
	}

	public void setUnidMed(String unidMed){
		this.unidMed = unidMed;
	}

	public String getUnidMed(){
		return unidMed;
	}

	public void setCodCanal(String codCanal){
		this.codCanal = codCanal;
	}

	public String getCodCanal(){
		return codCanal;
	}

	public void setVlrMinOrc(int vlrMinOrc){
		this.vlrMinOrc = vlrMinOrc;
	}

	public int getVlrMinOrc(){
		return vlrMinOrc;
	}

	public void setOnlyAgenda(boolean onlyAgenda){
		this.onlyAgenda = onlyAgenda;
	}

	public boolean isOnlyAgenda(){
		return onlyAgenda;
	}

	public void setIsMultiplo(boolean isMultiplo){
		this.isMultiplo = isMultiplo;
	}

	public boolean isIsMultiplo(){
		return isMultiplo;
	}

	public void setPlanta(String planta){
		this.planta = planta;
	}

	public String getPlanta(){
		return planta;
	}

	public void setCondicaoPagPadrao(String condicaoPagPadrao){
		this.condicaoPagPadrao = condicaoPagPadrao;
	}

	public String getCondicaoPagPadrao(){
		return condicaoPagPadrao;
	}

	public void setCondicaoPagSupply(String condicaoPagSupply){
		this.condicaoPagSupply = condicaoPagSupply;
	}

	public String getCondicaoPagSupply(){
		return condicaoPagSupply;
	}

	public void setCodBandeira(String codBandeira){
		this.codBandeira = codBandeira;
	}

	public String getCodBandeira(){
		return codBandeira;
	}

	public void setCnpj(String cnpj){
		this.cnpj = cnpj;
	}

	public String getCnpj(){
		return cnpj;
	}

	public void setFilial(String filial){
		this.filial = filial;
	}

	public String getFilial(){
		return filial;
	}

	public void setBairro(String bairro){
		this.bairro = bairro;
	}

	public String getBairro(){
		return bairro;
	}

	public void setEndereco(String endereco){
		this.endereco = endereco;
	}

	public String getEndereco(){
		return endereco;
	}

	public void setCep(String cep){
		this.cep = cep;
	}

	public String getCep(){
		return cep;
	}

	public void setIsSupply(boolean isSupply){
		this.isSupply = isSupply;
	}

	public boolean isIsSupply(){
		return isSupply;
	}

	public void setMunicipio(String municipio){
		this.municipio = municipio;
	}

	public String getMunicipio(){
		return municipio;
	}

	public void setCodClie(String codClie){
		this.codClie = codClie;
	}

	public String getCodClie(){
		return codClie;
	}

	public void setSaldoSupply(int saldoSupply){
		this.saldoSupply = saldoSupply;
	}

	public int getSaldoSupply(){
		return saldoSupply;
	}

	public void setUf(String uf){
		this.uf = uf;
	}

	public String getUf(){
		return uf;
	}

	public void setNomeReduz(String nomeReduz){
		this.nomeReduz = nomeReduz;
	}

	public String getNomeReduz(){
		return nomeReduz;
	}

	public void setHasDescBaby(boolean hasDescBaby){
		this.hasDescBaby = hasDescBaby;
	}

	public boolean isHasDescBaby(){
		return hasDescBaby;
	}

	public void setLoja(String loja){
		this.loja = loja;
	}

	public String getLoja(){
		return loja;
	}
}