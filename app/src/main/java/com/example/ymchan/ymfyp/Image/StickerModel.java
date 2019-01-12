package com.example.ymchan.ymfyp.Image;

import android.graphics.Bitmap;

/**
 * Created by yan min on 13/1/2019
 */
public class StickerModel {

    private Bitmap mStickerImg;
    private int mStickerID;

    public StickerModel(Bitmap img, int id) {
        mStickerImg = img;
        mStickerID = id;
    }

    public Bitmap getmStickerImg() {
        return mStickerImg;
    }

    public void setmStickerImg(Bitmap mStickerImg) {
        this.mStickerImg = mStickerImg;
    }

    public int getmStickerID() {
        return mStickerID;
    }

    public void setmStickerID(int mStickerID) {
        this.mStickerID = mStickerID;
    }
}
