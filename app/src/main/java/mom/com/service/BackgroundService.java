package mom.com.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import mom.com.activities.CartActivity;
import mom.com.activities.MainActivity;
import mom.com.activities.OrderPlacedActivity;
import mom.com.network.ApiCallService;
import mom.com.network.ThisApp;
import mom.com.network.response.PaytmPaymentResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackgroundService extends IntentService {
    public BackgroundService() {
        super("BackgroundService");
    }

    public static void action(Context context, String orderId) {
        Intent intent = new Intent(context, BackgroundService.class);
        intent.putExtra("orderId", orderId);
        intent.setAction("");
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Map map = new HashMap();
        map.put("orderId", intent.getStringExtra("orderId"));
        ThisApp.getApi(getApplicationContext()).paytmStatusApi(map).enqueue(new Callback<PaytmPaymentResponse>() {
            @Override
            public void onResponse(Call<PaytmPaymentResponse> call, Response<PaytmPaymentResponse> resp) {
                if (resp.code() == 200) {
                    PaytmPaymentResponse response = resp.body();
                    if (response.isSuccess()) {
                        if (response.getConfirmation() == 0) {
//                            repeat
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent in = new Intent(getApplicationContext(), BackgroundService.class);
                                    in.putExtra("orderId", intent.getStringExtra("orderId"));
                                    startService(in);
                                }
                            },5000);

                        } else if (response.getConfirmation() == 1) {
//                            success
                            try {
                                Intent intent = new Intent(getApplicationContext(), OrderPlacedActivity.class);
                                intent.putExtra("status","success");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        } else if (response.getConfirmation() == 2) {
//                            failed
                            Intent intent = new Intent(getApplicationContext(), OrderPlacedActivity.class);
                            intent.putExtra("status","failed");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PaytmPaymentResponse> call, Throwable t) {

            }
        });
    }
}
