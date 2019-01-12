package com.example.ymchan.ymfyp.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.ymchan.ymfyp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yan min on 7/1/2019
 */
public class StickerList {
//    List<String> a = new ArrayList<String>();
    private static final String TAG = "ymfyp.StickerList";

    private Context context;
    private List<Bitmap> stickerList;

    private static CustomStickersDatabase mCustomStickerDB = null;

    public StickerList(Context current){
        this.context = current;

        mCustomStickerDB = new CustomStickersDatabase(current);
        mCustomStickerDB.open();
        List<byte[]> stickersFromDB = mCustomStickerDB.getAllBitmap();

        Log.d(TAG, "stickersFromDB.size() = " + stickersFromDB.size());

        //add default sticker list
        final Bitmap BLACK_HAT = BitmapFactory.decodeResource(context.getResources(), R.drawable.black_hat);
        final Bitmap PINK_HAT = BitmapFactory.decodeResource(context.getResources(), R.drawable.pink_hat);
        final Bitmap BUBBLE_SPEECH = BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble_speech);
        final Bitmap BUBBLE_THOUGHT = BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble_thought);
        final Bitmap BUBBLE_EXCLAMATION = BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble_exclamation);

        stickerList = new ArrayList<>();

        stickerList.add(BLACK_HAT);
        stickerList.add(PINK_HAT);
        stickerList.add(BUBBLE_SPEECH);
        stickerList.add(BUBBLE_THOUGHT);
        stickerList.add(BUBBLE_EXCLAMATION);

        //add stickers from db
        for (int i=0; i < stickersFromDB.size(); i++) {
            byte[] currentItem = stickersFromDB.get(i);
            Bitmap bitmap = BitmapFactory.decodeByteArray(currentItem , 0, currentItem.length);
            stickerList.add(bitmap);
        }

        //debugging purpose
        Log.d(TAG, "stickerList.size() = " + stickerList.size());
        for (int i=0; i < stickerList.size(); i++){
            Log.d(TAG, "sticker item " + i + " = " + stickerList.get(i));
        }

        mCustomStickerDB.close();

    }


//    Bitmap[] stickerList = new Bitmap[]{R.drawable.black_hat, PINK_HAT, R.drawable.bubble_speech, R.drawable.bubble_thought, R.drawable.bubble_exclamation};

//    public StickerList(Bitmap[] stickerList) {
//        this.stickerList = stickerList;
//    }

    public List<Bitmap> getStickerList() {
        return stickerList;
    }

    public int getStickerListLength(){
        return stickerList.size();
    }

    public void setNewSticker(Bitmap newSticker){
        stickerList.add(newSticker);
    }

    public void removeCustomSticker(Bitmap selectedSticker){
        stickerList.remove(selectedSticker);
    }
}
