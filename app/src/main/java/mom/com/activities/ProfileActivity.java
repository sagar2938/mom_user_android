package mom.com.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mom.com.R;
import mom.com.adapter.AddressAdapter;
import mom.com.activities.address.AddAddressActivity;
import mom.com.network.ApiCallService;
import mom.com.network.response.AddressData;
import mom.com.network.response.AddressResponse;
import mom.com.utils.Preferences;

public class ProfileActivity extends BaseActivity {

    TextView edit_profile;
    TextView name,email,mobile,address;
    ImageView image;
    LinearLayout addAddress;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        edit_profile=findViewById(R.id.edit_profile);
        name = findViewById(R.id.name);
        email =findViewById(R.id.email);
        image = findViewById(R.id.image);
        mobile = findViewById(R.id.mobile);
        address = findViewById(R.id.address);
        addAddress = findViewById(R.id.addAddress);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        name.setText(Preferences.getInstance(getApplicationContext()).getName());
        mobile.setText(Preferences.getInstance(getApplicationContext()).getMobile());
        if (!Preferences.getInstance(getApplicationContext()).getEmail().equals("")){
            email.setText(Preferences.getInstance(getApplicationContext()).getEmail());
        }else {
            email.setHint("Not found");
        }
        if (!Preferences.getInstance(getApplicationContext()).getAddress().equals("")){
            address.setText(Preferences.getInstance(getApplicationContext()).getAddress());
        }else {
            address.setHint("Not found");
        }
        Map map=new HashMap();
        map.put("mobile",Preferences.getInstance(getApplicationContext()).getMobile());
        ApiCallService.action(this,map,ApiCallService.Action.ACTION_GET_ADDRESS_LIST);


        Glide.with(this)
                .load(Preferences.getInstance(getApplicationContext()).getProfileImage())
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .apply(RequestOptions.skipMemoryCacheOf(true))
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
//                .apply(new RequestOptions().placeholder(R.drawable.user))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(image);


        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), AddAddressActivity.class);
                startActivity(intent);
            }
        });



        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),EditProfileActivity.class);
                startActivity(intent);
            }
        });
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
        recyclerView.setAdapter( new AddressAdapter(this,list));
        recyclerView.setFocusable(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
