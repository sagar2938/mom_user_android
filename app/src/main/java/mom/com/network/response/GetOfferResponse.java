package mom.com.network.response;

import java.util.List;

import mom.com.network.Offer_data;

public class GetOfferResponse {

    boolean success;

    List<Offer_data> offer_data;

    public List<Offer_data> getOffer_data() {
        return offer_data;
    }

    public void setOffer_data(List<Offer_data> offer_data) {
        this.offer_data = offer_data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
