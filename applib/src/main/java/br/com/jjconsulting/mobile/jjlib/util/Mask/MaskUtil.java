package br.com.jjconsulting.mobile.jjlib.util.Mask;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TFormComponent;

public class MaskUtil {

    private static final String CEPMask = "#####-###";
    private static final String CPFMask = "###.###.###-##";
    private static final String CNPJMask = "##.###.###/####-##";
    private static final String HOURMaks = "##:##";
    private static final String DATEMask = "##/##/####";
    private static final String DATETIMEMask = "##/##/#### ##:##";
    private static final String TELMask = "(##) #####-####";

    public static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    private static String getDefaultMask(String str) {
        String defaultMask = CPFMask;
        if (str.length() > 11){
            defaultMask = CNPJMask;
        }

        return defaultMask;
    }

    public static TextWatcher insert(final EditText editText, final TFormComponent maskType) {
        return new TextWatcher() {

            boolean isUpdating;
            String oldValue = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = MaskUtil.unmask(s.toString());
                String mask;
                switch (maskType) {
                    case CPF:
                        mask = CPFMask;
                        break;
                    case CNPJ:
                        mask = CNPJMask;
                        break;
                    case CNPJ_CPF:
                        String defaultMask = getDefaultMask(value);

                        switch (value.length()) {
                            case 11:
                                mask = CPFMask;
                                break;
                            case 14:
                                mask = CNPJMask;
                                break;
                            default:
                                mask = defaultMask;
                                break;
                        }
                        break;
                    case CEP:
                        mask = CEPMask;
                        break;
                    case HOUR:
                        mask = HOURMaks;
                        break;
                    case DATE:
                        mask = DATEMask;
                        break;
                    case DATETIME:
                        mask = DATETIMEMask;
                        break;
                    case TEL:
                        mask = TELMask;
                        break;
                    default:
                        mask = getDefaultMask(value);
                        break;
                }

                String maskAux = "";
                if (isUpdating) {
                    oldValue = value;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if ((m != '#' && value.length() > oldValue.length()) || (m != '#' && value.length() < oldValue.length() && value.length() != i)) {
                        maskAux += m;
                        continue;
                    }

                    try {
                        maskAux += value.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(maskAux);
                editText.setSelection(maskAux.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        };
    }


}