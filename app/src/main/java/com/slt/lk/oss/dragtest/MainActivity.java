package com.slt.lk.oss.dragtest;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.easystudio.rotateimageview.RotateZoomImageView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    RelativeLayout playground;
    View currentView;
    boolean routericon;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

       // Spinner typeSpinner = findViewById(R.id.roomTypeSpinner);
        playground = findViewById(R.id.draglayout);
        routericon = false;

        Runnable savepic = new Runnable() {
            @Override
            public void run() {
                try {
                    byte[]    byteArray = prepherImage();
                    boolean process = true;
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "Wi-Finder_" + timeStamp ;
                    File photo = new File(Environment.getExternalStorageDirectory(), imageFileName+".jpg");

                    if (photo.exists()) {
                        photo.delete();
                    }

                    try {
                        FileOutputStream fos = new FileOutputStream(photo.getPath());
                        fos.write(byteArray);
                        fos.close();
                    } catch (java.io.IOException e) {
                        Log.e("PictureDemo", "Exception in photoCallback", e);
                        process = false;
                    }
                    if(process){
                        Toast.makeText(context, "Error in Saving Image ",Toast.LENGTH_LONG);
                    }else{
                        Toast.makeText(context, "Image Saved Successfully",Toast.LENGTH_LONG);
                    }

                } finally {

                }
            }
        };

