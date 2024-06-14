package br.com.jjconsulting.mobile.dansales.model;


import java.io.Serializable;


public class Usuario implements Serializable {

    private String codigo;
    private String nome;
    private String nomeReduzido;
    private String email;
    private String codigoFuncao;
    private String codigoRegional;
    private String cpfCnpj;
    private String nomeFuncao;

    private int codigoPerfil;
    private Perfil perfil;

    public Usuario() {

    }

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
        return toTitleCase(getNome());
    }

    public void setNomeReduzido(String nomeReduzido) {
        this.nomeReduzido = nomeReduzido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodigoFuncao() {
        return codigoFuncao;
    }

    public void setCodigoFuncao(String codigoFuncao) {
        this.codigoFuncao = codigoFuncao;
    }

    public String getCodigoRegional() {
        return codigoRegional;
    }

    public void setCodigoRegional(String codigoRegional) {
        this.codigoRegional = codigoRegional;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public int getCodigoPerfil() {
        return codigoPerfil;
    }

    public void setCodigoPerfil(int codigoPerfil) {
        this.codigoPerfil = codigoPerfil;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public String getNomeFuncao() {
        return nomeFuncao;
    }

    public void setNomeFuncao(String funcao) {
        this.nomeFuncao = funcao;
    }

    private String toTitleCase(String givenString) {
        if (givenString == null)
            return "";

        StringBuffer sb = new StringBuffer();
        try {
            String[] arr = givenString.split(" ");


            if (arr.length > 0) {
                sb.append(Character.toUpperCase(arr[0].charAt(0)))
                        .append(arr[0].toLowerCase().substring(1)).append(" ");
            }

            if (arr.length > 1) {
                int index = arr.length - 1;
                sb.append(Character.toUpperCase(arr[index].charAt(0)))
                        .append(arr[index].toLowerCase().substring(1)).append(" ");
            }

        } catch (Exception ex) {
            sb.append(givenString);
        }

        return sb.toString().trim();

    }


}
