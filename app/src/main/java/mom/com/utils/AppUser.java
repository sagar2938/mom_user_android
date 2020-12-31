package mom.com.utils;


import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import mom.com.model.CartModel;
import mom.com.network.response.MenuData;

public class AppUser {

    static AppUser appUser;
    public static AppUser getInstance(){
        if (AppUser.appUser==null){
            appUser=new AppUser();
        }
        return appUser;
    }


    public static AppUser getAppUser() {
        return appUser;
    }

    public static void setAppUser(AppUser appUser) {
        AppUser.appUser = appUser;
    }

    Map signUpRequest;
    Map loginRequest;
    public String time;
    public String date;

    List<CartModel> cartModels;
    String momMobile="";
    double amount;


    Map<Integer, Integer> mFullQuantity=new Hashtable<>();
    Map<Integer, MenuData> mItemDetail=new Hashtable<>();
    Map<Integer, String> mQuantityState=new Hashtable<>();


    public Map<Integer, String> getmQuantityState() {
        return mQuantityState;
    }

    public void setmQuantityState(Map<Integer, String> mQuantityState) {
        this.mQuantityState = mQuantityState;
    }

    public Map<Integer, MenuData> getmItemDetail() {
        return mItemDetail;
    }

    public void setmItemDetail(Map<Integer, MenuData> mItemDetail) {
        this.mItemDetail = mItemDetail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMomMobile() {
        return momMobile;
    }

    public void setMomMobile(String momMobile) {
        this.momMobile = momMobile;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<CartModel> getCartModels() {
        return cartModels;
    }

    public void setCartModels(List<CartModel> cartModels) {
        this.cartModels = cartModels;
    }

    public Map getSignUpRequest() {
        return signUpRequest;
    }

    public void setSignUpRequest(Map signUpRequest) {
        this.signUpRequest = signUpRequest;
    }

    public Map getLoginRequest() {
        return loginRequest;
    }

    public void setLoginRequest(Map loginRequest) {
        this.loginRequest = loginRequest;
    }

    public Map<Integer, Integer> getmFullQuantity() {
        return mFullQuantity;
    }

    public void setmFullQuantity(Map<Integer, Integer> mFullQuantity) {
        this.mFullQuantity = mFullQuantity;
    }
}
