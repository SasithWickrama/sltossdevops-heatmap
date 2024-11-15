package com.slt.lk.oss.dragtest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ReadingActivity extends AppCompatActivity {

    CustomView drawingView;
    private Handler mHandler;
    TextView result;
    private int mInterval;
    boolean taskstatus = false;
    Integer level;
    View levelBar;
    private static final int PERMISSION_REQUEST_CODE = 300;
    AlertDialog myAlertDialog;
    View viewInflated;
    File imagePath;
    CustomerData myCustomer;
    TextView cusdetails;
    ActivityResultLauncher<Intent> shareCompleateResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        mHandler = new Handler();
        myCustomer = new CustomerData();
        cusdetails = findViewById(R.id.cusdetails);


        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("BitmapImage");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        if (bmp != null) {
            ImageView im = findViewById(R.id.imageView);
            im.setImageBitmap(bmp);

        }

        FrameLayout layout = (FrameLayout) findViewById(R.id.fillLayout);
        drawingView = new CustomView(ReadingActivity.this);
        layout.addView(drawingView);
        result = findViewById(R.id.result);
        levelBar = findViewById(R.id.progressView);
        levelBar.setPivotY(0F);
        mInterval = 1000;


        findViewById(R.id.clearLast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingView.removeLast();
            }
        });

        findViewById(R.id.clearAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingView.removeAll();
            }
        });

        findViewById(R.id.popupButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.csize:
                                myAlertDialog  = new AlertDialog.Builder(ReadingActivity.this).create();
                                myAlertDialog.setTitle("Change Radius [50-150]");
                                viewInflated = LayoutInflater.from(ReadingActivity.this).inflate((R.layout.input_layout), null);
                                final EditText popupuname = (EditText) viewInflated.findViewById(R.id.inputvalue);
                                popupuname.setText(String.valueOf(drawingView.getRadius()));
                                myAlertDialog.setCanceledOnTouchOutside(false);
                                myAlertDialog.setView(viewInflated);

                                myAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(Integer.parseInt(popupuname.getText().toString()) < 50){
                                            new AlertDialog.Builder(ReadingActivity.this)
                                                    .setTitle("Alert")
                                                    .setMessage("Radius Value Cannot be Less Than 50")
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Continue with delete operation
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            return;

                                        }
                                        if(Integer.parseInt(popupuname.getText().toString()) > 150){
                                            new AlertDialog.Builder(ReadingActivity.this)
                                                    .setTitle("Alert")
                                                    .setMessage("Radius Value Cannot be More Than 150")
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Continue with delete operation
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            return;

                                        }
                                        drawingView.setRadius(Integer.parseInt(popupuname.getText().toString()));
                                        Toast.makeText(getApplicationContext(), "Radius Value Changed", Toast.LENGTH_LONG).show();
                                        return;

                                    }
                                });

                                myAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                        return;
                                    }
                                });
                                myAlertDialog.show();
                                return true;
                            case R.id.interval:
                                myAlertDialog  = new AlertDialog.Builder(ReadingActivity.this).create();
                                myAlertDialog.setTitle("Change Interval [1-60 Sec]");
                                viewInflated = LayoutInflater.from(ReadingActivity.this).inflate((R.layout.input_layout), null);
                                final EditText popupuname1 = (EditText) viewInflated.findViewById(R.id.inputvalue);
                                popupuname1.setText(String.valueOf(mInterval/1000));
                                myAlertDialog.setCanceledOnTouchOutside(false);
                                myAlertDialog.setView(viewInflated);

                                myAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(Integer.parseInt(popupuname1.getText().toString()) < 0){
                                            new AlertDialog.Builder(ReadingActivity.this)
                                                    .setTitle("Alert")
                                                    .setMessage("Interval Cannot be Negative Values")
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Continue with delete operation
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            return;

                                        }
                                        if(Integer.parseInt(popupuname1.getText().toString()) > 60){
                                            new AlertDialog.Builder(ReadingActivity.this)
                                                    .setTitle("Alert")
                                                    .setMessage("Interval Cannot be Higher Than 1 Minute")
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Continue with delete operation
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            return;

                                        }
                                        mInterval = Integer.parseInt(popupuname1.getText().toString())*1000;
                                        Toast.makeText(getApplicationContext(), "Data Refresh Interval Changed", Toast.LENGTH_LONG).show();
                                        return;

                                    }
                                });

                                myAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                        return;
                                    }
                                });
                                myAlertDialog.show();
                                return true;
                            case R.id.share:
                                myAlertDialog  = new AlertDialog.Builder(ReadingActivity.this).create();
                               // myAlertDialog.setTitle("Customer Details");
                                viewInflated = LayoutInflater.from(ReadingActivity.this).inflate((R.layout.cus_details), null);
                                final EditText bbuname = (EditText) viewInflated.findViewById(R.id.bbcct);
                                bbuname.setText(myCustomer.getBBUsername());
                                final EditText floor = (EditText) viewInflated.findViewById(R.id.floor);
                                floor.setText(myCustomer.getFloor());
                                final EditText rcomment = (EditText) viewInflated.findViewById(R.id.comment);
                                rcomment.setText(myCustomer.getComment());
                                final EditText cusname = (EditText) viewInflated.findViewById(R.id.cusname);
                                cusname.setText(myCustomer.getCusName());
                                myAlertDialog.setCanceledOnTouchOutside(false);
                                myAlertDialog.setView(viewInflated);

                                myAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
