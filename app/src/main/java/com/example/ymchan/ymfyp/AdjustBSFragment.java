package com.example.ymchan.ymfyp;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

/**
 * Created by yan min on 22/2/2019
 */
public class AdjustBSFragment extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    public AdjustBSFragment() {
        //required empty constructor
    }

    private Adjustments mAdjustments;

    public interface Adjustments {
        void onBrightnessChanged (int brightness);

        void onContrastChanged (int contrast);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.CustomDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_adjust_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SeekBar sbBrightness = view.findViewById(R.id.sb_brightness);
        SeekBar sbContrast = view.findViewById(R.id.sb_contrast);

        sbBrightness.setOnSeekBarChangeListener(this);
        sbContrast.setOnSeekBarChangeListener(this);
    }

    public void setAdjustmentsChangeListener(Adjustments adjustments) {
        mAdjustments = adjustments;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sb_brightness:
                if (mAdjustments != null) {
                    mAdjustments.onBrightnessChanged(i);
                }
                break;
            case R.id.sb_contrast:
                if (mAdjustments != null) {
                    mAdjustments.onContrastChanged(i);
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
