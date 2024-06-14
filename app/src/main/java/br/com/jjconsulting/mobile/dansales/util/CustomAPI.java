package br.com.jjconsulting.mobile.dansales.util;

public class CustomAPI {
    public static final String API_ATTACH = "mensagem/download.aspx?id=";
    public static final String API_ATTACH_CR = "cr/jjc_3770_cr_info_image.ashx";
    public static final String API_DOWNLOAD_APK = "/download/dansales.apk";
    public static final String API_TAP = "api/tap";
    public static final String API_TAPSALDO = "api/tapsaldo";
    public static final String API_PESQUISA = "api/pesquisaResposta";
    public static final String API_RESUME_STORE = "api/pesquisaChecklist";
    public static final String API_RESUME_ALL = "api/pesquisaCoaching";
    public static final String API_ISSAC = "/isaac/conversation.aspx";

    public static final String SOAPACTION = "http://tempuri.org/IEntregas/ConsultaSituacaoNotaFiscal";
    public static final String SOAPACTIONCHAT = "http://tempuri.org/IEntregas/EnviarMensagemChatEntrega";


    private static final String BODY_SOAP_NF =  "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" " +
            "xmlns:dom=\"http://schemas.datacontract.org/2004/07/Dominio.ObjetosDeValor.WebService.Entrega\">" +
            "<soapenv:Header>" +
             "<Token xmlns=\"Token\">&Token&</Token>" +
           "</soapenv:Header>" +
           "<soapenv:Body>" +
            "  <tem:ConsultaSituacaoNotaFiscal>" +
             "    <!--Optional:-->" +
             "    <tem:consultaSituacaoNotaFiscal>" +
                    "<!--Optional:-->" +
                    "<dom:NumeroNota>&numeroNota&</dom:NumeroNota>" +
                    "<!--Optional:-->" +
                    "<dom:SerieNota>&serieNota&</dom:SerieNota>" +
                    "<dom:CNPJEmissor>&cnpj&</dom:CNPJEmissor>" +
                    "</tem:consultaSituacaoNotaFiscal>" +
              "</tem:ConsultaSituacaoNotaFiscal>" +
           "</soapenv:Body>" +
        "</soapenv:Envelope>" ;


    private static final String BODY_SOAP_CHAT = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\"\n" +
            "    xmlns:dom=\"http://schemas.datacontract.org/2004/07/Dominio.ObjetosDeValor.WebService.Entrega\"\n" +
            "    xmlns:dom1=\"http://schemas.datacontract.org/2004/07/Dominio.ObjetosDeValor.Embarcador.NFe\" xmlns:dom2=\"http://schemas.datacontract.org/2004/07/Dominio.ObjetosDeValor.Embarcador.Carga\" xmlns:dom3=\"http://schemas.datacontract.org/2004/07/Dominio.ObjetosDeValor.Embarcador.Pessoas\" xmlns:dom4=\"http://schemas.datacontract.org/2004/07/Dominio.ObjetosDeValor.Embarcador.Localidade\" xmlns:dom5=\"http://schemas.datacontract.org/2004/07/Dominio.ObjetosDeValor\" xmlns:arr=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\" xmlns:dom6=\"http://schemas.datacontract.org/2004/07/Dominio.ObjetosDeValor.Embarcador.Pedido\" xmlns:dom7=\"http://schemas.datacontract.org/2004/07/Dominio.ObjetosDeValor.Embarcador.Filial\" xmlns:dom8=\"http://schemas.datacontract.org/2004/07/Dominio.ObjetosDeValor.Embarcador.Frota\">\n" +
            "    <soapenv:Header><Token xmlns=\"Token\">&Token&</Token></soapenv:Header>\n" +
            "    <soapenv:Body>\n" +
            "      <tem:EnviarMensagemChatEntrega>\n" +
            "         <tem:mensagemChat>\n" +
            "            <dom:Mensagem>&mensagem&</dom:Mensagem>\n" +
            "            <dom:NotaFiscal>\n" +
            "                <dom1:Chave>&chave&</dom1:Chave>\n" +
            "                <dom1:Destinatario>\n" +
            "                  <dom3:CPFCNPJ>&cnpjDestinatario&</dom3:CPFCNPJ>\n" +
            "                </dom1:Destinatario>\n" +
            "                <dom1:Emitente>\n" +
            "                  <dom3:CPFCNPJ>&cnpjEmitente&</dom3:CPFCNPJ>\n" +
            "               </dom1:Emitente>\n" +
            "               <dom1:Numero>&numeroNota&</dom1:Numero>\n" +
            "               <dom1:Serie>&serieNota&</dom1:Serie>\n" +
            "            </dom:NotaFiscal>\n" +
            "            <dom:Usuario>\n" +
            "               <dom:CPF></dom:CPF>\n" +
            "               <dom:CodigoIntegracao>&codigoIntegracao&</dom:CodigoIntegracao>\n" +
            "            </dom:Usuario>\n" +
            "         </tem:mensagemChat>\n" +
            "      </tem:EnviarMensagemChatEntrega>\n" +
            "   </soapenv:Body></soapenv:Envelope>";


    public static String getBodySoapNF(String token, String numeroNota, String serieNota, String cnpj){
        String xml = BODY_SOAP_NF;
        xml = xml.replace("&Token&", token);
        xml = xml.replace("&numeroNota&", numeroNota);
        xml = xml.replace("&serieNota&", serieNota);
        xml = xml.replace("&cnpj&", cnpj);

        return xml;
    }

    public static String getBodySoapChat(String token, String numeroNota, String serieNota, String chave,String cnpjDestinatario,  String cnpjEmitente, String codigoIntegracao, String mensagem){
        String xml = BODY_SOAP_CHAT;
        xml = xml.replace("&Token&", token);
        xml = xml.replace("&numeroNota&", numeroNota);
        xml = xml.replace("&serieNota&", serieNota);
        xml = xml.replace("&chave&", chave);
        xml = xml.replace("&cnpjDestinatario&", cnpjDestinatario);
        xml = xml.replace("&cnpjEmitente&", cnpjEmitente);
        xml = xml.replace("&mensagem&", mensagem);
        xml = xml.replace("&codigoIntegracao&", codigoIntegracao);

        return xml;
    }

}
