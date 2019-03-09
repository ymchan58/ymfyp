package com.example.ymchan.ymfyp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ymchan.ymfyp.Image.StickerModel;
import com.example.ymchan.ymfyp.R;
import com.example.ymchan.ymfyp.Util.CustomStickersDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yan min on 27/2/2019
 */
public class StickerDataAdapter extends RecyclerView.Adapter<StickerDataAdapter.StickerViewHolder> {

    private List<StickerModel> mStickerModelList = new ArrayList<>();
    private List<Bitmap> mBitmapStickerList = new ArrayList<>();
    private List<Integer> mStickerIDsFromDB = new ArrayList<>();

    private static CustomStickersDatabase mCustomStickerDB = null;

    public class StickerViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description;
        public ImageView thumbnail;

        public StickerViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.sticker_name);
            description = view.findViewById(R.id.sticker_des);
            thumbnail = view.findViewById(R.id.sticker_thumbnail);
        }
    }

    public StickerDataAdapter(List<StickerModel> stickerModelList) {
        mStickerModelList = stickerModelList;
//        mBitmapStickerList = bitmapStickerList;
//        mStickerIDsFromDB = stickerIDList;
//        mCustomStickerDB = new CustomStickersDatabase(current);
//        mCustomStickerDB.open();
//        List<byte[]> stickersFromDB = mCustomStickerDB.getAllBitmap();
//        mStickerIDsFromDB = mCustomStickerDB.getAllID();
//
//
//        //add stickers from db
//        for (int i=0; i < stickersFromDB.size(); i++) {
//            byte[] currentItem = stickersFromDB.get(i);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(currentItem , 0, currentItem.length);
//            mBitmapStickerList.add(bitmap);
//        }
//
//        for (int i=0; i < stickersFromDB.size(); i++) {
//            StickerModel currentSticker = new StickerModel(mBitmapStickerList.get(i), mStickerIDsFromDB.get(i));
//            mStickerModelList.add(currentSticker);
//        }
    }

    @Override
    public StickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_clist_sticker, parent, false);

        return new StickerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StickerViewHolder holder, int position) {

//        Bitmap item = mBitmapStickerList.get(position);
        StickerModel item = mStickerModelList.get(position);
        holder.itemView.setTag(item.getmStickerID());
//        holder.sticker.setImageBitmap(item.getmStickerImg());
        holder.thumbnail.setImageBitmap(item.getmStickerImg());
        holder.name.setText(item.getStickerName());
        holder.description.setText(item.getCreateDate());
    }

    @Override
    public int getItemCount() {
        return mStickerModelList.size();
    }

    public void remove(int position){
        mStickerModelList.remove(position);
    }

}
