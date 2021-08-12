package com.example.photoreviewapp.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.photoreviewapp.CustomAdapter;
import com.example.photoreviewapp.LocMan;
import com.example.photoreviewapp.Model.Photo;
import com.example.photoreviewapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class PhotoMakeFragment extends Fragment {

    private FirebaseDatabase databaseFire;
    private DatabaseReference myRef;
    private DatabaseReference refForSpinner;
    private LocMan locMan;

    private Button buttonImage, buttonSend;
    private ImageView imageView;
    private EditText editText;
    private Bitmap newPhoto;
    private TextView tvLocation;
    private Spinner spinner;
    public Double latitude;
    public Double longitude;
    private FirebaseAuth mAuth;
    private FirebaseUser cUser;

    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        cUser = mAuth.getCurrentUser();


        locMan = new LocMan(getActivity());
        databaseFire = FirebaseDatabase.getInstance();
        myRef = databaseFire.getReference("photoItems");
        refForSpinner = databaseFire.getReference("tours");

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        buttonImage = root.findViewById(R.id.button);
        spinner = root.findViewById(R.id.spinner);
        imageView = root.findViewById(R.id.imageView2);
        editText = root.findViewById(R.id.etComment);
        buttonSend = root.findViewById(R.id.btnSend);
        tvLocation = root.findViewById(R.id.tvLocation);

        //заполняю спинер значениями с базы
        takeTours(this.getContext());

        tvLocation.setText("Longitude:   "+locMan.getLocation().getLongitude()+"   Latitude:  "+locMan.getLocation().getLatitude());

        buttonImage.setOnClickListener(v -> captureImage());
        buttonSend.setOnClickListener(v -> sendToDB());

        return root;
    }

    private void captureImage() {
        // Create an implicit intent, for image capture.
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Start camera and wait for the results.
        this.startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);
    }

    //эта функция нужна для заполнения спинера значениями туры с базы
    private void takeTours(Context ctx) {
        List<String> tours = new ArrayList<>();
        refForSpinner.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for (DataSnapshot ds:task.getResult().getChildren()) {
                    tours.add(ds.getKey());
                }
                CustomAdapter adapter = new CustomAdapter(ctx,
                        android.R.layout.simple_spinner_item, tours.toArray(new String[tours.size()]));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {
                        // Set adapter flag that something has been chosen
                        CustomAdapter.flag = true;
                    }
                });
            }
        });

    }

    private void sendToDB() {
        latitude = locMan.getLocation().getLatitude();
        longitude = locMan.getLocation().getLongitude();
        Date date = new Date();

        if (newPhoto != null && cUser != null) {
            if (cUser.getPhotoUrl().toString() != null) {
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                newPhoto.compress(Bitmap.CompressFormat.PNG, 100, bao);
                byte[] byteArray = bao.toByteArray();
                String imageB64 = Base64.encodeToString(byteArray, Base64.URL_SAFE);
                Photo photo = new Photo(UUID.randomUUID().toString(), imageB64, latitude, longitude, editText.getText().toString(), cUser.getEmail(), cUser.getPhotoUrl().toString(), date);
                myRef.push().setValue(photo, (error, ref) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Some error occured, try again", Toast.LENGTH_SHORT).show();
                    } else {
                        //добавляю данный обзор к туру в базе
                        refForSpinner.child(spinner.getSelectedItem().toString()).child("review").child(ref.getKey()).setValue(cUser.getUid());

                        Toast.makeText(getContext(), "added to database successfully", Toast.LENGTH_SHORT).show();
                        imageView.setImageBitmap(null);
                        newPhoto = null;
                    }
                });
            } else Toast.makeText(getContext(), "Need to upload an avatar", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    // When results returned
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                newPhoto = (Bitmap) data.getExtras().get("data");
                Bitmap bp = newPhoto;
                this.imageView.setImageBitmap(bp);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(), "Action canceled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Action Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
}