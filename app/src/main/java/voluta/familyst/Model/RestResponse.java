package voluta.familyst.Model;

import org.json.JSONObject;

import java.util.Map;

public class RestResponse {

    public RestResponse(int _httpStatusCode, Map _headers, JSONObject _bodyResponse) {
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
