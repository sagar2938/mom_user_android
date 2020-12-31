package mom.com.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.greenrobot.eventbus.Subscribe;
import java.util.HashMap;
import java.util.Map;
import mom.com.R;
import mom.com.network.ApiCallService;
import mom.com.network.response.LoginSignUpMainResponse;
import mom.com.network.response.ResendOtpResponse;
import mom.com.network.response.TokenResponse;
import mom.com.utils.AppUser;
import mom.com.utils.Helper;
import mom.com.utils.Preferences;
import mom.com.utils.SMSReceiver;


public class OtpActivity extends BaseActivity implements SMSReceiver.OTPReceiveListener{

    Button submit;
    String from;
    String otp;
    Pinview pinView;
    TextView mobile;
    LinearLayout resend;

    private SMSReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_otp);
        submit = findViewById(R.id.submit);
        pinView = findViewById(R.id.pinView);
        mobile = findViewById(R.id.mobile);
        resend = findViewById(R.id.resend);
        mobile.setText(getIntent().getStringExtra("mobile"));
        from = getIntent().getStringExtra("from");
        otp = getIntent().getStringExtra("otp");
//        Map map = new HashMap();
//        map.put("mobile", getIntent().getStringExtra("mobile"));
//        map.put("fcmToken", FirebaseInstanceId.getInstance().getToken());
//        ApiCallService.action2(OtpActivity.this, map, ApiCallService.Action.ACTION_SEND_TOKEN);


        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(OtpActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Map map=new HashMap();
                map.put("mobile", Preferences.getInstance(getApplicationContext()).getMobile());
                map.put("fcmToken",newToken);
                map.put("userType","Customer");
                ApiCallService.action2(OtpActivity.this,map,ApiCallService.Action.ACTION_SAVE_TOKEN);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinView.getValue().isEmpty()) {
                    getDialog("Please enter opt");
                    return;
                }
                if (pinView.getValue().length() != 4) {
                    getDialog("Please enter complete opt");
                    return;
                }
                if (!pinView.getValue().equals(otp)) {
                    getDialog("Invalid Otp");
                    return;
                }
                if (from.equals("signUp")) {
                    ApiCallService.action(OtpActivity.this, AppUser.getAppUser().getSignUpRequest(), ApiCallService.Action.ACTION_SIGN_UP);
                } else {
                    Preferences.getInstance(getApplicationContext()).setLogin(true);
                    if (getIntent().getStringExtra("cart") == null) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        finish();
                    }
//                    ApiCallService.action(OtpActivity.this, AppUser.getAppUser().getLoginRequest(), ApiCallService.Action.ACTION_SIGN_IN);
                }


            }
        });

        
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp= Helper.getOtp();
                Map map = new HashMap();
                map.put("mobile", getIntent().getStringExtra("mobile"));
                map.put("otp", otp);
                ApiCallService.action(OtpActivity.this,map,ApiCallService.Action.ACTION_RESEND_OTP);
            }
        });
        startSMSListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    
    
    @Subscribe
    public void resendOtp(ResendOtpResponse response){
//        Toast.makeText(this, "success: "+otp, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "success: ", Toast.LENGTH_SHORT).show();
    }


    @Subscribe
    public void loginSignUp(LoginSignUpMainResponse response) {
        if (response.getResponse().getConfirmation() == 1) {
            Preferences.getInstance(getApplicationContext()).setLogin(true);
            Preferences.getInstance(getApplicationContext()).setName(response.getResponse().getName());
            Preferences.getInstance(getApplicationContext()).setEmail(response.getResponse().getEmail());
            Preferences.getInstance(getApplicationContext()).setMobile(response.getResponse().getMobile());
            Preferences.getInstance(getApplicationContext()).setApiKey(response.getResponse().getApi_key());
            Preferences.getInstance(getApplicationContext()).setProfileImage(response.getResponse().getProfileImage());

            Preferences.getInstance(getApplicationContext()).setAddress(response.getResponse().getAddress());
            Preferences.getInstance(getApplicationContext()).setLatitude(response.getResponse().getLatitude());
            Preferences.getInstance(getApplicationContext()).setLongitude(response.getResponse().getLongitude());
            Preferences.getInstance(getApplicationContext()).setAddressStatus(response.getResponse().getAddressStatus());
            if (getIntent().getStringExtra("cart") == null) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                finish();
            }
        } else {
            getDialog(response.getResponse().getMessage());
        }
    }


    @Subscribe
    public void tokenResponse(TokenResponse response) {
        if (response.getResponse().getConfirmation() == 1) {
//            Toast.makeText(this, ""+response.getResponse().getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            getDialog(response.getResponse().getMessage());
        }
    }


    @Override
    public void onOTPReceived(String otpMessage) {
        otpMessage=otpMessage.replace("<#> MOMApp: Your verification code is ","");
        otpMessage=otpMessage.replace(" 5/kYxluz6ei","");

//        final String otp = otpMessage.substring(46, 51);
        showToast("OTP Received: " + otpMessage);

        pinView.setValue(otpMessage);

        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }

    }

    @Override
    public void onOTPTimeOut() {
        showToast("OTP Time out");
    }

    @Override
    public void onOTPReceivedError(String error) {
        showToast(error);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }



    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            this.registerReceiver(smsReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}





