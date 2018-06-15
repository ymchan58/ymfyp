package com.example.ymchan.ymfyp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ymchan.ymfyp.Camera.CameraFragmentManager;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraView;

/**
 * Created by yan min on 10/6/2018.
 */

public class CameraFragment extends Fragment implements CameraFragmentManager.CameraFragmentListener {

    private final static String TAG = "ymfyp.CameraFragment";

    private ViewGroup parent;
    private CameraView mCamera;

    private int cameraMethod = CameraKit.Constants.METHOD_STANDARD;
    private boolean cropOutput = false;

    private CameraFragmentManager mCameraFragmentManager;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG , "onCreate");

        mCameraFragmentManager = CameraFragmentManager.getInstance(getContext());
        mCameraFragmentManager.setCameraFragmentListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        parent = view.findViewById(R.id.contentFrame);
        mCamera = view.findViewById(R.id.camera);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(mCamera != null){
            mCamera.setMethod(cameraMethod);
            mCamera.setCropOutput(cropOutput);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamera.start();
    }

    @Override
    public void onPause() {
        mCamera.stop();
        super.onPause();
    }

    private static abstract class CameraSetting {

        abstract String getTitle();
        abstract String getValue();
        abstract void toggle();

    }

    private CameraSetting captureMethodSetting = new CameraSetting() {
        @Override
        String getTitle() {
            return "ckMethod";
        }

        @Override
        String getValue() {
            switch (cameraMethod) {
                case CameraKit.Constants.METHOD_STANDARD: {
                    return "standard";
                }

                case CameraKit.Constants.METHOD_STILL: {
                    return "still";
                }

                default: return null;
            }
        }

        @Override
        void toggle() {
            if (cameraMethod == CameraKit.Constants.METHOD_STANDARD) {
                cameraMethod = CameraKit.Constants.METHOD_STILL;
            } else {
                cameraMethod = CameraKit.Constants.METHOD_STANDARD;
            }

            mCamera.setMethod(cameraMethod);
        }
    };

    private CameraSetting cropSetting = new CameraSetting() {
        @Override
        String getTitle() {
            return "ckCropOutput";
        }

        @Override
        String getValue() {
            if (cropOutput) {
                return "true";
            } else {
                return "false";
            }
        }

        @Override
        void toggle() {
            cropOutput = !cropOutput;
            mCamera.setCropOutput(cropOutput);
        }
    };

    @Override
    public void onPhotoTakenSuccess(boolean status) {
        if(status){
            MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                    new PreviewFragment(),
                    PreviewFragment.class.getName(),
                    0);
        }
    }

    @Override
    public void onPhotoTakenFailed() {

    }
}
