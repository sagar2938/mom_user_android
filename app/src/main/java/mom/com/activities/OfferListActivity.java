package mom.com.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import mom.com.R;
import mom.com.adapter.OfferListAdapter;
import mom.com.helper.FusedLocation;
import mom.com.network.ApiCallService;
import mom.com.network.response.GetOfferResponse;
import mom.com.utils.CustomProgressDialog;
import mom.com.utils.Preferences;

public class OfferListActivity extends BaseActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_list);
        recyclerView=findViewById(R.id.recyclerView);
        Map map=new HashMap();
        try {
            map.put("mobile", Preferences.getInstance(getApplicationContext()).getMobile());
            map.put("latitude", FusedLocation.location.getLatitude());
            map.put("longitude", FusedLocation.location.getLongitude());
        }catch (Exception e){
            map.put("latitude", "");
            map.put("longitude", "");
        }
        ApiCallService.action(this,map,ApiCallService.Action.ACTION_GET_OFFER_LIST);
    }




    @Subscribe
    public void getOffer(GetOfferResponse response){
        CustomProgressDialog.setDismiss();
        if (response.isSuccess()){
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(new OfferListAdapter(response.getOffer_data(),OfferListActivity.this));
            recyclerView.setFocusable(false);
        }
    }
}
