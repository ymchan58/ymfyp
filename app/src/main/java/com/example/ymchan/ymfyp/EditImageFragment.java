package com.example.ymchan.ymfyp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ymchan.ymfyp.Image.ResultHolder;
import com.example.ymchan.ymfyp.Util.EditingToolsAdapter;
import com.example.ymchan.ymfyp.Util.ToolType;
import com.example.ymchan.ymfyp.Util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

/**
 * Created by yan min on 11/6/2018.
 */

public class EditImageFragment extends Fragment implements OnPhotoEditorListener,
        EditingToolsAdapter.OnItemSelected,
        PropertiesBSFragment.Properties,
        OnSaveBitmap {

    private final static String TAG = "ymfyp.EditImageFragment";

    private PhotoEditor mPhotoEditor = null;
    private PhotoEditorView mPhotoEditorView = null;

    byte[] jpeg = null;
    Bitmap capturedImageBitmap = null;
    Bitmap editedImageBitmap = null;

    public static final String EXTRA_IMAGE_PATHS = "extra_image_paths";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;

    private PropertiesBSFragment mPropertiesBSFragment;
//    private EmojiBSFragment mEmojiBSFragment;
//    private StickerBSFragment mStickerBSFragment;

    private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private RecyclerView mRvTools, mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
//    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;

    public EditImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_image, container, false);

        mPhotoEditorView = view.findViewById(R.id.photoEditorView);
//        mPhotoEditorView.getSource().setImageResource(R.drawable.got);

        initViews(view);

        mPropertiesBSFragment = new PropertiesBSFragment();
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        jpeg = ResultHolder.getImage();
        File video = ResultHolder.getVideo();

        if (jpeg != null) {
            capturedImageBitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);

            if (capturedImageBitmap == null) {
                return;
            }

            mPhotoEditorView.getSource().setImageBitmap(capturedImageBitmap);
        }

        //Use custom font using latest support library
        Typeface mTextRobotoTf = ResourcesCompat.getFont(getActivity(), R.font.roboto_medium);

        //loading font from assest
        Typeface mEmojiTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "emojione-android.ttf");

        mPhotoEditor = new PhotoEditor.Builder(getActivity(), mPhotoEditorView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(mTextRobotoTf)
                .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();

        mPhotoEditor.setOnPhotoEditorListener(this);

        LinearLayoutManager llmTools = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

    }


    private void initViews(View view) {
        ImageView imgUndo;
        ImageView imgRedo;
//        ImageView imgCamera;
//        ImageView imgGallery;
        ImageView imgSave;
        ImageView imgClose;

        mPhotoEditorView = view.findViewById(R.id.photoEditorView);
        mTxtCurrentTool = view.findViewById(R.id.txtCurrentTool);
        mRvTools = view.findViewById(R.id.rvConstraintTools);
        mRvFilters = view.findViewById(R.id.rvFilterView);
        mRootView = view.findViewById(R.id.rootView);

        imgUndo = view.findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(mOnclickListener);

        imgRedo = view.findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(mOnclickListener);

//        imgCamera = view.findViewById(R.id.imgCamera);
//        imgCamera.setOnClickListener(mOnclickListener);
//
//        imgGallery = view.findViewById(R.id.imgGallery);
//        imgGallery.setOnClickListener(mOnclickListener);

        imgSave = view.findViewById(R.id.imgSave);
        imgSave.setOnClickListener(mOnclickListener);

        imgClose = view.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(mOnclickListener);

    }

    public View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.imgUndo:
                    mPhotoEditor.undo();
                    break;

                case R.id.imgRedo:
                    mPhotoEditor.redo();
                    break;

                case R.id.imgSave:
//                    saveImage();
//                    editedImageBitmap =  mPhotoEditorView.getSource().;
                    MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                            new PreviewFragment(),
                            PreviewFragment.class.getName(),
                            0);
                    break;

                case R.id.imgClose:
                    onPressImageClose();
                    break;

//                case R.id.imgCamera:
//                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                    break;
//
//                case R.id.imgGallery:
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
//                    break;
            }
        }
    };

    public void onPressImageClose() {
//        if (mIsFilterVisible) {
//            showFilter(false);
//            mTxtCurrentTool.setText(R.string.app_name);
//        } else if (!mPhotoEditor.isCacheEmpty()) {
//            showSaveDialog();
//        } else {
//            getActivity().getSupportFragmentManager().popBackStackImmediate();
//        }
        if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you want to exit without saving image ?");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                        new PreviewFragment(),
                        PreviewFragment.class.getName(),
                        0);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        builder.create().show();

    }

//    @SuppressLint("MissingPermission")
//    private void saveImage() {
//        Log.d(TAG, "save image to phone");
//        if (true) {
////            showLoading("Saving...");
//            File file = new File(Environment.getExternalStorageDirectory()
//                    + File.separator + ""
//                    + System.currentTimeMillis() + ".png");
//            try {
////                file.createNewFile();
//                mPhotoEditor.saveAsBitmap(new PhotoEditor.OnSaveBitmap() {
//                    @Override
//                    public void onBitmapReady(@NonNull Bitmap saveBitmap) {
////                        hideLoading();
////                        showSnackbar("Image Saved Successfully");
//                        Log.d(TAG, "Image Saved Successfully");
////                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        Log.d(TAG, "Failed to save image");
////                        hideLoading();
////                        showSnackbar("Failed to save Image");
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
////                hideLoading();
////                showSnackbar(e.getMessage());
//            }
//        }
//    }

    //Override methods for OnPhotoEditorListener
    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
//        TextEditorDialogFragment textEditorDialogFragment =
//                TextEditorDialogFragment.show(this, text, colorCode);
//        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
//            @Override
//            public void onDone(String inputText, int colorCode) {
//                mPhotoEditor.editText(rootView, inputText, colorCode);
//                mTxtCurrentTool.setText(R.string.label_text);
//            }
//        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    //Override methods for EditingToolsAdapter.OnItemSelected
    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case BRUSH:
                mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesBSFragment.show(getFragmentManager(), mPropertiesBSFragment.getTag());
                break;
//            case TEXT:
//                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
//                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
//                    @Override
//                    public void onDone(String inputText, int colorCode) {
//                        mPhotoEditor.addText(inputText, colorCode);
//                        mTxtCurrentTool.setText(R.string.label_text);
//                    }
//                });
//                break;
//            case ERASER:
//                mPhotoEditor.brushEraser();
//                mTxtCurrentTool.setText(R.string.label_eraser);
//                break;
//            case FILTER:
//                mTxtCurrentTool.setText(R.string.label_filter);
//                showFilter(true);
//                break;
//            case EMOJI:
//                mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
//                break;
//            case STICKER:
//                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
//                break;
        }
    }

    //Override methods for PropertiesBSFragment.Properties
    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    //Override methods for OnSaveBitmap

    @Override
    public void onBitmapReady(Bitmap saveBitmap) {

    }

    @Override
    public void onFailure(Exception e) {

    }
}
