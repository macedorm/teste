package br.com.jjconsulting.mobile.dansales.database;

public class DanSalesDatabaseSchema {

    public static final class ClienteTable {

        public static final String NAME = "TB_DECLIENTE";

        public static final class Cols {

            public static final String CODIGO = "COD_EMITENTE";
            public static final String LOJA = "";
            public static final String CODIGO_EMPRESA = "";
            public static final String CODIGO_FILIAL = "";
            public static final String CODIGO_BANDEIRA = "COD_BANDEIRA";
            public static final String CODIGO_CANAL = "";
            public static final String CONDICAO_PAGAMENTO_PADRAO = "";
            public static final String NOME = "NOM_CLIENTE";
            public static final String NOME_REDUZIDO = "NOME_ABREVIADO";
            public static final String CNPJ = "NUM_CNPF_CPF";
            public static final String ENDERECO = "DSC_ENDERECO";
            public static final String CEP = "COD_CEP";
            public static final String BAIRRO = "DSC_BAIRRO";
            public static final String CIDADE = "COD_CIDADE";
            public static final String UF = "COD_ESTADO";
            public static final String ATIVO = "";
            public static final String CODIGO_USUARIO = "COD_EMITENTE";
        }
    }

    public static final class ClienteUnidadeNegocioTable {

        public static final String NAME = "TB_DECLIENTEUN";

        public static final class Cols {

            public static final String CODIGO_CLIENTE = "COD_EMITENTE";
            public static final String CODIGO_UNIDADE_NEGOCIO = "COD_UNID_NEGOC";
        }
    }

    public static final class ClienteUnidadeNegocioRegistroTable {

        public static final String NAME = "TB_DECLIUNREG";

        public static final class Cols {

            public static final String CODIGO_CLIENTE = "COD_EMITENTE";
            public static final String CODIGO_UNIDADE_NEGOCIO = "COD_UNID_NEGOC";
            public static final String CODIGO_USUARIO = "COD_REG_FUNC";
        }
    }
}
