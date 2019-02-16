package com.example.ymchan.ymfyp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ymchan.ymfyp.Image.StickerModel;
import com.example.ymchan.ymfyp.Adapters.CustomStickerAdapter;
import com.example.ymchan.ymfyp.Util.CustomStickersDatabase;


/**
 * Created by yan min on 11/1/2019.
 *
 */
public class StickerListFragment extends Fragment implements CustomStickerAdapter.OnItemSelected{
    private final static String TAG = "ymfyp.StickerListFrag";

    private static CustomStickersDatabase mCustomStickerDB = null;

    private RecyclerView mRvStickers;
    private CustomStickerAdapter mCustomStickerAdapter = null;

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

        mCustomStickerAdapter = new CustomStickerAdapter(this, getContext());

//        LinearLayoutManager llmTools = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRvStickers.setLayoutManager(mLayoutManager);
        mRvStickers.setAdapter(mCustomStickerAdapter);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStickerSelected(StickerModel sticker) {
        mSelectedStickerID = sticker.getmStickerID();
        popAlertDialog();
    }

    public void popAlertDialog(){
        Log.d(TAG, "popAlertDialog");
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
                        deleteSticker(mSelectedStickerID);
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

    public void deleteSticker(int sticker){
        mCustomStickerDB = new CustomStickersDatabase(getContext());
        mCustomStickerDB.open();
        mCustomStickerDB.deleteEntry(sticker);
        refreshUI(sticker);
        mCustomStickerDB.close();
    }

    public void refreshUI(int sticker){
//        mCustomStickerAdapter.notifyItemRemoved(sticker);
        mCustomStickerAdapter.remove(sticker);
        mCustomStickerAdapter.notifyDataSetChanged();
        mRvStickers.setAdapter(mCustomStickerAdapter);

    }
}
