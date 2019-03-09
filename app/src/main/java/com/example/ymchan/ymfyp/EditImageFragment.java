package com.example.ymchan.ymfyp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.effect.EffectFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
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
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ymchan.ymfyp.Image.ResultHolder;
import com.example.ymchan.ymfyp.Util.CustomStickersDatabase;
import com.example.ymchan.ymfyp.Adapters.EditingToolsAdapter;
import com.example.ymchan.ymfyp.Util.FilterListener;
import com.example.ymchan.ymfyp.Adapters.FilterViewAdapter;
import com.example.ymchan.ymfyp.Util.ToolType;
import com.example.ymchan.ymfyp.Util.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import ja.burhanrashid52.photoeditor.CustomEffect;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.ViewType;

/**
 * Created by yan min on 11/6/2018.
 */

public class EditImageFragment extends Fragment implements OnPhotoEditorListener,
        EditingToolsAdapter.OnItemSelected,
        AdjustBSFragment.Adjustments,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener,
        LocationFragment.LocationListener,
//        FramesBSFragment.FramesListener,
        FilterListener,
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
    public static final int READ_WRITE_STORAGE = 52;

    private int mRotateValue = 0;

    private AdjustBSFragment mAdjustBSFragment;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private LocationBSFragment mLocBSFragment;

    private ImageView mSelectedFrameImageView;

    private ProgressDialog mProgressDialog;

    private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private RecyclerView mRvTools, mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;

    private CustomStickersDatabase mCustomStickerDB;

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

        mAdjustBSFragment = new AdjustBSFragment();
        mAdjustBSFragment.setAdjustmentsChangeListener(this);

        mPropertiesBSFragment = new PropertiesBSFragment();
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        mEmojiBSFragment = new EmojiBSFragment();
        mEmojiBSFragment.setEmojiListener(this);

        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);

        mLocBSFragment = new LocationBSFragment();
        mLocBSFragment.setLocationListener(this);

//        mFramesBSFragment = new FramesBSFragment();
//        mFramesBSFragment.setFramesListener(this);

        mSelectedFrameImageView = view.findViewById(R.id.selectedFrameView);

        mCustomStickerDB = new CustomStickersDatabase(getContext());

        mPhotoEditor = new PhotoEditor.Builder(getActivity(), mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

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

        LinearLayoutManager llmTools = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmFilters = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);

        mPhotoEditor = new PhotoEditor.Builder(getActivity(), mPhotoEditorView)
                .setPinchTextScalable(true)
//                .setDefaultTextTypeface(mTextRobotoTf)
//                .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();

        mPhotoEditor.setOnPhotoEditorListener(this);

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
                    saveImage();
