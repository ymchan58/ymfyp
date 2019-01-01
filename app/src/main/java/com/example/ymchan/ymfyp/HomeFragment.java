package com.example.ymchan.ymfyp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ymchan.ymfyp.Image.ResultHolder;
import com.example.ymchan.ymfyp.Util.Util;

import com.wonderkiln.camerakit.CameraKitImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yan min on 9/6/2018.
 */

public class HomeFragment extends Fragment {

    private final static String TAG = "ymfyp.HomeFragment";

    //views
    private ImageView btnAbout = null;
    private LinearLayout btnCamera = null;
    private LinearLayout btnFolder = null;
    private LinearLayout btnARCamera = null;
    private LinearLayout btnCustomSticker = null;

    static final int SELECT_FILE = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG , "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //initialise views
        btnAbout = view.findViewById(R.id.about_icn);
        btnCamera = view.findViewById(R.id.camera_icn);
        btnFolder = view.findViewById(R.id.folder_icn);
        btnARCamera = view.findViewById(R.id.arcamera_icn);
        btnCustomSticker = view.findViewById(R.id.customsticker_icn);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG , "onActivityCreated");

        //add listener for buttons
        addListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void addListener() {
        btnAbout.setOnClickListener(mOnclickListener);
        btnCamera.setOnClickListener(mOnclickListener);
        btnFolder.setOnClickListener(mOnclickListener);
        btnARCamera.setOnClickListener(mOnclickListener);
        btnCustomSticker.setOnClickListener(mOnclickListener);
    }

    public View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.about_icn:
                    Log.d(TAG, "about_icn pressed ");
                    MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                            new AboutFragment(),
                            AboutFragment.class.getName(),
                            0);
                    break;

                case R.id.camera_icn:
                    Log.d(TAG, "camera_icn pressed");
                    MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                            new CameraFragment(),
                            CameraFragment.class.getName(),
                            0);
                    break;

                case R.id.folder_icn:
                    Log.d(TAG, "folder_icn pressed ");
                    selectImage();
                    break;

                case R.id.arcamera_icn:
                    Log.d(TAG, "arcamera_icn pressed ");
                    MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                            new FaceEffectFragment(),
                            FaceEffectFragment.class.getName(),
                            0);
                    break;

                case R.id.customsticker_icn:
                    Log.d(TAG, "customsticker_icn pressed ");
                    MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                            new CustomStickerFragment(),
                            CustomStickerFragment.class.getName(),
                            0);
                    break;

                default:
                    break;
            }
        }

    };

    private void selectImage() {
        final CharSequence[] items = {"Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setTitle("Attach image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Util.checkPermission(getView().getContext());
                if (items[item].equals("Choose from Library")) {
                    Log.d(TAG, "Choose from Library");
                    if(result){}
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap image = null;
        long captureStartTime = System.currentTimeMillis();
        if(requestCode == SELECT_FILE && resultCode == RESULT_OK){
            try {
                image = MediaStore.Images.Media.getBitmap(getView().getContext().getContentResolver(), data.getData());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] jpeg = stream.toByteArray();
                image.recycle();

                long callbackTime = System.currentTimeMillis();
                ResultHolder.dispose();
                ResultHolder.setImage(jpeg);
//                ResultHolder.setNativeCaptureSize(Integer.parseInt(jpeg.length));
                ResultHolder.setTimeToCallback(callbackTime - captureStartTime);

                Log.d(TAG, "from gallery " + image.toString());

                MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                        new PreviewFragment(),
                        PreviewFragment.class.getName(),
                        0);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
