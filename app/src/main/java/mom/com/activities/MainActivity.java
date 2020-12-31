package mom.com.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import mom.com.fragment.CartFragment;
import mom.com.fragment.HomeFragment;
import mom.com.fragment.MenuFragment;
import mom.com.fragment.OrderFragment;
import mom.com.fragment.SearchFragment;
import mom.com.R;
import mom.com.network.ThisApp;
import mom.com.network.response.SucessResponse;
import mom.com.utils.LocalRepositories;
import mom.com.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {


    public static MainActivity context;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        Fragment fragment = null;

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    break;
                case R.id.navigation_dashboard:
                    fragment = new SearchFragment();
                    break;
                case R.id.navigation_notifications:
                    fragment = new OrderFragment();
                    break;
                case R.id.navigation_menu:
                    fragment = new MenuFragment();
                    break;
            }
            return MainActivity.this.loadFragment(fragment);
        }
    };


    LinearLayout home,search,order,profile;
    LinearLayout homeBar,searchBar,orderBar,profileBar,cartBar;
    TextView homeTxt,searchTxt,orderTxt,profileTxt,cartTxt;
    ImageView homeIcon,searchIcon,orderIcon,profileIcon,cartIcon;
    RelativeLayout cart;
    LinearLayout cartCountLayout;
    TextView cartCount;


    void setBottomView(String value){


        homeTxt.setTypeface(null, Typeface.NORMAL);
        searchTxt.setTypeface(null, Typeface.NORMAL);
        orderTxt.setTypeface(null, Typeface.NORMAL);
        profileTxt.setTypeface(null, Typeface.NORMAL);
        cartTxt.setTypeface(null, Typeface.NORMAL);

        if (value.equals("home")) {
            homeBar.setBackgroundColor(getResources().getColor(R.color.white));
            searchBar.setBackgroundColor(getResources().getColor(R.color.white));
            orderBar.setBackgroundColor(getResources().getColor(R.color.white));
            profileBar.setBackgroundColor(getResources().getColor(R.color.white));
            cartBar.setBackgroundColor(getResources().getColor(R.color.white));

            homeTxt.setTextColor(getResources().getColor(R.color.black));
            searchTxt.setTextColor(getResources().getColor(R.color.black));
            orderTxt.setTextColor(getResources().getColor(R.color.black));
            profileTxt.setTextColor(getResources().getColor(R.color.black));
            cartTxt.setTextColor(getResources().getColor(R.color.black));

            homeTxt.setTypeface(null, Typeface.NORMAL);
            searchTxt.setTypeface(null, Typeface.NORMAL);
            orderTxt.setTypeface(null, Typeface.NORMAL);
            profileTxt.setTypeface(null, Typeface.NORMAL);
            cartTxt.setTypeface(null, Typeface.NORMAL);

            homeBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            homeTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
            homeTxt.setTypeface(homeTxt.getTypeface(), Typeface.BOLD);

        }else  if (value.equals("search")) {
            homeBar.setBackgroundColor(getResources().getColor(R.color.white));
            searchBar.setBackgroundColor(getResources().getColor(R.color.white));
            orderBar.setBackgroundColor(getResources().getColor(R.color.white));
            profileBar.setBackgroundColor(getResources().getColor(R.color.white));
            cartBar.setBackgroundColor(getResources().getColor(R.color.white));

            homeTxt.setTextColor(getResources().getColor(R.color.black));
            searchTxt.setTextColor(getResources().getColor(R.color.black));
            orderTxt.setTextColor(getResources().getColor(R.color.black));
            profileTxt.setTextColor(getResources().getColor(R.color.black));
            cartTxt.setTextColor(getResources().getColor(R.color.black));

            homeTxt.setTypeface(null, Typeface.NORMAL);
            searchTxt.setTypeface(null, Typeface.NORMAL);
            orderTxt.setTypeface(null, Typeface.NORMAL);
            profileTxt.setTypeface(null, Typeface.NORMAL);
            cartTxt.setTypeface(null, Typeface.NORMAL);

            searchBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            searchTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
            searchTxt.setTypeface(homeTxt.getTypeface(), Typeface.BOLD);

        }else  if (value.equals("order")) {
            homeBar.setBackgroundColor(getResources().getColor(R.color.white));
            searchBar.setBackgroundColor(getResources().getColor(R.color.white));
            orderBar.setBackgroundColor(getResources().getColor(R.color.white));
            profileBar.setBackgroundColor(getResources().getColor(R.color.white));
            cartBar.setBackgroundColor(getResources().getColor(R.color.white));

            homeTxt.setTextColor(getResources().getColor(R.color.black));
            searchTxt.setTextColor(getResources().getColor(R.color.black));
            orderTxt.setTextColor(getResources().getColor(R.color.black));
            profileTxt.setTextColor(getResources().getColor(R.color.black));
            cartTxt.setTextColor(getResources().getColor(R.color.black));

            homeTxt.setTypeface(null, Typeface.NORMAL);
            searchTxt.setTypeface(null, Typeface.NORMAL);
            orderTxt.setTypeface(null, Typeface.NORMAL);
            profileTxt.setTypeface(null, Typeface.NORMAL);
            cartTxt.setTypeface(null, Typeface.NORMAL);


            orderBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            orderTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
            orderTxt.setTypeface(homeTxt.getTypeface(), Typeface.BOLD);

        }else  if (value.equals("profile")) {
            homeBar.setBackgroundColor(getResources().getColor(R.color.white));
            searchBar.setBackgroundColor(getResources().getColor(R.color.white));
            orderBar.setBackgroundColor(getResources().getColor(R.color.white));
            profileBar.setBackgroundColor(getResources().getColor(R.color.white));
            cartBar.setBackgroundColor(getResources().getColor(R.color.white));

            homeTxt.setTextColor(getResources().getColor(R.color.black));
            searchTxt.setTextColor(getResources().getColor(R.color.black));
            orderTxt.setTextColor(getResources().getColor(R.color.black));
            profileTxt.setTextColor(getResources().getColor(R.color.black));
            cartTxt.setTextColor(getResources().getColor(R.color.black));

            homeTxt.setTypeface(null, Typeface.NORMAL);
            searchTxt.setTypeface(null, Typeface.NORMAL);
            orderTxt.setTypeface(null, Typeface.NORMAL);
            profileTxt.setTypeface(null, Typeface.NORMAL);
            cartTxt.setTypeface(null, Typeface.NORMAL);


            profileBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            profileTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
            profileTxt.setTypeface(homeTxt.getTypeface(), Typeface.BOLD);

        }else  if (value.equals("cart")) {
            homeBar.setBackgroundColor(getResources().getColor(R.color.white));
            searchBar.setBackgroundColor(getResources().getColor(R.color.white));
            orderBar.setBackgroundColor(getResources().getColor(R.color.white));
            profileBar.setBackgroundColor(getResources().getColor(R.color.white));
            cartBar.setBackgroundColor(getResources().getColor(R.color.white));

            homeTxt.setTextColor(getResources().getColor(R.color.black));
            searchTxt.setTextColor(getResources().getColor(R.color.black));
            orderTxt.setTextColor(getResources().getColor(R.color.black));
            profileTxt.setTextColor(getResources().getColor(R.color.black));
            cartTxt.setTextColor(getResources().getColor(R.color.black));

            homeTxt.setTypeface(homeTxt.getTypeface(), Typeface.NORMAL);
            searchTxt.setTypeface(homeTxt.getTypeface(), Typeface.NORMAL);
            orderTxt.setTypeface(homeTxt.getTypeface(), Typeface.NORMAL);
            profileTxt.setTypeface(homeTxt.getTypeface(), Typeface.NORMAL);
            cartTxt.setTypeface(homeTxt.getTypeface(), Typeface.NORMAL);

            cartBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            cartTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
            cartTxt.setTypeface(homeTxt.getTypeface(), Typeface.BOLD);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        navView.clearAnimation();

        home=findViewById(R.id.home);
        search=findViewById(R.id.search);
        order=findViewById(R.id.order);
        profile=findViewById(R.id.profile);

        homeBar=findViewById(R.id.homeBar);
        searchBar=findViewById(R.id.searchBar);
        orderBar=findViewById(R.id.orderBar);
        profileBar=findViewById(R.id.profileBar);
        cartBar=findViewById(R.id.cartBar);

        homeIcon=findViewById(R.id.homeIcon);
        searchIcon=findViewById(R.id.searchIcon);
        orderIcon=findViewById(R.id.orderIcon);
        profileIcon=findViewById(R.id.profileIcon);

        homeTxt=findViewById(R.id.homeTxt);
        searchTxt=findViewById(R.id.searchTxt);
        orderTxt=findViewById(R.id.orderTxt);
        profileTxt=findViewById(R.id.profileTxt);

        cart=findViewById(R.id.cart);
        cartIcon=findViewById(R.id.cartIcon);
        cartTxt=findViewById(R.id.cartTxt);

        cartCountLayout=findViewById(R.id.cartCountLayout);
        cartCount=findViewById(R.id.cartCount);

        loadFragment(new HomeFragment());
        setBottomView("home");

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomView("cart");
                fragmentSwitching(new CartFragment());
//                startActivity(new Intent(getApplicationContext(),CartActivity.class));
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomView("home");
                fragmentSwitching(new HomeFragment());
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomView("search");
                fragmentSwitching(new SearchFragment());
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomView("order");
                fragmentSwitching(new OrderFragment());
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomView("profile");
                fragmentSwitching(new MenuFragment());
            }
        });

        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Map map=new HashMap();
                map.put("mobile", Preferences.getInstance(getApplicationContext()).getMobile());
                map.put("fcmToken",newToken);
                map.put("userType","Customer");
                ThisApp.getApi(getApplicationContext()).saveToken(map).enqueue(new Callback<SucessResponse>() {
                    @Override
                    public void onResponse(Call<SucessResponse> call, Response<SucessResponse> response) {
                        try {
                            if (response.body().getResponse().getConfirmation()==0){
                                getDialog(response.body().getResponse().getMessage());
                            }
                        }catch (Exception e){

                        }

//                        Toast.makeText(MainActivity.this, "Token saved", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<SucessResponse> call, Throwable t) {

                    }
                });

