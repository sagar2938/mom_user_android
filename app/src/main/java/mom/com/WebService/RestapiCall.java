package mom.com.WebService;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RestapiCall {
    private static RestapiCall mInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;

    private RestapiCall(Context context){
        mContext = context;
        mRequestQueue = getRequestQueue();
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }
    public static synchronized RestapiCall getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RestapiCall(context);
        }
        return mInstance;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);

    }
}
