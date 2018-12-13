package com.example.ymchan.ymfyp.Camera;

import android.content.Context;
import android.util.Log;

/**
 * Created by yan min on 10/6/2018
 * For Default simple camera
 */
public class CameraFragmentManager {

    private static final String TAG = "CameraFragmentManager";
    private CameraFragmentManager.CameraFragmentListener mCameraFragmentListener;

    private static final int OP_IS_PHOTO_TAKEN = 0;

    /**
     * Create a new CameraFragmentManager (singleton)
     */
    private static CameraFragmentManager INSTANCE = null;
    private Context mContext;

    public static synchronized CameraFragmentManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CameraFragmentManager(context);
            Log.v(TAG, "[CameraFragmentManager] Instantiated.");
        }
        return INSTANCE;
    }

    public CameraFragmentManager(Context context) {
        mContext = context;
    }

    public void release() {
        INSTANCE = null;
    }

    public synchronized boolean isPhotoTaken(boolean status) {
        try {
            Log.d(TAG, "[isPhotoTaken] = " + status);
            handleSuccessResponse(OP_IS_PHOTO_TAKEN, status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        handleFailedResponse(OP_IS_PHOTO_TAKEN);
        return false;
    }

    private void handleSuccessResponse(int operation, boolean status) {
        try {
            Log.d(TAG , "handleSuccessResponse  ****************************************");
            switch (operation) {
                case OP_IS_PHOTO_TAKEN:
                    Log.d(TAG ,  "OP_IS_PHOTO_TAKEN");
                    if (mCameraFragmentListener != null) {
                        mCameraFragmentListener.onPhotoTakenSuccess(status);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleFailedResponse(int operation) {
        try {
            Log.d(TAG , "handleFailedResponse  ****************************************");
            switch (operation) {
                case OP_IS_PHOTO_TAKEN:
                    if (mCameraFragmentListener != null) {
                        mCameraFragmentListener.onPhotoTakenFailed();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCameraFragmentListener(CameraFragmentListener listener) {
        mCameraFragmentListener = listener;
    }

    public interface CameraFragmentListener {
        /**
         * Called when successful
         */
        void onPhotoTakenSuccess(boolean status);

        /**
         * Called when failed
         */
        void onPhotoTakenFailed();

    }
    
}
