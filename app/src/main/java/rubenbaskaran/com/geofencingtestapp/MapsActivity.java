package rubenbaskaran.com.geofencingtestapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
    //region Properties
    GoogleMap googleMap;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        VerifyPermissions();
    }

    private void StartApplication()
    {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

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
        else
        {
            StartApplication();
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
                    return;
                }
            }

            StartApplication();
        }
    }
    //endregion

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
    public void onMapReady(GoogleMap _googleMap)
    {
        googleMap = _googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng copenhagen = new LatLng(55.67, 12.52);
        googleMap.addMarker(new MarkerOptions().position(copenhagen)
                .title("Marker in Copenhagen")
                .snippet("Capital of Denmark")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ruben_baskaran_billede_1)));

        LatLng sydney = new LatLng(-33.84, 150.65);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney")
                .snippet("Biggest city in Australia")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ruben_baskaran_billede_2)));

        googleMap.addCircle(new CircleOptions()
                .center(copenhagen)
                .radius(2000)
                .strokeColor(Color.RED)
                .fillColor(Color.RED));

        googleMap.addPolyline(new PolylineOptions()
                .add(copenhagen, sydney)
                .width(25)
                .color(Color.BLUE)
                .geodesic(false));

        // Move camera to Sydney
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(copenhagen));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(copenhagen, 12.0f));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                Toast.makeText(getApplicationContext(), "You clicked on lat: " + String.format("%.2f", latLng.latitude) + ", lng: " + String.format("%.2f", latLng.longitude), Toast.LENGTH_SHORT).show();
            }
        });

        GetAndShowMyLocation();
    }

    //region GetAndShowMyLocation using LocationListener
    @SuppressLint("MissingPermission")
    private void GetAndShowMyLocation()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                //googleMap.clear();
                Log.e("Location", "Lng: " + String.valueOf(location.getLongitude()) + ", Lat: " + String.valueOf(location.getLatitude()));

                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Me").snippet("My current location"));

                Toast.makeText(getApplicationContext(), "Found current location", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
                Log.e("GPS", "Status changed");
            }

            @Override
            public void onProviderEnabled(String provider)
            {
                Log.e("GPS", "Enabled");
            }

            @Override
            public void onProviderDisabled(String provider)
            {
                Log.e("GPS", "Disabled");
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }
    //endregion
}