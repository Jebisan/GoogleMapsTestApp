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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    //region Properties
    GoogleMap googleMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        VerifyPermissions();
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

    private void StartApplication()
    {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

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

        AddMarkerOnMyLocation();
    }

    //region GPS implementation
    protected void AddMarkerOnMyLocation()
    {
        locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                if (locationResult == null)
                {
                    Toast.makeText(getApplicationContext(), "GPS data is null", Toast.LENGTH_LONG).show();
                }
                for (Location location : locationResult.getLocations())
                {
                    //googleMap.clear();
                    Log.e("Location", "Lng: " + String.valueOf(location.getLongitude()) + ", Lat: " + String.valueOf(location.getLatitude()));

                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Me").snippet("My current location"));

                    Toast.makeText(getApplicationContext(), "Found current location", Toast.LENGTH_SHORT).show();
                }
            }
        };

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>()
        {
            @Override
            @SuppressLint("MissingPermission")
            public void onSuccess(LocationSettingsResponse locationSettingsResponse)
            {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        });

        task.addOnFailureListener(this, new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(getApplicationContext(), "Turn on the GPS and try again", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
    //endregion

    //region Helper methods
    @SuppressLint("MissingPermission")
    private void CreateGpsStatusListener()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationListener GpsStatusListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
            }

            @Override
            public void onProviderEnabled(String provider)
            {
                Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider)
            {
                Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600000, 100000, GpsStatusListener);
    }
    //endregion
}