package mom.com.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.tapadoo.alerter.Alerter;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import cdflynn.android.library.checkview.CheckView;
import mom.com.R;
import mom.com.WebService.ResponseCallBack;
import mom.com.WebService.WebServiceHelper;
import mom.com.activities.AddressListActivity;
import mom.com.activities.BaseFragment;
import mom.com.activities.CartActivity;
import mom.com.activities.LoginActivity;
import mom.com.activities.MainActivity;
import mom.com.activities.OfferListActivity;
import mom.com.adapter.CartFragmentAdapter;
import mom.com.activities.address.AddAddressActivity;
import mom.com.event.EventResetPromo;
import mom.com.helper.FusedLocation;
import mom.com.model.CartMainModel;
import mom.com.network.ApiCallService;
import mom.com.network.ThisApp;
import mom.com.network.request.EventPushRequest;
import mom.com.network.response.DeliveryChargeResponse;
import mom.com.network.response.GetStatusResponse;
import mom.com.network.response.OnlineOrderBookingResponse;
import mom.com.network.response.OrderBookingResponse;
import mom.com.network.response.PromoResponse;
import mom.com.network.response.PushNotificationResponse;
import mom.com.service.BackgroundService;
import mom.com.utils.AppUser;
import mom.com.utils.LocalRepositories;
import mom.com.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class CartFragment extends BaseFragment implements PaytmPaymentTransactionCallback {

    CardView applyPromo;
    RecyclerView recyclerView;
    public static CartFragment context;
    TextView amount;
    TextView gstTextView;
    TextView grandTotal;
    TextView deliveryCharge ;
    TextView subTotal;
    TextView textButton;
    TextView customerAddress;
    CardView change;
    View viewMain;
    LinearLayout submit;
    LinearLayout customerAddressLayout;

    LinearLayout cash;
    LinearLayout card;
    ImageView cashCircle;
    ImageView cardCircle;
    TextView cashTxt;
    TextView cardTxt;
    TextView promoAmount;
    TextView promoCode;
    LinearLayout noRecord;
    LinearLayout mainLayout;
    EditText note;
    LinearLayout close;

    CheckView check;
    LinearLayout orderPlaced;


    String latitude="";
    String longitude="";

    AppUser appUser;
    String promoCodeId;
    TextView apply_tv ;
    String promoCodeStr ;

    LinearLayout online;
    ImageView onlineCircle;
    TextView onlineTxt;

    private static int count = 1;


    String custid, orderId, mid = "rchmXY53623731345120";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart3,container , false);
        context=this;
        mainLayout=view.findViewById(R.id.mainLayout);
        recyclerView=view.findViewById(R.id.recyclerView);
        close = view.findViewById(R.id.close);
        applyPromo=view.findViewById(R.id.applyPromo);
        amount=view.findViewById(R.id.amount);
        gstTextView=view.findViewById(R.id.gstTextView);
        grandTotal=view.findViewById(R.id.grandTotal);
        subTotal=view.findViewById(R.id.subTotal);
        textButton=view.findViewById(R.id.textButton);
        customerAddress=view.findViewById(R.id.customerAddress);
        change=view.findViewById(R.id.change);
        viewMain=view.findViewById(R.id.view);
        customerAddressLayout=view.findViewById(R.id.customerAddressLayout);
        submit=view.findViewById(R.id.submit);
        cash=view.findViewById(R.id.cash);
        card=view.findViewById(R.id.card);
        cashCircle=view.findViewById(R.id.cashCircle);
        cardCircle=view.findViewById(R.id.cardCircle);
        cashTxt=view.findViewById(R.id.cashTxt);
        cardTxt=view.findViewById(R.id.cardTxt);
        promoCode=view.findViewById(R.id.promoCode);
        promoAmount=view.findViewById(R.id.promoAmount);
        noRecord=view.findViewById(R.id.noRecord);
        note=view.findViewById(R.id.note);
        orderPlaced=view.findViewById(R.id.orderPlaced);
        check = (CheckView) view.findViewById(R.id.check);
        orderPlaced.setVisibility(View.GONE);
        apply_tv = view.findViewById(R.id.apply_tv);
        onlineTxt =view. findViewById(R.id.onlineTxt);
        online = view.findViewById(R.id.online);
        onlineCircle =view. findViewById(R.id.onlineCircle);


        deliveryCharge = view.findViewById(R.id.deliveryCharge);

        Preferences.getInstance(getActivity()).setMode("Online");
        count = 3;
        onlineTxt.setTypeface(Typeface.DEFAULT_BOLD);
        cashTxt.setTypeface(Typeface.DEFAULT);
        onlineCircle.setImageDrawable(getResources().getDrawable(R.drawable.circle_check));
        cashCircle.setImageDrawable(getResources().getDrawable(R.drawable.circles));


        close.setVisibility(View.GONE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close.setVisibility(View.GONE);
                promoCode.setText("");
                promoAmount.setText("0.0");
//                deliveryCharge.setText("0.0");
//                grandTotal.setText(subTotal.getText().toString());
                apply_tv.setText("Apply");
                grandTotal.setText("" + String.format("%.2f", (Double.valueOf(subTotal.getText().toString())+Double.valueOf(deliveryCharge.getText().toString()))));

            }
        });


        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.getInstance(getActivity()).setMode("Online");
                count = 3;
                onlineTxt.setTypeface(Typeface.DEFAULT_BOLD);
                cashTxt.setTypeface(Typeface.DEFAULT);
                onlineCircle.setImageDrawable(getResources().getDrawable(R.drawable.circle_check));
                cashCircle.setImageDrawable(getResources().getDrawable(R.drawable.circles));

                /* Intent pamentIntent=new Intent(getActivity()(), PaytmDemoActivity.class);
                startActivity(pamentIntent);*/

            }
        });

        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.getInstance(getActivity()).setMode("Cash");
                count = 1;
                cashTxt.setTypeface(Typeface.DEFAULT_BOLD);
                onlineTxt.setTypeface(Typeface.DEFAULT);
                cashCircle.setImageDrawable(getResources().getDrawable(R.drawable.circle_check));
                cardCircle.setImageDrawable(getResources().getDrawable(R.drawable.circles));
                onlineCircle.setImageDrawable(getResources().getDrawable(R.drawable.circles));
            }
        });

        /*card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashTxt.setTypeface(Typeface.DEFAULT);
                cardTxt.setTypeface(Typeface.DEFAULT_BOLD);
                cardCircle.setImageDrawable(getResources().getDrawable(R.drawable.circle_check));
                cashCircle.setImageDrawable(getResources().getDrawable(R.drawable.circles));
            }
        });*/


       submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Preferences.getInstance(getActivity()).isLogin()){
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("cart","cart");
                    startActivity(intent);
                }else if (!textButton.getText().toString().equals("Place your order")){
                    Intent intent=new Intent(getActivity(), AddAddressActivity.class);
                    intent.putExtra("for",true);
                    startActivityForResult(intent, 1);
                }else {
                    if(!latitude.equalsIgnoreCase("") && !longitude.equalsIgnoreCase("")) {

                        double distance = distance(Double.valueOf(Preferences.getInstance(getActivity()).getMom_latitude()), Double.valueOf(Preferences.getInstance(getActivity()).getMom_longitude()),
                                Double.valueOf(latitude), Double.valueOf(longitude));
                        if (distance > 6.0) {
                             deliveryCharge.setText("NA");
                            getDialog("This MOM is not serving in this location");
                            return;
                        }
                    }

                    appUser = LocalRepositories.getAppUser(getActivity());
                    Map map=new HashMap();
                    map.put("mobile",LocalRepositories.getAppUser(getActivity()).getMomMobile());
                    ApiCallService.action(getActivity(),map,ApiCallService.Action.ACTION_GET_STATUS);
                    /*appUser=LocalRepositories.getAppUser(getActivity());
                    CartMainModel cartMainModel = new CartMainModel();
                    cartMainModel.setMom_mobile(Preferences.getInstance(getActivity()).getMomMobile());
                    cartMainModel.setProduct_list(appUser.getCartModels());
                    cartMainModel.setLocation(customerAddress.getText().toString());
                    cartMainModel.setLatitude(Preferences.getInstance(getContext()).getAddressLatitude());
                    cartMainModel.setLongitude(Preferences.getInstance(getContext()).getAddressLongitude());
                    cartMainModel.setTotal_price(grandTotal.getText().toString());
                    cartMainModel.setNote(note.getText().toString());
                    cartMainModel.setName(Preferences.getInstance(getActivity()).getName());
                    cartMainModel.setMobile(Preferences.getInstance(getActivity()).getMobile());
                    cartMainModel.setPaymentMode(Preferences.getInstance(getActivity()).getMode());
                    ApiCallService.action(getActivity(), cartMainModel, ApiCallService.Action.ACTION_SUBMIT_ORDER);*/
                }
            }
        });



        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.getInstance(getActivity()).isLogin()) {
                    startActivityForResult(new Intent(getActivity(), AddressListActivity.class), 1);
                }else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });


        customerAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.getInstance(getActivity()).isLogin()) {
                    startActivityForResult(new Intent(getActivity(), AddressListActivity.class), 1);
                }else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });


        applyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), OfferListActivity.class),100);
            }
        });





        if (Preferences.getInstance(getActivity()).isLogin()){
            if (!Preferences.getInstance(getActivity()).getAddress().equals("")){
                latitude=Preferences.getInstance(getActivity()).getAddressLatitude();
                longitude=Preferences.getInstance(getActivity()).getAddressLongitude();
                textButton.setText("Place your order");
                viewMain.setVisibility(View.GONE);
                customerAddress.setText(Preferences.getInstance(getActivity()).getAddress());
//                change.setText("CHANGE");
//                change.setTextColor(getResources().getColor(R.color.black));
            }else {
                latitude=""+FusedLocation.location.getLatitude();
                longitude=""+FusedLocation.location.getLongitude();
                textButton.setText("Add Address");
                customerAddress.setText("Select Address");
                viewMain.setVisibility(View.VISIBLE);
            }
        }else {
            textButton.setText("Login");
        }

        appUser= LocalRepositories.getAppUser(getActivity());
        setAmount(appUser.getAmount());
        setAdapter();
        return  view;

    }


    void setAdapter(){
//        appUser=LocalRepositories.getAppUser(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        if (appUser.getCartModels()!=null){
            noRecord.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            CartFragmentAdapter adapter = new CartFragmentAdapter(appUser.getCartModels(),this, getActivity());
            recyclerView.setAdapter(adapter);
        }else {
            noRecord.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        }

    }


    double delivery = 0;
    public void setAmount(double amt){
        amount.setText(""+amt);
        double gst= (amt*5)/100;
        if(!latitude.equalsIgnoreCase("") && !longitude.equalsIgnoreCase("")) {

            double distance = distance(Double.valueOf(Preferences.getInstance(getActivity()).getMom_latitude()), Double.valueOf(Preferences.getInstance(getActivity()).getMom_longitude()),
                    Double.valueOf(latitude), Double.valueOf(longitude));
            if (distance > 6.0) {
                delivery = 0;
                gstTextView.setText("" + String.format("%.2f", gst));
                grandTotal.setText("" + String.format("%.2f", (amt + gst + delivery - Double.valueOf(promoAmount.getText().toString()))));
                subTotal.setText("" + String.format("%.2f", (amt + gst)));
                deliveryCharge.setText("NA");
            }else {

                delivery = 10;
                Log.d("LatLongSelectedAddresIn", latitude + "..." + longitude);
                if (!latitude.equalsIgnoreCase("") && !longitude.equalsIgnoreCase("")) {
                    distance = distance(Double.valueOf(Preferences.getInstance(getActivity()).getMom_latitude()), Double.valueOf(Preferences.getInstance(getActivity()).getMom_longitude()),
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
                map.put("mom_lat",Preferences.getInstance(getActivity()).getMom_latitude());
                map.put("mom_lang",Preferences.getInstance(getActivity()).getMom_longitude());
                map.put("distance",distance);

                ThisApp.getApi(getActivity()).getDeliveryCharge(map).enqueue(new Callback<DeliveryChargeResponse>() {
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
                        getDialog(t.getMessage());
                    }
                });

            }
        }

    }
    public Double getAmount(){
        return Double.valueOf(amount.getText().toString());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==1){
                latitude=data.getStringExtra("latitude");
                longitude=data.getStringExtra("longitude");

                    Preferences.getInstance(getActivity()).setAddress(data.getStringExtra("name")+"\n"+ data.getStringExtra("address")+"\n"+ Preferences.getInstance(getActivity()).getMobile());
                    customerAddress.setText(data.getStringExtra("name")+"\n"+ data.getStringExtra("address")+"\n"+ Preferences.getInstance(getActivity()).getMobile());
                    viewMain.setVisibility(View.GONE);
                    calCulateDeliveryCharge();
                    appUser = LocalRepositories.getAppUser(getActivity());
//        setAmount(MomItemDetailActivity.context.getAmount());
                    setAmount(appUser.getAmount());
                    textButton.setText("Place your order");

            }else if (requestCode==100){
                    Map map = new Hashtable();
                    promoCodeStr = data.getStringExtra("promo");
                    promoCodeId = data.getStringExtra("id") + "_" + data.getStringExtra("promo");
                    map.put("total_price", subTotal.getText().toString());
                    map.put("promocode", promoCodeStr);
                    map.put("mobile",Preferences.getInstance(getActivity()).getMobile());
                    ApiCallService.action(getActivity(), map, ApiCallService.Action.ACTION_PROMO_CODE);
                }

        }
    }



    @Subscribe
    public void add_order(OrderBookingResponse response){
        if (response.isSuccess()) {
            orderPlaced.setVisibility(View.VISIBLE);
            check.check();

            EventPushRequest request=new EventPushRequest();
            request.setTo(Preferences.getInstance(getContext()).getMomMobile());
            request.setMessage("You have received one new order");
//            ApiCallService.action2(getActivity(),request,ApiCallService.Action.ACTION_PUSH_NOTIFICATION);


            ThisApp.getApi(getContext()).pushNotification(request).enqueue(new Callback<PushNotificationResponse>() {
                @Override
                public void onResponse(Call<PushNotificationResponse> call, Response<PushNotificationResponse> response) {
                    context.onResume();
                }

                @Override
                public void onFailure(Call<PushNotificationResponse> call, Throwable t) {

                }
            });
            Preferences.getInstance(getActivity()).setMomMobile("");
            AppUser appUser=LocalRepositories.getAppUser(getActivity());
            appUser.setCartModels(null);
            appUser.setMomMobile("");
            appUser.setAmount(0.0);
            LocalRepositories.saveAppUser(getActivity(),appUser);
            Alerter.create(getActivity())
                        .setTitle("")
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setText("Your order placed")
                        .setIcon(R.drawable.progress_launcher)
                        .setIconColorFilter(0)
                        .show();


            appUser=LocalRepositories.getAppUser(getActivity());
            appUser.setCartModels(null);
            appUser.setMomMobile("");
            appUser.setAmount(0.0);
            LocalRepositories.saveAppUser(getActivity(),appUser);



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    context.onResume();
                }
            },5000);
