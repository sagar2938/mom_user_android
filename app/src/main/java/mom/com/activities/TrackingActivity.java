package mom.com.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import mom.com.R;
import mom.com.WebService.ResponseCallBack;
import mom.com.WebService.WebServiceHelper;
import mom.com.network.ApiCallService;
import mom.com.network.response.Deliver_data;
import mom.com.network.response.TrackOnGoingResponse;
import mom.com.utils.CustomProgressDialog;
import mom.com.utils.Helper;
import mom.com.utils.Preferences;


public class TrackingActivity extends BaseActivity implements OnMapReadyCallback {

    GoogleMap googleMap;
    ProgressDialog progressDialog;

    TextView distance;
    TextView time;

    ImageView waitingImg;
    ImageView acceptedImg;
    ImageView assignedImg;
    ImageView onWayImg;


    LinearLayout waiting;
    LinearLayout accepted;
    LinearLayout assigned;
    LinearLayout onWay;

    TextView name;
    TextView mobile;
    LinearLayout call;


    List<Double> latitudeList = new ArrayList<>();
    List<Double> longitudeList = new ArrayList<>();
    List<Double> durationList = new ArrayList<>();

    RelativeLayout detailLayout;


//    OrderDatum orderDatum;


    Double latitude;
    Double longitude;
    String orderId;

    void init() {
        waitingImg = findViewById(R.id.waitingImg);
        acceptedImg = findViewById(R.id.acceptedImg);
        assignedImg = findViewById(R.id.assignedImg);
        onWayImg = findViewById(R.id.onWayImg);

        waiting = findViewById(R.id.waiting);
        accepted = findViewById(R.id.accepted);
        assigned = findViewById(R.id.assigned);
        onWay = findViewById(R.id.onWay);
        detailLayout = findViewById(R.id.detailLayout);
        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);
        call = findViewById(R.id.call);
    }


    Deliver_data deliver_data;
    LatLng source;
    LatLng destination;
    boolean recursion = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
//        getSupportActionBar().setTitle("Tracking");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fm.getMapAsync(this);
        CustomProgressDialog.getInstance(this).show();
        detailLayout.setVisibility(View.GONE);

        name.setText(getIntent().getStringExtra("name"));
        mobile.setText(getIntent().getStringExtra("mobile"));

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(TrackingActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TrackingActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + mobile.getText().toString()));
                    startActivity(callIntent);
                }

            }
        });

    }


    private void apiHit() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                latitude = Double.valueOf(getIntent().getStringExtra("latitude"));
                longitude = Double.valueOf(getIntent().getStringExtra("longitude"));
                orderId = getIntent().getStringExtra("orderId");
                Map map = new Hashtable();
                map.put("mobile", Preferences.getInstance(getApplicationContext()).getMobile());
                map.put("latitude", latitude);
                map.put("longitude", longitude);
                map.put("orderId", orderId);
                ApiCallService.action(getApplicationContext(), map, ApiCallService.Action.ACTION_TRACKING);
                apiHit();
            }
        }, 60 * 1000);

    }

    //@Override
    @SuppressLint("NewApi")
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        latitude = Double.valueOf(getIntent().getStringExtra("latitude"));
        longitude = Double.valueOf(getIntent().getStringExtra("longitude"));
        orderId = getIntent().getStringExtra("orderId");
        Map map = new Hashtable();
        map.put("mobile", Preferences.getInstance(getApplicationContext()).getMobile());
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("orderId", orderId);
        ApiCallService.action(getApplicationContext(), map, ApiCallService.Action.ACTION_TRACKING);
        apiHit();
    }


    void recursion() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        MarkerOptions markerOptions1 = new MarkerOptions();
        MarkerOptions markerOptions2 = new MarkerOptions();
