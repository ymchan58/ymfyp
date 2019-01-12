package com.example.ymchan.ymfyp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.ymchan.ymfyp.Image.ResultHolder;
import com.example.ymchan.ymfyp.Util.CustomStickersDatabase;
import com.example.ymchan.ymfyp.Util.WScratchView;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by yan min on 29/12/2018
 */
public class CustomStickerFragment extends Fragment {

    private final static String TAG = "ymfyp.CustomStickerFrag";

    private Button btnReset;
    private Button btnSave;

    byte[] jpeg = null;
    Bitmap capturedImageBitmap = null;

    private static CustomStickersDatabase mCustomStickerDB = null;

    private WScratchView mWScratchView;

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
        btnSave = view.findViewById(R.id.save_scratch);
        mWScratchView = (WScratchView) view.findViewById(R.id.scratch_view);

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
        btnSave.setOnClickListener(mOnclickListener);
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

                case R.id.save_scratch:
                    Log.d(TAG, "save_scratch pressed");
                    // Obtain bitmap from mWScratchView
                    Bitmap result = loadBitmapFromView(mWScratchView, mWScratchView.getWidth(), mWScratchView.getHeight());
                    Log.d(TAG , "result = " + result);

                    long captureStartTime = System.currentTimeMillis();

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
                    mCustomStickerDB.addBitmap(result.toString(), jpeg);
//                    Log.d(TAG, "result = " + result);
//                    Log.d(TAG, "jpeg = " + jpeg);
//                    Log.d(TAG, "")

                    MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                            new PreviewFragment(),
                            PreviewFragment.class.getName(),
                            0);

                    mCustomStickerDB.close();
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

}
