package com.example.ymchan.ymfyp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by yan min on 9/6/2018.
 */

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "ymfyp.MainActivity";
    public final static int LAYOUT_MAIN_ID = R.id.main_container;

    private Context mContext = null;

    private static final int REQUEST = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                //do here
            }
        } else {
            //do here
        }

        openFragment(MainActivity.LAYOUT_MAIN_ID,
                        new HomeFragment(),
                        HomeFragment.class.getName(),
                        0);
    }

    public void openFragment(int containerViewId, Fragment fragment, String tag, int title) {
        getSupportFragmentManager().popBackStackImmediate(containerViewId + "1", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerViewId, fragment, tag);
        transaction.commit();

    }

    public void pushFragment(int containerViewId, Fragment fragment, String tag, int title) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerViewId, fragment, tag);
        transaction.replace(containerViewId, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /*
     * static methods for child fragments
     */
    public static void pushFragment(FragmentActivity activity, int containerViewId, Fragment fragment, String tag, int title) {
        try {
            if (activity != null && activity instanceof MainActivity) {
                ((MainActivity) activity).pushFragment(containerViewId, fragment, tag, title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.storage_access_denied), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}