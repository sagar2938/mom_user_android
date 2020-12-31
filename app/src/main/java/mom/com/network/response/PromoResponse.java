package mom.com.network.response;

public class PromoResponse {
    double total_discount;
    int promo_status;
    boolean success;


    public double getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(double total_discount) {
        this.total_discount = total_discount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getPromo_status() {
        return promo_status;
    }

    public void setPromo_status(int promo_status) {
        this.promo_status = promo_status;
    }
}
