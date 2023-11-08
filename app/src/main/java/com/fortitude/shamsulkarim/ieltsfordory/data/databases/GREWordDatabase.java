package com.fortitude.shamsulkarim.ieltsfordory.data.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GREWordDatabase extends SQLiteOpenHelper {



    public static final String DATABASE_NAME = "GREWordDatabase.db";
    public static final String TABLE_NAME = "advance_table";
    public static final String COL1 = "ID";
    public static final String COL2 = "WORD";
    public static final String COL3 = "FAV";
    public static final String COL4 = "LEARNED";
    public static final String COL5 = "BLACKLIST";
    public static final String COL6 = "SKIP";










    public GREWordDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT,WORD TEXT,FAV TEXT,LEARNED TEXT, BLACKLIST TEXT, SKIP TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }

    public void insertData(String word, String fav, String learned, String blacklist, String skip){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL2,word);
        cv.put(COL3,fav);
        cv.put(COL4,learned);
        cv.put(COL5, blacklist);
        cv.put(COL6, skip);


        long data = db.insert(TABLE_NAME,null,cv);


    }

    public boolean updateBlacklist(String id, String blacklist){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1,id);
        cv.put(COL5,blacklist);
        db.update(TABLE_NAME, cv, "ID = ?", new String[] {id});

        return true;
    }

    public boolean updateskip(String id, String skip){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1,id);
        cv.put(COL6,skip);
        db.update(TABLE_NAME, cv, "ID = ?", new String[] {id});

        return true;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_NAME,null);


    }



    public boolean dbUpdate(String id, String newInt){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1,id);
        cv.put(COL2,newInt);
        db.update(TABLE_NAME, cv, "ID = ?", new String[] {id});

        return true;
    }

    public void updateFav(String id, String fav){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1,id);
        cv.put(COL3,fav);
        db.update(TABLE_NAME, cv, "ID = ?", new String[] {id});

    }

    public void updateLearned(String id, String learned){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1,id);
        cv.put(COL4,learned);
        db.update(TABLE_NAME, cv, "ID = ?", new String[] {id});

    }


    public int deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});



    }

    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }


}
