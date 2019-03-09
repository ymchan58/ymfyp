package com.example.ymchan.ymfyp;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ymchan.ymfyp.Adapters.StickerSwipeAdapter;
import com.example.ymchan.ymfyp.Image.StickerModel;
import com.example.ymchan.ymfyp.Adapters.CustomStickerAdapter;
import com.example.ymchan.ymfyp.Util.CustomStickersDatabase;

import com.example.ymchan.ymfyp.Adapters.CustomStickerAdapter;
import com.example.ymchan.ymfyp.Util.RecyclerItemTouchHelper;

/**
 * NOT USED!!!!!!
 *
 * Created by yan min on 9/2/2019
 */
public class StickerListSwipeFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    private final static String TAG = "ymfyp.StickerSwipeFrag";

    private static CustomStickersDatabase mCustomStickerDB = null;

    private RecyclerView mRvStickers;
    private StickerSwipeAdapter mStickerSwipeAdapter = null;

    private int mSelectedStickerID = -1;

    public StickerListSwipeFragment() {
        // Required empty public constructor
    }

    public static StickerListSwipeFragment newInstance(String param1, String param2) {
        StickerListSwipeFragment fragment = new StickerListSwipeFragment();
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
        View view = inflater.inflate(R.layout.fragment_sticker_swipe, container, false);

        //initialise views
        mRvStickers = view.findViewById(R.id.recycler_view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG , "onActivityCreated");

        mCustomStickerDB = new CustomStickersDatabase(getContext());
        mCustomStickerDB.open();

        mStickerSwipeAdapter = new StickerSwipeAdapter(mCustomStickerDB.getAllItems(), getContext());
//        mAdapter = new GroceryAdapter(this, getAllItems());

//        LinearLayoutManager llmTools = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
//        mRvStickers.setLayoutManager(mLayoutManager);
//        mRvStickers.setAdapter(mCustomStickerAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRvStickers.setLayoutManager(mLayoutManager);
        mRvStickers.setItemAnimator(new DefaultItemAnimator());
        mRvStickers.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRvStickers.setAdapter(mStickerSwipeAdapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRvStickers);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

//    @Override
//    public void onStickerSelected(StickerModel sticker) {
//        mSelectedStickerID = sticker.getmStickerID();
////        popAlertDialog();
//    }

//    public void popAlertDialog(){
//        Log.d(TAG, "popAlertDialog");
//        AlertDialog.Builder builder;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            builder = new AlertDialog.Builder(getActivity());
//        } else {
//            builder = new AlertDialog.Builder(getActivity());
//        }
//        builder.setTitle(getContext().getResources().getString(R.string.pop_del_sticker_title))
//                .setMessage(getContext().getResources().getString(R.string.pop_del_sticker_dialog))
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // positive
//                        deleteSticker(mSelectedStickerID);
//                    }
//                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
//    }

    public void deleteSticker(int sticker){
        mCustomStickerDB.deleteEntry(sticker);
//        refreshUI(sticker);
        mCustomStickerDB.close();
    }

//    public void refreshUI(int sticker){
////        mCustomStickerAdapter.notifyItemRemoved(sticker);
//        mCustomStickerAdapter.remove(sticker);
//        mCustomStickerAdapter.notifyDataSetChanged();
//        mRvStickers.setAdapter(mCustomStickerAdapter);
//
//    }

//    private Cursor getAllItems() {
//        return mCustomStickerDB.query(
//                GroceryContract.GroceryEntry.TABLE_NAME,
//                null,
//                null,
//                null,
//                null,
//                null,
//                GroceryContract.GroceryEntry.COLUMN_TIMESTAMP + " DESC"
//        );
//    }

    private void removeItem(int id) {
        deleteSticker(id);
//        mAdapter.swapCursor(getAllItems());
    }

    //Override methods for RecyclerItemTouchHelper.RecyclerItemTouchHelperListener
    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CustomStickerAdapter.ViewHolder) {
            // remove the item from recycler view
            Log.d(TAG, "position = " + position);
            mStickerSwipeAdapter.removeItem(viewHolder.getAdapterPosition());
//            mStickerSwipeAdapter.removeItem(position);
            removeItem((int) viewHolder.itemView.getTag());
            mStickerSwipeAdapter.swapCursor(mCustomStickerDB.getAllItems());

//            mSelectedStickerID = viewHolder.getAdapterPosition().getId();
//            deleteSticker(mSelectedStickerID); //delete sticker from DB

            Log.d(TAG, "getAdapterPosition() = " + viewHolder.getAdapterPosition());
            Log.d(TAG, "mSelectedStickerID = " + mSelectedStickerID);

        }
    }
}
