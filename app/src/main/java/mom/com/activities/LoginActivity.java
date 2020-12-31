package mom.com.activities;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import mom.com.R;
import mom.com.network.ApiCallService;
import mom.com.network.response.LoginSignUpMainResponse;
import mom.com.network.response.SucessResponse;
import mom.com.utils.AppUser;
import mom.com.utils.Helper;
import mom.com.utils.Preferences;

public class LoginActivity extends BaseActivity {

    EditText mobile;
    Button submit;
    Button signUp;
    TextInputLayout mobileError;

    String otp;
    String cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mobile=findViewById(R.id.mobile);
        mobileError=findViewById(R.id.mobileError);
        submit=findViewById(R.id.submit);
        signUp=findViewById(R.id.signUp);
        cart=getIntent().getStringExtra("cart");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobile.getText().toString().isEmpty()){
//                    mobileError.setError("Enter mobile number");
                    getDialog("Enter mobile number");
                    return;
                } if (mobile.getText().toString().length()!=10){
//                    mobileError.setError("Enter valid mobile number");
                    getDialog("Enter valid mobile number");
                    return;
                }
                otp= Helper.getOtp();
                Map map=new HashMap();
                map.put("mobile",mobile.getText().toString());
                map.put("otp",otp);
                map.put("referal_code","");
                AppUser.getInstance().getAppUser().setLoginRequest(map);
                ApiCallService.action(LoginActivity.this,map,ApiCallService.Action.ACTION_SIGN_IN);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            }
        });

    }


    @Subscribe
    public void loginSignUp(LoginSignUpMainResponse response) {
        if (response.getResponse().getConfirmation() == 1) {

            Preferences.getInstance(getApplicationContext()).setName(response.getResponse().getName());
            Preferences.getInstance(getApplicationContext()).setEmail(response.getResponse().getEmail());
            Preferences.getInstance(getApplicationContext()).setMobile(response.getResponse().getMobile());
            Preferences.getInstance(getApplicationContext()).setApiKey(response.getResponse().getApi_key());
            Preferences.getInstance(getApplicationContext()).setProfileImage(response.getResponse().getProfileImage());

            Preferences.getInstance(getApplicationContext()).setAddress(response.getResponse().getAddress());
            Preferences.getInstance(getApplicationContext()).setLatitude(response.getResponse().getLatitude());
            Preferences.getInstance(getApplicationContext()).setLongitude(response.getResponse().getLongitude());
            Preferences.getInstance(getApplicationContext()).setAddressLatitude(response.getResponse().getLatitude());
            Preferences.getInstance(getApplicationContext()).setAddressLongitude(response.getResponse().getLongitude());
            Preferences.getInstance(getApplicationContext()).setAddressStatus(response.getResponse().getAddressStatus());



//            Toast.makeText(this, "" + otp, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
            intent.putExtra("from", "login");
            intent.putExtra("otp", otp);
            intent.putExtra("mobile", mobile.getText().toString());
            intent.putExtra("cart", cart);
            if (cart==null) {
                startActivity(intent);
            }else {
                startActivity(intent);
                finish();
            }
        } else {
            getDialog(response.getResponse().getMessage());
        }
    }

    @Subscribe
    public void getOtp(SucessResponse response){
        if (response.getResponse().getConfirmation()==1) {
//            Toast.makeText(this, "" + otp, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
            intent.putExtra("from", "login");
            intent.putExtra("otp", otp);
            intent.putExtra("mobile", mobile.getText().toString());
            intent.putExtra("cart", cart);
            if (cart==null) {
                startActivity(intent);
            }else {
                startActivity(intent);
                finish();
            }
        }else {
            getDialog(response.getResponse().getMessage());
        }
    }
}
