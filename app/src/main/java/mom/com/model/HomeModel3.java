package mom.com.model;

public class HomeModel3 {

    int food_image;
    String food_title;

    public HomeModel3(int food_image, String food_title) {
        this.food_image = food_image;
        this.food_title = food_title;
    }

    public int getFood_image() {
        return food_image;
    }

    public void setFood_image(int food_image) {
        this.food_image = food_image;
    }

    public String getFood_title() {
        return food_title;
    }

    public void setFood_title(String food_title) {
        this.food_title = food_title;
    }
}
