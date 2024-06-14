package br.com.jjconsulting.mobile.jjlib.view.calendarEventView;

public class JJCalendarEvent {
    private String event;
    private String date;

    private String id;
    private String codCliente;
    private String name;
    private String adresss;
    private String uf;
    private String city;
    private String userID;
    private String promotor;
    private String promotorName;
    private String hoursStart;
    private String hoursEnd;
    private String note;
    private String nameType;
    private String unNeg;

    private boolean isRoute;
    private boolean isAdd;

    private int status;
    private int type;

    public static JJCalendarEvent copy( JJCalendarEvent other ) {
        JJCalendarEvent newEvent = new JJCalendarEvent();
        newEvent.date = other.date;
        newEvent.event = other.event;

        newEvent.id = other.id;
        newEvent.codCliente = other.codCliente;
        newEvent.name = other.name;
        newEvent.adresss = other.adresss;
        newEvent.uf = other.uf;
        newEvent.city = other.city;
        newEvent.userID = other.userID;
        newEvent.promotor = other.promotor;
        newEvent.promotorName = other.promotorName;
        newEvent.hoursStart = other.hoursStart;
        newEvent.hoursEnd = other.hoursEnd;
        newEvent.note = other.note;
        newEvent.nameType = other.nameType;
        newEvent.unNeg = other.unNeg;
        newEvent.isRoute = other.isRoute;
        newEvent.isAdd = other.isAdd;
        newEvent.status = other.status;
        newEvent.type = other.type;
        newEvent.type = other.type;

        return newEvent;
    }


    public JJCalendarEvent(String event, String date, int status) {
        this.event = event;
        this.date = date;
        this.status = status;
    }

    public JJCalendarEvent() {
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdresss() {
        return adresss;
    }

    public void setAdresss(String adresss) {
        this.adresss = adresss;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isRoute() {
        return isRoute;
    }

    public void setRoute(boolean route) {
        isRoute = route;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(String codCliente) {
        this.codCliente = codCliente;
    }

    public String getHoursStart() {
        return hoursStart;
    }

    public void setHoursStart(String hoursStart) {
        this.hoursStart = hoursStart;
    }

    public String getHoursEnd() {
        return hoursEnd;
    }

    public void setHoursEnd(String hoursEnd) {
        this.hoursEnd = hoursEnd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPromotor() {
        return promotor;
    }

    public void setPromotor(String promotor) {
        this.promotor = promotor;
    }

    public String getPromotorName() {
        return promotorName;
    }

    public void setPromotorName(String promotorName) {
        this.promotorName = promotorName;
    }

    public String getNameType() {
        return nameType;
    }

    public void setNameType(String nameType) {
        this.nameType = nameType;
    }

    public String getUnNeg() {
        return unNeg;
    }

    public void setUnNeg(String unNeg) {
        this.unNeg = unNeg;
    }
}
