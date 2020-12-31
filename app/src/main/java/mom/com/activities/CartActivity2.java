package mom.com.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import mom.com.R;
import mom.com.adapter.MomItemDetailAdapter;
import mom.com.activities.address.AddAddressActivity;
import mom.com.helper.FusedLocation;
import mom.com.model.CartMainModel;
import mom.com.model.CartModel;
import mom.com.network.ApiCallService;
import mom.com.network.response.OrderBookingResponse;
import mom.com.utils.Preferences;


public class CartActivity2 extends BaseActivity {

    TextView apply_promo_tv;
    ConstraintLayout applyPromo;
    RecyclerView recyclerView;
    public static CartActivity2 context;
    TextView amount;
    TextView gstTextView;
    TextView grandTotal;
    TextView name;
    TextView address;
    TextView textButton;
    TextView customerAddress;
    TextView change;
    ImageView image;
    LinearLayout submit;
    ConstraintLayout paymentSelection;
    TextView mode;
    ImageView modeImage;


    String latitude="";
    String longitude="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart3);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Cart");
        context=this;

        recyclerView=findViewById(R.id.recyclerView);
        apply_promo_tv=findViewById(R.id.apply_promo_tv);
        applyPromo=findViewById(R.id.applyPromo);
        amount=findViewById(R.id.amount);
        gstTextView=findViewById(R.id.gstTextView);
        grandTotal=findViewById(R.id.grandTotal);
        name=findViewById(R.id.name);
        address=findViewById(R.id.address);
        textButton=findViewById(R.id.textButton);
        customerAddress=findViewById(R.id.customerAddress);
        change=findViewById(R.id.change);
        image=findViewById(R.id.image);
        submit=findViewById(R.id.submit);
        paymentSelection=findViewById(R.id.paymentSelection);
        mode=findViewById(R.id.mode);
        modeImage=findViewById(R.id.modeImage);


        modeCondition();

        paymentSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),PaymentTypeActivity.class);
                intent.putExtra("amount",grandTotal.getText().toString());
                startActivityForResult(intent,2);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Preferences.getInstance(getApplicationContext()).isLogin()){
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }else {
                    CartMainModel cartMainModel = new CartMainModel();
                    cartMainModel.setMom_mobile(getIntent().getStringExtra("mobile"));
                    cartMainModel.setProduct_list(getList());
                    cartMainModel.setLocation(customerAddress.getText().toString());
                    cartMainModel.setLatitude("" + FusedLocation.location.getLatitude());
                    cartMainModel.setLongitude("" + FusedLocation.location.getLongitude());
                    cartMainModel.setTotal_price(grandTotal.getText().toString());
                    cartMainModel.setName(Preferences.getInstance(getApplicationContext()).getName());
                    cartMainModel.setMobile(Preferences.getInstance(getApplicationContext()).getMobile());
                    cartMainModel.setPaymentMode(Preferences.getInstance(getApplicationContext()).getMode());
                    ApiCallService.action(CartActivity2.this, cartMainModel, ApiCallService.Action.ACTION_SUBMIT_ORDER);
                }
            }
        });


        Glide.with(context)
                .load(getIntent().getStringExtra("image"))
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_food))
                .into(image);

        if (Preferences.getInstance(getApplicationContext()).isLogin()){
            if (Preferences.getInstance(getApplicationContext()).getAddressStatus()==1){
                latitude=Preferences.getInstance(getApplicationContext()).getLatitude();
                longitude=Preferences.getInstance(getApplicationContext()).getLongitude();
                textButton.setText("Proceed to pay");
                customerAddress.setText(Preferences.getInstance(getApplicationContext()).getAddress());
                change.setText("CHANGE");
                change.setTextColor(getResources().getColor(R.color.black));
            }else {
                latitude=""+FusedLocation.location.getLatitude();
                latitude=""+FusedLocation.location.getLongitude();
                textButton.setText("Proceed to pay");
                customerAddress.setText(FusedLocation.ADDRESS);
                change.setText("CHANGE");
                change.setTextColor(getResources().getColor(R.color.black));
            }
        }else {
            latitude=""+FusedLocation.location.getLatitude();
            latitude=""+FusedLocation.location.getLongitude();
            textButton.setText("Login");
            customerAddress.setText(FusedLocation.ADDRESS);
            change.setText("LOGIN");
            change.setTextColor(getResources().getColor(R.color.blue));
        }

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.getInstance(getApplicationContext()).isLogin()) {
                    startActivityForResult(new Intent(getApplicationContext(), AddAddressActivity.class), 1);
                }else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        });

        name.setText(getIntent().getStringExtra("name"));
        address.setText(getIntent().getStringExtra("address"));

        apply_promo_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ApplyCoupons.class);
                startActivity(intent);
            }
        });
        applyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ApplyCoupons.class);
                startActivity(intent);
            }
        });

        setAmount(MomItemDetailActivity.context.getAmount());
        setAdapter();
    }





    void setAdapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        CartAdapter adapter = new CartAdapter(getList(), CartActivity2.this);
