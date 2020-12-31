package mom.com.WebService;

import org.json.JSONObject;

public interface ResponseCallBack {
    void OnResponse(JSONObject Response);
    void OnError(JSONObject Response);
}
