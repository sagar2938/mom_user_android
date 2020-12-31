package mom.com.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.tapadoo.alerter.Alerter;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cdflynn.android.library.checkview.CheckView;
import mom.com.R;
import mom.com.WebService.ResponseCallBack;
import mom.com.WebService.WebServiceHelper;
import mom.com.adapter.CartAdapter;
import mom.com.adapter.MomItemDetailAdapter;
import mom.com.event.EventResetPromo;
import mom.com.helper.FusedLocation;
import mom.com.model.CartMainModel;
import mom.com.model.CartModel;
import mom.com.network.ApiCallService;
import mom.com.network.ThisApp;
import mom.com.network.request.EventPushRequest;
import mom.com.network.response.DeliveryChargeResponse;
import mom.com.network.response.GetStatusResponse;
import mom.com.network.response.OnlineOrderBookingResponse;
import mom.com.network.response.OrderBookingResponse;
import mom.com.network.response.PromoResponse;
import mom.com.network.response.PushNotificationResponse;
import mom.com.paytmpayment.JSONParser;
import mom.com.paytmpayment.PaytmDemoActivity;
import mom.com.paytmpayment.checksum;
import mom.com.service.BackgroundService;
import mom.com.utils.AppUser;
import mom.com.utils.LocalRepositories;
import mom.com.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartActivity extends BaseActivity implements PaytmPaymentTransactionCallback {

    CardView applyPromo;
    RecyclerView recyclerView;
    public static CartActivity context;
    TextView amount;
    TextView gstTextView;
    TextView grandTotal;
    TextView subTotal;
    TextView textButton;
    TextView deliveryCharge;
    TextView customerAddress;
    CardView change;
    View view;
    LinearLayout submit;
    LinearLayout customerAddressLayout;
    LinearLayout cash;
    LinearLayout card;
    LinearLayout online;
    ImageView cashCircle;
    ImageView cardCircle;
    ImageView onlineCircle;
    TextView cashTxt;
    TextView cardTxt;
    TextView onlineTxt;
    TextView promoAmount;
    TextView promoCode;
    LinearLayout noRecord;
    CardView constraintLayout5;
    EditText note;
    LinearLayout close;
    String latitude = "";
    String longitude = "";
    AppUser appUser;
    CheckView check;
    LinearLayout orderPlaced;
    String promoCodeId = "";
    TextView apply_tv;
    String promoCodeStr;
    private static int count = 1;
    String custid, orderId, mid = "rchmXY53623731345120";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart3);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Cart");
        context = this;


        recyclerView = findViewById(R.id.recyclerView);
        close = findViewById(R.id.close);
        applyPromo = findViewById(R.id.applyPromo);
        amount = findViewById(R.id.amount);
        gstTextView = findViewById(R.id.gstTextView);
        grandTotal = findViewById(R.id.grandTotal);
        subTotal = findViewById(R.id.subTotal);
        textButton = findViewById(R.id.textButton);
        customerAddress = findViewById(R.id.customerAddress);
        change = findViewById(R.id.change);
        view = findViewById(R.id.view);
        customerAddressLayout = findViewById(R.id.customerAddressLayout);
        submit = findViewById(R.id.submit);
        cash = findViewById(R.id.cash);
        card = findViewById(R.id.card);
        online = findViewById(R.id.online);

        cashCircle = findViewById(R.id.cashCircle);
        cardCircle = findViewById(R.id.cardCircle);
        onlineCircle = findViewById(R.id.onlineCircle);

        cashTxt = findViewById(R.id.cashTxt);
        cardTxt = findViewById(R.id.cardTxt);
        onlineTxt = findViewById(R.id.onlineTxt);

        promoCode = findViewById(R.id.promoCode);
        promoAmount = findViewById(R.id.promoAmount);
        noRecord = findViewById(R.id.noRecord);
        constraintLayout5 = findViewById(R.id.constraintLayout5);
        note = findViewById(R.id.note);
        orderPlaced = findViewById(R.id.orderPlaced);
        check = findViewById(R.id.check);
        orderPlaced.setVisibility(View.GONE);
        deliveryCharge = findViewById(R.id.deliveryCharge);

        apply_tv = findViewById(R.id.apply_tv);

        close.setVisibility(View.GONE);


        Preferences.getInstance(getApplicationContext()).setMode("Online");
        count = 3;
        onlineTxt.setTypeface(Typeface.DEFAULT_BOLD);
        cashTxt.setTypeface(Typeface.DEFAULT);
        onlineCircle.setImageDrawable(getResources().getDrawable(R.drawable.circle_check));
        cashCircle.setImageDrawable(getResources().getDrawable(R.drawable.circles));

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close.setVisibility(View.GONE);
                promoCode.setText("");
                promoAmount.setText("0.0");
