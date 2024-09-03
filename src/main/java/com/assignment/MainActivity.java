package com.assignment;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 123;
    private static final int CAMERA_REQUEST_CODE = 143;

    private static final int MY_GALLERY_REQUEST_CODE = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 2;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int REQUEST_CODE =100 ;

    private TextView textViewLocation;
    private RecyclerView recyclerViewUsers;
    private UserViewModel userViewModel;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    int result=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        textViewLocation = findViewById(R.id.textViewLocation);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);

        // Setup RecyclerView
        userAdapter = new UserAdapter(userList, user -> {
            // Handle image upload click
            showImagePickerDialog(user);
        });
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Observe user data
        userViewModel.getUsers().observe(this, users -> {
            if (users != null) {
                userList.clear();
                userList.addAll(users);
                userAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
    }

    private void fetchLocation() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String address = getAddressFromLocation(latitude, longitude);
                            textViewLocation.setText(String.format("Lat: %.4f, Lon: %.4f\n%s",
                                    latitude, longitude, address));
                        } else {
                            textViewLocation.setText("Unable to fetch location");
                        }
                    });
        }
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address not found";
    }

    private void showImagePickerDialog(User user) {
        // Implement dialog to choose between camera and gallery
        // Handle image selection and update user's avatar


        CoustomDialog optionDialog = new CoustomDialog(MainActivity.this);
        optionDialog.setCanceledOnTouchOutside(true);
        optionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        optionDialog.show();


        CoustomDialog.gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermission()) {

                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(pickPhoto, MY_GALLERY_REQUEST_CODE);//one can be replaced with any action code
                    optionDialog.dismiss();
                } else {
                    requestPermission();
                }


            }
        });
        CoustomDialog.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermission()) {

                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, MY_CAMERA_REQUEST_CODE);//zero can be replaced with any action code (called requestCode)
                    //main logic or main code

                    // . write your main code to execute, It will execute if the permission is already given.
                    optionDialog.dismiss();

                } else {
                    requestPermission();
                }

            }
        });


    }

   /* private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MY_GALLERY_REQUEST_CODE:
                    assert data != null;
                    Uri imageUri = data.getData();
                    saveImageToDatabase(imageUri);

                    // Handle the image URI and save it locally
                    break;
                case MY_CAMERA_REQUEST_CODE:
                    assert data != null;
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    Uri photoUri = saveBitmapToFile(imageBitmap);
                    saveImageToDatabase(photoUri);
                    // Handle the Bitmap and save it locally
                    break;
            }
        }
    }

    private Uri saveBitmapToFile(Bitmap bitmap) {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, "user_avatar_" + System.currentTimeMillis() + ".jpg");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(imageFile);
    }

    private void saveImageToDatabase(Uri imageUri) {
        if (imageUri != null) {

            //userViewModel.insertUser();
            // Assume we are updating a specific user
          /*  UserDao.
                    userViewModel.insertUser(user);
            if (user != null) {
                user.setLocalAvatarPath(imageUri.toString());

                //UserDao.updateUser(user);

            }*/
        }
    }


    private boolean checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          //  result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_MEDIA_IMAGES);
            result = ActivityCompat.checkSelfPermission(this, READ_MEDIA_IMAGES);

        } else {
            //result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
             result = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        }


        int result1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;


    }

    private void requestPermission() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat. requestPermissions(this,new String[]{CAMERA,READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }


    }
    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==REQUEST_CODE) {


            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }


        }else if(requestCode==PERMISSION_REQUEST_CODE){

                if (grantResults.length > 0) {
                    boolean permissionToCamera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToCamera && permissionToStore) {
                        // Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                    // main logic
                }


        }

    }


}

