package mom.com.network.request;

public class EventPushRequest {
    String to;
    String senderType = "Customer";
    String message;
    String title = "MOM";

    public EventPushRequest() {
        senderType = "Vendor";
        title = "MOM";
    }

    public EventPushRequest(String to, String message) {
        this.to = to;
        this.message = message;
        title = "MOM";
        senderType = "Vendor";
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
