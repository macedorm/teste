package br.com.jjconsulting.mobile.dansales.util;

public class SortimentoUtils {

   public static boolean isItemPrecoSelected(float edv, float promo, String user){
       float preco;

       //Transforma preço digitado pelo usuário em float
       preco = Float.parseFloat(user.replace(",", ".").trim());

       //Se preço digitado pelo usuário for 0 não está no RANGE
       if(preco == 0){
           return false;
       } else {
           //Se edv e promo forem 0 está no RANGE
            if(edv  == 0 && promo == 0){
                return true;
            } else {
                float precoMin;
                float precoMax;

                if(edv > promo){
                    precoMin = promo;
                    precoMax = edv;
                } else {
                    precoMin = edv;
                    precoMax = promo;
                }

                if(preco >= precoMin &&  preco <= precoMax){
                    return true;
                } else {
                    return false;
                }
            }
       }
   }
}
