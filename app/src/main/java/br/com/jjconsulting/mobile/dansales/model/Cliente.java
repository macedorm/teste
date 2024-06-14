package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public class Cliente implements Serializable {

	public static final int STATUS_CREDITO_GREEN = 0;
	public static final int STATUS_CREDITO_YELLOW = 4;
	public static final int STATUS_CREDITO_RED = 1;
    public static final int STATUS_CREDITO_BLACK = 3;
    public static final int STATUS_CREDITO_BLUE = 2;

    private Bandeira bandeira;
    private CondicaoPagamento condicaoPagamento;
    private PlanoCampo planoCampo;
    private Layout layout;

    private String codigo;
	private String nome;
	private String nomeReduzido;
	private String cnpj;
	private String endereco;
	private String cep;
	private String bairro;
	private String municipio;
	private String uf;
	private String codCanal;
	private String unidadeMedidaPadrao;
	private String codigoBandeira;
    private String codigoCondicaoPagamento;
    private String codigoUnidadeNegocio;
	private String planta;

	private float ultInspetoriaNota;
    private float ultInspetoriaEspaco;
    private float ultCheckListNota;
    private float ultCheckListEspaco;

    private int statusCredito;

	private double latitude;
	private double longitude;
    private double pesoMin;
    private double pesoMax;
    private double valMin;

    private boolean exclusivoPedidoAgenda;
    private boolean selected;

    private boolean isInativo;

    public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeReduzido() {
		return nomeReduzido;
	}

	public void setNomeReduzido(String nomeReduzido) {
		this.nomeReduzido = nomeReduzido;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getCodCanal() {
		return codCanal;
	}

	public void setCodCanal(String codCanal) {
		this.codCanal = codCanal;
	}

	public int getStatusCredito() {
		return statusCredito;
	}

	public void setStatusCredito(int statusCredito) {
		this.statusCredito = statusCredito;
	}

	public boolean isExclusivoPedidoAgenda() {
		return exclusivoPedidoAgenda;
	}

	public void setExclusivoPedidoAgenda(boolean exclusivoPedidoAgenda) {
		this.exclusivoPedidoAgenda = exclusivoPedidoAgenda;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getUnidadeMedidaPadrao() {
		return unidadeMedidaPadrao;
	}

	public void setUnidadeMedidaPadrao(String unidadeMedidaPadrao) {
		this.unidadeMedidaPadrao = unidadeMedidaPadrao;
	}

	public String getCodigoBandeira() {
		return codigoBandeira;
	}

	public void setCodigoBandeira(String codigoBandeira) {
		this.codigoBandeira = codigoBandeira;
	}

	public Bandeira getBandeira() {
		return bandeira;
	}

	public void setBandeira(Bandeira bandeira) {
		this.bandeira = bandeira;
	}

	public String getCodigoCondicaoPagamento() {
		return codigoCondicaoPagamento;
	}

	public void setCodigoCondicaoPagamento(String codigoCondicaoPagamento) {
		this.codigoCondicaoPagamento = codigoCondicaoPagamento;
	}

	public CondicaoPagamento getCondicaoPagamento() {
		return condicaoPagamento;
	}

	public void setCondicaoPagamento(CondicaoPagamento condicaoPagamento) {
		this.condicaoPagamento = condicaoPagamento;
	}

	public double getPesoMin() {
		return pesoMin;
	}

	public void setPesoMin(double pesoMin) {
		this.pesoMin = pesoMin;
	}

    public double getPesoMax() {
        return pesoMax;
    }

    public void setPesoMax(double pesoMax) {
        this.pesoMax = pesoMax;
    }

    public double getValMin() {
		return valMin;
	}

	public void setValMin(double valMin) {
		this.valMin = valMin;
	}

	public String getCodigoUnidadeNegocio() {
		return codigoUnidadeNegocio;
	}

	public void setCodigoUnidadeNegocio(String codigoUnidadeNegocio) {
		this.codigoUnidadeNegocio = codigoUnidadeNegocio;
	}

    public String getPlanta() {
		return planta;
	}

	public void setPlanta(String planta) {
		this.planta = planta;
	}

	public PlanoCampo getPlanoCampo() {
		return planoCampo;
	}

	public void setPlanoCampo(PlanoCampo planoCampo) {
		this.planoCampo = planoCampo;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

    public float getUltInspetoriaNota() {
        return ultInspetoriaNota;
    }

    public void setUltInspetoriaNota(float ultInspetoriaNota) {
        this.ultInspetoriaNota = ultInspetoriaNota;
    }

    public float getUltInspetoriaEspaco() {
        return ultInspetoriaEspaco;
    }

    public void setUltInspetoriaEspaco(float ultInspetoriaEspaco) {
        this.ultInspetoriaEspaco = ultInspetoriaEspaco;
    }

    public float getUltCheckListNota() {
        return ultCheckListNota;
    }

    public void setUltCheckListNota(float ultCheckListNota) {
        this.ultCheckListNota = ultCheckListNota;
    }

    public float getUltCheckListEspaco() {
        return ultCheckListEspaco;
    }

    public void setUltCheckListEspaco(float ultCheckListEspaco) {
        this.ultCheckListEspaco = ultCheckListEspaco;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

	public boolean isInativo() {return isInativo;}

	public void setInativo(boolean isInativo) {
    	this.isInativo = isInativo;
	}
}