//                                        cusdetails.setText("Customer Name : "+cusname.getText()+ "\nBB Username : "+bbuname.getText()+ "\nFloor : "+floor.getText().toString()+"\nComment :"+rcomment.getText().toString()+
//                                                "\nTime and Date : "+currentDateTimeString);
//                                        myCustomer.setBBUsername(bbuname.getText().toString());
//                                        myCustomer.setFloor(floor.getText().toString());
//                                        myCustomer.setComment(rcomment.getText().toString());
//                                        myCustomer.setCusName(cusname.getText().toString());
//                                        result.setText(getText(R.string.cus_msg));
//                                        findViewById(R.id.linearLayout).setVisibility(View.GONE);


                                        result.setText("Customer Name : "+cusname.getText()+ "\nBB Username : "+bbuname.getText()+ "\nFloor : "+floor.getText().toString()+"\nComment :"+rcomment.getText().toString()+
                                                "\nTime and Date : "+currentDateTimeString);
                                        myCustomer.setBBUsername(bbuname.getText().toString());
                                        myCustomer.setFloor(floor.getText().toString());
                                        myCustomer.setComment(rcomment.getText().toString());
                                        myCustomer.setCusName(cusname.getText().toString());
                                        cusdetails.setText(getText(R.string.cus_msg));
                                        findViewById(R.id.linearLayout).setVisibility(View.GONE);


                                        final Handler handler = new Handler(Looper.getMainLooper());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Bitmap bitmap = takeScreenshot();
                                                saveBitmap(bitmap);
                                                cusdetails.setText("");
                                                shareIt();
                                            }
                                        }, 100);

                                        return;

                                    }
                                });

                                myAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                        return;
                                    }
                                });
                                myAlertDialog.show();






                                return true;
                            default:
                                return false;
                        }

                    }
                });
                popup.inflate(R.menu.popup_menu);
                popup.show();
            }
        });


        shareCompleateResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    }
                });

        Button startstop = findViewById(R.id.start);
        startstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermission() ) {
                    requestPermission();
                    return;
                }else {
                    if (!taskstatus) {
                        if (enableWIFi()) {
                         //   Toast.makeText(getApplicationContext(), "Please enable Location Service to get more data", Toast.LENGTH_LONG).show();
                            taskstatus = true;
                            drawingView.setTaskstatus(true);
                            startstop.setText("Stop");
                            startRepeatingTask();
                        }
                    } else {
                        taskstatus = false;
                        drawingView.setTaskstatus(false);
                        startstop.setText("Start");
                        stopRepeatingTask();
                    }
                }

            }
        });


    }




    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateStatus();
            } finally {
                disp();
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    private void updateStatus() {
        disp();
    }

    private void disp() {

        if(isRouterConnected()) {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int rssi = wifiManager.getConnectionInfo().getRssi();
        level = WifiManager.calculateSignalLevel(rssi, 100);
        String ssid = wifiManager.getConnectionInfo().getSSID();
       // String MacAddr = wifiManager.getConnectionInfo().getMacAddress();
       // int speed = wifiManager.getConnectionInfo().getLinkSpeed();
      //  int maxRxSpeed = wifiManager.getConnectionInfo().getMaxSupportedRxLinkSpeedMbps();

        drawingView.setSignalLevel(level);
        drawingView.setRssi(rssi+"dbm");
        levelBar.animate().scaleY(1f-(level/100F)).start();


        result.setText("\t\tSignal Strength of "+ ssid+"\n\t\tRSSI = "+ rssi + " dbm \n\t\tLevel = "+ level + " out of 100");
        getSpeeds();
        }else{
            stopRepeatingTask();
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this).
                            setMessage("Please Connect to the Router and Start Again").
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
            builder.create().show();
        }
    }

    private void getSpeeds(){
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities nc = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                int downSpeed = nc.getLinkDownstreamBandwidthKbps() / 1000;
                int upSpeed = nc.getLinkUpstreamBandwidthKbps() / 1000;

                result.append("\n\t\tDownload Speed " + downSpeed + " Mbps \n\t\tUpload Speed " + upSpeed + " Mbps");
            }


    }

    public boolean enableWIFi() {
        final WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this).
                            setMessage("Please enable WIFI and Connect to the Router").
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
            builder.create().show();
            return false;
        } else {
            return true;
        }
    }

    public boolean isRouterConnected(){
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            return  true;
        }
        else{
            return  false;
        }
    }


    public boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_WIFI_STATE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result1 == PackageManager.PERMISSION_GRANTED  &&  result2 == PackageManager.PERMISSION_GRANTED &&  result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED  ;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_WIFI_STATE, ACCESS_FINE_LOCATION ,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE,ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
    }


    public Bitmap takeScreenshot() {

        findViewById(R.id.clearLast).setVisibility(View.INVISIBLE);
        findViewById(R.id.start).setVisibility(View.INVISIBLE);
        findViewById(R.id.share).setVisibility(View.INVISIBLE);
        findViewById(R.id.clearAll).setVisibility(View.INVISIBLE);
        View rootView = findViewById(R.id.fillLayout).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();

    }

    public void saveBitmap(Bitmap bitmap) {
        imagePath = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/screenshot.jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private void shareIt() {
        findViewById(R.id.clearLast).setVisibility(View.VISIBLE);
        findViewById(R.id.start).setVisibility(View.VISIBLE);
        findViewById(R.id.share).setVisibility(View.VISIBLE);
        findViewById(R.id.clearAll).setVisibility(View.VISIBLE);

        Uri uri = FileProvider.getUriForFile(ReadingActivity.this, "com.slt.lk.oss.heatmap",imagePath);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.setType("image/*");
        String shareBody = "Wi-Finder";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Wi-Finder Values");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));

        //shareCompleateResult.launch(Intent.createChooser(sharingIntent, "Share via"));
    }

}


