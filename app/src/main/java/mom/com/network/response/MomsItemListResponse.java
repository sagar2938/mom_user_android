package mom.com.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MomsItemListResponse {
    @SerializedName("menu_data")
    @Expose
    private List<MenuData> menuData = null;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public List<MenuData> getMenuData() {
        return menuData;
    }

    public void setMenuData(List<MenuData> menuData) {
        this.menuData = menuData;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
