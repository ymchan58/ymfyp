package com.example.ymchan.ymfyp.Camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ymchan.ymfyp.CameraFragment;
import com.example.ymchan.ymfyp.Image.ResultHolder;
import com.example.ymchan.ymfyp.MainActivity;
import com.example.ymchan.ymfyp.PreviewFragment;
import com.example.ymchan.ymfyp.R;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;
import com.wonderkiln.camerakit.OnCameraKitEvent;

import java.io.File;

/**
 * Created by yan min on 10/6/2018
 */


public class CameraControls extends LinearLayout {

    private final static String TAG = "ymfyp.CameraControls";

    private int cameraViewId = -1;
    private CameraView cameraView;

    private int coverViewId = -1;
    private View coverView;

    ImageView facingButton;
    ImageView flashButton;
    ImageView captureButton;

    private long captureDownTime;
    private long captureStartTime;
    private boolean pendingVideoCapture;
    private boolean capturingVideo;

    private CameraFragmentManager mCameraFragmentMgr = null;

    public CameraControls(Context context) {
        this(context, null);
    }

    public CameraControls(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraControls(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.camera_controls, this);
        //ButterKnife.bind(this);
        facingButton = (ImageView)findViewById(R.id.facingButton);
        flashButton = (ImageView)findViewById(R.id.flashButton);
        captureButton = (ImageView)findViewById(R.id.captureButton);

        mCameraFragmentMgr = CameraFragmentManager.getInstance(context);

        if(mCameraFragmentMgr != null) {
            mCameraFragmentMgr.isPhotoTaken(false);
        }

        addListener();


        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CameraControls,
                    0, 0);

            try {
                cameraViewId = a.getResourceId(R.styleable.CameraControls_camera, -1);
                coverViewId = a.getResourceId(R.styleable.CameraControls_cover, -1);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (cameraViewId != -1) {
            View view = getRootView().findViewById(cameraViewId);
            if (view instanceof CameraView) {
                cameraView = (CameraView) view;
                cameraView.bindCameraKitListener(this);
                setFacingImageBasedOnCamera();
            }
        }

        if (coverViewId != -1) {
            View view = getRootView().findViewById(coverViewId);
            if (view != null) {
                coverView = view;
                coverView.setVisibility(GONE);
            }
        }
    }

    private void setFacingImageBasedOnCamera() {
        if (cameraView.isFacingFront()) {
            facingButton.setImageResource(R.drawable.ic_facing_back);
        } else {
            facingButton.setImageResource(R.drawable.ic_facing_front);
        }
    }

    //@OnCameraKitEvent(CameraKitImage.class)
    public void imageCaptured(CameraKitImage image) {
        byte[] jpeg = image.getJpeg();

        long callbackTime = System.currentTimeMillis();
        ResultHolder.dispose();
        ResultHolder.setImage(jpeg);
        ResultHolder.setNativeCaptureSize(cameraView.getCaptureSize());
        ResultHolder.setTimeToCallback(callbackTime - captureStartTime);
        if(mCameraFragmentMgr != null) {
            mCameraFragmentMgr.isPhotoTaken(true);
        }
    }

    @OnCameraKitEvent(CameraKitVideo.class)
    public void videoCaptured(CameraKitVideo video) {
        File videoFile = video.getVideoFile();
        if (videoFile != null) {
            ResultHolder.dispose();
            ResultHolder.setVideo(videoFile);
            ResultHolder.setNativeCaptureSize(cameraView.getCaptureSize());
//            Intent intent = new Intent(getContext(), PreviewActivity.class);
//            getContext().startActivity(intent);
        }
    }

    public void addListener() {
        facingButton.setOnClickListener(mOnclickListener);
        flashButton.setOnClickListener(mOnclickListener);
        captureButton.setOnClickListener(mOnclickListener);
    }

    public View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.facingButton:
                    Log.d(TAG, "facingButton pressed ");
                    coverView.setAlpha(0);
                    coverView.setVisibility(VISIBLE);
                    coverView.animate()
                            .alpha(1)
                            .setStartDelay(0)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if (cameraView.isFacingFront()) {
                                        cameraView.setFacing(CameraKit.Constants.FACING_BACK);
                                        facingButton.setImageResource(R.drawable.ic_facing_front);
                                    } else {
                                        cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
                                        facingButton.setImageResource(R.drawable.ic_facing_back);
                                    }

                                    coverView.animate()
                                            .alpha(0)
                                            .setStartDelay(200)
                                            .setDuration(300)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);
                                                    coverView.setVisibility(GONE);
                                                }
                                            })
                                            .start();
                                }
                            })
                            .start();
                    break;

                case R.id.flashButton:
                    Log.d(TAG, "flashButton pressed");
                    if (cameraView.getFlash() == CameraKit.Constants.FLASH_OFF) {
                        cameraView.setFlash(CameraKit.Constants.FLASH_ON);
                        flashButton.setImageResource(R.drawable.ic_flash_on);
                    } else {
                        cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
                        flashButton.setImageResource(R.drawable.ic_flash_off);
                    }

                    break;

                case R.id.captureButton:
                    Log.d(TAG, "captureButton pressed ");
                    captureStartTime = System.currentTimeMillis();
                    cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
                        @Override
                        public void callback(CameraKitImage event) {
                            imageCaptured(event);
                        }
                    });

                    break;

                default:
                    break;
            }
        }

    };

    void touchDownAnimation(View view) {
        view.animate()
                .scaleX(0.88f)
                .scaleY(0.88f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    void touchUpAnimation(View view) {
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    void changeViewImageResource(final ImageView imageView, @DrawableRes final int resId) {
        imageView.setRotation(0);
        imageView.animate()
                .rotationBy(360)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator())
                .start();

        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(resId);
            }
        }, 120);
    }

}