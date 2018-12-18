package com.example.ymchan.ymfyp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.ymchan.ymfyp.Camera.CameraSourcePreview;
import com.example.ymchan.ymfyp.Camera.GraphicOverlay;
import com.example.ymchan.ymfyp.Image.ResultHolder;
import com.example.ymchan.ymfyp.Util.FaceGraphic;
import com.example.ymchan.ymfyp.Util.FaceTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.lang.*;

/**
 * Created by yan min on 12/12/2018
 * For Face effect cam
 */
public class FaceEffectFragment extends Fragment {

    private final static String TAG = "ymfyp.FacefxFragment";

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 255;

    //Select filter effect
    private static final int DEFAULT_FACE_EFFECT = 1;
    private static final int EMOJI_EFFECT = 2;
    private static final int LIGHTBULB_EFFECT = 3;

    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private boolean mIsFrontFacing = true;
    private int mSelectedFilter = DEFAULT_FACE_EFFECT;
    private FaceTracker mFaceTracker;

    private ImageButton filter1Btn;
    private ImageButton filter2Btn;
    private ImageButton filter3Btn;

    private ProgressDialog mProgressDialog;


    // Activity event handlers
    // =======================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_face_effect, container, false);

        mPreview = (CameraSourcePreview) view.findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) view.findViewById(R.id.faceOverlay);

        //flip button
        final ImageButton button = (ImageButton) view.findViewById(R.id.flipButton);
        button.setOnClickListener(mSwitchCameraButtonListener);

        //capture button
        final ImageButton captureButton = (ImageButton) view.findViewById(R.id.capButton);
        captureButton.setOnClickListener(mCaptureCameraButtonListener);

        //filter selection buttons
        filter1Btn = (ImageButton) view.findViewById(R.id.filter1);
        filter2Btn = (ImageButton) view.findViewById(R.id.filter2);
        filter3Btn = (ImageButton) view.findViewById(R.id.filter3);

        filter1Btn.setOnClickListener(mFilterOnclickListener);
        filter2Btn.setOnClickListener(mFilterOnclickListener);
        filter3Btn.setOnClickListener(mFilterOnclickListener);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onCreate called.");

        if (savedInstanceState != null) {
            mIsFrontFacing = savedInstanceState.getBoolean("IsFrontFacing");
        }

        // Start using the camera if permission has been granted to this app,
        // otherwise ask for permission to use it.
        int rc = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

    }

    private View.OnClickListener mSwitchCameraButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            mIsFrontFacing = !mIsFrontFacing;

            if (mCameraSource != null) {
                mCameraSource.release();
                mCameraSource = null;
            }

            createCameraSource();
            startCameraSource();
        }
    };

    private View.OnClickListener mCaptureCameraButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.d(TAG, "capturebutton clicked.");
            mCameraSource.takePicture(null, mPicture);
            showLoading("Capturing...");
        }
    };

    public View.OnClickListener mFilterOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.filter1:
                    Log.d(TAG, "filter1 pressed ");
                    mSelectedFilter = DEFAULT_FACE_EFFECT;
                    break;

                case R.id.filter2:
                    Log.d(TAG, "filter2 pressed");
                    mSelectedFilter = EMOJI_EFFECT;
                    break;

                case R.id.filter3:
                    Log.d(TAG, "filter3 pressed ");
                    mSelectedFilter = LIGHTBULB_EFFECT;
                    break;

                default:
                    break;
            }
            if(mFaceTracker!=null){
                mFaceTracker.setSelectedFilter(mSelectedFilter);
            }
        }

    };

    CameraSource.PictureCallback mPicture = new CameraSource.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data) {
            Log.d(TAG, "CameraSource.PictureCallback");
            Bitmap face = BitmapFactory.decodeByteArray(data, 0, data.length);

            //Rotate because camerasource reorients captured image for some reason?
            if(mIsFrontFacing){ //front face camera
                face = rotate(face, 270, mIsFrontFacing);
            } else { //back face camera
                face = rotate(face, 90, mIsFrontFacing);
            }


            // Generate the Eyes Overlay Bitmap
            mPreview.setDrawingCacheEnabled(true);
            Bitmap overlay = mPreview.getDrawingCache();

            // Generate the final merged image
            Bitmap result = mergeBitmaps(face, overlay);

            long captureStartTime = System.currentTimeMillis();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            result.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] jpeg = stream.toByteArray();
            result.recycle();

            long callbackTime = System.currentTimeMillis();
            ResultHolder.dispose();
            ResultHolder.setImage(jpeg);
            ResultHolder.setTimeToCallback(callbackTime - captureStartTime);

            hideLoading();

            MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                    new PreviewFragment(),
                    PreviewFragment.class.getName(),
                    0);
        }
    };

    public static Bitmap rotate(Bitmap bitmap, int degree, boolean isFront) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        //       mtx.postRotate(degree);
        mtx.setRotate(degree);
        if(isFront){ //flip image for front camera.
            mtx.preScale(1, -1);
        }

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public Bitmap mergeBitmaps(Bitmap face, Bitmap overlay) {
        // Create a new image with target size
        Log.d(TAG, "merge bitmaps face width, height = " + face.getWidth() + " , " + face.getHeight()
                + " overlay width, height = " + overlay.getWidth() + " , " + overlay.getHeight());
        int width = face.getWidth();
        int height = face.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Rect faceRect = new Rect(0,0,width,height);
        Rect overlayRect = new Rect(0,0,overlay.getWidth(), (int)(overlay.getWidth()*1.33));

        // Draw face and then overlay (Make sure rects are as needed)
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(face, faceRect, faceRect, null);
        canvas.drawBitmap(overlay, overlayRect, faceRect, null);
        return newBitmap;
    }

    protected void showLoading(@NonNull String message) {
        mProgressDialog = new ProgressDialog(getView().getContext());
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called.");

        startCameraSource();
    }

    @Override
    public void onPause() {
        super.onPause();

        mPreview.stop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("IsFrontFacing", mIsFrontFacing);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    // Handle camera permission requests
    // =================================

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission not acquired. Requesting permission.");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = getActivity();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);
            }
        };
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // We have permission to access the camera, so create the camera source.
            Log.d(TAG, "Camera permission granted - initializing camera source.");
            createCameraSource();
            return;
        }

        // If we've reached this part of the method, it means that the user hasn't granted the app
        // access to the camera. Notify the user and exit.
        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.app_name)
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.disappointed_ok, listener)
                .show();
    }

    // Camera source
    // =============

    private void createCameraSource() {
        Log.d(TAG, "createCameraSource called.");

        Context context = getActivity().getApplicationContext();
        FaceDetector detector = createFaceDetector(context);

        int facing = CameraSource.CAMERA_FACING_FRONT;
        if (!mIsFrontFacing) {
            facing = CameraSource.CAMERA_FACING_BACK;
        }

        // The camera source is initialized to use either the front or rear facing camera.  We use a
        // relatively low resolution for the camera preview, since this is sufficient for this app
        // and the face detector will run faster at lower camera resolutions.
        //
        // However, note that there is a speed/accuracy trade-off with respect to choosing the
        // camera resolution.  The face detector will run faster with lower camera resolutions,
        // but may miss smaller faces, landmarks, or may not correctly detect eyes open/closed in
        // comparison to using higher camera resolutions.  If you have any of these issues, you may
        // want to increase the resolution.
        mCameraSource = new CameraSource.Builder(context, detector)
                .setFacing(facing)
                .setRequestedPreviewSize(320, 240)
                .setRequestedFps(60.0f)
                .setAutoFocusEnabled(true)
                .build();
    }

    private void startCameraSource() {
        Log.d(TAG, "startCameraSource called.");

        // Make sure that the device has Google Play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getActivity().getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    // Face detector
    // =============

    /**
     *  Create the face detector, and check if it's ready for use.
     */
    @NonNull
    private FaceDetector createFaceDetector(final Context context) {
        Log.d(TAG, "createFaceDetector called.");

        FaceDetector detector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(mIsFrontFacing)
                .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
                .build();

        MultiProcessor.Factory<Face> factory = new MultiProcessor.Factory<Face>() {
            @Override
            public Tracker<Face> create(Face face) {
                mFaceTracker = new FaceTracker(mGraphicOverlay, context, mIsFrontFacing, mSelectedFilter);
                return mFaceTracker;
            }
        };

        Detector.Processor<Face> processor = new MultiProcessor.Builder<>(factory).build();
        detector.setProcessor(processor);

        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");

            // Check the device's storage.  If there's little available storage, the native
            // face detection library will not be downloaded, and the app won't work,
            // so notify the user.
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = getActivity().getApplicationContext().registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                Log.w(TAG, getString(R.string.low_storage_error));
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.app_name)
                        .setMessage(R.string.low_storage_error)
                        .setPositiveButton(R.string.disappointed_ok, listener)
                        .show();
            }
        }
        return detector;
    }

}
