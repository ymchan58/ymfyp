package com.example.ymchan.ymfyp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ymchan.ymfyp.Image.ResultHolder;
import com.example.ymchan.ymfyp.Util.WScratchView;

import java.io.ByteArrayOutputStream;

/**
 * Created by yan min on 29/12/2018
 */
public class CustomStickerFragment extends Fragment {

    private final static String TAG = "ymfyp.CustomStickerFrag";

    private Button btnReset;
    private Button btnSave;

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

        // set bitmap to scratchview
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.got_s);
        mWScratchView.setScratchBitmap(bitmap);

        mWScratchView.setDrawingCacheEnabled(true);

        // set drawable to scratchview
//        mWScratchView.setScratchDrawable(getResources().getDrawable(R.drawable.test));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG , "onActivityCreated");

//        mWScratchView = new WScratchView(getView().getContext());

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

                    Drawable d = ((BitmapDrawable) d).getBitmap();

                    // Generate the final merged image
                    Bitmap result = Bitmap.createBitmap(mWScratchView.getDrawingCache());

                    long captureStartTime = System.currentTimeMillis();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] jpeg = stream.toByteArray();
                    result.recycle();

                    long callbackTime = System.currentTimeMillis();
                    ResultHolder.dispose();
                    ResultHolder.setImage(jpeg);
                    ResultHolder.setTimeToCallback(callbackTime - captureStartTime);

                    MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                            new PreviewFragment(),
                            PreviewFragment.class.getName(),
                            0);

                    break;

                default:
                    break;
            }
        }

    };

}
