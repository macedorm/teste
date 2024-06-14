package br.com.jjconsulting.mobile.dansales.util;

public class UnidadeNegocioUtils {

    private UnidadeNegocioUtils() {

    }

    /**
     * Returns an array representing empresa and filial. <br>
     * Index 0 = empresa, index 1 = filial.
     */
    public static String[] getCodigoEmpresaFilial(String unidadeNegocio) {
        if (unidadeNegocio == null) {
            throw new NullPointerException("unidadeNegocio");
        }

        if (unidadeNegocio.length() != 4) {
            throw new IllegalArgumentException("unidadeNegocio should have 4 characters");
        }

        return new String[] { unidadeNegocio.substring(0, 2), unidadeNegocio.substring(2) };
    }
}
