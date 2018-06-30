package voluta.familyst.Services;

import android.app.Application;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.JsonSyntaxException;
import voluta.familyst.FamilystApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class JsonRestRequest extends JsonRequest<JsonRestRequest.JsonRestResponse> {

    private static final String baseURI = "http://192.168.15.53:8084/Familyst/";

    private Map<String, String> headers;
    private Response.Listener listener;

    public static String getRestUrl(String url) {
        return baseURI + url;
    }

    public JsonRestRequest(Application app, int method, boolean authenticated, String url, Map<String, String> headers, JSONObject body,
                           Response.Listener listener, Response.ErrorListener errorListener) {
        super(method, getRestUrl(url), body.toString(), listener, errorListener);

        if (headers == null) headers = new ArrayMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");

        if (authenticated)
            headers.put("Authorization", "Basic " + ((FamilystApplication)app).get_accessToken());

        this.headers = headers;
        this.listener = listener;

        Log.d("Requests", "Request efetuado para " + getRestUrl(url) + "utilizando metodo " + getMethodRequest(method) + ".");
    }

    private String getMethodRequest(int method) {
        switch (method){
            case Method.GET:
                return "GET";
            case Method.POST:
                return "POST";
            default:
                return null;
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Response<JsonRestResponse> parseNetworkResponse(NetworkResponse response) {
        try {

            //recupera status http
            int statusCode = response.statusCode;

            //recupera readers de resposta
            Map<String, String> headers = response.headers;

            //recupera corpo e transforma em JSON
            String bodyResponseString = new String(
                    response.data,"utf-8");
                    //HttpHeaderParser.parseCharset(response.headers));
            JSONObject bodyResponse = null;
            if (bodyResponseString.length() > 0 && !bodyResponseString.equals("null"))
                bodyResponse = new JSONObject(bodyResponseString);

            //monta nosso objeto de resposta
            JsonRestResponse jsonRestResponse = new JsonRestResponse(statusCode, headers, bodyResponse);

            //entrega resposta
            return Response.success(
                    jsonRestResponse,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(JsonRestResponse response) {
        listener.onResponse(response);
    }

    /**
     * Created by jdfid on 19/11/2016.
     */

    public static class JsonRestResponse {

        public JsonRestResponse(int _httpStatusCode, Map _headers, JSONObject _bodyResponse) {
            this._httpStatusCode = _httpStatusCode;
            this._headers = _headers;
            this._bodyResponse = _bodyResponse;
        }

        private int _httpStatusCode;
        private Map _headers;
        private JSONObject _bodyResponse;

        public int get_httpStatusCode() {
            return _httpStatusCode;
        }

        public Map get_headers() {
            return _headers;
        }

        public JSONObject get_bodyResponse() {
            return _bodyResponse;
        }
    }
}
