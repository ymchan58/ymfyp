package com.example.ymchan.ymfyp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.ymchan.ymfyp.Image.ResultHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ReceiveActivity extends AppCompatActivity {

    private final static String TAG = "ymfyp.ReceiveActivity";

    public static final String EXTRA_MESSAGE = "com.example.ymchan.ymfyp.MESSAGE";

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        // Get the intent that started this activity
        Intent receivedIntent = getIntent();
        Uri receivedUri = (Uri)receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);

        //find out what we are dealing with
        String receivedType = receivedIntent.getType();

        //get the action
        String receivedAction = receivedIntent.getAction();

        Log.d(TAG, receivedType);

//        if(receivedAction.equals(Intent.ACTION_EDIT)){
//            if (receivedType.startsWith("image/")) {
//                receivedUri = receivedIntent.getData();
//            }
//        }

        Log.d(TAG, receivedUri.toString());


        //make sure it's an action and type we can handle

        if(receivedAction.equals(Intent.ACTION_SEND)){ //content is being shared
//        if(receivedAction.equals(Intent.ACTION_SEND) || receivedAction.equals(Intent.ACTION_EDIT)){ //content is being shared
            // Figure out what to do based on the intent type
            if (receivedIntent.getType().indexOf("image/") != -1) {
                Bitmap image = null;
                long captureStartTime = System.currentTimeMillis();
                try {
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), receivedUri);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] jpeg = stream.toByteArray();
                    image.recycle();

                    long callbackTime = System.currentTimeMillis();
                    ResultHolder.dispose();
                    ResultHolder.setImage(jpeg);
//                ResultHolder.setNativeCaptureSize(Integer.parseInt(jpeg.length));
                    ResultHolder.setTimeToCallback(callbackTime - captureStartTime);

//                    openFragment(MainActivity.LAYOUT_MAIN_ID,
//                            new PreviewFragment(),
//                            PreviewFragment.class.getName(),
//                            0);

                    Intent intent = new Intent(ReceiveActivity.this,
                            MainActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, "FromReceiveActivity");
                    startActivity(intent);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        } else if(receivedAction.equals(Intent.ACTION_MAIN)){ //app has been launched directly, not from share list

        }

    }
}
