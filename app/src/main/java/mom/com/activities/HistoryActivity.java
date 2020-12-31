package mom.com.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import mom.com.R;
import mom.com.adapter.HistoryFragmentAdapter;
import mom.com.network.ApiCallService;
import mom.com.utils.CustomProgressDialog;
import mom.com.utils.Helper;
import mom.com.utils.Preferences;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayout dateLayout;
    LinearLayout fromLayout;
    LinearLayout toLayout;
    TextView start;
    TextView end,from_tv;
    CustomProgressDialog customProgressDialog;
    HistoryFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        recyclerView = findViewById(R.id.recyclerView);
        dateLayout = findViewById(R.id.dateLayout);
        fromLayout = findViewById(R.id.fromLayout);
        toLayout = findViewById(R.id.toLayout);
        from_tv=findViewById(R.id.from_tv);
        start = findViewById(R.id.from);
        end = findViewById(R.id.to);
        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.show();

        start.setText(Helper.getCurrentDate());
        end.setText(Helper.getCurrentDate());



        Map map = new HashMap();
        map.put("start_date", Helper.getCurrentDate());
        map.put("end_date", Helper.getCurrentDate());
        map.put("mobile", Preferences.getInstance(this).getMobile());
        ApiCallService.action2(this, map, ApiCallService.Action.ACTION_ORDER_HISTORY);



        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateDialog(start,end);
            }
        });

    }


    public void dateDialog(TextView start, TextView end) {

        Dialog dialog = new Dialog(HistoryActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().requestFeature(1);
        dialog.setContentView(R.layout.date_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView from = dialog.findViewById(R.id.from);
        LinearLayout close = dialog.findViewById(R.id.close);
        TextView to = dialog.findViewById(R.id.to);
        Button submit = dialog.findViewById(R.id.submit);

        from.setText(Helper.getCurrentDate());
        to.setText(Helper.getCurrentDate());

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateSelection(from, "start", to);
            }
        });
        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateSelection(to, "end", from);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date fromDate = null;
                Date endDate = null;
                try {
                    fromDate = sdf.parse(from.getText().toString());
                    endDate = sdf.parse(to.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (fromDate.getTime() > endDate.getTime()) {
                    Toast.makeText(HistoryActivity.this, "Enter valid date", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                start.setText(from.getText().toString());
                end.setText(to.getText().toString());

                Map map = new HashMap();
                map.put("start_date", from.getText().toString());
                map.put("end_date", to.getText().toString());
                map.put("mobile", Preferences.getInstance(HistoryActivity.this).getMobile());
                customProgressDialog.show();
                ApiCallService.action2(HistoryActivity.this, map, ApiCallService.Action.ACTION_ORDER_HISTORY);
            }
        });

        dialog.show();
    }

    public void dateSelection(TextView v2, String str, TextView v3) {


        Calendar calendar = Calendar.getInstance();
//        String date = v3.getText().toString();
//        int year = Integer.parseInt(date.split("/")[2]);
//        int month = Integer.parseInt(date.split("/")[1]);
//        int day = Integer.parseInt(date.split("/")[0]);
//        calendar.set(year, month - 1, day);
        DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                String d = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(calendar.getTime());
                if (str.equals("start")) {
                    v2.setText(d);
                } else if (str.equals("end")) {
                    v2.setText(d);
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }



}
