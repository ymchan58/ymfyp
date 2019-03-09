package com.example.ymchan.ymfyp.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.example.ymchan.ymfyp.Image.StickerModel;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yan min on 10/1/2019
 */
public class CustomStickersDatabase {
    private static final String TAG = "ymfyp.CustomStickersDB";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    //Database name
    private static final String DATABASE_NAME = "CustomStickersDB";

    // Login table name
    private static final String DATABASE_TABLE = "CustomStickerList";

    // Login Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_STICKER_NAME = "sticker_name";
    public static final String KEY_CREATE_DATE = "sticker_date";
    public static final String KEY_DATA = "sticker_data";

    private DbHelper myHelper;
    private Context myContext;
    private SQLiteDatabase myDatabase;

    private static final int SELECT_COUNT = 1;
    private static final int SELECT_ITEM = 2;
    private static final int CONDITION = 3;

    private List<byte[]> fromDBStickerList;
    private List<Integer> fromDBStickerIDs;
    private List<String> fromDBStickerNames;
    private List<String> fromDBStickerDates;

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub

            String CREATE_TABLE = "CREATE TABLE " + DATABASE_TABLE + " ( "
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_STICKER_NAME + " TEXT, "
                    + KEY_CREATE_DATE + " TEXT, "
                    + KEY_DATA + " BLOB " + ")";

            db.execSQL(CREATE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
            android.util.Log.w("Upgrade", "upgrade");
        }

    }

    public CustomStickersDatabase(Context c) throws SQLException {
        myContext = c;
    }

    public CustomStickersDatabase open() throws SQLException {
        myHelper = new DbHelper(myContext);
        myDatabase = myHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        myHelper.close();
    }

    public long createEntry(String stickerName) {
        // TODO Auto-generated method stub
        ContentValues cv = new ContentValues();
        cv.put(KEY_STICKER_NAME, stickerName);

        return myDatabase.insert(DATABASE_TABLE, null, cv);
    }

    //Add Bitmap byte array to DB
    public void addBitmap(String stickerName, String createDate, byte[] image) throws SQLException {
        Log.d(TAG, "addBitmap called");
        ContentValues cv = new ContentValues();
        cv.put(KEY_STICKER_NAME, stickerName);
        cv.put(KEY_CREATE_DATE, createDate);
        cv.put(KEY_DATA, image);

        Log.d(TAG, "database_table = " + DATABASE_TABLE);
        Log.d(TAG, "content values = " + cv);

        //try catch for debugging purpose
        long mid = 0;

        try
        {
            mid = myDatabase.insertOrThrow(DATABASE_TABLE, null, cv);
            Toast.makeText(myContext, "Custom Sticker saved", Toast.LENGTH_LONG).show();
        }
        catch(SQLException e)
        {
            Toast.makeText(myContext, "Custom Sticker fail", Toast.LENGTH_LONG).show();
            Log.e("ymfyp.CustomStickersDB","SQLException"+String.valueOf(e.getMessage()));
            e.printStackTrace();
        }
    }

    //Get all bitmap
    public List<byte[]> getAllBitmap(){
        Log.d(TAG, "getAllBitmap called");
        fromDBStickerList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE;
        Cursor c = myDatabase.rawQuery(selectQuery, null);
        Log.d(TAG, "cursor c = " + c);

        byte[] result = null;
        if(c.moveToFirst()){
            do {
                result = c.getBlob(c.getColumnIndex(KEY_DATA));
                Log.d(TAG, "result = " + result);
                fromDBStickerList.add(result);
            } while(c.moveToNext());
        }

        Log.d(TAG, "fromDBStickerList = " + fromDBStickerList);

        return fromDBStickerList;
    }

    //Get all ID
    public List<Integer> getAllID(){
        Log.d(TAG, "getAllID called");
        fromDBStickerIDs = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE;
        Cursor c = myDatabase.rawQuery(selectQuery, null);
        Log.d(TAG, "cursor c = " + c);

        int result = -1;
        if(c.moveToFirst()){
            do {
                result = c.getInt(c.getColumnIndex(KEY_ID));
                Log.d(TAG, "result = " + result);
                fromDBStickerIDs.add(result);
            } while(c.moveToNext());
        }

        Log.d(TAG, "fromDBStickerIDs = " + fromDBStickerIDs);

        return fromDBStickerIDs;
    }

    //Get all Name
    public List<String> getAllName(){
        Log.d(TAG, "getAllName called");
        fromDBStickerNames = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE;
        Cursor c = myDatabase.rawQuery(selectQuery, null);
        Log.d(TAG, "cursor c = " + c);

        String result;
        if(c.moveToFirst()){
            do {
                result = c.getString(c.getColumnIndex(KEY_STICKER_NAME));
                Log.d(TAG, "result = " + result);
                fromDBStickerNames.add(result);
            } while(c.moveToNext());
        }

        Log.d(TAG, "fromDBStickerIDs = " + fromDBStickerIDs);

        return fromDBStickerNames;
    }

    //Get all Date
    public List<String> getAllDate(){
        Log.d(TAG, "getAllDate called");
        fromDBStickerDates = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE;
        Cursor c = myDatabase.rawQuery(selectQuery, null);
        Log.d(TAG, "cursor c = " + c);

        String result;
        if(c.moveToFirst()){
            do {
                result = c.getString(c.getColumnIndex(KEY_CREATE_DATE));
                Log.d(TAG, "result = " + result);
                fromDBStickerDates.add(result);
            } while(c.moveToNext());
        }

        Log.d(TAG, "fromDBStickerIDs = " + fromDBStickerIDs);

        return fromDBStickerDates;
    }

    public boolean deleteEntry(int l) throws SQLException {
        Log.d(TAG, "deleteEntry sticker = " + l);
        return myDatabase.delete(DATABASE_TABLE, KEY_ID + "=" + l, null) > 0;

    }

    public Cursor getAllItems() {
        return myDatabase.query(
                DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }


}
