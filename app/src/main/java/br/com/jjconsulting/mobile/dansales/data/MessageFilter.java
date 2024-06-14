package br.com.jjconsulting.mobile.dansales.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.TMessageType;

public class MessageFilter implements Serializable {

    private TMessageType tMessageType;

    public MessageFilter() {
    }

    public MessageFilter(TMessageType tMessageType) {
        this.tMessageType = tMessageType;
    }

    public TMessageType getTMessageType() {
        return tMessageType;
    }

    public void settMessageType(TMessageType tMessageType) {
        this.tMessageType = tMessageType;
    }

    public boolean isEmpty() {
        return tMessageType == null;
    }
}