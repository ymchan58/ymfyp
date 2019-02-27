package com.example.ymchan.ymfyp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ymchan.ymfyp.Adapters.StickerDataAdapter;
import com.example.ymchan.ymfyp.Image.StickerModel;
import com.example.ymchan.ymfyp.Adapters.CustomStickerAdapter;
import com.example.ymchan.ymfyp.Util.CustomStickersDatabase;
import com.example.ymchan.ymfyp.Util.SwipeController;
import com.example.ymchan.ymfyp.Util.SwipeControllerActions;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yan min on 11/1/2019.
 *
 */
public class StickerListFragment extends Fragment{
    private final static String TAG = "ymfyp.StickerListFrag";

    private static CustomStickersDatabase mCustomStickerDB = null;

    private RecyclerView mRvStickers;
//    private CustomStickerAdapter mCustomStickerAdapter = null;
    private StickerDataAdapter mStickerDataAdapter = null;
    SwipeController swipeController = null;

    List<StickerModel> mStickerModelList;

    private int mSelectedStickerID = -1;

    public StickerListFragment() {
        // Required empty public constructor
    }

    public static StickerListFragment newInstance(String param1, String param2) {
        StickerListFragment fragment = new StickerListFragment();
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
        View view = inflater.inflate(R.layout.fragment_sticker_list, container, false);

        //initialise views
        mRvStickers = view.findViewById(R.id.sticker_recycler_view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG , "onActivityCreated");

//        mStickerDataAdapter = new StickerDataAdapter(getContext());

        setStickersDataAdapter();
        setupRecyclerView();

//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        mRvStickers.setLayoutManager(mLayoutManager);
//        mRvStickers.setAdapter(mStickerDataAdapter);

//        SwipeController swipeController = new SwipeController();
//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
//        itemTouchhelper.attachToRecyclerView(mRvStickers);
//
//        mRvStickers.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//                swipeController.onDraw(c);
//            }
//        });

//        mCustomStickerDB = new CustomStickersDatabase(getContext());
//        mCustomStickerDB.open();

    }

    private void setStickersDataAdapter() {
        mStickerModelList = new ArrayList<>();
        List<Bitmap> bitmapStickerList = new ArrayList<>();
        List<Integer> stickerIDList = new ArrayList<>();

        mCustomStickerDB = new CustomStickersDatabase(getContext());
        mCustomStickerDB.open();
        List<byte[]> stickersFromDB = mCustomStickerDB.getAllBitmap();
        stickerIDList = mCustomStickerDB.getAllID();


        //add stickers from db
        for (int i=0; i < stickersFromDB.size(); i++) {
            byte[] currentItem = stickersFromDB.get(i);
            Bitmap bitmap = BitmapFactory.decodeByteArray(currentItem , 0, currentItem.length);
            bitmapStickerList.add(bitmap);
        }

        for (int i=0; i < stickersFromDB.size(); i++) {
            StickerModel currentSticker = new StickerModel(bitmapStickerList.get(i), stickerIDList.get(i));
            mStickerModelList.add(currentSticker);
        }

        mStickerDataAdapter = new StickerDataAdapter(mStickerModelList);
    }

    private void setupRecyclerView() {

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRvStickers.setLayoutManager(mLayoutManager);
        mRvStickers.setAdapter(mStickerDataAdapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                popAlertDialog(position);
//                mStickerDataAdapter.remove(position);
//                mStickerDataAdapter.notifyItemRemoved(position);
//                mStickerDataAdapter.notifyItemRangeChanged(position, mStickerDataAdapter.getItemCount());
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(mRvStickers);

        mRvStickers.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "mCustomStickerDB.close()");
        mCustomStickerDB.close();
    }

//    @Override
//    public void onStickerSelected(StickerModel sticker) {
//        mSelectedStickerID = sticker.getmStickerID();
//        popAlertDialog();
//    }

    public void popAlertDialog(int position){
        Log.d(TAG, "popAlertDialog");
        mSelectedStickerID = mStickerModelList.get(position).getmStickerID();
        final int pos = position;
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity());
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setTitle(getContext().getResources().getString(R.string.pop_del_sticker_title))
                .setMessage(getContext().getResources().getString(R.string.pop_del_sticker_dialog))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // positive
                        deleteSticker(mSelectedStickerID, pos);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void deleteSticker(int stickerID, int position){
        Log.d(TAG, "deleteSticker: " + stickerID);
        mCustomStickerDB.deleteEntry(stickerID);
        mStickerDataAdapter.remove(position);
        mStickerDataAdapter.notifyItemRemoved(position);
        mStickerDataAdapter.notifyItemRangeChanged(position, mStickerDataAdapter.getItemCount());
//        refreshUI(sticker);
    }

//    public void refreshUI(int sticker){
////        mCustomStickerAdapter.notifyItemRemoved(sticker);
//        Log.d(TAG, "mCustomStickerAdapter.remove: " + sticker);
//        mCustomStickerAdapter.remove(sticker);
//        mCustomStickerAdapter.notifyDataSetChanged();
//        mRvStickers.setAdapter(mCustomStickerAdapter);
//
//    }
}