//
            MainActivity.context.CartIcon();

        }else {
            getDialog("Some thing went wrong");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        appUser= LocalRepositories.getAppUser(getActivity());
//        setAmount(MomItemDetailActivity.context.getAmount());
        setAmount(appUser.getAmount());
        setAdapter();

        if (Preferences.getInstance(getActivity()).isLogin()){
            if (!Preferences.getInstance(getActivity()).getAddress().equals("")){
                latitude=Preferences.getInstance(getActivity()).getAddressLatitude();
                longitude=Preferences.getInstance(getActivity()).getAddressLongitude();
                textButton.setText("Place your order");
                viewMain.setVisibility(View.GONE);
                customerAddress.setText(Preferences.getInstance(getActivity()).getAddress());
            }else {
                latitude=""+FusedLocation.location.getLatitude();
                longitude=""+FusedLocation.location.getLongitude();
                textButton.setText("Add Address");
                customerAddress.setText("Select Address");
                viewMain.setVisibility(View.VISIBLE);
            }
        }else {
            textButton.setText("Login");
        }
    }


    @Subscribe
    public void promo(PromoResponse response){
        if (response.isSuccess()){

            if(response.getPromo_status() == 2){
                Toast.makeText(getActivity(), "Minimum cart value does not full fill !", Toast.LENGTH_SHORT).show();
            }else if (response.getPromo_status() == 3){
                Toast.makeText(getActivity(), "User max limit excedds !", Toast.LENGTH_SHORT).show();

            }else {
                promoAmount.setText("" + String.format("%.2f", (response.getTotal_discount())));
                grandTotal.setText("" + String.format("%.2f", (Double.valueOf(subTotal.getText().toString()) + Double.valueOf(deliveryCharge.getText().toString()) - response.getTotal_discount())));
                apply_tv.setText("Applied");
                promoCode.setText(promoCodeStr);
                close.setVisibility(View.VISIBLE);

            }
        }
    }


    @Subscribe
    public void clearPromo(EventResetPromo eventResetPromo){
        promoCode.setText("");
        promoAmount.setText("0.0");
        close.setVisibility(View.GONE);
    }


    @Subscribe
    public void getStatus(GetStatusResponse response) {
        if (response.getResponse().getStatusInt() == 1) {
            CartMainModel cartMainModel = new CartMainModel();
            cartMainModel.setMom_mobile(Preferences.getInstance(getActivity()).getMomMobile());
            cartMainModel.setProduct_list(appUser.getCartModels());
            cartMainModel.setLocation(customerAddress.getText().toString());
            cartMainModel.setLatitude(Preferences.getInstance(getActivity()).getAddressLatitude());
            cartMainModel.setLongitude(Preferences.getInstance(getActivity()).getAddressLongitude());
            cartMainModel.setTotal_price(grandTotal.getText().toString());
            cartMainModel.setNote(note.getText().toString());
            cartMainModel.setName(Preferences.getInstance(getActivity()).getName());
            cartMainModel.setMobile(Preferences.getInstance(getActivity()).getMobile());
            cartMainModel.setPaymentMode(Preferences.getInstance(getActivity()).getMode());
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
                ApiCallService.action(getActivity(), cartMainModel, ApiCallService.Action.ACTION_SUBMIT_ORDER);
            } else if (count == 3) {
                ApiCallService.action(getActivity(), cartMainModel, ApiCallService.Action.ACTION_SUBMIT_ONOINE_ORDER);
            }
        } else {
            getDialog("Sorry for inconvenience","This MOMCHEF is not receiving orders currently. Please select another MOMCHEF to complete the order!");
        }

    }

    public  int calCulateDeliveryCharge() {
        int deliveryPrice = 10;

        if(!latitude.equalsIgnoreCase("") && !longitude.equalsIgnoreCase("")) {

            double distance = distance(Double.valueOf(Preferences.getInstance(getActivity()).getMom_latitude()), Double.valueOf(Preferences.getInstance(getActivity()).getMom_longitude()),
                    Double.valueOf(latitude), Double.valueOf(longitude));

            if ((distance >= 0.0) && (distance <= 1.0)) {
                deliveryPrice += 5;
            } else if ((distance > 1.0) && (distance <= 3.0)) {
                deliveryPrice += 12;

            } else if ((distance > 3.0 && (distance <= 6.0)) ) {
                deliveryPrice += 22;
            } else {
                deliveryPrice=0 ;
            }
        }
        return deliveryPrice ;
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



    public void getDialog( String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Sorry")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
//                .setNegativeButton("Cancel", null)
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }




    @Subscribe
    public void add_online_order(OnlineOrderBookingResponse response) {
        if (response.isSuccess()) {
            custid = Preferences.getInstance(getActivity()).getMobile();
            orderId = response.getOrderId();
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
           /* if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                CartFragment.this.requestPermissions( new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                return;
            }*/
            returnCheckSum(custid, orderId, grandTotal.getText().toString());
            BackgroundService.action(getActivity(), response.getOrderId());
        } else {
            getDialog("Some thing went wrong");
        }
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
        WebServiceHelper.getInstance().PostCall(getActivity(), url, jsonObject, new ResponseCallBack() {
            @Override
            public void OnResponse(JSONObject Response) {

                if (Response != null) {
                    PaytmPGService Service = PaytmPGService.getStagingService();
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
                        Service.startPaymentTransaction(getActivity(), true, true, CartFragment.this);


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
        Toast.makeText(getActivity(), "hi hello", Toast.LENGTH_SHORT).show();
        Log.d("LOG", "Payment Transaction is successful " + inResponse);
//        Toast.makeText(getActivity()(), "Payment_Transaction_response " + inResponse.toString(), Toast.LENGTH_LONG).show();

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
        Toast.makeText(getActivity(), "Payment Transaction response " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(getActivity(), "Back pressed. Transaction cancelled", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
        Toast.makeText(getActivity(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();

    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        returnCheckSum(custid, orderId, grandTotal.getText().toString());
        BackgroundService.action(getActivity(), orderId);
    }*/



}



