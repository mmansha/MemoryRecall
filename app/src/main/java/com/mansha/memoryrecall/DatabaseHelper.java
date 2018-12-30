package com.mansha.memoryrecall;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "memoryrecalldb";
    private static final int DB_VERSION = 1;
    private Context context;
    private static DatabaseHelper sDBInstance;

    public static synchronized DatabaseHelper getsDBInstance(Context context){
        if (sDBInstance == null){
            sDBInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sDBInstance;
    }

    private DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        Log.d("DatabaseHelper", "Database Helper constructor called");
        this.context = context;
//       context.deleteDatabase("memoryrecalldb");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "OnCreate methrod called");

        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.d("DatabaseHelper", "Update my database called");
        if (oldVersion < 1){
            db.execSQL("Create table MemoryRecall (" +
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "CATEGORY TEXT, " +
                            "GUID TEXT, " +
                            "NAME TEXT, " +
                            "IMAGE TEXT, " +
                            "SOUND TEXT, " +
                            "SEQ INTEGER" +
                            ");");

//            insertData(db, "People", java.util.UUID.randomUUID().toString(), "Camel", R.drawable.cat, null, 1);

        }


    }

    private byte[] getImageAsByteArray(int raw_image_name){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapDrawable bitmapDrawable = (BitmapDrawable)context.getDrawable(raw_image_name);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] image = baos.toByteArray();
        Log.d("DatabaseHelper", "Byte array size = " + image.length);
        return image;


    }

    protected  void insertData(SQLiteDatabase db, String category, String guid, String name, int imageId, byte[] sound, int seq){

        ContentValues dbValues = new ContentValues();
        dbValues.put("NAME", name);
        dbValues.put("CATEGORY", category);
        dbValues.put("GUID", guid);
        dbValues.put("IMAGE", getImageAsByteArray(imageId));
        dbValues.put("SOUND", sound);
        dbValues.put("SEQ", seq);
        db.insert("MemoryRecall", null, dbValues);
        Log.d("DatabaseHelper", "Inserted record into Database");
    }

    protected   void insertData(SQLiteDatabase db, String category, String guid, String name, String imageIdPath, String soundFilePath, int seq){

        ContentValues dbValues = new ContentValues();
        dbValues.put("NAME", name);
        dbValues.put("CATEGORY", category);
        dbValues.put("GUID", guid);
        dbValues.put("IMAGE", imageIdPath);
        dbValues.put("SOUND", soundFilePath);
        dbValues.put("SEQ", seq);
        db.insert("MemoryRecall", null, dbValues);
        Log.d("DatabaseHelper", "Inserted record into Database");
    }

    protected   void updateData(SQLiteDatabase db, String category, String guid, String name, String imageIdPath, String soundFilePath, int seq){

        ContentValues dbValues = new ContentValues();
        dbValues.put("NAME", name);
        dbValues.put("CATEGORY", category);
        dbValues.put("IMAGE", imageIdPath);
        dbValues.put("SOUND", soundFilePath);
        dbValues.put("SEQ", seq);
        db.update("MemoryRecall", dbValues, "GUID=?", new String[]{guid});
        Log.d("DatabaseHelper", "Inserted record into Database");
    }

    protected int deleteRow(SQLiteDatabase db, String guidToDetele){
        int returnValue = 0;
        returnValue = db.delete("MemoryRecall", "GUID=?", new String[]{guidToDetele});
        Log.d("DatabaseHelper", "Number of rows deleted " + returnValue);
        return returnValue;
    }

    protected Cursor getCursor(SQLiteDatabase db, String category){
        Cursor cursor = db.query("MemoryRecall", new String[]{"NAME", "IMAGE", "SOUND", "SEQ", "GUID"}, "CATEGORY = ?", new String[] {category}, null, null, "SEQ");
        return cursor;
    }
}
