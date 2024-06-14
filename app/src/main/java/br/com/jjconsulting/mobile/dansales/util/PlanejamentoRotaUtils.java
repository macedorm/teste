package br.com.jjconsulting.mobile.dansales.util;

public enum PlanejamentoRotaUtils {
    VISTA(1),
    VISITAPROMOTOR(2),
    COACHINGPORMOTOR(3),
    ROTINAADM(4);

    private int intValue;

    PlanejamentoRotaUtils(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static final int[] filter = new int[] {
            PlanejamentoRotaUtils.VISTA.getValue(),
            PlanejamentoRotaUtils.VISITAPROMOTOR.getValue(),
            PlanejamentoRotaUtils.COACHINGPORMOTOR.getValue(),
    };

    public static PlanejamentoRotaUtils fromInteger(int value){
        PlanejamentoRotaUtils planejamentoRotaUtils = PlanejamentoRotaUtils.ROTINAADM;
        switch (value) {
            case 1:
                planejamentoRotaUtils = planejamentoRotaUtils.VISTA;
                break;
            case 2:
                planejamentoRotaUtils = planejamentoRotaUtils.VISITAPROMOTOR;
                break;
            case 3:
                planejamentoRotaUtils = planejamentoRotaUtils.COACHINGPORMOTOR;
                break;

        }

        return planejamentoRotaUtils;
    }

}
