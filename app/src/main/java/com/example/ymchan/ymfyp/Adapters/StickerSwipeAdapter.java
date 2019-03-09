package com.example.ymchan.ymfyp.Adapters;

import android.content.Context;
import android.database.Cursor;
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
import com.example.ymchan.ymfyp.Util.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yan min on 10/2/2019
 */
public class StickerSwipeAdapter extends RecyclerView.Adapter<StickerSwipeAdapter.ViewHolder>{

    //    private List<byte[]> mStickerList = new ArrayList<>();
    private List<StickerModel> mStickerModelList = new ArrayList<>();
    private List<Bitmap> mBitmapStickerList = new ArrayList<>();
    private List<Integer> mStickerIDsFromDB = new ArrayList<>();
    private List<String> mStickerNamesFromDB = new ArrayList<>();
    private List<String> mStickerDatesFromDB = new ArrayList<>();
//    private StickerSwipeAdapter.OnItemSelected mOnItemSelected;

    private Cursor mCursor;

    private static CustomStickersDatabase mCustomStickerDB = null;

    public StickerSwipeAdapter(Cursor cursor, Context current) {
        mCursor = cursor;

        mCustomStickerDB = new CustomStickersDatabase(current);
        mCustomStickerDB.open();
        List<byte[]> stickersFromDB = mCustomStickerDB.getAllBitmap();
        mStickerIDsFromDB = mCustomStickerDB.getAllID();
        mStickerDatesFromDB = mCustomStickerDB.getAllName();
        mStickerDatesFromDB = mCustomStickerDB.getAllDate();


        //add stickers from db
        for (int i=0; i < stickersFromDB.size(); i++) {
            byte[] currentItem = stickersFromDB.get(i);
            Bitmap bitmap = BitmapFactory.decodeByteArray(currentItem , 0, currentItem.length);
            mBitmapStickerList.add(bitmap);
        }

        for (int i=0; i < stickersFromDB.size(); i++) {
            StickerModel currentSticker = new StickerModel(mBitmapStickerList.get(i), mStickerIDsFromDB.get(i), mStickerNamesFromDB.get(i), mStickerDatesFromDB.get(i));
            mStickerModelList.add(currentSticker);
        }


    }

    @NonNull
    @Override
    public StickerSwipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cart_list, parent, false);
        return new StickerSwipeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerSwipeAdapter.ViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

//        Bitmap item = mBitmapStickerList.get(position);
        StickerModel item = mStickerModelList.get(position);
        holder.itemView.setTag(item.getmStickerID());
//        holder.sticker.setImageBitmap(item.getmStickerImg());
        holder.thumbnail.setImageBitmap(item.getmStickerImg());
    }

    @Override
    public int getItemCount() {
        return mStickerModelList.size();
    }

//    public void remove(int stickerID){
//        //remove
//        for (int i=0; i < mStickerModelList.size(); i++) {
//            int currentID = mStickerIDsFromDB.get(i);
//            if(stickerID == currentID){
//                mStickerModelList.remove(i);
//            }
//        }
//    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public void removeItem(int stickerID) {
        //remove
        for (int i=0; i < mStickerModelList.size(); i++) {
            int currentID = mStickerIDsFromDB.get(i);
            if(stickerID == currentID){
                mStickerModelList.remove(i);
            }
        }
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(stickerID);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description;
        public ImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            description = view.findViewById(R.id.description);
            thumbnail = view.findViewById(R.id.thumbnail);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);

//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mOnItemSelected.onStickerSelected(mStickerModelList.get(getLayoutPosition()));
//                }
//            });
//            view.setOn

        }
    }
}