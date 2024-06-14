package br.com.jjconsulting.mobile.jjlib.dao.entity;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.jjlib.dao.DataAccessCommand;

public class FormElementDataItem {
    private DataAccessCommand command;
    private ArrayList<DataItemValue> itens;
    public TFirstOption firstOption;

    public boolean replaceTextOnGrid;

    @SerializedName("showimagelegend")
    public boolean showImageLegend;


    public FormElementDataItem()
    {
        this.firstOption = TFirstOption.NONE;
        this.replaceTextOnGrid = true;
    }

    public DataAccessCommand getCommand() {
        if (command == null)
            command = new DataAccessCommand();

        return command;
    }

    public void setCommand(DataAccessCommand command) {
        this.command = command;
    }

    public ArrayList<DataItemValue> getItens() {
        if (itens == null)
            itens = new ArrayList<>();

        return itens;
    }

    public void setItens(ArrayList<DataItemValue> itens) {
        this.itens = itens;
    }

    public TFirstOption getFirstOption() {
        return firstOption;
    }

    public void setFirstOption(TFirstOption firstOption) {
        this.firstOption = firstOption;
    }

    public boolean isReplaceTextOnGrid() {
        return replaceTextOnGrid;
    }

    public void setReplaceTextOnGrid(boolean replaceTextOnGrid) {
        this.replaceTextOnGrid = replaceTextOnGrid;
    }
}
