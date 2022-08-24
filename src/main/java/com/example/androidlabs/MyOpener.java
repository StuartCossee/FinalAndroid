package com.example.androidlabs;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * The class My opener extends SQ lite open helper
 */
public class MyOpener extends SQLiteOpenHelper {
    public final static String DB_NAME = "db";
    public final static int VER_NUM = 1;
    public final static String TB_NAME = "ARTICLES";
    public final static String COL_URG = "URGENT";
    public final static String COL_LOG = "LOG";
    public final static String COL_ID = "_id";


    /**
     *
     * My opener
     *
     * @param ctx  the ctx
     * @return public
     */
    public MyOpener(Context ctx){

        super(ctx, DB_NAME, null, VER_NUM);
    }

    @Override

    /**
     *
     * On create
     *
     * @param sqLiteDatabase  the sq lite database
     */
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TB_NAME + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_LOG +" text," + COL_URG + " integer);" );
    }

    @Override

/**
 *
 * On upgrade
 *
 * @param sqLiteDatabase  the sq lite database
 * @param i  the i
 * @param i1  the i1
 */
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


    }

    @SuppressLint("Range")

    /**
     *
     * Print cursor
     *
     */
    public void printCursor(){
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM LOGS_TB");

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(POSTS_SELECT_QUERY, null);
        Log.v("Lab", String.valueOf(c.getColumnCount()));
        Log.v("Lab", String.valueOf(c.getCount()));
        Log.v("Lab", String.valueOf(c.getColumnNames().toString()));
        try {
            if (c.moveToFirst()) {
                do {
                    Item newItem = new Item();
                    newItem.title = c.getString(c.getColumnIndex("LOG"));
                    if(Integer.parseInt(c.getString(c.getColumnIndex("URGENT"))) == 1){
                        newItem.b = true;
                    }
                    else
                        newItem.b = false;
                    Log.v("Lab", newItem.toString());
                } while(c.moveToNext());
            }
        } catch (Exception e) {
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
    }
    // Insert a post into the database

    /**
     *
     * Add post
     *
     * @param item  the item
     */
    public void addPost(Item item) {

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).

            ContentValues values = new ContentValues();
            values.put("LOG", item.title);
            values.put("URGENT", item.b ? 1: 0);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            long id = db.insertOrThrow("LOGS_TB", null, values);

            db.setTransactionSuccessful();
            this.getAllPosts()
            ;        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
    }


    /**
     *
     * Remove
     *
     * @param id  the id
     */
    public void remove(long id) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete("LOGS_TB", COL_ID+ '='+id, null);
        this.getAllPosts();

    }

    @SuppressLint("Range")

/**
 *
 * Gets the all posts
 *
 * @return the all posts
 */
    public List<Item> getAllPosts() {

        List<Item> items = new ArrayList<>();

        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM LOGS_TB");

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Item newItem = new Item();
                    newItem.title = cursor.getString(cursor.getColumnIndex("LOG"));
                    if(Integer.parseInt(cursor.getString(cursor.getColumnIndex("URGENT"))) == 1){
                        newItem.b = true;
                    }
                    else
                        newItem.b = false;
                    items.add(newItem);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return items;
    }

}
