package mom.com.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mom.com.R;
import mom.com.activities.address.AppUtils;
import mom.com.activities.address.FetchAddressIntentService;
import mom.com.network.volley.NetworkCallBacks.ResponseCallback;
import mom.com.network.volley.WebserviceHelper;
import mom.com.utils.Constants;
import mom.com.utils.CustomUploadProgressDialog;
import mom.com.utils.Preferences;


public class AddressPickerActivity2 extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    List<String> addressList ;
    private static String TAG = "MAP LOCATION";
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    Context mContext;
    TextView mLocationMarkerText;
    Button submit ;
    EditText address_et;
    EditText street_et,house_no,land_mark,pincode_et,state_et;
    TextView mLocationText;
    Toolbar mToolbar;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng mCenterLatLong;
    LatLng latLngData ;
    String mLatitude;
    String mLongitude;
    boolean from;

    String sendAddress;
    ImageView edit_address_iv;

    private AddressResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_picker2);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setIcon(R.mipmap.logo);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
        mContext = this;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mLocationMarkerText = findViewById(R.id.locationMarkertext);
        mLocationText = findViewById(R.id.mlocationtext_tv);
        address_et =  findViewById(R.id.address_tv);
        street_et = findViewById(R.id.street_et);
        house_no = findViewById(R.id.house_no);
        land_mark = findViewById(R.id.land_mark);
        pincode_et = findViewById(R.id.pincode_et);
        state_et = findViewById(R.id.state_et);
        submit = findViewById(R.id.submit);
        from=getIntent().getBooleanExtra("from",false);
        if (from){
            house_no.setVisibility(View.GONE);
            street_et.setVisibility(View.GONE);
            land_mark.setVisibility(View.GONE);
            state_et.setVisibility(View.GONE);
            pincode_et.setVisibility(View.GONE);
        }

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));


        mLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openAutocompleteActivity();

            }


        });



        address_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity();

            }


        });

        mapFragment.getMapAsync(this);
        mResultReceiver = new AddressPickerActivity2.AddressResultReceiver(new Handler());

        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(mContext)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
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
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                intent.putExtra("lat", mLatitude);
                intent.putExtra("lon", mLongitude);
                intent.putExtra("address", sendAddress);
                setResult(RESULT_OK, intent);
                finish();

//                if (from){
//                    Intent intent = getIntent();
//                    intent.putExtra("lat", mLatitude);
//                    intent.putExtra("lon", mLongitude);
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }else {
//                    if (house_no.getText().toString().trim().isEmpty()) {
//                        Toast.makeText(getApplicationContext(), "Please Enter House No.", Toast.LENGTH_SHORT).show();
//                    } else if (street_et.getText().toString().trim().isEmpty()) {
//                        Toast.makeText(getApplicationContext(), "Please Enter Street", Toast.LENGTH_SHORT).show();
//                    } else if (state_et.getText().toString().trim().isEmpty()) {
//                        Toast.makeText(getApplicationContext(), "Please Enter State", Toast.LENGTH_SHORT).show();
//                    } else if (pincode_et.getText().toString().trim().isEmpty()) {
//                        Toast.makeText(getApplicationContext(), "Please Enter Pincode", Toast.LENGTH_SHORT).show();
//                    } else if (land_mark.getText().toString().trim().isEmpty()) {
//                        Toast.makeText(getApplicationContext(), "Please Enter City", Toast.LENGTH_SHORT).show();
//                    } else {
//                        fetchProfile(house_no.getText().toString(), street_et.getText().toString(), state_et.getText().toString(), pincode_et.getText().toString(), land_mark.getText().toString());
//                    }
//                }
            }
        });

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return false;
        }
        onBackPressed();
        return true;
    }



    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;


                mMap.clear();

                try {

                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);

                    startIntentService(mLocation);
                    mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
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
                changeMap(location);
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

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(19f).tilt(70).build();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            mLocationMarkerText.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());
            startIntentService(location);


        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
//            if (mAreaOutput != null)
            // mLocationText.setText(mAreaOutput+ "");

//                address_et.setText(mAddressOutput);
//            mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);
        startService(intent);
    }

    private void openAutocompleteActivity() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(), 0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(mContext, data);

                // TODO call location based filter


                LatLng latLong;


                latLong = place.getLatLng();

                //mLocationText.setText(place.getName() + "");

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(19f).tilt(70).build();
                latLngData = latLong ;

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));


            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mContext, data);
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.d("Response Result", String.valueOf(resultData));
            addressList = new ArrayList<>();
            // Display the address string or an error message sent from the intent service.

            String fullAddress = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);
//            String[] skill = fullAddress.split("\\s*,\\s*");
//            Pattern zipPattern = Pattern.compile("(\\d{6})");
//            Matcher zipMatcher = zipPattern.matcher(resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET));
//            if (zipMatcher.find()) {
//                String[] skill11 = skill[skill.length-2].split(zipMatcher.group(1));
//                state_et.setText(skill11[0]);
//                Log.d("ZIPMATCHER", String.valueOf(zipMatcher.group(1))+"..."+skill11[0]);
//                pincode_et.setText(zipMatcher.group(1));
//            }
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);

            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);

            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);
            street_et.setText(mAreaOutput);
            land_mark.setText(mCityOutput);
            address_et.setText(resultData.getString("sub area")+","+resultData.getString("sub area"));
            if (resultData.getString("sub area")==null){
                sendAddress=resultData.getString("city");

            }else {
                sendAddress=resultData.getString("sub area")+","+resultData.getString("city");

            }

            address_et.setText(sendAddress);
            mLatitude = resultData.getString("latitude");
            mLongitude = resultData.getString("longitude");
//            house_no.setText(skill[0]);


            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));


            }


        }

    }

    private void fetchProfile( String houseNo, String locality, String state, String pincode,String city) {
        CustomUploadProgressDialog.getInstance(this);
        String url = Constants.BASE_URL + "api/add/user/address/";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", Preferences.getInstance(AddressPickerActivity2.this).getMobile());
            jsonObject.put("parentName","");
            jsonObject.put("gender", "");
            jsonObject.put("houseNo", houseNo);
            jsonObject.put("locality", locality);
            jsonObject.put("state", state);
            jsonObject.put("pincode", pincode);
            jsonObject.put("city",city);
            jsonObject.put("latitude",latLngData.latitude);
            jsonObject.put("longitude",latLngData.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebserviceHelper.getInstance().PostCall(getApplicationContext(), url, jsonObject, new ResponseCallback() {
            @Override
            public void OnSuccessFull(JSONObject Response) {
                if (Response != null) {
                    CustomUploadProgressDialog.setDismiss();
//                    startActivity(new Intent(getApplicationContext(), UploadIdentityActivity.class));
                    finish();
                }else {
                    CustomUploadProgressDialog.setDismiss();
                }
            }
        });
    }
}
