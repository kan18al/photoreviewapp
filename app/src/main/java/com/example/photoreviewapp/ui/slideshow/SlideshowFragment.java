package com.example.photoreviewapp.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoreviewapp.DBRes;
import com.example.photoreviewapp.Model.Photo;
import com.example.photoreviewapp.PhotoAdapter;
import com.example.photoreviewapp.R;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {
    private RecyclerView recyclerView;
//    private DatabaseReference mDatabase;
    private PhotoAdapter adapter;
    private ArrayList<Photo> photos = new ArrayList<Photo>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
//        mDatabase = FirebaseDatabase.getInstance().getReference("photoItems");
//        mDatabase.addValueEventListener(postListener);
        recyclerView = root.findViewById(R.id.list);

        photos = new DBRes(recyclerView).getPhotos();

        adapter = new PhotoAdapter(getContext(), photos);
        recyclerView.setAdapter(adapter);

        return root;
    }
//
//    ValueEventListener postListener = new ValueEventListener() {
//        @Override
//        public void onDataChange(DataSnapshot dataSnapshot) {
//            for (DataSnapshot ds:dataSnapshot.getChildren()) {
//                Photo photo = ds.getValue(Photo.class);
//                photos.add(photo);
//            }
//            recyclerView.getAdapter().notifyDataSetChanged();
//        }
//
//        @Override
//        public void onCancelled(@NonNull @NotNull DatabaseError error) {
//            // Getting Post failed, log a message
//            Log.w(TAG, "loadPost:onCancelled", error.toException());
//        }
//    };
}