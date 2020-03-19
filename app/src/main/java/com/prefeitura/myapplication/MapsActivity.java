package com.prefeitura.myapplication;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.GeoDataClient;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.Places;
import com.prefeitura.myapplication.AppFragments.ExitFragment;
import com.prefeitura.myapplication.AppFragments.MapMenuFragment;
import com.prefeitura.myapplication.AppFragments.SugestFragment;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 10f;
    //private static final int M_MAX_ENTRIES = 5;
    private GoogleMap mMap;
    private boolean locationPermissionGranted = false;
    private GeoDataClient mGeo;
    private PlaceDetectionClient mPlace;
    private FusedLocationProviderClient mFused;
    private Location mLastLocation;
    private LocationRequest locationRequest;
    private MapMenuFragment menu;
    /*
        Locations and data used for routes
     */
    private LatLng destiny;
    // Destination city when the user request a route
    private String desti_city;
    // Defines the zoom for the destinity
    private float ZOOM = 0.5f;
    /*
        Variables and objects used for sugesting cummon locations
    */
    // The main sugestion view used in the activity
    private SugestFragment sugest;
    // Constants cordenates used in the markers
    private final LatLng PRACA = new LatLng(-21.0100373, -40.8330501);
    private final LatLng EXPOSICAO = new LatLng(-21.0197895, -40.837828);
    // Sets the current state of the sugestFragment
    private boolean isSugestionsVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a GeoDataClient with new api(without null argumment)
        mGeo = Places.getGeoDataClient(this);

        // Construct a PlaceDetectionClient with new api(without null argumment)
        mPlace = Places.getPlaceDetectionClient(this);

        // Construct a fusedLocationProviderClient with new api(without null argumment)
        mFused = LocationServices.getFusedLocationProviderClient(this);

        // Get MapMenuFragment from activity
        menu = (MapMenuFragment) getSupportFragmentManager().findFragmentById(R.id.search);

        menu.Conclude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConclude();
            }
        });

        menu.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancel();
            }
        });

        // Tests the sugest fragment
        sugest = (SugestFragment) getSupportFragmentManager().findFragmentById(R.id.sugest);
    }

    // Functions that initializes the constant markers
    private void initConstatantMarkers(){
        // Markers based on the constant locations
        MarkerOptions praca = new MarkerOptions().position(PRACA)
                .title(getResources().getString(R.string.praca));
        MarkerOptions exposicao = new MarkerOptions().position(EXPOSICAO)
                .title(getResources().getString(R.string.exposicao));

        // Adds the map to the map
        Marker marker = mMap.addMarker(praca);
        marker.setTag(0);
        Marker marker1 = mMap.addMarker(exposicao);
        marker1.setTag(0);
    }

    @Override
    public boolean onMarkerClick(final Marker marker){
        // Names used in the markers
        String tmarkers[] = {getResources().getString(R.string.praca),
                            getResources().getString(R.string.exposicao)};

        // Verify the markers
        if (marker.getTitle().equals(tmarkers[0])){
            // Changes the visibility to the sugestFragment
            if (!sugest.getVisible())
                sugest.setVisibility(true);

            // Changes the sugestFragment
            sugest.setImage(R.drawable.praca);
            sugest.setText(getResources().getString(R.string.praca));

            // Sets the on click listener
            sugest.go_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onFirstSugestion();
                }
            });
        }
        else if (marker.getTitle().equals(tmarkers[1])){
            // Changes the visibility to the sugestFragment
            if (!sugest.getVisible())
                sugest.setVisibility(true);

            // Changes the sugestFragment
            sugest.setImage(R.drawable.exposicao);
            sugest.setText(getResources().getString(R.string.exposicao));

            // Sets the on click listener
            sugest.go_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSecondSugestion();
                }
            });
        }

        // Shows the title of the markers
        if (marker.isInfoWindowShown())
            marker.hideInfoWindow();
        else
            marker.showInfoWindow();

        return true;
    }

    // First route sugestion
    private void onFirstSugestion(){
        // Changes the menu item
        // Change Conclude Button State
        menu.Conclude.setEnabled(false);
        menu.Conclude.setBackground(ContextCompat.getDrawable(this,
                R.drawable.conclude_button_back_dasable));

        // Change Cancel button state
        menu.cancel.setEnabled(true);
        menu.cancel.setBackground(ContextCompat.getDrawable(this,
                R.drawable.cancel_button_back));

        // Changes the RouteTask layout
        menu.RouteTask.setVisibility(View.VISIBLE);

        // Change the text of the menu
        menu.AutoText.setText(getResources().getString(R.string.praca));
        menu.AutoText.setEnabled(false);

        // Sets the destiny global variable location
        destiny = PRACA;

        // Calls the functions to draw the route
        getRouteByLocations(new LatLng(mLastLocation.getLatitude(),
                mLastLocation.getLongitude()), destiny);
    }

    // Second route sugestion
    private void onSecondSugestion(){
        // Changes the menu item
        // Change Conclude Button State
        menu.Conclude.setEnabled(false);
        menu.Conclude.setBackground(ContextCompat.getDrawable(this,
                R.drawable.conclude_button_back_dasable));

        // Change Cancel button state
        menu.cancel.setEnabled(true);
        menu.cancel.setBackground(ContextCompat.getDrawable(this,
                R.drawable.cancel_button_back));

        // Changes the RouteTask layout
        menu.RouteTask.setVisibility(View.VISIBLE);

        // Change the text of the menu
        menu.AutoText.setText(getResources().getString(R.string.exposicao));
        menu.AutoText.setEnabled(false);

        // Sets the destiny global variable location
        destiny = EXPOSICAO;

        // Calls the functions to draw the route
        getRouteByLocations(new LatLng(mLastLocation.getLatitude(),
                mLastLocation.getLongitude()), destiny);
    }

    @Override
    public void onBackPressed() {
        // Shows a custom fragment
        DialogFragment dialog = new ExitFragment();
        dialog.show(getSupportFragmentManager(), "");
    }

    /*
         Methods used by the memu object
         */
    // onConclude method is called
    public void onConclude(){
        // Change Conclude Button State
        menu.Conclude.setEnabled(false);
        menu.Conclude.setBackground(ContextCompat.getDrawable(this,
                R.drawable.conclude_button_back_dasable));

        // Change Cancel button state
        menu.cancel.setEnabled(true);
        menu.cancel.setBackground(ContextCompat.getDrawable(this,
                R.drawable.cancel_button_back));

        // Changes the AutoText to disable
        menu.AutoText.setEnabled(false);

        // Request the location related with the user input
        getLocationByCityName(menu.AutoText.getText().toString());

        // Defines the dest_city variable
        desti_city = menu.AutoText.getText().toString();
    }

    // onConclude method is called
    public void onCancel(){
        menu.AutoText.setText("");
        menu.isVisible = false;

        // Changes the fragment mode
        menu.RouteTask.setVisibility(View.GONE);
        menu.cancel.setEnabled(false);
        menu.cancel.setBackground(ContextCompat.getDrawable(this,
                R.drawable.cancel_button_back_desable));

        menu.Conclude.setEnabled(true);
        menu.Conclude.setBackground(ContextCompat.getDrawable(this,
                R.drawable.conclude_button_back));

        // Changes the AutoText to enable
        menu.AutoText.setEnabled(true);

        // Clean the map from previous markers
        onDestinyCanceled();

        // Defines the desti_city to an empty string
        desti_city = "";
    }

    // getLocationByCityName in locationiq api
    public void getLocationByCityName(String city){
        String url;
        url = "https://us1.locationiq.com/v1/search.php?key=%s&q=%s&format=json&limit=1&accept-language=pt-br";
        String key = "LOCATION_IQ_API_KEY";
        String query;

        // Concatenate these values
        query = String.format(url, key, city);

        RequestQueue request = Volley.newRequestQueue(this);

        // Creates a request object in order to request a LatLng by city name
        StringRequest stringRequest = new StringRequest(Request.Method.GET, query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Requests the destiny location
                        destiny = HandleRequests.getCordenatesByRequest(response);

                        // Request the route geometry
                        getRouteByLocations(new LatLng(mLastLocation.getLatitude(),
                                mLastLocation.getLongitude()), destiny);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Adiciona um AlertDialog caso não obtenha a localização da cidade
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(MapsActivity.this);

                // Atributos do AlertDialog
                builder.setMessage(R.string.CityError);
                builder.setPositiveButton(R.string.AlertOkButton,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                onCancel();
                            }
                        });

                AlertDialog alert = builder.create();

                // Mostra o AlertDialog
                alert.show();
            }
        });

        request.add(stringRequest);
    }

    // Formats the double value
    private String formatDouble(double num){
        DecimalFormatSymbols formatter = DecimalFormatSymbols.getInstance();

        formatter.setDecimalSeparator('.');

        return new DecimalFormat("0.000000000", formatter).format(num);
    }

    // Requests the route in openrouteservice api
    @SuppressLint("DefaultLocale")
    private void getRouteByLocations(LatLng start, LatLng end){
        // Variables needed to make the url string
        String url, key = "OPENROUTESERVICE_API_KEY", query;
        url = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=%s&start=%s,%s&end=%s,%s";

        // Concatenates the values in order to make the request
        query = String.format(url, key, formatDouble(start.longitude), formatDouble(start.latitude),
                formatDouble(end.longitude), formatDouble(end.latitude));

        RequestQueue request = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Gets an ArrayList to represent the route
                        ArrayList<LatLng> route = HandleRequests.getRouteByRequest(response);

                        // Draw the route geometry into the map
                        drawRoute(route);

                        // Adds the marker and zoom the route
                        onDestinyRequested(destiny, route);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Adiciona um AlertDialog caso não obtenha a localização da cidade
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(MapsActivity.this);

                // Atributos do AlertDialog
                builder.setMessage(R.string.RouteError);
                builder.setPositiveButton(R.string.AlertOkButton,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                onCancel();
                            }
                        });

                AlertDialog alert = builder.create();

                // Mostra o AlertDialog
                alert.show();
            }
        });

        request.add(stringRequest);
    }

    private void drawRoute(ArrayList<LatLng> route){
        // Polyline options used to draw the route
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.clickable(false);

        // Adds the dots to the route
        for (LatLng dots : route){
            polylineOptions.add(dots);
        }

        // Final polyline in google maps
        Polyline polyline = mMap.addPolyline(polylineOptions);
        polyline.setTag("A");
        polyline.setColor(R.color.route);
    }

    // This function executes when the user gives a destination
    private void onDestinyRequested(LatLng destiny, ArrayList<LatLng> route){
        // Defines a latlng for the starting point
        LatLng location = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        // Defines the bounds builder
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(location);
        builder.include(destiny);

        // Includes the dots to the route
        for (LatLng dots : route){
            builder.include(dots);
        }

        // Define the bounds
        LatLngBounds bounds = builder.build();

        // Adds the marker a zoom the route out
        mMap.addMarker(new MarkerOptions().position(destiny))
                .setTitle(getResources().getString(R.string.destino));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    // This functions executes when the user cancel his route previously requested
    private void onDestinyCanceled(){
        // Clean the map for previous changes
        mMap.clear();

        // GetDeviceLocation
        getDeviceLocation();

        // Init the markers again
        initConstatantMarkers();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setCompassEnabled(false);

        getLocationPermission();

        createLocationRequest();

        getDeviceLocation();

        startLocationUpdates();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (sugest.getVisible())
                    sugest.setVisibility(false);
            }
        });

        // Sets onMarkerClicker listener to the map
        mMap.setOnMarkerClickListener(this);

        // Init the constant markers
        initConstatantMarkers();
    }

    private void getLocationPermission(){
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationPermissionGranted = true;
        }
        else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults){
        locationPermissionGranted = false;

        switch (requestCode){
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 &&
                                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
                break;
        }
    }


    private void startLocationUpdates(){
        LocationCallback locationCallback = new LocationCallback();
        mFused.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void createLocationRequest(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task locationResult = mFused.getLastLocation();
                locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastLocation = location;
                            Marker l = mMap.addMarker(new MarkerOptions().position(
                                    new LatLng(mLastLocation.getLatitude(),
                                            mLastLocation.getLongitude()))
                                    .title(getResources().getString(R.string.atual)));
                            l.setTag(3);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastLocation.getLatitude(),
                                            mLastLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            // Adiciona um AlertDialog caso não obtenha a localização
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(MapsActivity.this);

                            // Atributos do AlertDialog
                            builder.setMessage(R.string.sem_localizacao);
                            builder.setPositiveButton(R.string.AlertOkButton,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });

                            AlertDialog alert = builder.create();

                            // Mostra o AlertDialog
                            alert.show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
