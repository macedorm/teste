package br.com.jjconsulting.mobile.dansales.model;

public enum PesquisaPerguntaType {
    MULTI_SELECAO(1),
    SELECAO_UNICA(2),
    LISTA(3),
    CAMPO_MEMO(4),
    TEXTO_LIVRE(5),
    UPLOAD_IMAGEM(6),
    DATA(7),
    NUMERO_DECIMAL(8),
    NUMERO(9),
    MOEDA(10),
    OUTROS(11),
    SORTIMENTO_OBRIGATORIO(12),
    SORTIMENTO_RECOMENDADO(13),
    SORTIMENTO_INOVACAO(14),
    SORTIMENTO_OBRIGATORIO_PRECO(15);


    private final int value;

    private PesquisaPerguntaType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PesquisaPerguntaType getEnumValue(int value) {
        switch (value) {
            case 1:
                return PesquisaPerguntaType.MULTI_SELECAO;
            case 2:
                return PesquisaPerguntaType.SELECAO_UNICA;
            case 3:
                return PesquisaPerguntaType.LISTA;
            case 4:
                return PesquisaPerguntaType.CAMPO_MEMO;
            case 5:
                return PesquisaPerguntaType.TEXTO_LIVRE;
            case 6:
                return PesquisaPerguntaType.UPLOAD_IMAGEM;
            case 7:
                return PesquisaPerguntaType.DATA;
            case 8:
                return PesquisaPerguntaType.NUMERO_DECIMAL;
            case 9:
                return PesquisaPerguntaType.NUMERO;
            case 10:
                return PesquisaPerguntaType.MOEDA;
            case 11:
                return PesquisaPerguntaType.OUTROS;
            case 12:
                return PesquisaPerguntaType.SORTIMENTO_OBRIGATORIO;
            case 13:
                return PesquisaPerguntaType.SORTIMENTO_RECOMENDADO;
            case 14:
                return PesquisaPerguntaType.SORTIMENTO_INOVACAO;
            case 15:
                return PesquisaPerguntaType.SORTIMENTO_OBRIGATORIO_PRECO;
            default:
                return null;

        }
    }
}
