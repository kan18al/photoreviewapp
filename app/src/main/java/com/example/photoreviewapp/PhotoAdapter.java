package com.example.photoreviewapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoreviewapp.Model.Photo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<Photo> photos;
    private Float result;

    public PhotoAdapter(Context context, List<Photo> photos) {
        this.inflater = LayoutInflater.from(context);
        this.photos = photos;
    }

    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.photo_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoAdapter.ViewHolder holder, int position) {
        Photo photo = photos.get(position);
        holder.imageView.setImageBitmap(StringToBitMap(photo.photo));
        holder.tvDate.setText(photo.currentDate.toString());
        holder.tvComment.setText(photo.comment);
        holder.tvLocate.setText("Longitude:   "+photo.longitude+"   Latitide:  "+photo.latitude);
        Picasso.get()
                .load(photo.avaUserUri)
                .into(holder.ivAvaMini);
        holder.tvEmailUserPublic.setText(photo.EmailUser);

        FirebaseDatabase.getInstance().getReference().child("ratings").child(photo.id).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        result = 0f;
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        for (DataSnapshot data:task.getResult().getChildren()) {
                            result += data.getValue(Float.class);
                        }
                        result /= task.getResult().getChildrenCount();
                        holder.ratingBar.setRating(result);
                    }
                });

        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Map<String, Object> childUpdates = new HashMap<>();

                    childUpdates.put("/ratings/" + photo.id + "/" + userId, rating);
                    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
                } else {
                    Toast.makeText(this.inflater.getContext(), "You are not logged in",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView, ivAvaMini;
        final TextView tvComment, tvDate, tvLocate, tvEmailUserPublic;
        final RatingBar ratingBar;
        ViewHolder(View view){
            super(view);
            imageView = view.findViewById(R.id.imageView);
            tvDate = view.findViewById(R.id.tvDate);
            tvComment = view.findViewById(R.id.tvComment);
            tvLocate = view.findViewById(R.id.tvLocate);
            ivAvaMini = view.findViewById(R.id.ivAvaMini);
            tvEmailUserPublic = view.findViewById(R.id.tvEmailUserPublic);
            ratingBar = view.findViewById(R.id.ratingBar);
        }

    }

    public static Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.URL_SAFE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
