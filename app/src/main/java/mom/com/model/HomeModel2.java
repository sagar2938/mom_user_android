package mom.com.model;

public class HomeModel2 {

    int food_image;
    String food_title,food_subtitle,food_per_person,food_tracking,food_two_wheeler,food_minus,food_add_text,food_plus;

    public HomeModel2(int food_image, String food_title, String food_subtitle, String food_per_person, String food_tracking, String food_two_wheeler, String food_minus, String food_add_text, String food_plus) {
        this.food_image = food_image;
        this.food_title = food_title;
        this.food_subtitle = food_subtitle;
        this.food_per_person = food_per_person;
        this.food_tracking = food_tracking;
        this.food_two_wheeler = food_two_wheeler;
        this.food_minus = food_minus;
        this.food_add_text = food_add_text;
        this.food_plus = food_plus;
    }

    public String getFood_minus() {
        return food_minus;
    }

    public void setFood_minus(String food_minus) {
        this.food_minus = food_minus;
    }

    public String getFood_add_text() {
        return food_add_text;
    }

    public void setFood_add_text(String food_add_text) {
        this.food_add_text = food_add_text;
    }

    public String getFood_plus() {
        return food_plus;
    }

    public void setFood_plus(String food_plus) {
        this.food_plus = food_plus;
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

}
