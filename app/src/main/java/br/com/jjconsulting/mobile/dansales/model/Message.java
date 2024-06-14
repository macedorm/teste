package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.util.TMessageType;

public class Message implements Comparator<Message> {

    @SerializedName("ID_MENSAGEM")
    private int idMessage;

    @SerializedName("TITULO")
    private String title;

    @SerializedName("MENSAGEM")
    private String body;

    @SerializedName("VIGENCIA_DE")
    private String startDate;

    @SerializedName("VIGENCIA_ATE")
    private String endDate;

    @SerializedName("DATA_ENVIO")
    private String date;

    @SerializedName("REMETENTE")
    private String sender;

    @SerializedName("UNID_NEG")
    private String unidNeg;

    @SerializedName("COD_REG_FUNC")
    private String codRegFunc;

    @SerializedName("DT_ULT_ALT")
    private String dtUltAlt;

    @SerializedName("DEL_FLAG")
    private String delFlag;

    @SerializedName("TIPO")
    private int type;

    @SerializedName("OBRIGATORIO_LEITURA")
    private int obrig;

    private boolean isRead;

    private List<AttachMessage> attachMessage;

    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDate() {
        return date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public TMessageType getType() {
        return TMessageType.fromInteger(type);
    }

    public void setType(TMessageType type) {
        this.type = type.getValue();
    }

    public String getUnidNeg() {
        return unidNeg;
    }

    public void setUnidNeg(String unidNeg) {
        this.unidNeg = unidNeg;
    }

    public String getCodRegFunc() {
        return codRegFunc;
    }

    public void setCodRegFunc(String codRegFunc) {
        this.codRegFunc = codRegFunc;
    }

    public String getDtUltAlt() {
        return dtUltAlt;
    }

    public void setDtUltAlt(String dtUltAlt) {
        this.dtUltAlt = dtUltAlt;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }


    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public List<AttachMessage> getAttachMessage() {
        return attachMessage;
    }

    public void setAttachMessage(List<AttachMessage> attachMessage) {
        this.attachMessage = attachMessage;
    }

    public int getObrig() {
        return obrig;
    }

    public void setObrig(int obrig) {
        this.obrig = obrig;
    }

    @Override
    public int compare(Message abc1, Message abc2){

        boolean b1 = abc1.isRead;
        boolean b2 = abc2.isRead;

        if (b1 == !b2){
            return 1;
        }
        if (!b1 == b2){
            return -1;
        }
        return 0;
    }



}
