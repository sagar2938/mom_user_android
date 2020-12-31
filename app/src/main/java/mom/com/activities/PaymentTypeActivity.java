package mom.com.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import mom.com.R;
import mom.com.utils.Preferences;

public class PaymentTypeActivity extends AppCompatActivity {

    CardView paytm;
    CardView cash;
    TextView amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_type);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Pay Via");

        paytm=findViewById(R.id.paytm);
        cash=findViewById(R.id.cash);
        amount=findViewById(R.id.amount);


        amount.setText("Pay  ₹"+getIntent().getStringExtra("amount"));

        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode("Paytm");
            }
        });

        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode("Cash On Delivery");
            }
        });


    }


    void mode(String mode){
        Preferences.getInstance(getApplicationContext()).setMode(mode);
        Intent intent = getIntent();
        intent.putExtra("mode",mode);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
