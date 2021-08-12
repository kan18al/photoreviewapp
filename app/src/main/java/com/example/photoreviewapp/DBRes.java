package com.example.photoreviewapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoreviewapp.Model.Photo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DBRes {
    private DatabaseReference mDatabase;
    private ArrayList<Photo> photos = new ArrayList<Photo>();
    private RecyclerView recyclerView;
    private GoogleMap mMap;
    private Context context;

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public DBRes() {
        mDatabase = FirebaseDatabase.getInstance().getReference("photoItems");
        mDatabase.addValueEventListener(postListener);
    }

    public DBRes(GoogleMap mMap, Context context) {
        this.context = context;
        this.mMap = mMap;
        mDatabase = FirebaseDatabase.getInstance().getReference("photoItems");
        mDatabase.addValueEventListener(postListener);
    }

    public DBRes(RecyclerView recyclerView) {

        mDatabase = FirebaseDatabase.getInstance().getReference("photoItems");
        mDatabase.addValueEventListener(postListener);
        this.recyclerView = recyclerView;
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot ds:dataSnapshot.getChildren()) {
                Photo photo = ds.getValue(Photo.class);
                photos.add(photo);
                if (recyclerView != null) {
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
                if (mMap != null) {
                    Bitmap bitmap = PhotoAdapter.StringToBitMap(photo.photo);

//                    GroundOverlayOptions newarkMap = new GroundOverlayOptions()
//                            .image(BitmapDescriptorFactory.fromBitmap(bitmap))
//                            .position(new LatLng(photo.latitude, photo.longitude), 80f, 80f);
//                    mMap.addGroundOverlay(newarkMap);

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(photo.latitude, photo.longitude))
                            .title(photo.comment)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    );
                }
            }
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", error.toException());
        }
    };
}