//        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String selected = adapterView.getSelectedItem().toString();
//                if(selected.equals("Living Room")){
//                    playground.addView(createNewImageView(R.drawable.livingroom));
//                }
//                if(selected.equals("Bed Room")){
//                    playground.addView(createNewImageView(R.drawable.bedroom));
//                }
//
//                adapterView.setSelection(0);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//
//
//        });



        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.livingroom:
                                playground.addView(createNewImageView(R.drawable.r_living_room,"livingroom"));
                                return true;
                            case R.id.bedroom:
                                playground.addView(createNewImageView(R.drawable.r_bed_room,"bedroom"));
                                return true;
                            case R.id.diningroom:
                                playground.addView(createNewImageView(R.drawable.r_dining_room,"diningroom"));
                                return true;
                            case R.id.bathroom:
                                playground.addView(createNewImageView(R.drawable.r_bathroom,"bathroom"));
                                return true;
                            case R.id.officeroom:
                                playground.addView(createNewImageView(R.drawable.r_office_roon,"officeroom"));
                                return true;
                            case R.id.garage:
                                playground.addView(createNewImageView(R.drawable.r_garage,"garage"));
                                return true;
                            case R.id.kitchen:
                                playground.addView(createNewImageView(R.drawable.r_kitchen,"kitchen"));
                                return true;
                            case R.id.backyard:
                                playground.addView(createNewImageView(R.drawable.r_back_yard,"backyard"));
                                return true;
                            case R.id.lobby:
                                playground.addView(createNewImageView(R.drawable.r_lobby,"lobby"));
                                return true;
                            case R.id.yard:
                                playground.addView(createNewImageView(R.drawable.r_yard,"yard"));
                                return true;
                            case R.id.router:
                                routericon = true;
                                playground.addView(createNewImageView(R.drawable.router_ico,"router"));
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.object_menu);
                popup.show();
            }
        });


        findViewById(R.id.removeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(currentView != null){
                  if(currentView.getTag().equals("router")){
                      routericon = false;
                  }
                  ((ViewGroup) currentView.getParent()).removeView(currentView);
                  currentView = null;
              }
            }
        });

       /* findViewById(R.id.clearAllButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playground.removeAllViews();
                routericon = false;
            }
        });*/

        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the image from drawable resource as drawable object
                RelativeLayout  view = (RelativeLayout)findViewById(R.id.draglayout);
                findViewById(R.id.widthadd).setVisibility(View.INVISIBLE);
                findViewById(R.id.widdthminus).setVisibility(View.INVISIBLE);
                findViewById(R.id.heightminus).setVisibility(View.INVISIBLE);
                findViewById(R.id.heightadd).setVisibility(View.INVISIBLE);
                        view.setBackgroundResource(R.color.apppbground);
                        Bitmap b = screenShot(view);
                Drawable drawable = new BitmapDrawable(getResources(), b);

                // Get the bitmap from drawable object
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                // Save image to gallery
                String savedImageURL = MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        bitmap,
                        timeStamp,
                        "wifider_floor_plan"
                );

                Toast.makeText(getApplicationContext(),"Image saved to gallery.", Toast.LENGTH_SHORT).show();

                findViewById(R.id.widthadd).setVisibility(View.VISIBLE);
                findViewById(R.id.widdthminus).setVisibility(View.VISIBLE);
                findViewById(R.id.heightminus).setVisibility(View.VISIBLE);
                findViewById(R.id.heightadd).setVisibility(View.VISIBLE);

            }

        });


        findViewById(R.id.widthadd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentView != null){
                    if(!currentView.getTag().equals("router")){
                        currentView.getLayoutParams().width = currentView.getWidth()+20;
                        currentView.requestLayout();
                    }
                }
            }
        });


        findViewById(R.id.widdthminus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentView != null){
                    if(!currentView.getTag().equals("router")){
                        currentView.getLayoutParams().width = currentView.getWidth()-20;
                        currentView.requestLayout();
                    }
                }
            }
        });


        findViewById(R.id.heightadd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentView != null){
                    if(!currentView.getTag().equals("router")){
                        currentView.getLayoutParams().height = currentView.getHeight()+20;
                        currentView.requestLayout();
                    }
                }
            }
        });


        findViewById(R.id.heightminus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentView != null){
                    if(!currentView.getTag().equals("router")){
                        currentView.getLayoutParams().height = currentView.getHeight()-20;
                        currentView.requestLayout();
                    }
                }
            }
        });


        findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(routericon){
                    byte[]    byteArray = prepherImage();
                    Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                    intent.putExtra("BitmapImage", byteArray);
                    startActivity(intent);
                }else{
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Alert")
                            .setMessage("Please Place the Router on the Plan to Continue")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

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
        return image;
    }

    public  Bitmap viewToBitmap() {
        RelativeLayout  view = (RelativeLayout)findViewById(R.id.draglayout);
        view.setBackgroundResource(0);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public byte[] prepherImage(){
        findViewById(R.id.widdthminus).setVisibility(View.GONE);
        findViewById(R.id.widthadd).setVisibility(View.GONE);
        findViewById(R.id.heightadd).setVisibility(View.GONE);
        findViewById(R.id.heightminus).setVisibility(View.GONE);
        RelativeLayout  view = (RelativeLayout)findViewById(R.id.draglayout);
        view.setBackgroundResource(0);
        Bitmap b = screenShot(view);
        view.setBackgroundResource(R.drawable.bg_boarder);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        findViewById(R.id.widdthminus).setVisibility(View.VISIBLE);
        findViewById(R.id.widthadd).setVisibility(View.VISIBLE);
        findViewById(R.id.heightadd).setVisibility(View.VISIBLE);
        findViewById(R.id.heightminus).setVisibility(View.VISIBLE);

        return byteArray;
    }

    private RotateZoomImageView createNewImageView(int dr,String tag) {
        final  RotateZoomImageView iv = new RotateZoomImageView(getApplicationContext());
        iv.setPadding(3,3,3,3);
        iv.setImageDrawable(getDrawable(dr));
        RelativeLayout.LayoutParams lp;
        if(tag.equals("router")){
            lp = new RelativeLayout.LayoutParams(150, 150);
        }else {
            lp = new RelativeLayout.LayoutParams(350, 350);
            iv.setScaleType(RotateZoomImageView.ScaleType.FIT_XY);
        }
        lp.addRule(RelativeLayout.BELOW);
        iv.setLayoutParams(lp);
        iv.setTag(tag);

        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable highlight = getResources().getDrawable( R.drawable.highlight);
                iv.setBackground(highlight);
              //  iv.bringToFront();
                currentView = iv;
                if(event.getAction() == MotionEvent.ACTION_UP){
                    iv.setBackground(null);
                }
                return iv.onTouch(v,event);
            }
        });

//        iv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                Log.i("TAG" , "hasFocus "+hasFocus );
//                if(!hasFocus){
//                    iv.setBackground(null);
//                }
//            }
//        });

        return iv;
    }

}


