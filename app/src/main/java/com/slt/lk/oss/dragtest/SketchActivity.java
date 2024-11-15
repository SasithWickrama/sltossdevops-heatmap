package com.slt.lk.oss.dragtest;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.easystudio.rotateimageview.RotateZoomImageView;
import com.rm.freedrawview.FreeDrawView;
import com.rm.freedrawview.PathDrawnListener;
import com.rm.freedrawview.PathRedoUndoCountChangeListener;
import com.rm.freedrawview.ResizeBehaviour;

public class SketchActivity extends AppCompatActivity {
    FreeDrawView planCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch);

        planCanvas = (FreeDrawView) findViewById(R.id.your_id);

        // Setup the View
        planCanvas.setPaintColor(Color.BLACK);
        //planCanvas.setPaintWidthPx(getResources.getDimensionPixelSize(R.dimen.paint_width));
        planCanvas.setPaintWidthPx(12);
        //planCanvas.setPaintWidthDp(getResources.getDimension(R.dimen.paint_width));
        planCanvas.setPaintWidthDp(3);
        planCanvas.setPaintAlpha(255);// from 0 to 255
        planCanvas.setResizeBehaviour(ResizeBehaviour.CROP);// Must be one of ResizeBehaviour
        // values;

        // This listener will be notified every time the path done and undone count changes
        planCanvas.setPathRedoUndoCountChangeListener(new PathRedoUndoCountChangeListener() {
            @Override
            public void onUndoCountChanged(int undoCount) {
                // The undoCount is the number of the paths that can be undone
            }

            @Override
            public void onRedoCountChanged(int redoCount) {
                // The redoCount is the number of path removed that can be redrawn
            }
        });
        // This listener will be notified every time a new path has been drawn
        planCanvas.setOnPathDrawnListener(new PathDrawnListener() {
            @Override
            public void onNewPathDrawn() {
                // The user has finished drawing a path
            }

            @Override
            public void onPathStart() {
                // The user has started drawing a path
            }
        });

        // This will take a screenshot of the current drawn content of the view
        planCanvas.getDrawScreenshot(new FreeDrawView.DrawCreatorListener() {
            @Override
            public void onDrawCreated(Bitmap draw) {
                // The draw Bitmap is the drawn content of the View
            }

            @Override
            public void onDrawCreationError() {
                // Something went wrong creating the bitmap, should never
                // happen unless the async task has been canceled
            }
        });


        findViewById(R.id.eraseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planCanvas.undoLast();
            }
        });

        findViewById(R.id.redoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planCanvas.redoLast();
            }
        });

        findViewById(R.id.clearButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planCanvas.clearDraw();
            }
        });

        findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planCanvas.clearDraw();
            }
        });


    }

}
