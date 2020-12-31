package mom.com.network.response;

import java.util.List;

public class MomListResponse {
    int position;
    List<VendorData> vendor_data;
    List<VendorData> toprated_list;
    List<Offer> offer;

    public List<VendorData> getVendor_data() {
        return vendor_data;
    }

    public void setVendor_data(List<VendorData> vendor_data) {
        this.vendor_data = vendor_data;
    }

    public List<VendorData> getToprated_list() {
        return toprated_list;
    }

    public void setToprated_list(List<VendorData> toprated_list) {
        this.toprated_list = toprated_list;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<Offer> getOffer() {
        return offer;
    }

    public void setOffer(List<Offer> offer) {
        this.offer = offer;
    }
}
