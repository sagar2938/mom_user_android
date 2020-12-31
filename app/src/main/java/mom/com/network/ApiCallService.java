package mom.com.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import mom.com.R;
import mom.com.model.CartMainModel;
import mom.com.network.request.EventPushRequest;
import mom.com.network.response.AddResponse;
import mom.com.network.response.AddressResponse;
import mom.com.network.response.GetFavouriteResponse;
import mom.com.network.response.GetOfferResponse;
import mom.com.network.response.GetStatusResponse;
import mom.com.network.response.LoginSignUpMainResponse;
import mom.com.network.response.LoginSignUpResponse;
import mom.com.network.response.MomListResponse;
import mom.com.network.response.MomListResponse2;
import mom.com.network.response.MomsItemListResponse;
import mom.com.network.response.OnlineOrderBookingResponse;
import mom.com.network.response.OrderBookingResponse;
import mom.com.network.response.PromoResponse;
import mom.com.network.response.PushNotificationResponse;
import mom.com.network.response.ResendOtpResponse;
import mom.com.network.response.RunningOrderResponse;
import mom.com.network.response.SucessResponse;
import mom.com.network.response.TokenResponse;
import mom.com.network.response.TrackOnGoingResponse;
import mom.com.network.response.order.GetBookedResponse;
import mom.com.network.response.order.GetNewOrderResponse;
import mom.com.network.response.order.GetNewOrderResponse2;
import mom.com.utils.CustomProgressDialog;
import mom.com.utils.Helper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCallService extends IntentService {

    static Object request;
    static Activity context;
    static String action;


    public ApiCallService() {
        super("ApiCallService");
    }


    public static void action(Activity ctx, String action) {
        ApiCallService.action=action;
        Intent intent = new Intent(ctx, ApiCallService.class);
        intent.setAction(action);
        ctx.startService(intent);
        context = ctx;
    }


    public static void action(Activity ctx, Object request, String action) {
        context = ctx;
        ApiCallService.action=action;
        if (!Helper.isNetworkAvailable(context)){
            getDialog(context,"No Internet","Please check your internet connection!!!",request,action);
            return;
        }
        CustomProgressDialog customProgressDialog=CustomProgressDialog.getInstance(ctx);
        if (!customProgressDialog.isShowing()){
            customProgressDialog.show();
        }
        ApiCallService.request = request;
        Intent intent = new Intent(ctx, ApiCallService.class);
        intent.setAction(action);
        ctx.startService(intent);
    }


    public static void action(Context ctx, Object request, String action) {
        ApiCallService.action=action;
        ApiCallService.request = request;
        Intent intent = new Intent(ctx, ApiCallService.class);
        intent.setAction(action);
        ctx.startService(intent);
    }

    class Local<T> implements Callback<T> {

        public void onResponse(Call<T> call, Response<T> response) {
            CustomProgressDialog.setDismiss();
            if (response.code() == 200) {
                T body = response.body();
                EventBus.getDefault().post(body);
            } else {
//                getDialog("Some thing went wrong!!! " + response.code());
                EventBus.getDefault().post("Some thing went wrong!!! " /*+ response.code()*/);
            }

        }

        public void onFailure(Call<T> call, Throwable t) {
            CustomProgressDialog.setDismiss();
            if (t.getMessage().contains("Unable to resolve")){
                getDialog(context,"No Internet","Please check your internet connection!!!",request,action);
//                EventBus.getDefault().post(t.getMessage());
            }else {
                EventBus.getDefault().post(t.getMessage());

            }
        }
    }



    public static void action2(Activity ctx, Object request, String action) {
        context = ctx;
        ApiCallService.action=action;
        ApiCallService.request = request;
        Intent intent = new Intent(ctx, ApiCallService.class);
        intent.setAction(action);
        ctx.startService(intent);
    }


    public  class Local2<T> implements Callback<T> {

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            CustomProgressDialog.setDismiss();
            if (response.code() == 200) {
                T body = response.body();
                EventBus.getDefault().post(body);
            } else {
                EventBus.getDefault().post(ApiCallService.Action.ERROR + " " + response.code());
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            CustomProgressDialog.setDismiss();
            if (t.getMessage().contains("Unable to resolve")){
                getDialog(context,"No Internet","Please check your internet connection!!!",request,action);
//                EventBus.getDefault().post(t.getMessage());
            }else {
                EventBus.getDefault().post(t.getMessage());

            }
        }
    }


    public interface Action{

        String PUSH_SUCCESS_MESSAGE ="New Order Booked";
        String ACTION_SIGN_IN="ACTION_SIGN_IN";
        String ACTION_SIGN_UP="ACTION_SIGN_UP";
        String ACTION_RESEND_OTP ="ACTION_RESEND_OTP";
        String ERROR = "Some thing went wrong";
        String DOCUMENT = "documents";
        String ACTION_SEND_OTP = "ACTION_SEND_OTP";
        String ACTION_SEND_TOKEN = "ACTION_SEND_TOKEN";
        String ACTION_MOM_LIST = "ACTION_MOM_LIST";
        String ACTION_GET_MENU = "ACTION_GET_MENU";
        String ACTION_SEARCH = "ACTION_SEARCH";
        String ACTION_SUBMIT_ORDER = "ACTION_SUBMIT_ORDER";
        String ACTION_SUBMIT_ONOINE_ORDER = "ACTION_SUBMIT_ONOINE_ORDER";
        String ACTION_ADD_ADDRESS = "ACTION_ADD_ADDRESS";
        String ACTION_UPDATE_ADDRESS = "ACTION_UPDATE_ADDRESS";
        String ACTION_RUNNING_ORDER = "ACTION_RUNNING_ORDER";
        String ACTION_GET_ADDRESS_LIST = "ACTION_GET_ADDRESS_LIST";
        String ACTION_UPDATE_RESPONSE = "ACTION_UPDATE_RESPONSE";
        String ACTION_PROMO_CODE = "ACTION_PROMO_CODE";
        String ACTION_LIVE_LOCATION = "ACTION_LIVE_LOCATION";
        String ACTION_ONGOING_ORDER = "ACTION_ONGOING_ORDER";
        String ACTION_ORDER_HISTORY = "ACTION_ORDER_HISTORY";
        String ACTION_TRACKING = "ACTION_TRACKING";
        String ACTION_RATING = "ACTION_RATING";
        String ACTION_GET_OFFER_LIST = "ACTION_GET_OFFER_LIST";
        String ACTION_GET_FAVOURITE = "ACTION_GET_FAVOURITE";
        String ACTION_SAVE_TOKEN = "ACTION_SAVE_TOKEN";
        String ACTION_BOOKED_ORDER = "ACTION_BOOKED_ORDER";
        String ACTION_PUSH_NOTIFICATION = "ACTION_PUSH_NOTIFICATION";
        String ACTION_GET_STATUS = "ACTION_GET_STATUS";
        String ACTION_SEARCH_WITH_KEY = "ACTION_SEARCH_WITH_KEY";
    }

    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Api api = ThisApp.getApi(this.getApplicationContext());
        if (action.equals(Action.ACTION_SIGN_IN)) {
            api.login((Map) request).enqueue(new Local<LoginSignUpMainResponse>());
        }else if (action.equals(Action.ACTION_SIGN_UP)) {
            api.signUp((Map) request).enqueue(new Local<LoginSignUpMainResponse>());
        }else if (action.equals(Action.ACTION_SEND_OTP)) {
            api.sendOtp((Map) request).enqueue(new Local<SucessResponse>());
        }else if (action.equals(Action.ACTION_SEND_TOKEN)) {
            api.sendToken((Map) request).enqueue(new Local<TokenResponse>());
        }else if (action.equals(Action.ACTION_MOM_LIST)) {
            api.getMomList(((Map) request).get("pageNo").toString(),(Map) request).enqueue(new Local<MomListResponse>());
        }else if (action.equals(Action.ACTION_SEARCH)) {
            api.getMomList2((Map) request).enqueue(new Local<MomListResponse2>());
        }else if (action.equals(Action.ACTION_SEARCH_WITH_KEY)) {
            api.getMomSearch((Map) request).enqueue(new Local<MomListResponse2>());
        }else if (action.equals(Action.ACTION_GET_MENU)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com");
            api.getMenu((Map) request).enqueue(new Local<MomsItemListResponse>());
        }else if (action.equals(Action.ACTION_SUBMIT_ORDER)) {
            api=ThisApp.getApi(this);
            api.submitOrder((CartMainModel) request).enqueue(new Local<OrderBookingResponse>());
        }else if (action.equals(Action.ACTION_SUBMIT_ONOINE_ORDER)) {
            api=ThisApp.getApi(this);
            api.submitOnlineOrder((CartMainModel) request).enqueue(new Local<OnlineOrderBookingResponse>());
        }else if (action.equals(Action.ACTION_ADD_ADDRESS)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.addAddress((Map) request).enqueue(new Local<SucessResponse>());
        }else if (action.equals(Action.ACTION_UPDATE_ADDRESS)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.updateAddress((Map) request).enqueue(new Local<SucessResponse>());
        }else if (action.equals(Action.ACTION_RUNNING_ORDER)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.getRunningOrder((Map) request).enqueue(new Local2<RunningOrderResponse>());
        }else if (action.equals(Action.ACTION_GET_ADDRESS_LIST)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.getAddressList((Map) request).enqueue(new Local<AddressResponse>());
        }else if (action.equals(Action.ACTION_UPDATE_RESPONSE)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.updateResponse((Map) request).enqueue(new Local<SucessResponse>());
        }else if (action.equals(Action.ACTION_PROMO_CODE)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.applyPromo((Map) request).enqueue(new Local<PromoResponse>());
        }else if (action.equals(Action.ACTION_LIVE_LOCATION)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.liveLocation((Map) request).enqueue(new Local2<SucessResponse>());
        }else if (action.equals(Action.ACTION_ONGOING_ORDER)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.getOnGoingOrder((Map) request).enqueue(new Local2<GetNewOrderResponse>());
        }else if (action.equals(Action.ACTION_BOOKED_ORDER)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.bookedOrder((Map) request).enqueue(new Local2<GetBookedResponse>());
        }else if (action.equals(Action.ACTION_ORDER_HISTORY)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.getHistory((Map) request).enqueue(new Local2<GetNewOrderResponse2>());
        }else if (action.equals(Action.ACTION_TRACKING)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.tracking((Map) request).enqueue(new Local2<TrackOnGoingResponse>());
        }else if (action.equals(Action.ACTION_RATING)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.rating((Map) request).enqueue(new Local<AddResponse>());
        }else if (action.equals(Action.ACTION_GET_OFFER_LIST)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.getOfferList((Map) request).enqueue(new Local<GetOfferResponse>());
        }else if (action.equals(Action.ACTION_GET_FAVOURITE)) {
//            api=ThisApp.getApi(this,"https://mom-apicalls.appspot.com/");
//            api=ThisApp.getApi(this);
            api.getFavourite((Map) request).enqueue(new Local<GetFavouriteResponse>());
        }else if (action.equals(Action.ACTION_RESEND_OTP)) {
            api.resendOtpNew((Map) request).enqueue(new Local<ResendOtpResponse>());
        }else if (action.equals(Action.ACTION_SAVE_TOKEN)) {
            api.saveToken((Map) request).enqueue(new Local<SucessResponse>());
        }else if (action.equals(Action.ACTION_PUSH_NOTIFICATION)) {
            api.pushNotification((EventPushRequest) request).enqueue(new Local2<PushNotificationResponse>());
        }else if (action.equals(Action.ACTION_GET_STATUS)) {
            api.getStatus((Map) request).enqueue(new Local<GetStatusResponse>());
        }
    }





    static void getDialog(final Activity context, String tittle, String message, final Object request, final String action) {
        new AlertDialog.Builder(context)
                .setTitle(tittle)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ApiCallService.action(context,request,action);
                    }
                })
//                .setNegativeButton("Exit", null)
                .setIcon(R.drawable.ic_launcher)
                .show();
    }


    public void getDialog( String message) {
        new AlertDialog.Builder(this)
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

}

