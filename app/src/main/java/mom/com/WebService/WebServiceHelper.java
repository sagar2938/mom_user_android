package mom.com.WebService;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class WebServiceHelper {
    private static WebServiceHelper webServiceInstance = new WebServiceHelper();
    private static int TIMOUT_IN_SECONDS = 15*1000;
    private static int MAX_RETRIES = 2;
    private static int BACKOFF_MULT=1;

        public static WebServiceHelper getInstance(){
        return webServiceInstance;
    }
    private WebServiceHelper(){}

    public void PostCall(Context context, String url, JSONObject data, final ResponseCallBack ResponseCallBack){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null)
                    Log.d("response",response.toString());
                    ResponseCallBack.OnResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                ResponseCallBack.OnResponse(null);
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMOUT_IN_SECONDS,
                MAX_RETRIES,
                BACKOFF_MULT));
        RestapiCall.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
