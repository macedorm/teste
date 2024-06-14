package br.com.jjconsulting.mobile.jjlib.masterdata;

import java.util.Hashtable;

import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElementField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TFormComponent;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TPageState;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;
import br.com.jjconsulting.mobile.jjlib.util.ValidField;

public class FieldManager {

    protected ExpressionManager expression;
    private ValidField validField;

    public FieldManager(){
        expression = new ExpressionManager();
        validField = new ValidField();
    }

    public boolean isVisible(FormElementField f, TPageState state, Hashtable formValues) {
        return expression.getBoolValue(f.visibleExpression, f.getFieldname(), state, formValues, false);
    }

    public boolean isEnable(FormElementField f, TPageState state, Hashtable formValues) {
        return expression.getBoolValue(f.enableExpression, f.getFieldname(), state, formValues, true);
    }

    private String configExpresision(String exp){
        exp = exp.replace("<>", "!=");
        exp = exp.replace("=", "==");
        return exp;
    }




    public String validateField(FormElementField field, String objname, String value) {
        String err = null;

        if (TextUtils.isNullOrEmpty(value)) {
            if (field.getIsrequired() || field.getIspk())
                err =  String.format("Campo %s obrigatório", field.getLabel());
        }
        else
        {
            if(field.getDatatype() != null) {
                //Validate DataType
                switch (field.getDatatype()) {
                    case DATE:
                    case DATETIME:
                        if (!validField.isValidDate(value, "dd/MM/yyyy")) {
                            err = String.format("Campo %s  inválido", field.getLabel());
                        }
                        break;
                    case INT:
                        try {
                            int nInt = Integer.parseInt(value);
                        } catch (Exception ex) {
                            err = String.format("Campo %s : número inválido", field.getLabel());
                        }

                        break;
                    case FLOAT:
                        try {
                            double nDbl = Double.parseDouble(value.replace(",", "."));
                        } catch (Exception ex) {
                            err = String.format("Campo %s : número inválido", field.getLabel());
                        }
                        break;
                    default:
                        if (value.length() > field.getSize() && field.getSize() > 0)
                            err = String.format("Campo %s não pode conter mais de %d caracteres", field.getLabel(), field.getSize());
                        break;
                }
            }

            //Validate Component Requerements
            if (TextUtils.isNullOrEmpty(err))
            {
                switch (TFormComponent.fromInteger(field.getComponent()))
                {
                    case EMAIL:
                        if (!validField.isValidEmail(value))
                            err = messageError(field.getLabel());
                        break;
                    case HOUR:
                        if(!validField.isValidDate(value, "HH:mm")){
                            err = messageError(field.getLabel());
                        }

                        break;
                    case CNPJ:
                        if (!validField.isCNPJ(value)) {
                            err = messageError(field.getLabel());
                        }
                        break;
                    case CPF:
                        if (!validField.isCPF(value)) {
                            err = messageError(field.getLabel());
                        }
                        break;
                    case CNPJ_CPF:
                        if (!validField.isCPF(value) && !validField.isCNPJ(value)) {
                            err = messageError(field.getLabel());
                        }
                        break;
                    case TEL:
                        if (!validField.idValidPhone(value)) {
                            err = messageError(field.getLabel());
                        }
                        break;

                }
            }
        }

        return err;
    }

    private String messageError(String field){
       return String.format("Campo %s com valor inválido", field);
    }


    public String formatVal(Object value, FormElementField f)
    {
        if (value == null)
            return "";

        String sVal = value.toString();
        if (TextUtils.isNullOrEmpty(sVal))
            return "";

        TField type = f.getDatatype();
        switch (TFormComponent.fromInteger(f.getComponent())) {
            case CNPJ:
                sVal = sVal.replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})",
                        "$1.$2.$3/$4-$5");
            case CPF:
                sVal = sVal.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1.$2.$3-$4");
                break;
            case CNPJ_CPF:
                if(sVal.length() < 12){
                    sVal.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1.$2.$3-$4");
                } else {
                    sVal = sVal.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1.$2.$3-$4");
                }
                break;
            case NUMBER:
                switch (type){
                    case INT:
                        Double valuedouble = Double.parseDouble(sVal);
                        sVal = String.valueOf(valuedouble.intValue());
                        break;
                }
                break;
            case DATE:
                try {
                    sVal =  FormatUtils.toConvertDate(sVal.replace("T", " "), "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy");
                }catch (Exception ex){
                    sVal = "";
                }
                break;
            case DATETIME:
            case TEXT:
                if(type == TField.INT || type == TField.FLOAT) {
                    Double valuedouble = Double.parseDouble(sVal);
                    sVal = valuedouble.intValue() + "";
                }
                break;
        }
        return sVal;
    }
}
