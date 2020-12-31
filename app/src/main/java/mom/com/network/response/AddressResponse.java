package mom.com.network.response;

import java.util.List;

public class AddressResponse {
    List<AddressData> address_data;
    boolean success;


    public List<AddressData> getAddress_data() {
        return address_data;
    }

    public void setAddress_data(List<AddressData> address_data) {
        this.address_data = address_data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
