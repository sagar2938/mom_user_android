package mom.com.network.response;

public class Deliver_data {
    Double deliver_long;
    Double latitude;
    Double longitude;
    Double deliver_lat;
    String orderId;
    int orderStatus;


    public Double getDeliver_long() {
        return deliver_long;
    }

    public void setDeliver_long(Double deliver_long) {
        this.deliver_long = deliver_long;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getDeliver_lat() {
        return deliver_lat;
    }

    public void setDeliver_lat(Double deliver_lat) {
        this.deliver_lat = deliver_lat;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }
}
