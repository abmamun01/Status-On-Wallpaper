package com.mamunsproject.status_onwallpaper_api.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.mamunsproject.status_onwallpaper_api.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE;

public class SetWallpaperActivity extends AppCompatActivity {

    Intent intent;
    ImageView imageView;
    RelativeLayout editImage,downloadImage,shareImage;
    Button setBtn;
    ProgressBar progressBar;
    LottieAnimationView loadingAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_wallpaper);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(SetWallpaperActivity.this);
       


        setBtn = findViewById(R.id.set);
        imageView = findViewById(R.id.finalImage);
        editImage = findViewById(R.id.editImage);
        progressBar = findViewById(R.id.progressbar);
        loadingAnimation=findViewById(R.id.animationView);
        downloadImage=findViewById(R.id.downloadImage);
        shareImage=findViewById(R.id.shareImage );

        intent = getIntent();

        String url = intent.getStringExtra("image");
        Glide.with(SetWallpaperActivity.this).load(url).into(imageView);

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                try {
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    wallpaperManager.setBitmap(bitmap);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SetWallpaperActivity.this, "Successfully Set!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(SetWallpaperActivity.this, "Exception" + e, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(intent.getStringExtra("image"));
                Intent intent = new Intent(SetWallpaperActivity.this, DsPhotoEditorActivity.class);
                intent.setData(uri);
                intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Images");
                startActivityForResult(intent, 101);
            }
        });
        
        
        downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //    setBackground();
                if (checkPermission()) {
                    new Downloading().execute(url);
                }
                
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        assert data != null;
        Uri uri = data.getData();
        if (requestCode == 101) {
            imageView.setImageURI(uri);
            Toast.makeText(this, "Photo Saved!", Toast.LENGTH_SHORT).show();
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            imageView.getLayoutParams().height = 1200;

        }
    }



    public class Downloading extends AsyncTask<String, Integer, String> {

        @Override
        public void onPreExecute() {
            super .onPreExecute();
            loadingAnimation.setVisibility(View.VISIBLE);


        }

        @Override
        protected String doInBackground(String... url) {
            File mydir = new File(Environment.getExternalStorageDirectory() + "/11zon");
            if (!mydir.exists()) {
                mydir.mkdirs();
            }

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(url[0].trim());
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            SimpleDateFormat dateFormat = new SimpleDateFormat("mmddyyyyhhmmss");
            String date = dateFormat.format(new Date());

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle("Downloading")
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,"parag.jpeg");


            manager.enqueue(request);
            return mydir.getAbsolutePath() + File.separator + date + ".jpg";
        }

        @Override
        public void onPostExecute(String s) {
            super .onPostExecute(s);
            loadingAnimation.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
        }
    }

    //runtime storage permission
    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_WRITE);
            return false;
        }
        return true;
    }

}