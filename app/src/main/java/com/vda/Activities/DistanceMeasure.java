package com.vda.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.vda.Classes.ParserTask;
import com.vda.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DistanceMeasure extends AppCompatActivity implements OnMapReadyCallback, ParserTask.TaskLoadedCallback {

    private static final String TAG = "ali";
    int DESTI_PLACE_PICKER_REQUEST = 1;
    int CURRENT_PLACE_PICKER_REQUEST = 111;
    View mapFragment;
    private GoogleMap mMap;
    TextView destination,current,title,distance,duration;
    Float camZoom = 14.0f;
    private Bitmap smallMarker;
    LatLng destinationLatLng, currentLatLng;
    private Polyline currentPolyline;
    //static int markersCount =0;
    static List<Marker> markerList;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_measure);
        mapFragment = (View) findViewById(R.id.location);
        destination = findViewById(R.id.destination_location);
        current = findViewById(R.id.current_location);
        title = findViewById(R.id.title);
        distance = findViewById(R.id.distance);
        duration = findViewById(R.id.duration);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location);
        mapFragment.getMapAsync(this);

        markerList = new ArrayList<Marker>();

        // Initialize Places.


// Create a new Places client instance.
//        PlacesClient placesClient = Places.createClient(this);
//
//
//        /**
//         * Initialize Places. For simplicity, the API key is hard-coded. In a production
//         * environment we recommend using a secure mechanism to manage API keys.
//         */
//        if (!Places.isInitialized()) {
//            Places.initialize(getApplicationContext(), "AIzaSyB982qUZj0laetYaxiB4aDerUkXWdka2qo");
//        }




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                camZoom = mMap.getCameraPosition().zoom;

            }
        });

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    public void pickDestination(View view) {
        progressDialog = ProgressDialog.show(DistanceMeasure.this, "",
                "Please wait...", true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), DESTI_PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        // Set the fields to specify which types of place data to return.
//        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
//
//        // Start the autocomplete intent.
//        Intent intent = new Autocomplete.IntentBuilder(
//                AutocompleteActivityMode.FULLSCREEN, fields)
//                .build(this);
//        startActivityForResult(intent, DESTI_PLACE_PICKER_REQUEST);
    }

    public void pickCurrentLocation(View view) {
        progressDialog = ProgressDialog.show(DistanceMeasure.this, "",
                "Please wait...", true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), CURRENT_PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == DESTI_PLACE_PICKER_REQUEST) {
            progressDialog.dismiss();
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                addMarker(place.getLatLng(),"Destination");
                String toastMsg = String.format("Place: %s", place.getName());
                destination.setText(place.getAddress());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                destinationLatLng = place.getLatLng();
            }
        }else if (requestCode == CURRENT_PLACE_PICKER_REQUEST) {
            progressDialog.dismiss();
            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(data, this);
                addMarker(place.getLatLng(),"Current");
                current.setText(place.getAddress());
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                currentLatLng = place.getLatLng();

            }
        }
        if (currentLatLng != null && destinationLatLng != null){
//            double dis =distance(currentLatLng.latitude,currentLatLng.longitude,destinationLatLng.latitude,destinationLatLng.longitude);
//            DecimalFormat df = new DecimalFormat("#.##");
//            double p = Double.parseDouble(df.format(dis));
//            title.setText("Distance Measured: "+p+"KM");
            distance(currentLatLng.latitude,currentLatLng.longitude,destinationLatLng.latitude,destinationLatLng.longitude);
            fetchPath(currentLatLng.latitude,currentLatLng.longitude,destinationLatLng.latitude,destinationLatLng.longitude);
        }
    }

    private void fetchPath(double latitude, double longitude, double latitude1, double longitude1) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
//        params.put("units","metric");
//        params.put("origins",lat1+","+lon1);
//        params.put("destinations",lat2+","+lon2);
//        params.put("key","AIzaSyCn4kgpxwaM6co-YY1olrFDOm2IWguCs74");

        client.post("https://maps.googleapis.com/maps/api/directions/json?units=metric&origin="+latitude+","+longitude+"&destination="
                + latitude1+","+longitude1+"&sensor=false&mode=driving&key=AIzaSyCn4kgpxwaM6co-YY1olrFDOm2IWguCs74", params, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String response, Throwable throwable) {
                Log.d("ali", "onFailure: "+response);

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String response) {
                Log.d("alii", "onSuccess: "+response);
                ParserTask parserTask= new ParserTask(DistanceMeasure.this,"driving");
                parserTask.execute(response);
            }

            @Override
            public void onStart() {

            }
        });
    }

    void addMarker(LatLng latLng,String title){
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(latLng);

        // Setting the title for the marker.
        // This will be displayed on taping the marker
        markerOptions.title(title);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car);

        markerOptions.icon(icon);

        // Clears the previously touched position
        //mMap.clear();


        // Animating to the touched position

        // Placing a marker on the touched position
        if (markerList.size() >1){
            mMap.clear();
        }
        Marker marker1 = mMap.addMarker(markerOptions);
        mMap.addMarker(markerOptions);
        markerList.add(marker1);


        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
        mMap.animateCamera(cu);
//

//        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latLng, camZoom);
//        CameraUpdate zoom = CameraUpdateFactory.zoomTo(camZoom);
//        mMap.moveCamera(center);
//        mMap.animateCamera(zoom);

    }

    private void distance(double lat1, double lon1, double lat2, double lon2) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
//        params.put("units","metric");
//        params.put("origins",lat1+","+lon1);
//        params.put("destinations",lat2+","+lon2);
//        params.put("key","AIzaSyCn4kgpxwaM6co-YY1olrFDOm2IWguCs74");

        client.post("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins="+lat1+","+lon1+"&destinations="
               + lat2+","+lon2+"&key=AIzaSyCn4kgpxwaM6co-YY1olrFDOm2IWguCs74", params, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String response, Throwable throwable) {
                Log.d("ali", "onFailure: "+response);

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String response) {
                Log.d("alii", "onSuccess: "+response);
                JSONObject obj;
                JSONArray array;
                JSONObject object;
                JSONObject distanceObject;
                JSONObject durationObject;

                try {
                    obj = new JSONObject(response);
                    array = obj.getJSONArray("rows");
                    object = array.getJSONObject(0);

                    array = object.getJSONArray("elements");
                    object = array.getJSONObject(0);

                    distanceObject = object.getJSONObject("distance");

                    durationObject  = object.getJSONObject("duration");

                    Log.d(TAG, "onSuccess: "+distanceObject.getString("text")+", "+durationObject.getString("text"));
                    distance.setText("Distance measured: "+distanceObject.getString("text"));
                    duration.setText("Time required: "+durationObject.getString("text"));

                } catch (Exception e) {
                    Log.d(TAG, "onSuccess: "+e.getMessage());
                }
            }

            @Override
            public void onStart() {

            }
        });

    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
