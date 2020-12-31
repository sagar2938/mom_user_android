package mom.com.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import mom.com.adapter.MomItemDetailAdapter;
import mom.com.R;
import mom.com.fragment.HomeFragment;
import mom.com.model.CartModel;
import mom.com.network.ApiCallService;
import mom.com.network.ThisApp;
import mom.com.network.response.AddResponse;
import mom.com.network.response.MenuData;
import mom.com.network.response.MomsItemListResponse;
import mom.com.utils.AppUser;
import mom.com.utils.CustomProgressDialog;
import mom.com.utils.LocalRepositories;
import mom.com.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MomItemDetailActivity extends BaseActivity {

    RecyclerView recyclerView;
    LinearLayout view_cart;
    TextView name;
    TextView address;
    TextView time;
    TextView rating;
    TextView count;
    TextView amount;
    LinearLayout back;
    LinearLayout descriptionLayout;
    LinearLayout addressLayout;
    TextView description;
    LinearLayout favouriteLayout;
    TextView favouriteTxt;
    ImageView favouriteIcon;
    ImageView image;
    Switch switchButton;
    ConstraintLayout cart;
    public static MomItemDetailActivity context;
    MomItemDetailAdapter adapter;
    List<MenuData> vegResponse;
    List<MenuData> response;
    boolean favourite;
    LinearLayout mainLayout;

    String momIdStr;
    int onlineStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);
//        Toolbar toolbar=findViewById(R.id.toolbar_rest_details);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Food Details");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = findViewById(R.id.mainLayout);
        addressLayout = findViewById(R.id.addressLayout);
        description = findViewById(R.id.description);
        descriptionLayout = findViewById(R.id.descriptionLayout);
        back = findViewById(R.id.back);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        time = findViewById(R.id.time);
        rating = findViewById(R.id.rating);
        count = findViewById(R.id.count);
        amount = findViewById(R.id.amount);
        image = findViewById(R.id.image);
        switchButton = findViewById(R.id.switchButton);
        cart = findViewById(R.id.cart);
        favouriteLayout = findViewById(R.id.favouriteLayout);
        favouriteTxt = findViewById(R.id.favouriteTxt);
        favouriteIcon = findViewById(R.id.favouriteIcon);
        cart.setVisibility(View.GONE);
        context = this;

        HomeFragment.updateList=true;

        mainLayout.setVisibility(View.GONE);
        momIdStr = getIntent().getStringExtra("momId");
        Log.d("MOMID---->",momIdStr+".....");

        if(momIdStr!=null) {
            favouriteChange();
        }

        if (favourite) {
            favouriteTxt.setText("Favourite");
            favouriteTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
            ImageViewCompat.setImageTintList(favouriteIcon, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
        } else {
            favouriteTxt.setText("Mark as favourite");
            favouriteTxt.setTextColor(getResources().getColor(R.color.default_text));
            ImageViewCompat.setImageTintList(favouriteIcon, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_light)));
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        address.setText(Helper.getAddressText(this,new LatLng(FusedLocation.location.getLatitude(),FusedLocation.location.getLongitude())));

        Map map = new HashMap();
        map.put("mobile", getIntent().getStringExtra("mobile"));
