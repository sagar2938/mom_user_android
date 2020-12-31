package mom.com.network;

import java.util.Map;

import mom.com.model.CartMainModel;
import mom.com.network.request.EventPushRequest;
import mom.com.network.response.AddResponse;
import mom.com.network.response.AddressResponse;
import mom.com.network.response.DeliveryChargeResponse;
import mom.com.network.response.GetFavouriteResponse;
import mom.com.network.response.GetOfferResponse;
import mom.com.network.response.GetVersionResponse;
import mom.com.network.response.profile.GetProfileResponse;
import mom.com.network.response.GetStatusResponse;
import mom.com.network.response.LoginSignUpMainResponse;
import mom.com.network.response.MomListResponse;
import mom.com.network.response.MomListResponse2;
import mom.com.network.response.MomsItemListResponse;
import mom.com.network.response.OnlineOrderBookingResponse;
import mom.com.network.response.OrderBookingResponse;
import mom.com.network.response.PaytmPaymentResponse;
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
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {

    @POST("/customer/user/login/")
    Call<LoginSignUpMainResponse> login(@Body Map signUp);

    @POST("/customer/add/user/")
    Call<LoginSignUpMainResponse> signUp(@Body Map signUp);


    @POST("/customer/customer/send/otp/")
    Call<SucessResponse> resendOtp(@Body Map signUp);


    @POST("/customer/customer/check/auth/")
    Call<SucessResponse> sendOtp(@Body Map signUp);

    @POST("/customer/customer/addtoken/")
    Call<TokenResponse> sendToken(@Body Map signUp);

    @POST("/customer/get/vendor/list/paginated/{id}/")
    Call<MomListResponse> getMomList(@Path("id") String id, @Body Map map);

    @POST("/customer/get/vendor/search/list/")
    Call<MomListResponse2> getMomList2(@Body Map signUp);

    @POST("/customer/get/vendor/search/keyword/")
    Call<MomListResponse2> getMomSearch(@Body Map signUp);

    @POST("/api/get/menu/list/customer/")
    Call<MomsItemListResponse> getMenu(@Body Map map);

    @POST("/customer/add/order/list/updated/")
    Call<OrderBookingResponse> submitOrder(@Body CartMainModel cartMainModel);


    @POST("/customer/add/order/list/updated/app/online/")
    Call<OnlineOrderBookingResponse> submitOnlineOrder(@Body CartMainModel cartMainModel);

    @POST("/customer/add/user/address/")
    Call<SucessResponse> addAddress(@Body Map address);

    @POST("/customer/update/user/address/")
    Call<SucessResponse> updateAddress(@Body Map address);

    @POST("/customer/get/running/order/list/")
    Call<RunningOrderResponse> getRunningOrder(@Body Map address);

    @POST("/api/get/complete/order/list/")
    Call<RunningOrderResponse> getCompleteOrder(@Body Map address);

    @POST("/customer/get/address/")
    Call<AddressResponse> getAddressList(@Body Map address);

    @POST("/customer/update/user/profile/")
    Call<SucessResponse> updateResponse(@Body Map address);

    @POST("/customer/apply/promocode/")
    Call<PromoResponse> applyPromo(@Body Map address);

    @POST("/delivery/live/location/")
    Call<SucessResponse> liveLocation(@Body Map address);

    @POST("/customer/get/customer/running/order/")
    Call<GetNewOrderResponse> getOnGoingOrder(@Body Map address);

    @POST("/customer/get/new/order/list/")
    Call<GetBookedResponse> bookedOrder(@Body Map address);

    @POST("/customer/get/complete/order/list/datewise/")
    Call<GetNewOrderResponse2> getHistory(@Body Map address);

    @POST("/api/delivery/location/")
    Call<TrackOnGoingResponse> tracking(@Body Map address);

    @POST("/api/customer/order/rating/")
    Call<AddResponse> rating(@Body Map address);

    @POST("/customer/favourate/mom/")
    Call<AddResponse> favourite(@Body Map address);

    @POST("/api/get/offer/list/")
    Call<GetOfferResponse> getOfferList(@Body Map address);

    @POST("/customer/get/favourate/mom/")
    Call<GetFavouriteResponse> getFavourite(@Body Map address);

    @POST("/customer/resend/otp/")
    Call<ResendOtpResponse> resendOtpNew(@Body Map address);

    @POST("/customer/delete/address/")
    Call<SucessResponse> deleteAddress(@Body Map address);


    @POST("api/mom/add/token/")
    Call<SucessResponse> saveToken(@Body Map request);

    @POST("customer/cancel/order/")
    Call<SucessResponse> deleteOrder(@Body Map request);


    @POST("api/user/fcm/updated/")
    Call<PushNotificationResponse> pushNotification(@Body EventPushRequest request);


    @POST("/api/get/online/status/")
    Call<GetStatusResponse> getStatus(@Body Map request);


    @POST("/api/check/vendor/license/")
    Call<SucessResponse> checkFoodLicence(@Body Map request);


    @POST("/customer/update/payment/status/")
    Call<PaytmPaymentResponse> paytmStatusApi(@Body Map request);

    @POST("/api/get/profile/pics/")
    Call<GetProfileResponse> getProfile(@Body Map request);


    @POST("/customer/get/delivery/charge/cal/")
    Call<DeliveryChargeResponse> getDeliveryCharge(@Body Map request);


    @POST("/customer/latest/app/version/")
    Call<GetVersionResponse> getVersion();


}
