package mom.com.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mom.com.R;
import mom.com.adapter.AddressListAdapter;
import mom.com.activities.address.AddAddressActivity;
import mom.com.network.ApiCallService;
import mom.com.network.response.AddressData;
import mom.com.network.response.AddressResponse;
import mom.com.utils.Preferences;

public class AddressListActivity extends BaseActivity {


    LinearLayout addAddress;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Address List");
        addAddress = findViewById(R.id.addAddress);
        recyclerView = findViewById(R.id.recyclerView);

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), AddAddressActivity.class);
                startActivity(intent);
            }
        });





    }

    @Override
    protected void onResume() {
        super.onResume();
        Map map=new HashMap();
        map.put("mobile",Preferences.getInstance(getApplicationContext()).getMobile());
        ApiCallService.action(this,map,ApiCallService.Action.ACTION_GET_ADDRESS_LIST);
    }

    @Subscribe
    public void getAddressList(AddressResponse response){
        if (response.isSuccess()){
            setAdapter(response.getAddress_data());
        }else {
            getDialog("Something went wrong");
        }
    }


    void setAdapter(List<AddressData> list){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter( new AddressListAdapter(this,list));
        recyclerView.setFocusable(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
