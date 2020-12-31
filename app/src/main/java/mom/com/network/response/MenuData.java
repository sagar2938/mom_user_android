package mom.com.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MenuData {
    @SerializedName("halfPrice")
    @Expose
    private String halfPrice;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("quarter")
    @Expose
    private boolean quarter;
    @SerializedName("quarterPrice")
    @Expose
    private String quarterPrice;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("fullPrice")
    @Expose
    private String fullPrice;
    @SerializedName("itemDescription")
    @Expose
    private String itemDescription;
    @SerializedName("full")
    @Expose
    private boolean full;
    @SerializedName("half")
    @Expose
    private boolean half;
    @SerializedName("food_type")
    @Expose
    private String food_type;
    @SerializedName("itemGroup")
    @Expose
    private String itemGroup;
    @SerializedName("itemName")
    @Expose
    private String itemName;
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("item_image")
    @Expose
    private String itemImage;

    @SerializedName("image_path")
    @Expose
    private String imagePath;

    List<Price_list> price_list;
    public String getHalfPrice() {
        return halfPrice;
    }

    public void setHalfPrice(String halfPrice) {
        this.halfPrice = halfPrice;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public boolean getQuarter() {
        return quarter;
    }

    public void setQuarter(boolean quarter) {
        this.quarter = quarter;
    }

    public String getQuarterPrice() {
        return quarterPrice;
    }

    public void setQuarterPrice(String quarterPrice) {
        this.quarterPrice = quarterPrice;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(String fullPrice) {
        this.fullPrice = fullPrice;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public boolean getFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public boolean getHalf() {
        return half;
    }

    public void setHalf(boolean half) {
        this.half = half;
    }

    public String getFoodType() {
        return food_type;
    }

    public void setFoodType(String food_type) {
        this.food_type = food_type;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public List<Price_list> getPrice_list() {
        return price_list;
    }

    public void setPrice_list(List<Price_list> price_list) {
        this.price_list = price_list;
    }

    public boolean isQuarter() {
        return quarter;
    }

    public boolean isFull() {
        return full;
    }

    public boolean isHalf() {
        return half;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
