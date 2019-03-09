package com.example.ymchan.ymfyp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.ymchan.ymfyp.Image.ResultHolder;
import com.example.ymchan.ymfyp.Util.CustomStickersDatabase;
import com.example.ymchan.ymfyp.Util.WScratchView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yan min on 29/12/2018
 */
public class CustomStickerFragment extends Fragment {

    private final static String TAG = "ymfyp.CustomStickerFrag";

    private LinearLayout btnReset;
    private LinearLayout btnScratchSize;
    private ImageView btnSave;
    private ImageView btnClose;

    byte[] jpeg = null;
    Bitmap capturedImageBitmap = null;

    private static CustomStickersDatabase mCustomStickerDB = null;

    private WScratchView mWScratchView;
    private SeekBar mScratchSizeBar;
    private Animation bottomUp;
    private Animation bottomDown;

    private RelativeLayout mProgressCircle;
    private SaveCustomStickerAsyncTask mSaveCustomStickerAsyncTask = null;

    public CustomStickerFragment() {
        // Required empty public constructor
    }

    public static CustomStickerFragment newInstance(String param1, String param2) {
        CustomStickerFragment fragment = new CustomStickerFragment();
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
        View view = inflater.inflate(R.layout.fragment_custom_sticker, container, false);

        //initialise views
        btnReset = view.findViewById(R.id.reset_scratch);
        btnScratchSize = view.findViewById(R.id.set_scratch_size);
        btnSave = view.findViewById(R.id.save_scratch);
        btnClose = view.findViewById(R.id.close_scratch);
        mWScratchView = (WScratchView) view.findViewById(R.id.scratch_view);
        mScratchSizeBar = view.findViewById(R.id.scratch_size_slider);
        mProgressCircle = view.findViewById(R.id.loadingPanel);

        bottomUp = AnimationUtils.loadAnimation(getContext(),
                R.anim.bottom_up);
        bottomDown = AnimationUtils.loadAnimation(getContext(),
                R.anim.bottom_down);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG , "onActivityCreated");

        jpeg = ResultHolder.getImage();
//        File video = ResultHolder.getVideo();

        if (jpeg != null) {
            capturedImageBitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);

            if (capturedImageBitmap == null) {
                return;
            }
            // set bitmap to scratchview
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.got_s);
            mWScratchView.setScratchBitmap(capturedImageBitmap);
            mWScratchView.setDrawingCacheEnabled(true);
        }

        //add listener for buttons
        addListener();

        //listener for seekbar
        mScratchSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 30;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                Log.d(TAG, "onProgressChanged progress value = " + progressValue);
                progress = progressValue;
                int revealSize = Math.round(progressValue*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
                mWScratchView.setRevealSize(revealSize);
//                Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch");
//                Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch");
//                textView.setText("Covered: " + progress + "/" + seekBar.getMax());
//                Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }
        });
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
        btnReset.setOnClickListener(mOnclickListener);
        btnScratchSize.setOnClickListener(mOnclickListener);
        btnSave.setOnClickListener(mOnclickListener);
        btnClose.setOnClickListener(mOnclickListener);
    }

    public View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.reset_scratch:
                    Log.d(TAG, "reset_scratch pressed ");
                    mWScratchView.resetView();
                    mWScratchView.setScratchAll(false);
                    break;

                case R.id.set_scratch_size:
                    Log.d(TAG, "set_scratch_size pressed ");
                    if(mScratchSizeBar.getVisibility() == View.INVISIBLE){
                        mScratchSizeBar.startAnimation(bottomUp);
                        mScratchSizeBar.setVisibility(View.VISIBLE);
                    } else {
                        mScratchSizeBar.startAnimation(bottomDown);
                        mScratchSizeBar.setVisibility(View.INVISIBLE);
                    }
                    break;

                case R.id.save_scratch:
                    Log.d(TAG, "save_scratch pressed");
                    mSaveCustomStickerAsyncTask = new SaveCustomStickerAsyncTask();
                    mSaveCustomStickerAsyncTask.execute();
//                    // Obtain bitmap from mWScratchView
//                    Bitmap result = loadBitmapFromView(mWScratchView, mWScratchView.getWidth(), mWScratchView.getHeight());
//                    Log.d(TAG , "result = " + result);
//
//                    long captureStartTime = System.currentTimeMillis();
//                    Date captureDate = Calendar.getInstance().getTime();
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                    String createDate = dateFormat.format(captureDate);
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    result.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] jpeg = stream.toByteArray();
//                    result.recycle();
//
//                    long callbackTime = System.currentTimeMillis();
//                    ResultHolder.dispose();
//                    ResultHolder.setImage(jpeg);
//                    ResultHolder.setTimeToCallback(callbackTime - captureStartTime);
//
//                    mCustomStickerDB = new CustomStickersDatabase(getContext());
//                    mCustomStickerDB.open();
//                    mCustomStickerDB.addBitmap(result.toString(),createDate,jpeg);

//                    Log.d(TAG, "result = " + result);
//                    Log.d(TAG, "jpeg = " + jpeg);
//                    Log.d(TAG, "")

//                    MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
//                            new PreviewFragment(),
//                            PreviewFragment.class.getName(),
//                            0);
//
//                    mCustomStickerDB.close();
                    break;

                case R.id.close_scratch:
                    Log.d(TAG, "close_scratch pressed");
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                    break;

                default:
                    break;
            }
        }

    };

    public Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }

    // asynctask
    private class SaveCustomStickerAsyncTask extends AsyncTask<Integer, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            Log.v(TAG, "[onPreExecute]");

            // show progress
            mProgressCircle.setVisibility(View.VISIBLE);

        }

        @Override
        protected Boolean doInBackground(Integer... mode) {
            Log.v(TAG, "[doInBackground]");
            try {
                // Obtain bitmap from mWScratchView
                Bitmap result = loadBitmapFromView(mWScratchView, mWScratchView.getWidth(), mWScratchView.getHeight());
                Log.d(TAG , "result = " + result);

                long captureStartTime = System.currentTimeMillis();
                Date captureDate = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String createDate = dateFormat.format(captureDate);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] jpeg = stream.toByteArray();
                result.recycle();

                long callbackTime = System.currentTimeMillis();
                ResultHolder.dispose();
                ResultHolder.setImage(jpeg);
                ResultHolder.setTimeToCallback(callbackTime - captureStartTime);

                mCustomStickerDB = new CustomStickersDatabase(getContext());
                mCustomStickerDB.open();
                mCustomStickerDB.addBitmap(result.toString(),createDate,jpeg);

            } catch (Throwable e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.v(TAG, "[onProgressUpdate]");
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.v(TAG, "[onPostExecute]");
            // hide progress
            mProgressCircle.setVisibility(View.INVISIBLE);

            MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                    new StickerListFragment(),
                    StickerListFragment.class.getName(),
                    0);

            mCustomStickerDB.close();
        }

        @Override
        protected void onCancelled() {
            Log.v(TAG, "[onCancelled]");
            if (mSaveCustomStickerAsyncTask == null) {
                // hide progress
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        }
    }

}
