package com.example.ymchan.ymfyp.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ymchan.ymfyp.Image.StickerModel;
import com.example.ymchan.ymfyp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yan min on 11/1/2019
 */
public class CustomStickerAdapter extends RecyclerView.Adapter<CustomStickerAdapter.ViewHolder> {

//    private List<byte[]> mStickerList = new ArrayList<>();
    private List<StickerModel> mStickerModelList = new ArrayList<>();
    private List<Bitmap> mBitmapStickerList = new ArrayList<>();
    private List<Integer> mStickerIDsFromDB = new ArrayList<>();
    private OnItemSelected mOnItemSelected;

    private static CustomStickersDatabase mCustomStickerDB = null;

    public CustomStickerAdapter(OnItemSelected onItemSelected, Context current) {
        mOnItemSelected = onItemSelected;

        mCustomStickerDB = new CustomStickersDatabase(current);
        mCustomStickerDB.open();
        List<byte[]> stickersFromDB = mCustomStickerDB.getAllBitmap();
        mStickerIDsFromDB = mCustomStickerDB.getAllID();

//        StickerList stickers = new StickerList(current);
//        mBitmapStickerList = stickers.getStickerList();

        //add stickers from db
        for (int i=0; i < stickersFromDB.size(); i++) {
            byte[] currentItem = stickersFromDB.get(i);
            Bitmap bitmap = BitmapFactory.decodeByteArray(currentItem , 0, currentItem.length);
            mBitmapStickerList.add(bitmap);
        }

        for (int i=0; i < stickersFromDB.size(); i++) {
            StickerModel currentSticker = new StickerModel(mBitmapStickerList.get(i), mStickerIDsFromDB.get(i));
            mStickerModelList.add(currentSticker);
        }


    }

    public interface OnItemSelected {
        void onStickerSelected(StickerModel sticker);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_custom_sticker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Bitmap item = mBitmapStickerList.get(position);
        StickerModel item = mStickerModelList.get(position);
        holder.sticker.setImageBitmap(item.getmStickerImg());
    }

    @Override
    public int getItemCount() {
        return mStickerModelList.size();
    }

    public void remove(int stickerID){
        //remove
        for (int i=0; i < mStickerModelList.size(); i++) {
            int currentID = mStickerIDsFromDB.get(i);
            if(stickerID == currentID){
                mStickerModelList.remove(i);
            }
        }
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView sticker;
//        TextView txtTool;

        ViewHolder(View itemView) {
            super(itemView);
            sticker = itemView.findViewById(R.id.custom_sticker_item);
//            txtTool = itemView.findViewById(R.id.txtTool);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemSelected.onStickerSelected(mStickerModelList.get(getLayoutPosition()));
                }
            });
        }
    }
}
