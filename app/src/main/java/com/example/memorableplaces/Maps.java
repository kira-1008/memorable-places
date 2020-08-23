package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.memorableplaces.MainActivity.locations;
import static com.example.memorableplaces.MainActivity.places;

public class Maps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Geocoder geocoder;
    List<Address>addressList;
    Intent intent;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,1,locationListener);
                Location lastknownlocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                assert lastknownlocation != null;
                LatLng lklocation=new LatLng(lastknownlocation.getLatitude(),lastknownlocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lklocation,6));
                mMap.addMarker(new MarkerOptions().position(lklocation).title("Your location"));

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        intent=getIntent();


        if(intent.getIntExtra("place",0)==0)
        {locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            locationListener=new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mMap.clear();
                    LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation,6));
                    mMap.addMarker(new MarkerOptions().position(mylocation).title("Your location"));
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };




        }
        else
        { int index=intent.getIntExtra("place",0);
            LatLng locate=new LatLng(MainActivity.locations.get(index).latitude,MainActivity.locations.get(index).longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locate,6));
            mMap.addMarker(new MarkerOptions()
                    .position(locate).title(places.get(index)));

        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {



                geocoder=new Geocoder(getApplicationContext(),Locale.getDefault());
                try {
                    addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    Log.i("address",addressList.get(0).toString());
                    Toast.makeText(Maps.this, "Location Saved", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }mMap.addMarker(new MarkerOptions().position(latLng).title(addressList.get(0).getAddressLine(0)));
                places.add(addressList.get(0).getAddressLine(0));
                MainActivity.locations.add(latLng);
                MainActivity.arrayAdapter.notifyDataSetChanged();
            }
        });


        }





    }



