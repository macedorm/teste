package br.com.jjconsulting.mobile.jjlib.masterdata;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TPageState;
import br.com.jjconsulting.mobile.jjlib.model.DataItem;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ExpressionManager {

    public Hashtable userValues;
    private ArrayList<DataItem> dataAccess;
    private ScriptEngine engine;


    public ExpressionManager(Hashtable userValues, ArrayList<DataItem> dataAccess)
    {
        this.userValues = userValues;
        this.dataAccess = dataAccess;
    }

    public ExpressionManager(){

    }

    public String parseExpression(String expression, TPageState state){
      String exp = parseExpression(expression, state,false, null);
      exp = exp.replace("{", "");
      exp = exp.replace("}", "");
      return exp;
    }

    public String parseExpression(String expression, TPageState state, boolean quotationMarks, Hashtable formValues) {
        String parsedExpression = expression
                .replace("val:", "")
                .replace("exp:", "")
                .replace("sql:", "")
                .replace("protheus:", "")
                .trim();

        String item = "";
        List<String> list = new ArrayList<>();

        for(int ind = 0; ind < expression.length(); ind++) {
            if (expression.substring(ind, ind+1).equals("{")) {
                item = "";
            } else if (expression.substring(ind, ind+1).equals("}")) {
                list.add(item);
            }else {
                item += expression.substring(ind, ind+1);
            }
        }

        for (String field: list) {

            String val = null;
            if (userValues != null && userValues.contains(field)) {
                //Valor customizado pelo usuário
                val = String.format("{0}", userValues.get(field).toString());
            }
            else if (formValues != null && formValues.containsKey(field)){
                //Valor nos campos do form
                val = String.format("%s", formValues.get(field).toString());
            }
            else if ("pagestate".equals(field.toString().toLowerCase())) {
                //Estado da Pagina
                val = String.format("%s", state.toString());
            }

            if (val != null) {
                // Note: Use "{{" to denote a single "{"
                if (quotationMarks)
                    val = "'" + val + "'";

                parsedExpression = parsedExpression.replace(String.format("{%s}", field), val);
            }

        }

        return parsedExpression;
    }


    public boolean getBoolValue(String expression, String actionName, TPageState state, Hashtable formValues, boolean isEnableType) {
        boolean result = false;

        if(Config.REGSYC.equals(actionName))
            return false;

        if (state == TPageState.VIEW && isEnableType)
            return false;

        if (expression.startsWith("val:")) {
            result = parseBool(expression);
        } else if (expression.startsWith("exp:")) {
            try {
                String exp = parseExpression(expression, state, true, formValues); ;

                try {
                    exp = exp.replace("<>", "!=");
                    exp = exp.replace("=", "==");
                    result = (boolean) getEngine().eval(exp);
                } catch (ScriptException e) {
                    LogUser.log(Config.TAG, e.toString());
                }
            }
            catch (Exception ex) {
                throw new IllegalArgumentException(String.format("Erro ao executar a expressão para o campo %s. %s", actionName, ex.toString()), new Throwable("VisibleExpression"));
            }
        } else {
            throw new IllegalArgumentException(String.format("Expressão não inciada com [val:] ou [exp:] para o campo %s", actionName), new Throwable("VisibleExpression"));
        }

        return result;
    }


    protected boolean parseBool(String value) {

        // 1
        // Avoid exceptions
        if (value == null) {
            return false;
        }

        // 2
        // Remove whitespace from string
        value = value.replace("val:", "").trim();

        // 3
        // Lowercase the string
        value = value.toLowerCase();

        // 4
        // Check for word true
        if (value.equals("true"))
        {
            return true;
        }

        // 5
        // Check for letter true
        if (value.equals("t")){
            return true;
        }

        // 6
        // Check for one
        if (value.equals("1")) {
            return true;
        }

        // 7
        // Check for word yes
        if (value.equals("yes")) {
            return true;
        }

        // 8
        // Check for letter yes
        if (value.equals("y")) {
            return true;
        }

        // 9
        // It is false
        return false;

    }

    public ScriptEngine getEngine(){
        if(engine == null){
            ScriptEngineManager manager = new ScriptEngineManager();
            engine = manager.getEngineByName("js");
        }
        return engine;
    }

}
