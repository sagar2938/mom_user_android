package mom.com.network.response.order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDatum {

    @SerializedName("orderId")
    @Expose
    private String orderId;
    @SerializedName("total_price")
    @Expose
    private String totalPrice;
    @SerializedName("mom_mobile")
    @Expose
    private String momMobile;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("productList")
    @Expose
    private List<ProductList> productList = null;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("id")
    @Expose
    private Integer id;


    String momName;
    String deliverBoyName;


    String customerRating;
    int ratingStatus;
    int favourate;

    String createdAt;
    String updatedAt;
    String momId;
    String note;
    int orderStatus;
    String deliver_number;
    String delivery_name;
    String mom_name;
//    String mom_mobile;


    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMomId() {
        return momId;
    }

    public void setMomId(String momId) {
        this.momId = momId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getMomMobile() {
        return momMobile;
    }

    public void setMomMobile(String momMobile) {
        this.momMobile = momMobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductList> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductList> productList) {
        this.productList = productList;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(String customerRating) {
        this.customerRating = customerRating;
    }

    public int getRatingStatus() {
        return ratingStatus;
    }

    public void setRatingStatus(int ratingStatus) {
        this.ratingStatus = ratingStatus;
    }

    public int getFavourate() {
        return favourate;
    }

    public void setFavourate(int favourate) {
        this.favourate = favourate;
    }

    public String getDeliver_number() {
        return deliver_number;
    }

    public void setDeliver_number(String deliver_number) {
        this.deliver_number = deliver_number;
    }

    public String getMomName() {
        return momName;
    }

    public void setMomName(String momName) {
        this.momName = momName;
    }

    public String getDeliveryBoyName() {
        return deliverBoyName;
    }

    public void setDeliveryBoyName(String deliverBoyName) {
        this.deliverBoyName = deliverBoyName;
    }


    public String getDelivery_name() {
        return delivery_name;
    }

    public void setDelivery_name(String delivery_name) {
        this.delivery_name = delivery_name;
    }

    public String getMom_name() {
        return mom_name;
    }

    public void setMom_name(String mom_name) {
        this.mom_name = mom_name;
    }
}
