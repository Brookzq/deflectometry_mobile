package com.quanzhang.camera2TakerWithoutPreview.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.quanzhang.camera2TakerWithoutPreview.R;
import com.quanzhang.camera2TakerWithoutPreview.listeners.PictureCapturingListener;
import com.quanzhang.camera2TakerWithoutPreview.services.APictureCapturingService;
import com.quanzhang.camera2TakerWithoutPreview.services.PictureCapturingServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


/**
 * App's Main Activity showing a simple usage of the picture taking service.
 * @author quan zhang
 */
public class MainActivity extends AppCompatActivity implements PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String[] requiredPermissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
    };
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;

    private Button btn;
    private Button choosePictures;
    private ConstraintLayout constraintLayout;
    private TextView exposureTimeShow;
    private TextView isoShow;
    private TextView awbShow;
    private TextView timeDelayShow;
    private EditText exposureTimeEdit;
    private EditText isoEdit;
    private EditText awbEdit;
    private EditText timeDelayEdit;


    //The capture service
    private APictureCapturingService pictureService;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();

        btn = (Button) findViewById(R.id.startCaptureBtn);
        choosePictures = (Button) findViewById(R.id.choose_album);
        constraintLayout = (ConstraintLayout) findViewById(R.id.activity_main);
        exposureTimeShow = (TextView) findViewById(R.id.exposure_text);
        isoShow = (TextView) findViewById(R.id.ISO);
        awbShow = (TextView) findViewById(R.id.WB);
        timeDelayShow = (TextView) findViewById(R.id.time_delay);
        exposureTimeEdit = (EditText) findViewById(R.id.exposure_edit);
        isoEdit = (EditText) findViewById(R.id.ISO_edit);
        awbEdit = (EditText) findViewById(R.id.WB_edit);
        timeDelayEdit = (EditText) findViewById(R.id.time_delay_edit);

        // getting instance of the Service from PictureCapturingServiceImpl
        pictureService = PictureCapturingServiceImpl.getInstance(this);

        // trigger button onClick event
        btn.setOnClickListener(v -> {

            pictureService.setExposureValue(exposureTimeEdit.getText().toString());
            pictureService.setDelayMills(timeDelayEdit.getText().toString());
            pictureService.setAwb(awbEdit.getText().toString());
            pictureService.setIso(isoEdit.getText().toString());

            // eliminate the effect of the top of phone
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            getWindow().setAttributes(lp);

            showToast("Starting capture!");
            pictureService.startCapturing(this);

            // hide all the components
            btn.setVisibility(View.INVISIBLE);
            choosePictures.setVisibility(View.INVISIBLE);
            exposureTimeShow.setVisibility(View.INVISIBLE);
            exposureTimeEdit.setVisibility(View.INVISIBLE);
            isoEdit.setVisibility(View.INVISIBLE);
            isoShow.setVisibility(View.INVISIBLE);
            awbEdit.setVisibility(View.INVISIBLE);
            awbShow.setVisibility(View.INVISIBLE);
            timeDelayShow.setVisibility(View.INVISIBLE);
            timeDelayEdit.setVisibility(View.INVISIBLE);
            // set background to Signal image when taking pictures.
            constraintLayout.setBackgroundResource(R.drawable.bg);
        });

    }

    private void showToast(final String text) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show()
        );
    }

    /**
    * We've finished taking pictures from all phone's cameras
    */
    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        if (picturesTaken != null && !picturesTaken.isEmpty()) {
            showToast("Done capturing all photos!");
            return;
        }
        showToast("No camera detected!");
    }

    /**
    * Displaying the pictures taken.
    */
    @Override
    public void onCaptureDone(String pictureUrl, byte[] pictureData) {
        showToast("pictureUrl: " + pictureUrl);
        constraintLayout.setBackgroundResource(0);

        // show all the components
        btn.setVisibility(View.VISIBLE);
        choosePictures.setVisibility(View.VISIBLE);
        exposureTimeShow.setVisibility(View.VISIBLE);
        exposureTimeEdit.setVisibility(View.VISIBLE);
        isoEdit.setVisibility(View.VISIBLE);
        isoShow.setVisibility(View.VISIBLE);
        awbEdit.setVisibility(View.VISIBLE);
        awbShow.setVisibility(View.VISIBLE);
        timeDelayShow.setVisibility(View.VISIBLE);
        timeDelayEdit.setVisibility(View.VISIBLE);

        if (pictureData != null && pictureUrl != null) {
            runOnUiThread(() -> {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
                final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
//                if (pictureUrl.contains("1_pic.bmp")) {
//                    uploadFrontPhoto.setImageBitmap(scaled);
//                }
            });
            showToast("Picture saved to " + pictureUrl);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermissions();
                }
            }
        }
    }

    /**
     * checking  permissions at Runtime.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        final List<String> neededPermissions = new ArrayList<>();
        for (final String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
        }
    }
}

