package mom.com.model;

public class HomeModel {

    int food_image;
    String food_title,food_subtitle,food_per_person,food_tracking,food_two_wheeler,food_rate;

    public HomeModel(int food_image, String food_title, String food_subtitle, String food_per_person, String food_tracking, String food_two_wheeler, String food_rate) {

        this.food_image = food_image;
        this.food_title = food_title;
        this.food_subtitle = food_subtitle;
        this.food_per_person = food_per_person;
        this.food_tracking = food_tracking;
        this.food_two_wheeler = food_two_wheeler;
        this.food_rate = food_rate;
    }

   public HomeModel() {
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

    public String getFood_subtitle() {
        return food_subtitle;
    }

    public void setFood_subtitle(String food_subtitle) {
        this.food_subtitle = food_subtitle;
    }

    public String getFood_per_person() {
        return food_per_person;
    }

    public void setFood_per_person(String food_per_person) {
        this.food_per_person = food_per_person;
    }

    public String getFood_tracking() {
        return food_tracking;
    }

    public void setFood_tracking(String food_tracking) {
        this.food_tracking = food_tracking;
    }

    public String getFood_two_wheeler() {
        return food_two_wheeler;
    }

    public void setFood_two_wheeler(String food_two_wheeler) {
        this.food_two_wheeler = food_two_wheeler;
    }

    public String getFood_rate() {
        return food_rate;
    }

    public void setFood_rate(String food_rate) {
        this.food_rate = food_rate;
    }
}
