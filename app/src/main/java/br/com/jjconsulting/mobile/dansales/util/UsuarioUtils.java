package br.com.jjconsulting.mobile.dansales.util;

public enum UsuarioUtils {
    OTHERS(1),
    PROMOTOR(46),
    GA(6),
    SUPERVISOR(5),
    GR(3);

    private int intValue;

    UsuarioUtils(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }


    public static UsuarioUtils fromInteger(int value){
        UsuarioUtils usuarioUtils = UsuarioUtils.PROMOTOR;

        switch (value) {
            case 46:
                usuarioUtils = UsuarioUtils.PROMOTOR;
                break;
            case 6:
                usuarioUtils = UsuarioUtils.GA;
                break;
            case 3:
                usuarioUtils = UsuarioUtils.GR;
                break;
            case 5:
                usuarioUtils = UsuarioUtils.SUPERVISOR;
                break;
            default:
                usuarioUtils = UsuarioUtils.OTHERS;
                    break;
        }

        return usuarioUtils;
    }

    public static boolean isPromGaGr(String codRegFunc) {
        boolean confirm = false;
        switch (UsuarioUtils.fromInteger(Integer.parseInt(codRegFunc))){
            case GA:
            case GR:
            case PROMOTOR:
            case SUPERVISOR:
                confirm = true;
                break;
        }
        return confirm;
    }

    public static boolean isPromotor(String codFunc){
        if(UsuarioUtils.fromInteger(Integer.parseInt(codFunc)) == UsuarioUtils.PROMOTOR){
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPromGa(String codRegFunc) {
        boolean confirm = false;
        switch (UsuarioUtils.fromInteger(Integer.parseInt(codRegFunc))){
            case GA:
            case PROMOTOR:
            case SUPERVISOR:
                confirm = true;
                break;
        }

        return confirm;
    }

    public static boolean isGa(String codRegFunc) {
        boolean confirm = false;
        switch (UsuarioUtils.fromInteger(Integer.parseInt(codRegFunc))){
            case GA:
                confirm = true;
                break;
        }

        return confirm;
    }
}
