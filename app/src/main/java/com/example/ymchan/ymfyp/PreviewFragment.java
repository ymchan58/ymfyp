package com.example.ymchan.ymfyp;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.ymchan.ymfyp.Image.ResultHolder;
import com.example.ymchan.ymfyp.Util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

/**
 * Created by yan min on 10/6/2018.
 */

public class PreviewFragment extends Fragment {

    private static final String TAG = "ymfyp.PreviewFragment";

    ImageView imageView;
    VideoView videoView;
    TextView actualResolution;
    TextView approxUncompressedSize;
    TextView captureLatency;
    ImageView btnSaveImage;
    ImageView btnEditImage;
    ImageView btnExit;

    byte[] jpeg = null;
    Bitmap capturedImageBitmap = null;

    private boolean isImageSaved = false;


    public PreviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);

        imageView = view.findViewById(R.id.captured_image);
        videoView = view.findViewById(R.id.captured_video);
        actualResolution = view.findViewById(R.id.actualResolution);
        approxUncompressedSize = view.findViewById(R.id.approxUncompressedSize);
        captureLatency = view.findViewById(R.id.captureLatency);
        btnSaveImage = view.findViewById(R.id.save_image_btn);
        btnSaveImage.setImageResource(R.drawable.ic_if_icons_save_1564526);
        btnEditImage = view.findViewById(R.id.edit_image_btn);
        btnExit = view.findViewById(R.id.exit_btn);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isImageSaved = false;
        addListener();
//        setupToolbar();

        jpeg = ResultHolder.getImage();
        File video = ResultHolder.getVideo();

        if (jpeg != null) {
            imageView.setVisibility(View.VISIBLE);

            capturedImageBitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);

            if (capturedImageBitmap == null) {
//                finish();
                return;
            }

            imageView.setImageBitmap(capturedImageBitmap);

            actualResolution.setText(capturedImageBitmap.getWidth() + " x " + capturedImageBitmap.getHeight());
            approxUncompressedSize.setText(getApproximateFileMegabytes(capturedImageBitmap) + "MB");
            captureLatency.setText(ResultHolder.getTimeToCallback() + " milliseconds");
        }

        else if (video != null) {
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(Uri.parse(video.getAbsolutePath()));
            MediaController mediaController = new MediaController(getActivity());
            mediaController.setVisibility(View.GONE);
            videoView.setMediaController(mediaController);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    mp.start();

                    float multiplier = (float) videoView.getWidth() / (float) mp.getVideoWidth();
                    videoView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (mp.getVideoHeight() * multiplier)));
                }
            });
            //videoView.start();
        }

        else {
//            finish();
            return;
        }
    }

    public void addListener() {
        btnSaveImage.setOnClickListener(mOnclickListener);
        btnEditImage.setOnClickListener(mOnclickListener);
        btnExit.setOnClickListener(mOnclickListener);
    }

    public View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save_image_btn:
                    Log.d(TAG, "save_image_btn pressed ");
                    if(!isImageSaved){
                        saveImagetoPhone();
                    }
                    break;

                case R.id.edit_image_btn:
                    Log.d(TAG, "edit_image_btn pressed");
                    MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                            new EditImageFragment(),
                            EditImageFragment.class.getName(),
                            0);
                    break;

                case R.id.exit_btn:
                    Log.d(TAG, "exit_btn pressed ");
                    if(!isImageSaved) {
                        popAlertDialog();
                    } else {
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                    }
                    break;

                default:
                    break;
            }
        }

    };

    public void saveImagetoPhone(){
        Log.d(TAG, "call saveImagetoPhone()");

        // Save results to file
        File cacheFile = Util.makeTempFile(getView().getContext(), ".jpg");
        Log.d(TAG, "Saving result to: " + (cacheFile.getAbsolutePath().replace("%", "%%")).toString());
        FileOutputStream os = null;
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;

        try {
            os = new FileOutputStream(cacheFile);
            capturedImageBitmap.compress(format, quality, os);
            Toast.makeText(getView().getContext(), getContext().getResources().getString(R.string.toast_img_saved) ,Toast.LENGTH_LONG).show();
            btnSaveImage.setImageResource(R.drawable.ic_if_checkmark_24_103184);
            isImageSaved = true;
        } catch (Exception e) {
            Log.d(TAG, "Error: " + (e.getMessage()).toString());
            e.printStackTrace();
            cacheFile = null;
        } finally {
            Util.closeQuietely(os);
        }
    }

    public void popAlertDialog(){
        Log.d(TAG, "popAlertDialog");
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity());
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setTitle(getContext().getResources().getString(R.string.pop_delete_title))
                .setMessage(getContext().getResources().getString(R.string.pop_delete))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private static float getApproximateFileMegabytes(Bitmap bitmap) {
        return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024 / 1024;
    }

}
