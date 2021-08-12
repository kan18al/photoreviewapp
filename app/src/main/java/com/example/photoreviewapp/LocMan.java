package com.example.photoreviewapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;

public class LocMan implements LocationListener {

    private LocationManager locationManager;
    private Location location;

    public Location getLocation() {
        return location;
    }

    public LocMan(Activity activity) {
        this.locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(intent);
        }
        this.location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;
    }
}
