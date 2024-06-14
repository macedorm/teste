package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;
import java.util.Date;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TRegSync;

public class PesquisaResposta implements Serializable {

    private int codigoPesquisa;
    private int codigoPergunta;
    private String codigoUsuario;
    private String codigoCliente;
    private String resposta;
    private String opcao;
    private Date data;
    private String freq;
    private String extensaoArquivo;
    private double posLatitute;
    private double posLongitude;
    private TRegSync regSync;

    public PesquisaResposta() {
        regSync = TRegSync.INSERT;
    }

    public int getCodigoPesquisa() {
        return codigoPesquisa;
    }

    public void setCodigoPesquisa(int codigoPesquisa) {
        this.codigoPesquisa = codigoPesquisa;
    }

    public int getCodigoPergunta() {
        return codigoPergunta;
    }

    public void setCodigoPergunta(int codigoPergunta) {
        this.codigoPergunta = codigoPergunta;
    }

    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public String getOpcao() {
        return opcao;
    }

    public double getPosLatitute() {
        return posLatitute;
    }

    public void setPosLatitute(double posLatitute) {
        this.posLatitute = posLatitute;
    }

    public double getPosLongitude() {
        return posLongitude;
    }

    public void setPosLongitude(double posLongitude) {
        this.posLongitude = posLongitude;
    }

    public void setOpcao(String opcao) {
        this.opcao = opcao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getExtensaoArquivo() {
        return extensaoArquivo;
    }

    public void setExtensaoArquivo(String extensaoArquivo) {
        this.extensaoArquivo = extensaoArquivo;
    }

    public TRegSync getRegSync() {
        return regSync;
    }

    public void setRegSync(TRegSync regSync) {
        this.regSync = regSync;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }
}
