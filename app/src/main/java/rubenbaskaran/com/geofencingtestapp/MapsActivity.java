package rubenbaskaran.com.geofencingtestapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        VerifyPermissions();

        //GetLocation();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    public void onMapReady(GoogleMap googleMap)
    {
        GoogleMap mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng hadsten = new LatLng(56.33, 10.05);
        mMap.addMarker(new MarkerOptions().position(hadsten)
                .title("Marker in Hadsten")
                .snippet("My first home")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ruben_baskaran_billede_1)));

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney")
                .snippet("My second home")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ruben_baskaran_billede_2)));

        mMap.addCircle(new CircleOptions()
                .center(hadsten)
                .radius(2000)
                .strokeColor(Color.RED)
                .fillColor(Color.RED));

        mMap.addPolyline(new PolylineOptions()
                .add(hadsten, sydney)
                .width(25)
                .color(Color.BLUE)
                .geodesic(false));

        // Move camera to Sydney
        mMap.moveCamera(CameraUpdateFactory.newLatLng(hadsten));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hadsten, 12.0f));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                Toast.makeText(getApplicationContext(), "You clicked on lat: " + String.format("%.2f", latLng.latitude) + ", lng: " + String.format("%.2f", latLng.longitude), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //region RequestLocationUpdates using LocationListener
    @SuppressLint("MissingPermission")
    private void GetLocation()
    {
        MyLocationListener myLocationListener = new MyLocationListener(this);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, myLocationListener);
    }
    //endregion

    //region Permissions
    private void VerifyPermissions()
    {
        int locationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (locationPermission != PackageManager.PERMISSION_GRANTED)
        {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            int callback_code = 123;
            ActivityCompat.requestPermissions(this, permissions, callback_code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 123)
        {
            for (int result : grantResults)
            {
                if (result != 0)
                {
                    finish();
                }
            }
        }
    }
    //endregion
}

