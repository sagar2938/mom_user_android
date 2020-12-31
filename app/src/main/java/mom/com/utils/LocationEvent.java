package mom.com.utils;

import android.location.Location;

public class LocationEvent {
    Location location;
    public LocationEvent(Location location){
        this.location=location;
    }
    public Location getLocation(){
        return this.location;
    }

}