//                deliveryCharge.setText("0.0");
                apply_tv.setText("   Add Promo Code   ");
                grandTotal.setText("" + String.format("%.2f", (Double.valueOf(subTotal.getText().toString()) + Double.valueOf(deliveryCharge.getText().toString()))));

            }
        });

        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.getInstance(getApplicationContext()).setMode("Cash");
                count = 1;
                cashTxt.setTypeface(Typeface.DEFAULT_BOLD);
                onlineTxt.setTypeface(Typeface.DEFAULT);
                cashCircle.setImageDrawable(getResources().getDrawable(R.drawable.circle_check));
                cardCircle.setImageDrawable(getResources().getDrawable(R.drawable.circles));
                onlineCircle.setImageDrawable(getResources().getDrawable(R.drawable.circles));

            }
        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                count=2;
                cashTxt.setTypeface(Typeface.DEFAULT);
                cardTxt.setTypeface(Typeface.DEFAULT_BOLD);
                cardCircle.setImageDrawable(getResources().getDrawable(R.drawable.circle_check));
                cashCircle.setImageDrawable(getResources().getDrawable(R.drawable.circles));*/
            }
        });
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.getInstance(getApplicationContext()).setMode("Online");
                count = 3;
                onlineTxt.setTypeface(Typeface.DEFAULT_BOLD);
                cashTxt.setTypeface(Typeface.DEFAULT);
                onlineCircle.setImageDrawable(getResources().getDrawable(R.drawable.circle_check));
                cashCircle.setImageDrawable(getResources().getDrawable(R.drawable.circles));

                /* Intent pamentIntent=new Intent(getApplicationContext(), PaytmDemoActivity.class);
                startActivity(pamentIntent);*/

            }
        });


