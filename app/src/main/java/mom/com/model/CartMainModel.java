package mom.com.model;

import java.util.ArrayList;
import java.util.List;

public class CartMainModel {

    String name;
    String location;
    String latitude;
    String longitude;
    String mom_mobile;
    String mobile;
    String total_price;
    String payment_mode;
    String note;
    String promoCode = "";
    List<CartModel> product_list = new ArrayList<>();

    String tax_amount;
    String delivery_charge;
    String discount_amount;
    String sub_total;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMom_mobile() {
        return mom_mobile;
    }

    public void setMom_mobile(String mom_mobile) {
        this.mom_mobile = mom_mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public List<CartModel> getProduct_list() {
        return product_list;
    }

    public void setProduct_list(List<CartModel> product_list) {
        this.product_list = product_list;
    }

    public String getPaymentMode() {
        return payment_mode;
    }

    public void setPaymentMode(String paymentMode) {
        this.payment_mode = paymentMode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPromoCodeId() {
        return promoCode;
    }

    public void setPromoCodeId(String promoCodeId) {
        this.promoCode = promoCodeId;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getTax_amount() {
        return tax_amount;
    }

    public void setTax_amount(String tax_amount) {
        this.tax_amount = tax_amount;
    }

    public String getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(String delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    public String getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(String discount_amount) {
        this.discount_amount = discount_amount;
    }

    public String getSub_total() {
        return sub_total;
    }

    public void setSub_total(String sub_total) {
        this.sub_total = sub_total;
    }
}