//        ApiCallService.action(MomItemDetailActivity.this,map,ApiCallService.Action.ACTION_GET_MENU);

        name.setText(getIntent().getStringExtra("name"));
        address.setText(getIntent().getStringExtra("address"));
        rating.setText(getIntent().getStringExtra("rating"));
        addressLayout.setVisibility(View.GONE);
        onlineStatus = getIntent().getIntExtra("onlineStatus",0);
        if (getIntent().getStringExtra("description") != null) {
            if (getIntent().getStringExtra("description").equals("")) {
                descriptionLayout.setVisibility(View.GONE);
                addressLayout.setVisibility(View.VISIBLE);
            }
        } else {
            descriptionLayout.setVisibility(View.GONE);
            addressLayout.setVisibility(View.VISIBLE);
        }
        description.setText(getIntent().getStringExtra("description"));

        Glide.with(context)
                .load(getIntent().getStringExtra("image"))
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_food))
                .into(image);


        recyclerView = findViewById(R.id.recyclerview);
        view_cart = findViewById(R.id.view_cart);
        view_cart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (count.getText().toString().trim().equals("0 Item")) {
                    getDialog("Please add at least one item");
                    return;
                }


                AsyncTaskExample asyncTask = new AsyncTaskExample();
                asyncTask.execute();

                /*AppUser appUser= LocalRepositories.getAppUser(getApplicationContext());
                if (appUser.getCartModels()==null){
                    appUser.setMomMobile(getIntent().getStringExtra("mobile"));
                    appUser.setCartModels(CartActivity.context.getList());
                    appUser.setAmount(getAmount());
                    LocalRepositories.saveAppUser(getApplicationContext(),appUser);
                }else {
                    appUser.setMomMobile(getIntent().getStringExtra("mobile"));
//                    appUser.getCartModels().addAll(CartActivity.context.getList());
//                    appUser.getCartModels().stream().collect(Collectors.groupingBy(CartModel::getItemName, Collectors.summingInt(CartModel::getQuantity)));
                    manageList2(appUser.getCartModels());
//                    appUser.setCartModels();
                    appUser.setAmount((appUser.getAmount()+getAmount()));
                    LocalRepositories.saveAppUser(getApplicationContext(),appUser);
                }

                Intent intent=new Intent(MomItemDetailActivity.this, CartActivity.class);
                intent.putExtra("name",getIntent().getStringExtra("name"));
                intent.putExtra("address",getIntent().getStringExtra("address"));
                intent.putExtra("image",getIntent().getStringExtra("image"));
                intent.putExtra("mobile",getIntent().getStringExtra("mobile"));
                startActivity(intent);*/