//        if (recursion) {
        BitmapDescriptor icon2 = BitmapDescriptorFactory.fromResource(R.drawable.bike);
        googleMap.addMarker(markerOptions1.position(source).title("Destination"))/*.setIcon(icon)*/;
        googleMap.addMarker(markerOptions2.position(destination).title("Source")).setIcon(icon2);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(source));
        builder.include(markerOptions1.getPosition());
        builder.include(markerOptions2.getPosition());
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.animateCamera(cu);
//        }


        if (recursion) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    recursion();
                }
            }, 1 * 1000);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        recursion = false;
    }

    @Subscribe
    public void timeOut(String messge) {
        CustomProgressDialog.setDismiss();
        getDialog(messge);
    }

    @Subscribe
    public void track_on_going(TrackOnGoingResponse response) {
        CustomProgressDialog.setDismiss();
        if (response.getResponse().getConfirmation() == 1) {
            googleMap.clear();
            deliver_data = response.getResponse().getDeliver_data().get(0);
            destination = new LatLng(deliver_data.getLatitude(), deliver_data.getLongitude());
            source = new LatLng(deliver_data.getDeliver_lat(), deliver_data.getDeliver_long());
            recursion();
            String url = getDirectionsUrl(new LatLng(source.latitude, source.longitude), new LatLng(destination.latitude, destination.longitude));
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);
            waiting.setVisibility(View.VISIBLE);
            accepted.setVisibility(View.GONE);
            assigned.setVisibility(View.GONE);
            onWay.setVisibility(View.GONE);
            if (deliver_data.getOrderStatus() == 0) {
//            waitingImg.setColorFilter(ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.ADD);
                waiting.setVisibility(View.VISIBLE);
                accepted.setVisibility(View.GONE);
                assigned.setVisibility(View.GONE);
                onWay.setVisibility(View.GONE);
            } else if (deliver_data.getOrderStatus() == 1) {
//            acceptedImg.setColorFilter(ContextCompat.getColor(this, R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);
                waiting.setVisibility(View.GONE);
                accepted.setVisibility(View.VISIBLE);
                assigned.setVisibility(View.GONE);
                onWay.setVisibility(View.GONE);
            } else if (deliver_data.getOrderStatus() == 2) {
//            waitingImg.setColorFilter(ContextCompat.getColor(this, R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);
//            acceptedImg.setColorFilter(ContextCompat.getColor(this, R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);
//            assignedImg.setColorFilter(ContextCompat.getColor(this, R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);
                waiting.setVisibility(View.GONE);
                accepted.setVisibility(View.GONE);
                assigned.setVisibility(View.VISIBLE);
                onWay.setVisibility(View.GONE);
            } else if (deliver_data.getOrderStatus() == 2) {
//            onWayImg.setColorFilter(ContextCompat.getColor(this, R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);
                waiting.setVisibility(View.GONE);
                accepted.setVisibility(View.GONE);
                assigned.setVisibility(View.GONE);
                onWay.setVisibility(View.VISIBLE);
            }
            detailLayout.setVisibility(View.VISIBLE);
//        Glide.with(getApplicationContext()).load(response.getResponse().getImage_path() + response.getResponse().getImage_name()).into(image);
//        name.setText(response.getResponse().getName());

        } else {
            getDialog(response.getResponse().getMessage());
        }
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyDFrE1WgFtmWfWPExKEnreTaFdsyqJLVfs";
        return url;
    }


    @SuppressLint("NewApi")
    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    @SuppressLint("NewApi")
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            try {
                for (int i = 0; i < routes.size(); i++) {
                    latitudeList.clear();
                    longitudeList.clear();
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                        latitudeList.add(lat);
                        longitudeList.add(lng);
                    }
                    polyLineOptions.addAll(points);
                    polyLineOptions.width(7);
                    polyLineOptions.color(Color.BLUE);
                    googleMap.addPolyline(polyLineOptions);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("http ex  " + e.getMessage());
            }
            double d = 0.0;
//            double d2 = 0.0;
            for (int i = 0; i < latitudeList.size() - 1; i++) {
                d = d + Helper.getDistance(latitudeList.get(i), longitudeList.get(i), latitudeList.get(i + 1), longitudeList.get(i + 1));
//                d2 = d2 + Helper.getDistanceBetween(new LatLng(latitudeList.get(i), longitudeList.get(i)),new LatLng( latitudeList.get(i + 1), longitudeList.get(i + 1)));
            }

            double t = 0;
            for (int i = 0; i < durationList.size(); i++) {
                t = t + durationList.get(i);
            }

//            System.out.println("llllllllll " + latitudeList.get(0) + "," + longitudeList.get(0));
//            System.out.println("llllllllll " + latitudeList.get(latitudeList.size() - 1) + "," + longitudeList.get(latitudeList.size() - 1));


            int hours = (int) (t / 60);
            int minutes = (int) (t % 60);
            System.out.printf("%d:%02d", hours, minutes);
//            distance.setText(String.format("%.1f", d) + " km");
//            time.setText(String.format("%02d:%02d", hours, minutes)+" min");

        }
    }


    @Subscribe
    public void timeout(String msg) {
        progressDialog.dismiss();
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

    }


    public class HttpConnection {
        @SuppressLint("LongLogTag")
        public String readUrl(String mapsApiDirectionsUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(mapsApiDirectionsUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();
            } catch (Exception e) {
                Log.d("Exception while reading url", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

    }


    public class PathJSONParser {

        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;
            try {
                jRoutes = jObject.getJSONArray("routes");
                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
                    durationList.clear();
                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {

                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps
                                    .get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

//                            source = list.get(0);
//                            destination = list.get(list.size() - 1);
                            recursion = false;
                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat",
                                        Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }

//                            distance_text = (String) ((JSONObject) ((JSONObject) jSteps
//                                    .get(k)).get("distance")).get("text");
//                            duration_text = (String) ((JSONObject) ((JSONObject) jSteps
//                                    .get(k)).get("duration")).get("text");
                            String[] ar = ((String) ((JSONObject) ((JSONObject) jSteps
                                    .get(k)).get("duration")).get("text")).split(" ");
                            durationList.add(Double.valueOf(ar[0]));

                        }
                        routes.add(path);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        /**
         * Method Courtesy :
         * jeffreysambells.com/2010/05/27
         * /decoding-polylines-from-google-maps-direction-api-with-java
         */
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse(mobile.getText().toString()));
                    startActivity(callIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
