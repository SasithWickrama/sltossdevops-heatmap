package com.slt.lk.oss.dragtest;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 300;
    public String currentPhotoPath ;
    private ActivityResultLauncher<Intent> picActivityResultLauncher;
    private ActivityResultLauncher<Intent> pickImageResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        picActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                          //  Uri selectedImage = data.getData();


                           /* Bitmap bitmap = (Bitmap)data.getExtras()
                                    .get("data");
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();*/

                            File f = new File(currentPhotoPath);
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bmOptions);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            int options = 100;
                            while (stream.toByteArray().length / 1024 > 400) { // 900kb,
                                stream.reset();// baosbaos
                                bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);// options%baos
                                options -= 10;// 10
                            }
                            byte[] byteArray = stream.toByteArray();

                            Intent intent = new Intent(getApplicationContext(), PictureEditActivity.class);
                            intent.putExtra("BitmapImage", byteArray);
                            startActivity(intent);
                        }
                    }
                });


        pickImageResultLauncher =    registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Uri selectedImage = data.getData();
                            String[] filePathColumn = { MediaStore.Images.Media.DATA };

                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            cursor.close();

                            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();

                            Intent intent = new Intent(getApplicationContext(), PictureEditActivity.class);
                            intent.putExtra("BitmapImage", byteArray);
                            startActivity(intent);
                        }
                    }
                });




    }


    public void takePic(View v){
        if(checkPermission()){
            dispatchTakePictureIntent(picActivityResultLauncher);
        }else{
            requestPermission();
        }
    }

    public void selectPic(View v){
        if(checkPermission()){
            dispatchSelectImageIntent(pickImageResultLauncher);
        }else{
            requestPermission();
        }
    }

    public void selectSketch(View v){
        Intent intent = new Intent(SelectActivity.this, SketchActivity.class);
        startActivity(intent);
    }

    public void selectPlaner(View v){
        Intent intent = new Intent(SelectActivity.this, MainActivity.class);
        startActivity(intent);
    }


    public boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_WIFI_STATE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result1 == PackageManager.PERMISSION_GRANTED  &&  result2 == PackageManager.PERMISSION_GRANTED &&  result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED  && result5 == PackageManager.PERMISSION_GRANTED ;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_WIFI_STATE, ACCESS_FINE_LOCATION ,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE,CAMERA,ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    public void dispatchTakePictureIntent(ActivityResultLauncher<Intent> myresult) {
        Log.i("myapp" , "inside pic");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA) ) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.i("myapp" , "error"+ex.getMessage());
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.slt.lk.oss.heatmap",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                myresult.launch(takePictureIntent);
            }
        }else{
            Log.i("myapp" , "inside pic else part");
        }
    }


    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void dispatchSelectImageIntent(ActivityResultLauncher<Intent> myresult) {
        Log.i("myapp" , "inside pic");
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        myresult.launch(takePictureIntent);
    }

}
