package uk.ac.tees.aad.b1475063;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import uk.ac.tees.aad.b1475063.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
//    private static final int REQUEST_LOCATION_PERMISSION =1 ;
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Marker marker;
    LatLng touchCoordinates;
    Button chooseLocation;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        chooseLocation = findViewById(R.id.saveLocationButton);



        /* Geocoder derives the location name, the specific country and city of the user as a list. */
//        Geocoder geocoder = new Geocoder(getApplicationContext());
        //            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
//            String adress = addresses.get(0).getLocality() + ":";
//            adress += addresses.get(0).getCountryName();

        /* The latitude and longitude is combined and placed on the google map using a marker in the following part. */

//            LatLng latLng = new LatLng(latitude, longitude);

        chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = touchCoordinates.latitude;
                double longitude = touchCoordinates.longitude;
                Geocoder geocoder = new Geocoder(getApplicationContext());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    Log.d("Maps: ","could not find location based on coordinates.");
                    e.printStackTrace();
                }
                String address = addresses.get(0).getLocality();
                if(address==""){
                    address+="???";
                }
                Log.d("the chosen location: ","="+address);
                MainActivity.chosenCustomLocation = address;
                finish();
            }
        });

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
    protected void onStop() {
        super.onStop();
        Log.d("Maps: ","maps activity is completed properly.");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mMap.setOnMapClickListener(this);

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("touch coordinate from the map: ", latLng.toString());
        touchCoordinates = latLng;
        try {
            if (marker != null) {
                marker.remove();
            }
            marker = mMap.addMarker(new MarkerOptions().position(touchCoordinates).title("location ???"));
            mMap.setMaxZoomPreference(10);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(touchCoordinates, 10.0f));
        }catch(Exception e){
            Log.e("maps marker: ","marker problem");
        }
    }
}
