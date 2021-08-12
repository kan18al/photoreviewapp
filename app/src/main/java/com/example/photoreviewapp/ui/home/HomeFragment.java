package com.example.photoreviewapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.photoreviewapp.DBRes;
import com.example.photoreviewapp.LocMan;
import com.example.photoreviewapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocMan locMan;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        locMan = new LocMan(getActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        DBRes dbRes = new DBRes(mMap, getContext());

        LatLng i = new LatLng(locMan.getLocation().getLatitude(), locMan.getLocation().getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(i)
                .title("I'm here"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(i));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
    }
}