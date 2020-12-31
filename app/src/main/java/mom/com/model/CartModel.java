package mom.com.model;

public class CartModel {
    String image="";
    Integer item_id;
    String food_item;
    String itemActualPrice;
    Double price;
    String type;
    Integer quantity;
    String itemDescription="";

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getItem_id() {
        return item_id;
    }

    public void setItem_id(Integer item_id) {
        this.item_id = item_id;
    }

    public String getFood_item() {
        return food_item;
    }

    public void setFood_item(String food_item) {
        this.food_item = food_item;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Integer getId() {
        return item_id;
    }

    public void setId(Integer id) {
        this.item_id = id;
    }

    public String getItemName() {
        return food_item;
    }

    public void setItemName(String food_item) {
        this.food_item = food_item;
    }

    public String getItemActualPrice() {
        return itemActualPrice;
    }

    public void setItemActualPrice(String itemActualPrice) {
        this.itemActualPrice = itemActualPrice;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
