package com.example.oitzh.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by oitzh on 25/11/2017.
 */

public class LocationMock {
    public static Place home = null;
    public static Place currentPlace = null;
    private FusedLocationProviderClient mFusedLocationClient;
    private MainActivity mainActivity;

    LocationMock(FusedLocationProviderClient mFusedLocationClient, MainActivity activity) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        this.mFusedLocationClient = mFusedLocationClient;
        this.mainActivity = activity;
        if (home == null) {
            // activity.createPlacePickerActivity();
        }
    }


    public void getLastLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            float[] distance = new float[2];
                            Location.distanceBetween(location.getLatitude(), location.getLongitude(), home.getLatLng().latitude, home.getLatLng().longitude, distance);
                            float distanceCurrentFromHome = distance[0];
                            if (Math.round(distanceCurrentFromHome) < 50) {
                                mainActivity.publishLocation(ScenarioInput.Trigger.Location.When_Arriving);
                            } else {
                                mainActivity.publishLocation(ScenarioInput.Trigger.Location.When_Leaving);
                            }
                            //location.distanceTo()
                            // Logic to handle location object
                        }
                    }
                });
    }
}
