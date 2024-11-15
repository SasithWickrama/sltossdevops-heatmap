package com.slt.lk.oss.dragtest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.easystudio.rotateimageview.RotateZoomImageView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureEditActivity extends AppCompatActivity {
    RelativeLayout playground;
    boolean routericon;
    View currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);

        playground = findViewById(R.id.draglayout);
        routericon = false;

        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("BitmapImage");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        Log.i("MyAPP","BMP not null xx");
        if (bmp != null) {
            Drawable dr = new BitmapDrawable(bmp);
            playground.addView(createNewImageView(0,"background",dr));
            Log.i("MyAPP","BMP not null");

        }

        findViewById(R.id.addRouterButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playground.addView(createNewImageView(R.drawable.router_ico,"router",null));
                routericon = true;
            }
        });

        findViewById(R.id.removeRouterButton).setOnClickListener(new View.OnClickListener() {
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

        findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(routericon){
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

                    Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                    intent.putExtra("BitmapImage", byteArray);
                    startActivity(intent);
                }else{
                    new AlertDialog.Builder(PictureEditActivity.this)
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


    }

    private RotateZoomImageView createNewImageView(int dr, String tag , Drawable img) {
        final  RotateZoomImageView iv = new RotateZoomImageView(getApplicationContext());
        iv.setPadding(3,3,3,3);

        RelativeLayout.LayoutParams lp;
        if(tag.equals("router")){
            iv.setImageDrawable(getDrawable(dr));
            lp = new RelativeLayout.LayoutParams(250, 250);
            lp.addRule(RelativeLayout.ABOVE);
            iv.setLayoutParams(lp);
        }else {
            iv.setImageDrawable(img);
            iv.setScaleType(RotateZoomImageView.ScaleType.FIT_XY);
            //lp = new RelativeLayout.LayoutParams(350, 350);
        }

        iv.setTag(tag);

        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable highlight = getResources().getDrawable( R.drawable.highlight);
                iv.setBackground(highlight);
                iv.bringToFront();
                currentView = iv;
                if(event.getAction() == MotionEvent.ACTION_UP){
                    iv.setBackground(null);
                }
                return iv.onTouch(v,event);
            }
        });

        return iv;
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}
