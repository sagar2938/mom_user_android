package mom.com.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import mom.com.model.LocationEventHome;
import mom.com.model.LocationEventSearch;
import mom.com.utils.Preferences;

/**
 * Created by silence12 on 10/12/18.
 */

public class FusedLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, ResultCallback<LocationSettingsResult> {
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Context context;
    LocationSettingsRequest mLocationSettingsRequest;
    public Location lastLocation;
    public LatLng lastLatLng;  // initialise the value with locality of the driver
    Activity activity;
//    TextView textView;
    public static String ADDRESS="";
    public static String ADDRESS2="";
    public static Location location;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public FusedLocation() {
    }


    public FusedLocation(Context context) {

        this.context = context;
        lastLatLng = new LatLng(28.6129, 77.2293);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
    }


    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        googleApiClient, mLocationSettingsRequest

                );

        result.setResultCallback(this);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        mLocationSettingsRequest = builder.build();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);


    public void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }catch (Exception e){

        }
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        startLocationUpdates();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//        lastLatLng=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        if(lastLocation!=null){
//            Toast.makeText(context, "Lat: " + lastLocation.getLatitude() + "\nLong: " + lastLocation.getLongitude(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(context, "Connection Suspended", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onLocationChanged(Location location) {
        this.lastLocation = location;
        this.lastLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        ADDRESS2=getAddressTextNew(this.lastLatLng);
//        getAddressTextNew(this.lastLatLng);
        FusedLocation.location=location;
        Preferences.getInstance(context).setLatitude(""+location.getLatitude());
        Preferences.getInstance(context).setLongitude(""+location.getLongitude());
//        stopLocationUpdates();

        EventBus.getDefault().post(new LocationEventHome(location.getLatitude(),location.getLongitude(),getAddressTextNew(this.lastLatLng)));
        EventBus.getDefault().post(new LocationEventSearch(location.getLatitude(),location.getLongitude(),getAddressTextNew(this.lastLatLng)));
    }

    String getAddressText(LatLng latLng) {
        Geocoder geocoder = new Geocoder(context);
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        List<Address> addresses = null;
        String addressText = "";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            if (address.getSubLocality()==null){
                addressText = String.format("%s, %s, %s,%s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getMaxAddressLineIndex() >= 1 ? address.getAddressLine(1) : "",
                        address.getLocality(),
                        address.getCountryName());
            }else {
                addressText = String.format("%s, %s, %s,%s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getMaxAddressLineIndex() >= 1 ? address.getAddressLine(1) : "",
                        address.getSubLocality(),
                        address.getLocality(),
                        address.getCountryName());
            }
        }

        try {
            addressText=addressText.trim().substring(4);
        }catch (Exception e){

        }

        return addressText;
    }



   /* void getAddressTextNew(LatLng latLng) {
        Geocoder geocoder = new Geocoder(context);
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        List<Address> addresses = null;
        String addressText = "";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            ADDRESS=address.getAddressLine(0);
        }


    }*/


    String getAddressTextNew(LatLng latLng) {
        Geocoder geocoder = new Geocoder(context);
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        List<Address> addresses = null;
        String addressText = "";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            ADDRESS=address.getAddressLine(0);
        }


        return ADDRESS;

    }




    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // Log.i("", "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i("", "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i("", "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i("", "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

}



