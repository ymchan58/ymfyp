package com.example.ymchan.ymfyp.Image;

import android.graphics.Bitmap;

/**
 * Created by yan min on 13/1/2019
 */
public class StickerModel {

    private Bitmap mStickerImg;
    private int mStickerID;
    private String stickerName;
    private String createDate;

    public StickerModel(Bitmap img, int id, String name, String date) {
        mStickerImg = img;
        mStickerID = id;
        stickerName = name;
        createDate = date;
    }

    public Bitmap getmStickerImg() {
        return mStickerImg;
    }

    public String getStickerName() {
        return stickerName;
    }

    public String getCreateDate() {
        return createDate;
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
