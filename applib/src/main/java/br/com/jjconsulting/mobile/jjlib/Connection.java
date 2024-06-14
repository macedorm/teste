package br.com.jjconsulting.mobile.jjlib;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.io.ByteStreams;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TResponseType;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class Connection {
    //LOGIN
    public static final String API_LOGIN = "api/accounts/login";
    public static final String API_CHANGE_PASSWORD = "api/accounts/changepassword";
    public static final String API_RECOVER_PASSWORD = "api/accounts/recoverpassword";
    public static final String API_CHANGE_PASSWORD_PASS = "api/accounts/changepasswordpassthrough";

    public static final String API_CHANGE_DATA_USER = "/api/accounts/changepersonaldata";

    public static final String API_ORCAMENTO = "api/orcamento";
    public static final String API_MASTER = "MasterApi/";

    public static final String API_DICITIONARIES = "api/dictionaries";
    public static final String API_DICITIONARIES_COUNT = "api/dictionaries/count";

    public static final int INITIALTIMEOUTSMALL = 15000;
    public static final int INITIALTIMEOUTMED = 25000;
    public static final int INITIALTIMEOUTLARGE = 40000;
    public static final int INITIALTIMEEXTRALARGE = (60000 * 2);
    public static final int COUNT_TIMEOUT_VALUE = (60000 * 4);
    public static final int NO_CONNECTION = -2;
    public static final int AUTH_FAILURE = -3;
    public static final int SERVER_ERROR = -4;
    public static final int PARSE_ERROR = -5;
    public static final int TIMEOUT = -1;
    public static final int CANCEL = -6;

    public static final int SUCCESS = 200;
    public static final int CREATED = 201;

    public static final int VALIDATION_ERROR = 400;
    public static final int NOT_REGISTER = 404;

    private Context context;
    private RequestQueue requestQueue;
    private ConnectionListener connectionListener;

    private ArrayList<String> info;
    private ArrayList<String> key;
    private ArrayList<String> header;
    private ArrayList<String> keyHeader;

    private String URL;
    private int typeMethod;
    private int timeout;
    private int maxNumRetries;
    private long timeStart;

    private String requestBody;

    private TResponseType responseType;

    private boolean ignoreHeaders;

    public Connection(Context context) {
        this.context = context;
        maxNumRetries = -1;
    }


    public void GET(String URL, ArrayList<String> infoHeader, ArrayList<String> keyInfoHeader) {
        this.timeout = Connection.INITIALTIMEOUTMED;
        this.responseType  = TResponseType.TEXT;
        createGET(URL, null, null, infoHeader, keyInfoHeader, "");
    }

    /**
     * Post connection
     *
     * @param URL
     * @param info
     * @param key
     * @param infoHeader
     * @param keyInfoHeader
     */
    public void GET(String URL, ArrayList<String> info, ArrayList<String> key, ArrayList<String> infoHeader, ArrayList<String> keyInfoHeader, String raw, int timeout) {
        this.timeout = timeout;
        this.responseType  = TResponseType.TEXT;
        createGET(URL, info, key, infoHeader, keyInfoHeader, raw);
    }

    /**
     * Post connection
     *
     * @param URL
     * @param info
     * @param key
     * @param infoHeader
     * @param keyInfoHeader
     */
    public void GET(String URL, ArrayList<String> info, ArrayList<String> key, ArrayList<String> infoHeader, ArrayList<String> keyInfoHeader, int timeout, TResponseType responseType) {
        this.timeout = timeout;
        this.responseType  = responseType;
        createGET(URL, info, key, infoHeader, keyInfoHeader, "");
    }

    /**
     * Post connection
     *
     * @param URL
     * @param info
     * @param key
     * @param infoHeader
     * @param keyInfoHeader
     */
    public void GET(String URL, ArrayList<String> info, ArrayList<String> key, ArrayList<String> infoHeader, ArrayList<String> keyInfoHeader, int timeout) {
        this.timeout = timeout;
        this.responseType  = TResponseType.TEXT;
        createGET(URL, info, key, infoHeader, keyInfoHeader, "");
    }


    /**
     * @param URL
     * @param info
     * @param keyInfo
     * @param header
     * @param keyHeader
     */
    private void createGET(String URL, ArrayList<String> info, ArrayList<String> keyInfo, ArrayList<String> header, ArrayList<String> keyHeader, String raw) {
        this.typeMethod = Request.Method.GET;
        this.URL = URL;
        this.info = info;
        this.key = keyInfo;
        this.header = header;
        this.keyHeader = keyHeader;
        this.requestBody = raw;

        timeStart = System.currentTimeMillis();

        run(requestBody.length() > 0);
    }


    /**
     * Post connection
     *
     * @param URL
     * @param infoHeader
     * @param keyInfoHeader
     */
    public void PUT(String URL, ArrayList<String> infoHeader, ArrayList<String> keyInfoHeader, String raw, int timeout) {
        this.timeout = timeout;
        this.responseType = TResponseType.TEXT;
        createPUT(URL, new ArrayList<>(), new ArrayList<>(), infoHeader, keyInfoHeader, raw);
    }

    /**
     * Post connection
     *
     * @param URL
     * @param infoHeader
     * @param keyInfoHeader
     */
    public void PUT(String URL, ArrayList<String> info, ArrayList<String> keyInfo, ArrayList<String> infoHeader, ArrayList<String> keyInfoHeader, int timeout) {
        this.timeout = timeout;
        this.responseType = TResponseType.TEXT;
        createPUT(URL, info, keyInfo, infoHeader, keyInfoHeader, "");
    }

    /**
     * @param URL
     * @param info
     * @param keyInfo
     * @param header
     * @param keyHeader
     */
    private void createPUT(String URL, ArrayList<String> info, ArrayList<String> keyInfo, ArrayList<String> header, ArrayList<String> keyHeader, String raw) {
        this.typeMethod = Request.Method.PUT;
        this.URL = URL;
        this.info = info;
        this.key = keyInfo;
        this.header = header;
        this.keyHeader = keyHeader;
        this.requestBody = raw;

        timeStart = System.currentTimeMillis();

        run(requestBody.length() > 0);
    }

    /**
     * Post connection
     *
     * @param URL
     * @param infoHeader
     * @param keyInfoHeader
     */
    public void POST(String URL, ArrayList<String> infoHeader, ArrayList<String> keyInfoHeader, String raw, int timeout) {
        this.timeout = timeout;
        this.responseType = TResponseType.TEXT;
        createPOST(URL, new ArrayList<>(), new ArrayList<>(), infoHeader, keyInfoHeader, raw);
    }

    /**
     * Post connection
     *
     * @param URL
     * @param infoHeader
     * @param keyInfoHeader
     */
    public void POST(String URL, ArrayList<String> infoHeader, ArrayList<String> keyInfoHeader, String raw, int timeout, TResponseType responseType) {
        this.timeout = timeout;
        this.responseType = responseType;
        createPOST(URL, new ArrayList<>(), new ArrayList<>(), infoHeader, keyInfoHeader, raw);
    }

    /**
     * Post connection
     *
     * @param URL
     * @param info
     * @param key
     * @param infoHeader
     * @param keyInfoHeader
     */
    public void POST(String URL, ArrayList<String> info, ArrayList<String> key, ArrayList<String> infoHeader, ArrayList<String> keyInfoHeader, int timeout) {
        this.timeout = timeout;
        this.responseType = TResponseType.TEXT;
        createPOST(URL, info, key, infoHeader, keyInfoHeader, "");
    }

    /**
     * Post connection
     *
     * @param URL
     * @param info
     * @param key
     * @param infoHeader
     * @param keyInfoHeader
     */
    public void POST(String URL, ArrayList<String> info, ArrayList<String> key, ArrayList<String> infoHeader, ArrayList<String> keyInfoHeader, int timeout, TResponseType responseType) {
        this.timeout = timeout;
        this.responseType = responseType;
        createPOST(URL, info, key, infoHeader, keyInfoHeader, "");
    }

    /**
     * @param URL
     * @param info
     * @param keyInfo
     * @param header
     * @param keyHeader
     */
    private void createPOST(String URL, ArrayList<String> info, ArrayList<String> keyInfo, ArrayList<String> header, ArrayList<String> keyHeader, String raw) {
        this.typeMethod = Request.Method.POST;
        this.URL = URL;
        this.info = info;
        this.key = keyInfo;
        this.header = header;
        this.keyHeader = keyHeader;
        this.requestBody = raw;

        timeStart = System.currentTimeMillis();

        run(requestBody.length() > 0);
    }


    public void DELETE(String URL, ArrayList<String> info, ArrayList<String> key,  ArrayList<String> infoHeader, ArrayList<String> keyInfoHeader, int timeout) {
        this.timeout = timeout;
        createDELETE(URL, infoHeader, keyInfoHeader, null, null);
    }

    public void DELETE(String URL, ArrayList<String> infoHeader, ArrayList<String> keyInfoHeader, int timeout) {
        this.timeout = timeout;
        createDELETE(URL, infoHeader, keyInfoHeader, null, null);
    }

    /**
     * @param URL
     * @param header
     * @param keyHeader
     */
    private void createDELETE(String URL, ArrayList<String> header,  ArrayList<String> keyHeader, ArrayList<String> info,  ArrayList<String> key) {
        this.typeMethod = Request.Method.DELETE;
        this.URL = URL;
        this.header = header;
        this.keyHeader = keyHeader;
        this.info = info;
        this.key = key;
        this.requestBody = "";
        timeStart = System.currentTimeMillis();
        run(requestBody.length() > 0);
    }

    private StringRequest createRequestRaw() {
        StringRequest postRequest = new StringRequest(typeMethod, URL,
                response -> {
                    solveResponse(response);
                },
                error -> {
                    solveError(error);
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return Response.success(solveNetworkResponse(response), HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            protected Map<String, String> getParams() {
                return getUserParams();
            }

            @Override
            public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
                return getUserHeaders();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }
        };

        return postRequest;
    }

    private StringRequest createRequest(){
        StringRequest postRequest = new StringRequest(typeMethod, URL,
                response -> {
                    solveResponse(response);
                },
                error -> {
                    solveError(error);
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return Response.success(solveNetworkResponse(response), HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            protected Map<String, String> getParams() {
                return getUserParams();
            }

            @Override
            public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
                return getUserHeaders();
            }
        };

        return postRequest;
    }

    /**
     * Response - content with json
     */
    private void run(boolean  isRaw) {
        LogUser.log(Config.TAG, "Connection URL =" + URL);

        if (connectionListener != null) {
            connectionListener.onStart();
        }

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

      /*  StringRequest postRequest;
        postRequest = isRaw ? createRequestRaw():createRequest();
        postRequest.setRetryPolicy(new DefaultRetryPolicy(timeout,
                1,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES));

        requestQueue.add(postRequest);*/

        StringRequest postRequest;
        postRequest = isRaw ? createRequestRaw():createRequest();
        postRequest.setRetryPolicy(new DefaultRetryPolicy(timeout,
                maxNumRetries,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        SingletonRequestQueue.getInstance(context).addToRequestQueue(postRequest);

    }

    /**
     * Header Auth ISS
     */
    private void createAuthHeader() {
        if(!isIgnoreHeaders()){
            String credentials;
            credentials = JJSDK.getUser(context) +  ":" + JJSDK.getUserKey(context);
            String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT);

            keyHeader.add("Authorization");
            header.add(auth);
        }

    }

    /**
     * Cancel RequestQueue
     */
    public void cancelRequest() {
        if (requestQueue != null) {
            requestQueue.cancelAll(request -> {
                connectionListener.onError(-6, null, null);
                return true;
            });
        }
    }

    /**
     * Set Listener Volley
     *
     * @param connectionListener
     */
    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public void setMaxNumRetries(int maxNumRetries) {
        this.maxNumRetries = maxNumRetries;
    }

    public interface ConnectionListener {
        void onStart();

        void onError(int code, VolleyError error, String response);

        void onResponse(String response);

        void onResponse(InputStreamReader reader);

    }

    private String getLogTime() {
        return String.format("Time Connection: %d milesegundos", (int) (System.currentTimeMillis() - timeStart));
    }


    public void solveResponse(String response){
        if (TResponseType.TEXT == responseType) {
            connectionListener.onResponse(response);
        }
    }

    public void solveError(VolleyError error){
        LogUser.log(Config.TAG, getLogTime());
        if (connectionListener != null) {
            int errorCode = 0;
            if (error instanceof com.android.volley.TimeoutError) {
                errorCode = Connection.TIMEOUT;
                LogUser.log(Config.TAG, "Connection HTTP Error = TIMEOUT");
            } else if (error instanceof NoConnectionError) {
                errorCode = Connection.NO_CONNECTION;
                LogUser.log(Config.TAG, "Connection HTTP Error = NO_CONNECTION");
            } else if (error instanceof com.android.volley.AuthFailureError) {
                errorCode = Connection.AUTH_FAILURE;
                LogUser.log(Config.TAG, "Connection HTTP Error = AUTH_FAILURE");
            } else if (error instanceof com.android.volley.ServerError) {
                errorCode = Connection.SERVER_ERROR;
                LogUser.log(Config.TAG, "Connection HTTP Error = SERVER_ERROR");
            } else if (error instanceof NetworkError) {
                errorCode = Connection.NO_CONNECTION;
                LogUser.log(Config.TAG, "Connection HTTP Error = PARSE_ERROR");
            } else if (error instanceof com.android.volley.ParseError) {
                errorCode = Connection.PARSE_ERROR;
                LogUser.log(Config.TAG, "Connection HTTP Error = PARSE_ERROR");
            }

            String body = null;

            if(error.networkResponse != null && error.networkResponse.data!=null) {
                try {
                    body = new String(error.networkResponse.data,"UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            connectionListener.onError(errorCode, error, body);
        }
    }

    public String solveNetworkResponse(NetworkResponse response){
        String encoding = response.headers.get("Content-Encoding");
        String dataResponse = "";

        try {
            if (encoding != null && encoding.equals("gzip")) {
                final GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(response.data), 65536);

                if (TResponseType.TEXT == responseType) {
                    dataResponse = new String(ByteStreams.toByteArray(gStream));

                } else {
                    InputStreamReader reader = new InputStreamReader(gStream, "UTF-8");
                    connectionListener.onResponse(reader);
                    gStream.close();
                }
            } else {
                if (TResponseType.TEXT == responseType) {
                    dataResponse = new String(response.data);

                } else {
                    InputStream myInputStream = new ByteArrayInputStream(response.data);
                    InputStreamReader reader = new InputStreamReader(myInputStream);
                    connectionListener.onResponse(reader);
                }
            }
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }


        if(!TextUtils.isNullOrEmpty(dataResponse)){
            LogUser.log(Config.TAG, "Response: " + dataResponse);
            LogUser.log(Config.TAG, "Response: " + dataResponse);
        }

        return dataResponse;
    }

    public  Map<String, String>  getUserParams(){
        Map<String, String> params = new HashMap<>();
        if (info != null) {
            for (int ind = 0; ind < info.size(); ind++) {
                LogUser.log(Config.TAG, "Connection Add Param = " + key.get(ind) + " - " + info.get(ind));
                params.put(key.get(ind), info.get(ind));
            }
        }

        return params;
    }

    public Map<String, String> getUserHeaders(){
        Map<String, String> params = new HashMap<String, String>();
        if (header != null || keyHeader != null) {
            createAuthHeader();
            for (int ind = 0; ind < header.size(); ind++) {
                LogUser.log(Config.TAG, "Connection Add Param Headers = " + keyHeader.get(ind) + " - " + header.get(ind));
                params.put(keyHeader.get(ind), header.get(ind));
            }
        }

        return params;
    }

    public boolean isIgnoreHeaders() {
        return ignoreHeaders;
    }

    public void setIgnoreHeaders(boolean ignoreHeaders) {
        this.ignoreHeaders = ignoreHeaders;
    }
}