//                Preferences.getInstance(getApplicationContext()).setMomMobile(getIntent().getStringExtra("mobile"));

            }
        });


        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    count.setText("0 Item");
                    amount.setText("0.0");
                    if (isChecked) {
                        setAdapter(vegResponse);
                    } else {
                        setAdapter(response);
                    }
                } catch (Exception e) {

                }

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


    @Subscribe
    public void getMomItemList(MomsItemListResponse response) {
        mainLayout.setVisibility(View.VISIBLE);
        setAdapter(response.getMenuData());
        this.response = response.getMenuData();
        getVegList(response.getMenuData());
    }


    void setAdapter(List<MenuData> menuDataList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MomItemDetailAdapter(menuDataList, MomItemDetailActivity.this, cart,onlineStatus);
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);
    }

    public void setAmount(String amt) {
        amount.setText(amt);
    }

    public Double getAmount() {
        return Double.valueOf(amount.getText().toString());
    }


    public void setQuantity(Map<Integer, String> map) {
        int i = 0;
        for (Integer key : map.keySet()) {
            Integer val = Integer.valueOf(map.get(key));
            i = i + val;
        }
        if (i < 2) {
            count.setText("" + i + " Item");
        } else {
            count.setText("" + i + " Items");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        /*try {
            adapter.notifyDataSetChanged();
            if (LocalRepositories.getAppUser(getApplicationContext()).getCartModels()==null){
                cart.setVisibility(View.GONE);
            }
        }catch (Exception e){

        }*/
        cart.setVisibility(View.GONE);
        amount.setText("0.0");
        Map map = new HashMap();
        map.put("mobile", getIntent().getStringExtra("mobile"));
        ApiCallService.action(MomItemDetailActivity.this, map, ApiCallService.Action.ACTION_GET_MENU);
    }


    List<MenuData> getVegList(List<MenuData> momsItemListResponse) {
        vegResponse = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            if (response.get(i).getFoodType().equals("Veg")) {
                vegResponse.add(response.get(i));
            }
        }
        return vegResponse;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    void manageList(List<CartModel> cartModelListPre) {
        List<CartModel> cartModelListNew = CartActivity.getList();
        for (int i = 0; i < cartModelListNew.size(); i++) {
            for (int j = 0; j < cartModelListPre.size(); j++) {
                try {
                    CartModel cartModelNew = cartModelListNew.get(i);
                    CartModel cartModelPre = cartModelListPre.get(j);
                    if (cartModelPre.getItemName().equals(cartModelNew.getItemName())) {
                        cartModelPre.setItemName(cartModelNew.getItemName());
                        cartModelPre.setItemDescription(cartModelNew.getItemDescription());
                        cartModelPre.setPrice(cartModelPre.getPrice() + cartModelNew.getPrice());
                        cartModelPre.setQuantity(cartModelPre.getQuantity() + cartModelNew.getQuantity());
                        cartModelListPre.set(i, cartModelPre);
                        cartModelListNew.remove(i);
                    }
                } catch (Exception e) {

                }

            }
        }
        cartModelListPre.addAll(cartModelListNew);
    }


    void manageList2(List<CartModel> addedList) {
        List<CartModel> newList = CartActivity.getList();
        for (int i = 0; i < newList.size(); i++) {
            for (int j = 0; j < addedList.size(); j++) {
                try {
                    CartModel newModel = newList.get(i);
                    CartModel addedModel = addedList.get(j);
                    if (newModel.getId().equals(addedModel.getId())) {
                        addedModel.setItemName(newModel.getItemName());
                        addedModel.setItemDescription(newModel.getItemDescription());
                        addedModel.setPrice(addedModel.getPrice() + newModel.getPrice());
                        addedModel.setQuantity(addedModel.getQuantity() + newModel.getQuantity());
//                        addedList.set(i,addedModel);
                        newList.remove(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        addedList.addAll(newList);
        List<CartModel> list = addedList;
    }


    void manageListNew(List<CartModel> addedList) {
        for (int i = 0; i < addedList.size(); i++) {
            for (int j = 0; j < addedList.size(); j++) {
                try {
                    if (addedList.get(i).getId().equals(addedList.get(j).getId())) {
//                        addedModel.setItemName(newModel.getItemName());
//                        addedModel.setItemDescription(newModel.getItemDescription());
                        addedList.get(i).setPrice(addedList.get(i).getPrice() + addedList.get(j).getPrice());
                        addedList.get(i).setQuantity(addedList.get(i).getQuantity() + addedList.get(j).getQuantity());
                        addedList.set(i, addedList.get(i));
                        addedList.remove(j);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
//        addedList.addAll(addedList);
//        List<CartModel> addedList=addedList;
    }


    class AsyncTaskExample extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
//            CustomProgressDialog.getInstance(MomItemDetailActivity.this).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            AppUser appUser = LocalRepositories.getAppUser(getApplicationContext());
            if (appUser.getCartModels() == null) {
                appUser.setMomMobile(getIntent().getStringExtra("mobile"));
                appUser.setCartModels(CartActivity.context.getList());
                appUser.setAmount(getAmount());
            } else {
                appUser.setMomMobile(getIntent().getStringExtra("mobile"));
//                appUser.setCartModels(CartActivity.context.getList());
                manageList2(appUser.getCartModels());
                appUser.setAmount((appUser.getAmount() + getAmount()));
            }
            LocalRepositories.saveAppUser(getApplicationContext(), appUser);
            return "";
        }

        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);
//            CustomProgressDialog.setDismiss();
            Intent intent = new Intent(MomItemDetailActivity.this, CartActivity.class);
            intent.putExtra("name", getIntent().getStringExtra("name"));
            intent.putExtra("address", getIntent().getStringExtra("address"));
            intent.putExtra("image", getIntent().getStringExtra("image"));
            intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
            intent.putExtra("count",1);
            startActivity(intent);

        }
    }


    void favouriteChange() {
        String momId = momIdStr;
        favourite = getIntent().getBooleanExtra("favourite", false);
        favouriteCondition();
        favouriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomProgressDialog.getInstance(context).show();
                Map map = new Hashtable();
                map.put("mobile", Preferences.getInstance(MomItemDetailActivity.this).getMobile());
                map.put("momId", momId);
                ThisApp.getApi(MomItemDetailActivity.this).favourite(map).enqueue(new Callback<AddResponse>() {
                    @Override
                    public void onResponse(Call<AddResponse> call, Response<AddResponse> response) {
                        CustomProgressDialog.setDismiss();
                        if (favourite) {
                            favourite = false;
                            favouriteCondition();
                        } else {
                            favourite = true;
                            favouriteCondition();
                        }

                    }

                    @Override
                    public void onFailure(Call<AddResponse> call, Throwable t) {
                        CustomProgressDialog.setDismiss();
                    }
                });
            }
        });

    }

    void favouriteCondition() {
        if (favourite) {
            favouriteTxt.setText("Favourite");
            favouriteTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
            ImageViewCompat.setImageTintList(favouriteIcon, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
        } else {
            favouriteTxt.setText("Mark as favourite");
            favouriteTxt.setTextColor(getResources().getColor(R.color.default_text));
            ImageViewCompat.setImageTintList(favouriteIcon, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_light)));
        }
    }


}


