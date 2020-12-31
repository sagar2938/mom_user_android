package mom.com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import mom.com.R;
import mom.com.network.ApiCallService;
import mom.com.network.response.SucessResponse;
import mom.com.utils.AppUser;
import mom.com.utils.Helper;
import mom.com.utils.Validation;

public class SignUpActivity extends BaseActivity {

    EditText name;
    EditText email;
    EditText mobile;
    Button submit;

    String otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        mobile=findViewById(R.id.mobile);
        submit=findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().isEmpty()){
                    getDialog("Enter Name");
                    return;
                }
                if (!email.getText().toString().trim().isEmpty()){
                    if (!Validation.isEmailValid(email.getText().toString())){
                        getDialog("Enter Valid email");
                        return;
                    }
                }
                if (mobile.getText().toString().trim().isEmpty()){
                    getDialog("Enter Mobile Number");
                    return;
                }

                otp=Helper.getOtp();
                Map map=new HashMap();
                map.put("name",name.getText().toString());
                map.put("mobile",mobile.getText().toString());
                map.put("email",email.getText().toString());
                map.put("otp",otp);
                map.put("referal_code","");
                map.put("address","");
                map.put("latitude","");
                map.put("longitude","");
                AppUser.getInstance().getAppUser().setSignUpRequest(map);
                ApiCallService.action(SignUpActivity.this,map,ApiCallService.Action.ACTION_SEND_OTP);
            }
        });

    }


    @Subscribe
    public void getOtp(SucessResponse response){
        if (response.getResponse().getConfirmation()==0) {
//            Toast.makeText(this, "" + otp, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
            intent.putExtra("from", "signUp");
            intent.putExtra("otp", otp);
            intent.putExtra("mobile", mobile.getText().toString());
            startActivity(intent);
        }else {
            getDialog(response.getResponse().getMessage());
        }
    }
}
