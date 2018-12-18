package com.example.ymchan.ymfyp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by yan min on 16/12/2018
 * FramesBSFragment is fragment for stickers.
 * referenced from PhotoEditor test app. StickerBSFragment
 */
public class FramesBSFragment extends BottomSheetDialogFragment {

    public FramesBSFragment() {
        // Required empty public constructor
    }

    private FramesBSFragment.FramesListener mFramesListener;

    public void setFramesListener(FramesBSFragment.FramesListener framesListener) {
        mFramesListener = framesListener;
    }

    public interface FramesListener {
        void onFrameClick(Bitmap bitmap);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sticker_emoji_dialog, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        RecyclerView rvEmoji = contentView.findViewById(R.id.rvEmoji);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvEmoji.setLayoutManager(gridLayoutManager);
        FramesBSFragment.FramesAdapter frameAdapter = new FramesBSFragment.FramesAdapter();
        rvEmoji.setAdapter(frameAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class FramesAdapter extends RecyclerView.Adapter<FramesBSFragment.FramesAdapter.ViewHolder> {

        int[] framesList = new int[]{R.drawable.polaroid_frame};

        @Override
        public FramesBSFragment.FramesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sticker, parent, false);
            return new FramesBSFragment.FramesAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FramesBSFragment.FramesAdapter.ViewHolder holder, int position) {
            holder.imgFrame.setImageResource(framesList[position]);
        }

        @Override
        public int getItemCount() {
            return framesList.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgFrame;

            ViewHolder(View itemView) {
                super(itemView);
                imgFrame = itemView.findViewById(R.id.imgSticker);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mFramesListener != null) {
                            mFramesListener.onFrameClick(
                                    BitmapFactory.decodeResource(getResources(),
                                            framesList[getLayoutPosition()]));
                        }
                        dismiss();
                    }
                });
            }
        }
    }

    private String convertEmoji(String emoji) {
        String returnedEmoji = "";
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = getEmojiByUnicode(convertEmojiToInt);
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}
