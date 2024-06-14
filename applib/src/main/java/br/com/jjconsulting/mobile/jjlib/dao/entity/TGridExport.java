package br.com.jjconsulting.mobile.jjlib.dao.entity;

public enum TGridExport {
    XLS(1),
    PDF(2),
    CSV(3),
    TXT(4),
    HTML(5);

    private int intValue;

    TGridExport(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TGridExport fromInteger(int x) {
        switch(x) {
            case 1:
                return XLS;
            case 2:
                return PDF;
            case 3:
                return CSV;
            case 4:
                return TXT;
            case 5:
                return HTML;
        }
        return null;
    }
}
