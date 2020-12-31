package mom.com.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mom.com.R;
import mom.com.adapter.TimeAdapter;
import mom.com.network.response.Time_data;

public class ContactDetailActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        recyclerView=findViewById(R.id.recyclerView);

        List<Time_data> time_dataList=new ArrayList<>();
        time_dataList.add(new Time_data("12 AM-01 PM"));
        time_dataList.add(new Time_data("01 AM-02 PM"));
        time_dataList.add(new Time_data("02 AM-03 PM"));
        time_dataList.add(new Time_data("03 AM-04 PM"));
        time_dataList.add(new Time_data("04 AM-05 PM"));
        time_dataList.add(new Time_data("05 AM-06 PM"));
        time_dataList.add(new Time_data("06 AM-07 PM"));
        time_dataList.add(new Time_data("07 AM-08 PM"));
        time_dataList.add(new Time_data("08 AM-09 PM"));
        time_dataList.add(new Time_data("09 AM-10 PM"));
        time_dataList.add(new Time_data("10 AM-11 PM"));
        time_dataList.add(new Time_data("11 AM-12 PM"));
        time_dataList.add(new Time_data("12 PM - 01 AM"));
        time_dataList.add(new Time_data("01 AM - 02 AM"));
        time_dataList.add(new Time_data("02 AM - 03 AM"));
        time_dataList.add(new Time_data("03 AM - 04 AM"));
        time_dataList.add(new Time_data("04 AM - 05 AM"));
        time_dataList.add(new Time_data("05 AM - 06 AM"));
        time_dataList.add(new Time_data("06 AM - 07 AM"));
        time_dataList.add(new Time_data("07 AM - 08 AM"));
        time_dataList.add(new Time_data("08 AM - 09 AM"));
        time_dataList.add(new Time_data("09 AM - 10 AM"));
        time_dataList.add(new Time_data("10 AM - 11 AM"));
        time_dataList.add(new Time_data("11 AM - 12 AM"));

        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new TimeAdapter(this, time_dataList));
    }
}