//                ApiCallService.action(MainActivity.this,map,ApiCallService.Action.ACTION_SAVE_TOKEN);

            }
        });

//        Intent intent = new Intent(MainActivity.this, GPSService.class);
//        startService(intent);

    }



    private boolean loadFragment(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
        return true;
    }


    /*@Subscribe
    public void eventClick(LocationEvent event) {
        if (Helper.isNetworkAvailable(getApplicationContext())) {
            Map map=new Hashtable();
            map.put("latitude",event.getLocation().getLatitude());
            map.put("latitude",event.getLocation().getLongitude());
            map.put("mobile", Preferences.getInstance(getApplicationContext()).getMobile());
            ApiCallService.action(this,map,ApiCallService.Action.ACTION_LIVE_LOCATION);
        }
    }*/

    public void CartIcon(){
        if (LocalRepositories.getAppUser(getApplicationContext()).getCartModels()==null){
            cartCountLayout.setVisibility(View.GONE);
        }else {
            cartCountLayout.setVisibility(View.VISIBLE);
            cartCount.setText(""+LocalRepositories.getAppUser(getApplicationContext()).getCartModels().size());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CartIcon();
        /*home.setBackgroundColor(getResources().getColor(R.color.white));
        search.setBackgroundColor(getResources().getColor(R.color.white));
        order.setBackgroundColor(getResources().getColor(R.color.white));
        cart.setBackgroundColor(getResources().getColor(R.color.white));
        profile.setBackgroundColor(getResources().getColor(R.color.white));

        homeTxt.setTextColor(getResources().getColor(R.color.black));
        searchTxt.setTextColor(getResources().getColor(R.color.black));
        orderTxt.setTextColor(getResources().getColor(R.color.black));
        cartTxt.setTextColor(getResources().getColor(R.color.black));
        profileTxt.setTextColor(getResources().getColor(R.color.black));


        ImageViewCompat.setImageTintList(homeIcon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
        ImageViewCompat.setImageTintList(searchIcon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
        ImageViewCompat.setImageTintList(orderIcon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
        ImageViewCompat.setImageTintList(profileIcon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
        ImageViewCompat.setImageTintList(cartIcon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));

        home.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        homeTxt.setTextColor(getResources().getColor(R.color.white));
        ImageViewCompat.setImageTintList(homeIcon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        */


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Preferences.getInstance(getApplicationContext()).setAdd("");
        Preferences.getInstance(getApplicationContext()).setLat("");
        Preferences.getInstance(getApplicationContext()).setLong("");
    }
}
