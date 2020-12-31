package mom.com.activities;

import android.app.Dialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mom.com.R;

public class ApplyCoupons extends AppCompatActivity {
  TextView view_details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_coupons);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Apply Coupons");
        view_details=findViewById(R.id.view_details);

        view_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog view_details_dialog = new Dialog(ApplyCoupons.this);
                view_details_dialog.setContentView(R.layout.view_details_dialog);
                view_details_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view_details_dialog.show();
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
