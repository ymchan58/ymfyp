package com.example.ymchan.ymfyp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

/**
 * Created by yan min on 14/2/2019
 */
public class LocationFragment extends Fragment implements PermissionsListener, OnMapReadyCallback{

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final String TAG = "ymfyp.LocationFragment";

    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";

    private MapView mapView;
    private MapboxMap mMapboxMap;
    PermissionsManager permissionsManager;

    private String mMBAccessToken = "pk.eyJ1IjoieW1jaGFuIiwiYSI6ImNqcjZqdTEwMDA5NzgzenBkaHFpYnFuM2MifQ.BrIwReNcmIXTsHUU823Pew";

    private CarmenFeature home;
    private CarmenFeature work;
    private Marker featureMarker;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";

    private View mView;
    private TextView mSelectedLocationTextView;
    private Button mConfirmLocationBtn;

    private LocationListener mLocationListener;

    public LocationFragment() {
        // Required empty public constructor
    }

    public static LocationFragment show(@NonNull FragmentActivity fragActivity) {
        return show(fragActivity);
    }

    public interface LocationListener {
        void onLocationSelected(String textLocation);
    }

    public void setLocationListener(LocationListener locationListener) {
        mLocationListener = locationListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(getContext(), mMBAccessToken);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_location, container, false);

        // This contains the MapView in XML and needs to be called after the access token is configured.
//        setContentView(R.layout.activity_main);
        mSelectedLocationTextView = mView.findViewById(R.id.selected_location_info_textview);
        mSelectedLocationTextView.setText("Location");

        mConfirmLocationBtn = mView.findViewById(R.id.confirm_location_btn);

        mapView = mView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mConfirmLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                String inputText = mSelectedLocationTextView.getText().toString();
                Log.d(TAG, "mLocationListener = " + mLocationListener);
                Log.d(TAG, "inputText = " + inputText);
                if(mLocationListener != null){
                    Log.d(TAG, "mLocationLisenter success ***************");
//                    Log.d(TAG, "inputText = " + inputText);
                    mLocationListener.onLocationSelected(inputText);
                }
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mMapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                initSearchFab();

                enableLocationComponent();
                addUserLocations();

                // Add the symbol layer icon to map for future use
                style.addImage(symbolIconId, BitmapFactory.decodeResource(
                        getActivity().getResources(), R.drawable.blue_marker_view));

                // Create an empty GeoJSON source using the empty feature collection
                setUpSource(style);

                // Set up a new symbol layer for displaying the searched location's feature coordinates
                setupLayer(style);

                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        Log.d(TAG, "onMapClick");

                        mMapboxMap.clear();

                        MarkerOptions markerOptions = new MarkerOptions().position(point);

                        Style style = mMapboxMap.getStyle();
                        Log.d(TAG, "onMapClick style = " + style);
//
                        // Use the map camera target's coordinates to make a reverse geocoding search
                        reverseGeocode(style, Point.fromLngLat(point.getLongitude(), point.getLatitude()));

                        featureMarker = mMapboxMap.addMarker(markerOptions);


                        return true;
                    }
                });
                Toast.makeText(getActivity(),
                        getString(R.string.click_on_map_instruction), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    public boolean onMapClick(@NonNull LatLng point) {
//        Log.d(TAG, "onMapClick");
//
//        mMapboxMap.clear();
//
//        MarkerOptions markerOptions = new MarkerOptions().position(point);
//
//        Style style = mMapboxMap.getStyle();
//        Log.d(TAG, "onMapClick style = " + style);
////
//        // Use the map camera target's coordinates to make a reverse geocoding search
//        reverseGeocode(style, Point.fromLngLat(point.getLongitude(), point.getLatitude()));
//
//        featureMarker = mMapboxMap.addMarker(markerOptions);
//
//
//        return true;
//    }

    private void initSearchFab() {
        mView.findViewById(R.id.fab_location_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken())
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .addInjectedFeature(home)
                                .addInjectedFeature(work)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(getActivity());
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });
    }

    private void addUserLocations() {
        home = CarmenFeature.builder().text("Mapbox SF Office")
                .geometry(Point.fromLngLat(-122.399854, 37.7884400))
                .placeName("85 2nd St, San Francisco, CA")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work = CarmenFeature.builder().text("Mapbox DC Office")
                .placeName("740 15th Street NW, Washington DC")
                .geometry(Point.fromLngLat(-77.0338348, 38.899750))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent() {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

            // Get an instance of the component
            LocationComponent locationComponent = mMapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(getContext(), mMapboxMap.getStyle());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {

            permissionsManager = new PermissionsManager(this);



            permissionsManager.requestLocationPermissions(getActivity());

        }
    }

    /**
     * This method is used to reverse geocode where the user has dropped the marker.
     *
     * @param style style
     * @param point The location to use for the search
     */

    private void reverseGeocode(@NonNull final Style style, final Point point) {
        Log.d(TAG, "reverseGeocode run");
        try {
            Log.d(TAG, "reverseGeocode try");
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(mMBAccessToken)
                    .query(Point.fromLngLat(point.longitude(), point.latitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                    Log.d(TAG, "callback onResponse");

                    List<CarmenFeature> results = response.body().features();
                    if (results.size() > 0) {
                        CarmenFeature feature = results.get(0);
                        Log.d(TAG, "results feature = " + feature);

                        Toast.makeText(getContext(),
                                String.format(getString(R.string.location_picker_place_name_result),
                                        feature.text()), Toast.LENGTH_SHORT).show();

                        mSelectedLocationTextView.setText(feature.text());

//                        // If the geocoder returns a result, we take the first in the list and show a Toast with the place name.
//                        if (style.isFullyLoaded() && style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
//                            Log.d(TAG, "location = " + String.format(getString(R.string.location_picker_place_name_result),
//                                    feature.placeName()));
//                            Toast.makeText(MainActivity.this,
//                                    String.format(getString(R.string.location_picker_place_name_result),
//                                            feature.placeName()), Toast.LENGTH_SHORT).show();
//                        }

                    } else {
                        Log.d(TAG, getString(R.string.location_picker_dropped_marker_snippet_no_results));
                        Toast.makeText(getContext(),
                                getString(R.string.location_picker_dropped_marker_snippet_no_results), Toast.LENGTH_SHORT).show();

                        mSelectedLocationTextView.setText(getString(R.string.location_picker_dropped_marker_snippet_no_results));
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                    Log.e(TAG, "Geocoding Failure: " + throwable.getMessage());
                }
            });
        } catch (ServicesException servicesException) {
            Log.e(TAG, "Error geocoding: " + servicesException.toString());
            servicesException.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (mMapboxMap != null) {
                Style style = mMapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    // Move map camera to the selected location
                    mMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                }
            }

            // Set the TextView text to the entire CarmenFeature. The CarmenFeature
            // also be parsed through to grab and display certain information such as
            // its placeName, text, or coordinates.
            mSelectedLocationTextView.setText(selectedCarmenFeature.text());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    //override methods for PermissionsListener

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LocationListener) {
            mLocationListener = (LocationListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLocationListener = null;
    }

}