//        modeCondition();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!Preferences.getInstance(getApplicationContext()).isLogin()) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("cart", "cart");
                    startActivity(intent);
                } else if (!textButton.getText().toString().equals("Place your order")) {
                    Intent intent = new Intent(getApplicationContext(), AddressListActivity.class);
                    intent.putExtra("for", true);
                    startActivityForResult(intent, 1);
                } else {

                    if (!latitude.equalsIgnoreCase("") && !longitude.equalsIgnoreCase("")) {

                        double distance = distance(Double.valueOf(Preferences.getInstance(getApplicationContext()).getMom_latitude()), Double.valueOf(Preferences.getInstance(getApplicationContext()).getMom_longitude()),
                                Double.valueOf(latitude), Double.valueOf(longitude));
                        if (distance > 6.0) {
                            getDialog("This MOM is not serving in this location");
                            return;
                        }
                    }

                    appUser = LocalRepositories.getAppUser(getApplicationContext());
                    Map map = new HashMap();
                    map.put("mobile", Preferences.getInstance(getApplicationContext()).getMomMobile());
                    ApiCallService.action(CartActivity.this, map, ApiCallService.Action.ACTION_GET_STATUS);

                   /* CartMainModel cartMainModel = new CartMainModel();
                    cartMainModel.setMom_mobile(Preferences.getInstance(getApplicationContext()).getMomMobile());
                    cartMainModel.setProduct_list(appUser.getCartModels());
                    cartMainModel.setLocation(customerAddress.getText().toString());
                    cartMainModel.setLatitude(Preferences.getInstance(getApplicationContext()).getAddressLatitude());
                    cartMainModel.setLongitude(Preferences.getInstance(getApplicationContext()).getAddressLongitude());
                    cartMainModel.setTotal_price(grandTotal.getText().toString());
                    cartMainModel.setNote(note.getText().toString());
                    cartMainModel.setName(Preferences.getInstance(getApplicationContext()).getName());
                    cartMainModel.setMobile(Preferences.getInstance(getApplicationContext()).getMobile());
                    cartMainModel.setPaymentMode(Preferences.getInstance(getApplicationContext()).getMode());
                    ApiCallService.action(CartActivity.this, cartMainModel, ApiCallService.Action.ACTION_SUBMIT_ORDER);*/
                }
            }
        });


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.getInstance(getApplicationContext()).isLogin()) {
                    startActivityForResult(new Intent(getApplicationContext(), AddressListActivity.class), 1);
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        });


        customerAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.getInstance(getApplicationContext()).isLogin()) {
                    startActivityForResult(new Intent(getApplicationContext(), AddressListActivity.class), 1);
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        });


        applyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), OfferListActivity.class), 100);
            }
        });


        if (Preferences.getInstance(getApplicationContext()).isLogin()) {
            if (!Preferences.getInstance(getApplicationContext()).getAddress().equals("")) {
                latitude = Preferences.getInstance(getApplicationContext()).getAddressLatitude();
                longitude = Preferences.getInstance(getApplicationContext()).getAddressLongitude();
                textButton.setText("Place your order");
                view.setVisibility(View.GONE);
                customerAddress.setText(Preferences.getInstance(getApplicationContext()).getAddress());
//                change.setText("CHANGE");
//                change.setTextColor(getResources().getColor(R.color.black));
            } else {
                latitude = "" + FusedLocation.location.getLatitude();
                longitude = "" + FusedLocation.location.getLongitude();
                textButton.setText("Add Address");
                customerAddress.setText("Select Address");
                view.setVisibility(View.VISIBLE);
//                customerAddress.setText(FusedLocation.ADDRESS);
//                change.setText("CHANGE");
//                change.setTextColor(getResources().getColor(R.color.black));
            }
        } else {
//            latitude=""+FusedLocation.location.getLatitude();
//            latitude=""+FusedLocation.location.getLongitude();
            textButton.setText("Login");
//            customerAddress.setText(FusedLocation.ADDRESS);
//            change.setText("LOGIN");
//            change.setTextColor(getResources().getColor(R.color.blue));
        }

//       getAddressLongitude
        Log.d("LAtLong", Preferences.getInstance(this).getMom_latitude() + "..." + Preferences.getInstance(this).getMom_longitude() + ".." +
                latitude + "..." + longitude);
        appUser = LocalRepositories.getAppUser(this);
