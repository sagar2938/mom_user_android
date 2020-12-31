package mom.com.model;

public class LocationEventHome {
    Double latitude;
    Double longitude;
    String address;

    public LocationEventHome(Double latitude, Double longitude, String address){
        this.latitude=latitude;
        this.longitude=longitude;
        this.address=address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }
}