//        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);
    }


    List<CartModel> getList(){
        List<CartModel> cartModelList=new ArrayList<>();
        CartModel cartModel;

        /*for (Integer key : MomItemDetailAdapter.context.mQuarterQuantity.keySet()){
            cartModel =new CartModel();
            cartModel.setId(key);
            cartModel.setItemName(MomItemDetailAdapter.context.mItemDetail.get(key).getItemName());
            cartModel.setItemDescription(MomItemDetailAdapter.context.mItemDetail.get(key).getItemDescription());
            cartModel.setImage(MomItemDetailAdapter.context.mItemDetail.get(key).getImagePath()+MomItemDetailAdapter.context.mItemDetail.get(key).getItemImage());
            cartModel.setItemActualPrice(MomItemDetailAdapter.context.mItemDetail.get(key).getQuarterPrice());
            cartModel.setQuantity(Integer.valueOf(MomItemDetailAdapter.context.mQuarterQuantity.get(key)));
            cartModel.setType("Quarter");
            double price=Integer.valueOf(MomItemDetailAdapter.context.mQuarterQuantity.get(key))*Double.valueOf(MomItemDetailAdapter.context.mItemDetail.get(key).getQuarterPrice());
            cartModel.setPrice(price);
            cartModelList.add(cartModel);
        }*/




        /*for (Integer key : MomItemDetailAdapter.context.mHalfQuantity.keySet()){
            cartModel =new CartModel();
            cartModel.setId(key);
            cartModel.setItemName(MomItemDetailAdapter.context.mItemDetail.get(key).getItemName());
            cartModel.setItemDescription(MomItemDetailAdapter.context.mItemDetail.get(key).getItemDescription());
            cartModel.setImage(MomItemDetailAdapter.context.mItemDetail.get(key).getImagePath()+MomItemDetailAdapter.context.mItemDetail.get(key).getItemImage());
            cartModel.setItemActualPrice(MomItemDetailAdapter.context.mItemDetail.get(key).getHalfPrice());
            cartModel.setQuantity(Integer.valueOf(MomItemDetailAdapter.context.mHalfQuantity.get(key)));
            cartModel.setType("Half");
            double price=Integer.valueOf(MomItemDetailAdapter.context.mHalfQuantity.get(key))*Double.valueOf(MomItemDetailAdapter.context.mItemDetail.get(key).getHalfPrice());
            cartModel.setPrice(price);
            cartModelList.add(cartModel);
        }*/


        for (Integer key : MomItemDetailAdapter.context.mFullQuantity.keySet()){
            cartModel =new CartModel();
            cartModel.setId(key);
            cartModel.setItemName(MomItemDetailAdapter.context.mItemDetail.get(key).getItemName());
            cartModel.setItemDescription(MomItemDetailAdapter.context.mItemDetail.get(key).getItemDescription());
            cartModel.setImage(MomItemDetailAdapter.context.mItemDetail.get(key).getItemImage());
            cartModel.setItemActualPrice(MomItemDetailAdapter.context.mItemDetail.get(key).getFullPrice());
            cartModel.setQuantity(Integer.valueOf(MomItemDetailAdapter.context.mFullQuantity.get(key)));
            cartModel.setType("Full");
            double price=Integer.valueOf(MomItemDetailAdapter.context.mFullQuantity.get(key))*Double.valueOf(MomItemDetailAdapter.context.mItemDetail.get(key).getFullPrice());
            cartModel.setPrice(price);
            cartModelList.add(cartModel);
        }
        return cartModelList;

    }




    public void setAmount(Double amt){
        amount.setText(""+amt);
        double gst= (amt*5)/100;
        gstTextView.setText(""+gst);
        grandTotal.setText(""+(amt+gst));
    }
    public Double getAmount(){
        return Double.valueOf(amount.getText().toString());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==1){
                latitude=data.getStringExtra("latitude");
                longitude=data.getStringExtra("longitude");
                customerAddress.setText(data.getStringExtra("address"));
            }else if (requestCode==2){
                modeCondition();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


    @Subscribe
    public void add_order(OrderBookingResponse response){
        if (response.isSuccess()) {
            getDialogSucess("Your order placed");
        }else {
            getDialog("Some thing went wrong");
        }
    }

    public void getDialogSucess( String message) {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
//                .setNegativeButton("Cancel", null)
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }


    void modeCondition(){
        if (Preferences.getInstance(getApplicationContext()).getMode().equals("Paytm")){
            modeImage.setImageDrawable(getResources().getDrawable(R.drawable.paytm_48px));
        }else {
            modeImage.setImageDrawable(getResources().getDrawable(R.drawable.cash_depostd));
        }
        mode.setText(Preferences.getInstance(getApplicationContext()).getMode());

    }

}



