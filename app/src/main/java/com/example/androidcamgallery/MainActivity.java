package com.example.androidcamgallery;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidcamgallery.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int GALLERY_REQUEST_CODE = 1002;
    private static final int PERMISSION_CODE_GALLERY = 1003;
    Uri image_String;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.cam.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String []parem = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(parem, PERMISSION_CODE);
                } else {
                    openCam();
                }
            } else {
                openCam();
            }
        });

        binding.gallery.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String []parem = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(parem, PERMISSION_CODE_GALLERY);
                } else {
                    openGallery();
                }
            } else {
                openGallery();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCam();
            } else {
//                Tost
            }
        } else if(requestCode == PERMISSION_CODE_GALLERY) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    public void openCam() {
        ContentValues value = new ContentValues();
        value.put(MediaStore.Images.Media.TITLE, "IMG");
        image_String = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, IMAGE_CAPTURE_CODE);
        }
    }

    public void openGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK ) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    binding.imageView.setImageURI(selectedImage);
                    break;
                case IMAGE_CAPTURE_CODE:
                    assert data != null;
                    Bundle extra = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extra.get("data");
                    binding.imageView.setImageBitmap(imageBitmap);
                    break;
                default:

            }
//            Glide.with(this).load(imageBitmap).into(binding.imageView);
        }
    }
}
