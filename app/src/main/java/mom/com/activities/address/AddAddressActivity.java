package mom.com.activities.address;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.Places;
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import mom.com.R;

import mom.com.activities.BaseActivity;

import mom.com.network.ApiCallService;
import mom.com.network.response.SucessResponse;
import mom.com.utils.Preferences;


public class AddAddressActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    TextView mLocationMarkerText;
    EditText address;
    EditText enteredAddress;
    EditText phone_number;
    EditText name;
    Button submit;
    ImageView edit_address_iv ;


    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    private LatLng mCenterLatLong;
    String mLatitude = "";
    String mLongitude = "";
    ProgressDialog mProgressDialog;
    private AddressResultReceiver mResultReceiver;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    Map map;
    boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_address_activity);
        address = findViewById(R.id.address);
        enteredAddress = findViewById(R.id.enteredAddress);
        phone_number = findViewById(R.id.phone_number);
        name = findViewById(R.id.name);
        submit = (Button) findViewById(R.id.submit);
        mLocationMarkerText = (TextView) findViewById(R.id.locationMarkertext);

        edit_address_iv = findViewById(R.id.edit_address_iv);

        edit_address_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(AddAddressActivity.this, PlacePickerActivity.class);
//                startActivityForResult(intent,2);

                showAutocomplete();


            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");

        if (getIntent().getStringExtra("for")==null){
            isUpdate = false;
        } else if (getIntent().getStringExtra("for").equals("edit")) {
            name.setText(getIntent().getStringExtra("name"));
            phone_number.setText(getIntent().getStringExtra("phone_number"));

            try {
                address.setText(getIntent().getStringExtra("address").split("\\n")[1]);
            }catch (Exception e){
                address.setText(getIntent().getStringExtra("address").split("\\n")[0]);
                e.printStackTrace();
            }

            try {
                enteredAddress.setText(getIntent().getStringExtra("address").split("\\n")[0]);
            }catch (Exception e){
                e.printStackTrace();
            }


            double lat = Double.valueOf(getIntent().getStringExtra("lat"));
            double lng = Double.valueOf(getIntent().getStringExtra("long"));
            mCenterLatLong = new LatLng(lat, lng);
            isUpdate = true;
        } else {
            isUpdate = false;
        }


        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openAutocompleteActivity();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty()) {
                    getDialog("Please enter name");
                    return;
                }

                if (!phone_number.getText().toString().isEmpty()) {
                    if (phone_number.getText().toString().length()!=10) {
                        getDialog("Please valid number");
                        return;
                    }
                }
                map = new HashMap();
                map.put("mobile", Preferences.getInstance(getApplicationContext()).getMobile());
                map.put("latitude", mLatitude);
                map.put("longitude", mLongitude);
                map.put("phone_number", phone_number.getText().toString());
                map.put("name", name.getText().toString());

                if (enteredAddress.getText().toString().trim().isEmpty()) {
                    map.put("address", address.getText().toString());
                } else {
                    map.put("address", enteredAddress.getText().toString() + "\n" + address.getText().toString());
                }

                if (isUpdate){
                    map.put("id",getIntent().getStringExtra("id"));
                    ApiCallService.action(AddAddressActivity.this, map, ApiCallService.Action.ACTION_UPDATE_ADDRESS);
                }else {
                    ApiCallService.action(AddAddressActivity.this, map, ApiCallService.Action.ACTION_ADD_ADDRESS);
                }
            }

        });
        mapFragment.getMapAsync(this);
        mResultReceiver = new AddressResultReceiver(new Handler());

        if (checkPlayServices()) {
            if (!AppUtils.isLocationEnabled(this)) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(this, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                try {
                    Log.d("Camera position change" + "", cameraPosition + "");
                    mCenterLatLong = cameraPosition.target;
                    mMap.clear();
                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                    startIntentService(mLocation);
                    mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "\n" + "Long : " + mCenterLatLong.longitude);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (isUpdate){
            mLastLocation.setLatitude(mCenterLatLong.latitude);
            mLastLocation.setLongitude(mCenterLatLong.longitude);
        }
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        }
//        changeMap(mLastLocation);
        Log.d(TAG, "ON connected");

        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                if (!isUpdate) {
                    changeMap(location);
                }
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;
            try {
                latLong = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(19f).tilt(70).build();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
                mLocationMarkerText.setText("Lat : " + location.getLatitude() + "\n" + "Long : " + location.getLongitude());
                startIntentService(location);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }


    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

                address.setText(resultData.getString("address"));
                mLatitude = resultData.getString("latitude");
                mLongitude = resultData.getString("longitude");

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));
            }


        }

    }

    protected void startIntentService(Location mLocation) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);
        startService(intent);
    }


    private void openAutocompleteActivity() {
        try {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_COUNTRY)
                    .setCountry("IN")
                    .build();

            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(autocompleteFilter)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showAutocomplete() {
        try {
            Intent intent = new com.google.android.libraries.places.compat.ui.PlaceAutocomplete.IntentBuilder(com.google.android.libraries.places.compat.ui.PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng latLong;
                latLong = place.getLatLng();
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(19f).tilt(70).build();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }


        }else if (requestCode == 2) {

            if (resultCode == RESULT_OK) {
                try {

                    mLatitude = data.getStringExtra("lat");
                    mLongitude = data.getStringExtra("lon");
                    double lat = Double.valueOf(getIntent().getStringExtra("lat"));
                    double lng = Double.valueOf(getIntent().getStringExtra("long"));
                    mCenterLatLong = new LatLng(lat, lng);
                    Log.d("AddressComming",data.getStringExtra("address"));
                    address.setText(data.getStringExtra("address"));
                    isUpdate = true;

                } catch (Exception e) {

                }
            }
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
        } else if (resultCode == RESULT_CANCELED) {
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.icon_id) {
            openAutocompleteActivity();
        } else {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Subscribe
    public void addAddress(SucessResponse response) {
        if (response.getResponse().getConfirmation() == 1) {
            Preferences.getInstance(getApplicationContext()).setAddressStatus(1);
//            Preferences.getInstance(getApplicationContext()).setAddress(map.get("address").toString());
//            Preferences.getInstance(getApplicationContext()).setLatitude(map.get("latitude").toString());
//            Preferences.getInstance(getApplicationContext()).setLongitude(map.get("longitude").toString());
            intent();
        } else {
            getDialog(response.getResponse().getMessage());
        }
    }


    void intent() {
        Intent intent = getIntent();
        if (enteredAddress.getText().toString().trim().isEmpty()) {
            intent.putExtra("address", address.getText().toString());
        } else {
            intent.putExtra("address", enteredAddress.getText().toString() + "\n" + address.getText().toString());
        }
        intent.putExtra("name", name.getText().toString());
        intent.putExtra("latitude", mLatitude);
        intent.putExtra("longitude", mLongitude);
        intent.putExtra("mobile", phone_number.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

}