//                    editedImageBitmap =  mPhotoEditorView.getSource().;
//                    MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
//                            new PreviewFragment(),
//                            PreviewFragment.class.getName(),
//                            0);
                    break;

                case R.id.imgClose:
                    onPressImageClose();
                    break;

            }
        }
    };

    public void onPressImageClose() {
        if (mIsFilterVisible) {
            showFilter(false);
            mTxtCurrentTool.setText(R.string.app_name);
        }
//        } else if (!mPhotoEditor.isCacheEmpty()) {
//            showSaveDialog();
//        } else {
//            getActivity().getSupportFragmentManager().popBackStackImmediate();
//        }
        else if (!mPhotoEditor.isCacheEmpty()) {
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

    public boolean requestPermission(String permission) {
        boolean isGranted = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{permission},
                    READ_WRITE_STORAGE);
        }
        return isGranted;
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.d(TAG, "Saving image");
            showLoading("Saving...");
//            File file = new File(Environment.getExternalStorageDirectory()
//                    + File.separator + ""
//                    + System.currentTimeMillis() + ".png");
            File cacheFile = Util.makeTempFile(getView().getContext(), ".jpg");

            try {
                cacheFile.createNewFile();

                SaveSettings saveSettings = new SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build();

                mPhotoEditor.saveAsFile(cacheFile.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        Log.d(TAG, "Image Saved Successfully");
                        hideLoading();

                        Bitmap image = BitmapFactory.decodeFile(imagePath);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] editedJpeg = stream.toByteArray();

                        ResultHolder.dispose();
                        ResultHolder.setImage(editedJpeg);

                        MainActivity.pushFragment(getActivity(), MainActivity.LAYOUT_MAIN_ID,
                            new PreviewFragment(),
                            PreviewFragment.class.getName(),
                            0);
//                        showSnackbar("Image Saved Successfully");
                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d(TAG, "Failed to save image");
                        hideLoading();
//                        showSnackbar("Failed to save Image");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
//                hideLoading();
//                showSnackbar(e.getMessage());
            }
        }

    }

    protected void showLoading(@NonNull String message) {
        mProgressDialog = new ProgressDialog(getView().getContext());
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    //Override methods for OnPhotoEditorListener
    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(getActivity(), text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.editText(rootView, inputText, colorCode);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
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
//            case CROP:
//                break;
            case ROTATE:
                if(mRotateValue == 0) {
                    mRotateValue = 90;
                } else if (mRotateValue == 90) {
                    mRotateValue = 180;
                } else if (mRotateValue == 180) {
                    mRotateValue = 270;
                } else {
                    mRotateValue = 0;
                }
                CustomEffect customEffect = new CustomEffect.Builder(EffectFactory.EFFECT_ROTATE)
                        .setParameter("angle", mRotateValue)
                        .build();
                mPhotoEditor.setFilterEffect(customEffect);
                break;
            case ADJUST:
                mAdjustBSFragment.show(getFragmentManager(), mAdjustBSFragment.getTag());
                break;
            case BRUSH:
                mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesBSFragment.show(getFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(getActivity());
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        mPhotoEditor.addText(inputText, colorCode);
                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;
            case ERASER:
                mPhotoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser);
                break;
            case FILTER:
                mTxtCurrentTool.setText(R.string.label_filter);
                showFilter(true);
                break;
            case EMOJI:
                mEmojiBSFragment.show(getFragmentManager(), mEmojiBSFragment.getTag());
                break;
            case STICKER:
                mStickerBSFragment.show(getFragmentManager(), mStickerBSFragment.getTag());
                break;
            case LOCATION:
                mLocBSFragment.show(getFragmentManager(), mLocBSFragment.getTag());
                break;
        }
    }

    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    //Override methods for AdjustBSFragment.Adjustments


    @Override
    public void onBrightnessChanged(int brightness) {
        CustomEffect customEffect = new CustomEffect.Builder(EffectFactory.EFFECT_BRIGHTNESS)
                .setParameter("brightness", ((float)brightness/50))
                .build();
        mPhotoEditor.setFilterEffect(customEffect);
    }

    @Override
    public void onContrastChanged(int contrast) {
        CustomEffect customEffect = new CustomEffect.Builder(EffectFactory.EFFECT_CONTRAST)
                .setParameter("contrast", (float)((float)contrast/50))
                .build();
        mPhotoEditor.setFilterEffect(customEffect);
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

    //Override methods for EmojiBSFragment.EmojiListener,
    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);
    }

    //Override methods for StickerBSFragment.StickerListener
    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
    }

//    //Override methods for FramesBSFragment.FramesListener
//    @Override
//    public void onFrameClick(Bitmap bitmap) {
//        ViewGroup.LayoutParams params = mPhotoEditorView.getLayoutParams();
//        mSelectedFrameImageView.setLayoutParams(params);
//        mSelectedFrameImageView.setImageBitmap(bitmap);
////        mPhotoEditor.addImage(bitmap);
////        mTxtCurrentTool.setText(R.string.label_frames);
//    }

    //Override method for LocationListener


    @Override
    public void onLocationSelected(String textLocation) {
//        String loc = MainActivity.getLocationString();
        Log.d(TAG, "location = " + textLocation);
        mPhotoEditor.addTextWBG("\uD83D\uDCCD " + textLocation, Color.BLACK);
    }

    //Override methods for FilterListener
    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }


    //Override methods for OnSaveBitmap

    @Override
    public void onBitmapReady(Bitmap saveBitmap) {

    }

    @Override
    public void onFailure(Exception e) {

    }
}
