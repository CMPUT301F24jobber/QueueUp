package com.example.queueup.services;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.concurrent.TimeUnit;

public class LocationService {
    private static final String TAG = "LocationService";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final long MIN_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(1);
    private static final float MIN_DISTANCE_UPDATE = 10f;

    private final LocationManager locationManager;
    private final Activity activity;
    private final Context context;
    private LocationCallback locationCallback;

    public interface LocationCallback {
        void onLocationReceived(Location location);
        void onLocationError(String error);
    }

    public LocationService(@NonNull Context context,
                           @NonNull Activity activity,
                           @NonNull LocationManager locationManager) {
        this.activity = activity;
        this.context = context;
        this.locationManager = locationManager;
    }

    public void setLocationCallback(LocationCallback callback) {
        this.locationCallback = callback;
    }

    public void handleLocationPermissions() {
        if (hasLocationPermissions()) {
            Log.d(TAG, "Location permissions already granted");
            checkLocationServices();
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            showPermissionRationaleDialog();
        } else {
            requestLocationPermissions();
        }
    }

    private boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void showPermissionRationaleDialog() {
        new AlertDialog.Builder(activity)
                .setTitle("Permission Required")
                .setMessage("Location permission is needed to provide location-based services")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    requestLocationPermissions();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Log.d(TAG, "Location permission denied by user");
                    if (locationCallback != null) {
                        locationCallback.onLocationError("Location permission denied");
                    }
                    dialog.dismiss();
                })
                .show();
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    private void checkLocationServices() {
        if (!isLocationEnabled()) {
            showLocationSettingsDialog();
        }
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void getLocation() {
        if (!hasLocationPermissions()) {
            if (locationCallback != null) {
                locationCallback.onLocationError("Location permissions not granted");
            }
            return;
        }

        if (!isLocationEnabled()) {
            if (locationCallback != null) {
                locationCallback.onLocationError("Location services disabled");
            }
            return;
        }

        try {
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locationManager.removeUpdates(this);
                    if (locationCallback != null) {
                        locationCallback.onLocationReceived(location);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
            };

            // Try GPS first, then network provider
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_UPDATE_INTERVAL,
                    MIN_DISTANCE_UPDATE,
                    locationListener
            );

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_UPDATE_INTERVAL,
                    MIN_DISTANCE_UPDATE,
                    locationListener
            );

            // Check last known location while waiting for updates
            Location lastLocation = getBestLastKnownLocation();
            if (lastLocation != null && locationCallback != null) {
                locationCallback.onLocationReceived(lastLocation);
            }

        } catch (SecurityException e) {
            Log.e(TAG, "Security exception when requesting location updates", e);
            if (locationCallback != null) {
                locationCallback.onLocationError("Security exception: " + e.getMessage());
            }
        }
    }

    private Location getBestLastKnownLocation() {
        try {
            Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (gpsLocation != null && networkLocation != null) {
                return gpsLocation.getTime() > networkLocation.getTime() ? gpsLocation : networkLocation;
            }
            return gpsLocation != null ? gpsLocation : networkLocation;
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception when getting last location", e);
            return null;
        }
    }

    private void showLocationSettingsDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Location Services Required")
                .setMessage("Please enable location services to continue")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}