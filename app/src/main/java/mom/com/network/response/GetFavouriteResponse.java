package mom.com.network.response;

import java.util.List;

import mom.com.network.Offer_data;

public class GetFavouriteResponse {

    boolean success;

    List<Mom_data> mom_data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Mom_data> getMom_data() {
        return mom_data;
    }

    public void setMom_data(List<Mom_data> mom_data) {
        this.mom_data = mom_data;
    }
}