//        setAmount(MomItemDetailActivity.context.getAmount());
        setAmount(appUser.getAmount());

    }

    @Override
    public void onResume() {
        super.onResume();
        appUser = LocalRepositories.getAppUser(this);
//        setAmount(MomItemDetailActivity.context.getAmount());
        setAmount(appUser.getAmount());
        setAdapter();


        if (Preferences.getInstance(getApplicationContext()).isLogin()) {
            if (!Preferences.getInstance(getApplicationContext()).getAddress().equals("")&&!Preferences.getInstance(getApplicationContext()).getAddressLatitude().isEmpty()&&!Preferences.getInstance(getApplicationContext()).getAddressLongitude().isEmpty()) {
                latitude = Preferences.getInstance(getApplicationContext()).getAddressLatitude();
                longitude = Preferences.getInstance(getApplicationContext()).getAddressLongitude();
                textButton.setText("Place your order");
                view.setVisibility(View.GONE);
                customerAddress.setText(Preferences.getInstance(getApplicationContext()).getAddress());
            } else {
                latitude = "" + FusedLocation.location.getLatitude();
                longitude = "" + FusedLocation.location.getLongitude();
                textButton.setText("Add Address");
                customerAddress.setText("Select Address");
                view.setVisibility(View.VISIBLE);
            }
        } else {
            textButton.setText("Login");
        }
    }

    void setAdapter() {
        appUser = LocalRepositories.getAppUser(getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        if (appUser.getCartModels() != null) {
            noRecord.setVisibility(View.GONE);
            constraintLayout5.setVisibility(View.VISIBLE);
            CartAdapter adapter = new CartAdapter(appUser.getCartModels(), CartActivity.this);
            recyclerView.setAdapter(adapter);
        } else {
            noRecord.setVisibility(View.VISIBLE);
            constraintLayout5.setVisibility(View.GONE);
        }

    }


    public static List<CartModel> getList() {
        List<CartModel> cartModelList = new ArrayList<>();
        CartModel cartModel;

       /* for (Integer key : MomItemDetailAdapter.context.mQuarterQuantity.keySet()){
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


        for (Integer key : MomItemDetailAdapter.context.mFullQuantity.keySet()) {
            cartModel = new CartModel();
            cartModel.setId(key);
            cartModel.setItemName(MomItemDetailAdapter.context.mItemDetail.get(key).getItemName());
            cartModel.setItemDescription(MomItemDetailAdapter.context.mItemDetail.get(key).getItemDescription());
            cartModel.setImage(MomItemDetailAdapter.context.mItemDetail.get(key).getItemImage());
            cartModel.setItemActualPrice(MomItemDetailAdapter.context.mItemDetail.get(key).getFullPrice());
            cartModel.setQuantity(Integer.valueOf(MomItemDetailAdapter.context.mFullQuantity.get(key)));
            cartModel.setType("Full");
            double price = Integer.valueOf(MomItemDetailAdapter.context.mFullQuantity.get(key)) * Double.valueOf(MomItemDetailAdapter.context.mItemDetail.get(key).getFullPrice());
            cartModel.setPrice(price);
            cartModelList.add(cartModel);
        }
        return cartModelList;

    }
    double delivery = 0;
    public void setAmount(double amt) {
        amount.setText("" + amt);
        double gst = (amt * 5) / 100;
        if (!latitude.equalsIgnoreCase("") && !longitude.equalsIgnoreCase("")) {

            double distance = distance(Double.valueOf(Preferences.getInstance(getApplicationContext()).getMom_latitude()), Double.valueOf(Preferences.getInstance(getApplicationContext()).getMom_longitude()),
                    Double.valueOf(latitude), Double.valueOf(longitude));
            if (distance > 6.0) {
                delivery = 0;
                gstTextView.setText("" + String.format("%.2f", gst));
                grandTotal.setText("" + String.format("%.2f", (amt + gst + delivery - Double.valueOf(promoAmount.getText().toString()))));
                subTotal.setText("" + String.format("%.2f", (amt + gst)));
                deliveryCharge.setText("NA");
            } else {

                delivery = 10;
                Log.d("LatLongSelectedAddresIn", latitude + "..." + longitude);
                if (!latitude.equalsIgnoreCase("") && !longitude.equalsIgnoreCase("")) {
                    distance = distance(Double.valueOf(Preferences.getInstance(this).getMom_latitude()), Double.valueOf(Preferences.getInstance(this).getMom_longitude()),
                            Double.valueOf(latitude), Double.valueOf(longitude));
                    Log.d("LatLongSelectedAddresO", latitude + "..." + longitude);
                    if ((distance >= 0.0) && (distance <= 1.0)) {
                        delivery += 5;
                    } else if ((distance > 1.0) && (distance <= 3.0)) {
                        delivery += 12;

                    } else if ((distance > 3.0 && (distance <= 6.0))) {
                        delivery += 22;
                    } else {
                        delivery = 0;
                    }
                }

                Map map=new HashMap();
                map.put("delivery_lat",latitude);
                map.put("delivery_lang",longitude);
                map.put("mom_lat",Preferences.getInstance(getApplicationContext()).getMom_latitude());
                map.put("mom_lang",Preferences.getInstance(getApplicationContext()).getMom_longitude());
                map.put("distance",distance);

                ThisApp.getApi(getApplicationContext()).getDeliveryCharge(map).enqueue(new Callback<DeliveryChargeResponse>() {
                    @Override
                    public void onResponse(Call<DeliveryChargeResponse> call, Response<DeliveryChargeResponse> res) {
                        DeliveryChargeResponse response=res.body();
                        if (response.isSuccess()){
                            delivery=response.getDelivery_charge();
//                            delivery = calCulateDeliveryCharge();
                            gstTextView.setText("" + String.format("%.2f", gst));
                            grandTotal.setText("" + String.format("%.2f", (amt + gst + delivery - Double.valueOf(promoAmount.getText().toString()))));
                            subTotal.setText("" + String.format("%.2f", (amt + gst)));
                            deliveryCharge.setText("" + String.format("%.2f", delivery));
                        }
                    }

                    @Override
                    public void onFailure(Call<DeliveryChargeResponse> call, Throwable t) {

                    }
                });

            }
        }

    }

    public Double getAmount() {
        return Double.valueOf(amount.getText().toString());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
                Preferences.getInstance(getApplicationContext()).setAddress(data.getStringExtra("name") + "\n" + data.getStringExtra("address") + "\n" + Preferences.getInstance(getApplicationContext()).getMobile());
                customerAddress.setText(data.getStringExtra("name") + "\n" + data.getStringExtra("address") + "\n" + /*data.getStringExtra("mobile")*/Preferences.getInstance(getApplicationContext()).getMobile());
                view.setVisibility(View.GONE);
                appUser = LocalRepositories.getAppUser(this);
//        setAmount(MomItemDetailActivity.context.getAmount());
                setAmount(appUser.getAmount());
                textButton.setText("Place your order");
            } else if (requestCode == 2) {
                modeCondition();
            } else if (requestCode == 100) {
                Map map = new Hashtable();
                promoCodeStr = data.getStringExtra("promo");
                promoCodeId = data.getStringExtra("id") + "_" + data.getStringExtra("promo");
                map.put("total_price", subTotal.getText().toString());
                map.put("promocode", promoCodeStr);
                map.put("mobile", Preferences.getInstance(getApplicationContext()).getMobile());
                ApiCallService.action(getApplicationContext(), map, ApiCallService.Action.ACTION_PROMO_CODE);

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.icon_id) {
            dialogPlusClear();
        } else {
            finish();

        }
        return super.onOptionsItemSelected(item);
    }


    @Subscribe
    public void add_order(OrderBookingResponse response) {
        if (response.isSuccess()) {


            check.check();
            orderPlaced.setVisibility(View.VISIBLE);

            EventPushRequest request = new EventPushRequest();
            request.setTo(Preferences.getInstance(getApplicationContext()).getMomMobile());
            request.setMessage("You have received one new order");
//            ApiCallService.action2(CartActivity.this,request,ApiCallService.Action.ACTION_PUSH_NOTIFICATION);
            ThisApp.getApi(getApplicationContext()).pushNotification(request).enqueue(new Callback<PushNotificationResponse>() {
                @Override
                public void onResponse(Call<PushNotificationResponse> call, Response<PushNotificationResponse> response) {

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    Alerter.create(context)
                            .setTitle("")
                            .setBackgroundColorRes(R.color.colorPrimary)
                            .setText("Your order placed")
                            .setIcon(R.drawable.progress_launcher)
                            .setIconColorFilter(0)
                            .show();
                }

                @Override
                public void onFailure(Call<PushNotificationResponse> call, Throwable t) {

                }
            });
            Preferences.getInstance(getApplicationContext()).setMomMobile("");
            AppUser appUser = LocalRepositories.getAppUser(getApplicationContext());
            appUser.setCartModels(null);
            appUser.setMomMobile("");
            appUser.setAmount(0.0);
            LocalRepositories.saveAppUser(getApplicationContext(), appUser);


//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    onResume();
//                }
//            },5000);


//            getDialogSucess("Your order placed");
        } else {
            getDialog("Some thing went wrong");
        }
    }

    @Subscribe
    public void add_online_order(OnlineOrderBookingResponse response) {
        if (response.isSuccess()) {
            custid = Preferences.getInstance(getApplicationContext()).getMobile();
            orderId = response.getOrderId();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            /*if (ContextCompat.checkSelfPermission(CartActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CartActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                return;
            }*/
            returnCheckSum(custid, orderId, grandTotal.getText().toString());
            BackgroundService.action(this, response.getOrderId());
        } else {
            getDialog("Some thing went wrong");
        }
    }

    public void getDialogSucess(String message) {
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


    void modeCondition() {
        if (Preferences.getInstance(getApplicationContext()).getMode().equals("Paytm")) {
//            modeImage.setImageDrawable(getResources().getDrawable(R.drawable.paytm_48px));
        } else {
//            modeImage.setImageDrawable(getResources().getDrawable(R.drawable.cash_depostd));
        }
//        mode.setText(Preferences.getInstance(getApplicationContext()).getMode());

    }


    @Subscribe
    public void promo(PromoResponse response) {
        if (response.isSuccess()) {
            if (response.getPromo_status() == 2) {
                Toast.makeText(getApplicationContext(), "Minimum cart value does not full fill !", Toast.LENGTH_SHORT).show();
            } else if (response.getPromo_status() == 3) {
                Toast.makeText(getApplicationContext(), "User max limit excedds !", Toast.LENGTH_SHORT).show();

            } else {
                promoAmount.setText("" + String.format("%.2f", (response.getTotal_discount())));
                grandTotal.setText("" + String.format("%.2f", (Double.valueOf(subTotal.getText().toString()) + Double.valueOf(deliveryCharge.getText().toString()) - response.getTotal_discount())));
                apply_tv.setText("  Applied  ");
                promoCode.setText(promoCodeStr);
                close.setVisibility(View.VISIBLE);

            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (appUser.getCartModels() != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.cancel, menu);
        }
        return true;
    }


    public void dialogPlusClear() {
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setHeader(R.layout.clear_header2)
                .setContentHolder(new GridHolder(2))
//                .setCancelable(false)
                .setGravity(Gravity.CENTER)
//                .setAdapter(new CancelDialogAdapter(this, 2))
                .setMargin(15, 100, 15, 20)
                .setPadding(0, 0, 0, 50)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

                    }
                }).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        Log.e(" inside", " dialog is dismiss issssssssssssssss");
                        view.findViewById(R.id.footer_close_button);
                        if (view.getId() == R.id.yes) {
                            findViewById(R.id.icon_id).setVisibility(View.GONE);
                            appUser = LocalRepositories.getAppUser(getApplicationContext());
                            appUser.setCartModels(null);
                            appUser.setMomMobile("");
                            appUser.setAmount(0.0);
                            LocalRepositories.saveAppUser(getApplicationContext(), appUser);
                            onResume();
                        } else {

                        }
                        dialog.dismiss();
                    }
                })
                .setFooter(R.layout.cancel_footer2)
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
    }


    @Subscribe
    public void clearPromo(EventResetPromo eventResetPromo) {
        promoCode.setText("");
        promoAmount.setText("0.0");
        close.setVisibility(View.GONE);
    }


    @Subscribe
    public void getStatus(GetStatusResponse response) {
        Log.d("responseeee", response.toString());
        if (response.getResponse().getStatusInt() == 1) {
            CartMainModel cartMainModel = new CartMainModel();
            cartMainModel.setMom_mobile(Preferences.getInstance(getApplicationContext()).getMomMobile());
            cartMainModel.setProduct_list(appUser.getCartModels());
            cartMainModel.setLocation(customerAddress.getText().toString());
            cartMainModel.setLatitude(Preferences.getInstance(getApplicationContext()).getAddressLatitude());
            cartMainModel.setLongitude(Preferences.getInstance(getApplicationContext()).getAddressLongitude());
            cartMainModel.setTotal_price(grandTotal.getText().toString());
            cartMainModel.setNote(note.getText().toString());
            cartMainModel.setName(Preferences.getInstance(getApplicationContext()).getName());
            cartMainModel.setMobile(Preferences.getInstance(getApplicationContext()).getMobile());
            cartMainModel.setPaymentMode(Preferences.getInstance(getApplicationContext()).getMode());
            cartMainModel.setPromoCode(promoCode.getText().toString());
            cartMainModel.setSub_total(subTotal.getText().toString());
            cartMainModel.setTax_amount(gstTextView.getText().toString());
            cartMainModel.setDelivery_charge(deliveryCharge.getText().toString());
            cartMainModel.setDiscount_amount(promoAmount.getText().toString());
            if (promoCodeId == null) {
                cartMainModel.setPromoCodeId("");
            } else {
                cartMainModel.setPromoCodeId(promoCodeId);
            }
            if (count == 1) {
                ApiCallService.action(CartActivity.this, cartMainModel, ApiCallService.Action.ACTION_SUBMIT_ORDER);
            } else if (count == 3) {
                ApiCallService.action(CartActivity.this, cartMainModel, ApiCallService.Action.ACTION_SUBMIT_ONOINE_ORDER);
            }
        } else {
            getDialog("Sorry for inconvenience", "Sorry this MOM is not serving any order now. Please select the another MOM the proceed.");
        }

    }




    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public void returnCheckSum(String mobile, String orderId, String amount) {
        String url = "https://mom-apicalls.appspot.com/api/genrate/checksum/app/";
        String varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", mobile);
            jsonObject.put("orderId", orderId);
            jsonObject.put("TXN_AMOUNT", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceHelper.getInstance().PostCall(getApplicationContext(), url, jsonObject, new ResponseCallBack() {
            @Override
            public void OnResponse(JSONObject Response) {

                if (Response != null) {
                    PaytmPGService Service = PaytmPGService.getProductionService();
                    HashMap<String, String> paramMap = new HashMap<String, String>();

                    // when app is ready to publish use production service
                    // PaytmPGService  Service = PaytmPGService.getProductionService();
                    // now call paytm service here
                    //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
                    try {
                        JSONObject params = Response.getJSONObject("params");

                        String WEBSITE = params.getString("WEBSITE");
                        String CHANNEL_ID = params.getString("CHANNEL_ID");
                        String INDUSTRY_TYPE_ID = params.getString("INDUSTRY_TYPE_ID");
                        String CUST_ID = params.getString("CUST_ID");
                        String ORDER_ID = params.getString("ORDER_ID");
                        String CALLBACK_URL = params.getString("CALLBACK_URL");
                        String CHECKSUMHASH = params.getString("CHECKSUMHASH");
                        String MID = params.getString("MID");
                        String TXN_AMOUNT = params.getString("TXN_AMOUNT");

                        //these are mandatory parameters
                        paramMap.put("MID", MID); //MID provided by paytm
                        paramMap.put("ORDER_ID", ORDER_ID);
                        paramMap.put("CUST_ID", CUST_ID);
                        paramMap.put("CHANNEL_ID", CHANNEL_ID);
                        paramMap.put("TXN_AMOUNT", TXN_AMOUNT);
                        paramMap.put("WEBSITE", WEBSITE);
                        paramMap.put("CALLBACK_URL", CALLBACK_URL);
                        paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
                        paramMap.put("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID);

                        PaytmOrder Order = new PaytmOrder(paramMap);
                        Log.e("checksum ", "parammmmmm " + paramMap.toString());
                        Service.initialize(Order, null);
                        // start payment service call here
                        Service.startPaymentTransaction(CartActivity.this, true, true, CartActivity.this);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void OnError(JSONObject Response) {

            }
        });

    }


    @Override
    public void onTransactionResponse(Bundle inResponse) {
        Toast.makeText(context, "hi hello", Toast.LENGTH_SHORT).show();
        Log.d("LOG", "Payment Transaction is successful " + inResponse);
//        Toast.makeText(getApplicationContext(), "Payment_Transaction_response " + inResponse.toString(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void networkNotAvailable() {
        // If network is not
        // available, then this
        // method gets called.

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        // This method gets called if client authentication
        // failed. // Failure may be due to following reasons //
        // 1. Server error or downtime. // 2. Server unable to
        // generate checksum or checksum response is not in
        // proper format. // 3. Server failed to authenticate
        // that client. That is value of payt_STATUS is 2. //
        // Error Message describes the reason for failure.

    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {

        // Some UI Error Occurred in Payment Gateway Activity.
        // // This may be due to initialization of views in
        // Payment Gateway Activity or may be due to //
        // initialization of webview. // Error Message details
        // the error occurred.
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        Log.d("LOG", "Payment Transaction is successful " + inErrorMessage);
        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(CartActivity.this, "Back pressed. Transaction cancelled", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();

    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        returnCheckSum(custid, orderId, grandTotal.getText().toString());
        BackgroundService.action(this, orderId);
    }*/
}



