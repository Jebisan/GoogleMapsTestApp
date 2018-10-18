package rubenbaskaran.com.geofencingtestapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener
{
    private Context context;

    public MyLocationListener(Context context)
    {
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.e("Ruben - Location output", "Log: " + String.valueOf(location.getLongitude()) + ", Lat: " + String.valueOf(location.getLatitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.e("Ruben - GPS", "Status changed");
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.e("Ruben - GPS", "Enabled");
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.e("Ruben - GPS", "Disabled");
    }
}
