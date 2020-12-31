package mom.com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tapadoo.alerter.Alerter;

import cdflynn.android.library.checkview.CheckView;
import mom.com.R;
import mom.com.network.ThisApp;
import mom.com.network.request.EventPushRequest;
import mom.com.network.response.PushNotificationResponse;
import mom.com.utils.AppUser;
import mom.com.utils.LocalRepositories;
import mom.com.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.paytm.pgsdk.easypay.manager.PaytmAssist.getContext;

public class OrderPlacedActivity extends AppCompatActivity {

    CheckView check;
    ImageView cancel;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_place);
        check = findViewById(R.id.check);
        cancel = findViewById(R.id.cancel);
        text = findViewById(R.id.text);
        check.check();
        cancel.setAnimation(AnimationUtils.loadAnimation(this, R.anim.blink));



        if (getIntent().getStringExtra("status") != null) {
            if (getIntent().getStringExtra("status").equals("success")) {
                cancel.setVisibility(View.GONE);
                check.setVisibility(View.VISIBLE);
                text.setText("Order Placed !");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }, 5000);


                EventPushRequest request = new EventPushRequest();
                request.setTo(Preferences.getInstance(getApplicationContext()).getMomMobile());
                request.setMessage("You have received one new order");
                ThisApp.getApi(getApplicationContext()).pushNotification(request).enqueue(new Callback<PushNotificationResponse>() {
                    @Override
                    public void onResponse(Call<PushNotificationResponse> call, Response<PushNotificationResponse> response) {
                        Alerter.create(OrderPlacedActivity.this)
                                .setTitle("")
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .setText("Your order placed")
                                .setIcon(R.drawable.progress_launcher)
                                .setIconColorFilter(0)
                                .show();
                    }

                    @Override
                    public void onFailure(Call<PushNotificationResponse> call, Throwable t) {

                    }
                });


                Preferences.getInstance(getApplicationContext()).setMomMobile("");
                AppUser appUser = LocalRepositories.getAppUser(getApplicationContext());
                appUser.setCartModels(null);
                appUser.setMomMobile("");
                appUser.setAmount(0.0);
                LocalRepositories.saveAppUser(getApplicationContext(), appUser);

            } else {
                cancel.setVisibility(View.VISIBLE);
                check.setVisibility(View.GONE);
                text.setText("Payment has not been success, Please try again.");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }, 5000);
            }

        }


    }
}
