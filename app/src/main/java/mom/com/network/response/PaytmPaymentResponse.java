package mom.com.network.response;

public class PaytmPaymentResponse {
    boolean success;
    int confirmation;

//    1==sucess
//    0== repeat
//    2== faild


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(int confirmation) {
        this.confirmation = confirmation;
    }
}
