package com.fortitude.shamsulkarim.ieltsfordory.data.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by sk on 1/1/17.
 */

/**
 * SAT vocabulary database helper with WAL, indexes, and synchronized transactional writes.
 */
public class SATWordDatabase extends SQLiteOpenHelper{
    private final Object lock = new Object();
    private static volatile SATWordDatabase INSTANCE;

    public static final String DATABASE_NAME = "SATWordDatabase.db";
    public static final String TABLE_NAME = "advance_table";
    public static final String COL1 = "ID";
    public static final String COL2 = "WORD";
    public static final String COL3 = "FAV";
    public static final String COL4 = "LEARNED";
    public static final String COL5 = "BLACKLIST";
    public static final String COL6 = "SKIP";











    public SATWordDatabase(Context context) {
        super(context, DATABASE_NAME, null, 2);
        setWriteAheadLoggingEnabled(true);
    }

    public static SATWordDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SATWordDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SATWordDatabase(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT,WORD TEXT,FAV TEXT,LEARNED TEXT, BLACKLIST TEXT, SKIP TEXT)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_"+TABLE_NAME+"_fav ON "+TABLE_NAME+"("+COL3+")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_"+TABLE_NAME+"_learned ON "+TABLE_NAME+"("+COL4+")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_"+TABLE_NAME+"_fav ON "+TABLE_NAME+"("+COL3+")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_"+TABLE_NAME+"_learned ON "+TABLE_NAME+"("+COL4+")");
    }

    public void insertData(String word, String fav, String learned, String blacklist, String skip){
        if (word == null) throw new IllegalArgumentException("word is required");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL2,word);
        cv.put(COL3,fav);
        cv.put(COL4,learned);
        cv.put(COL5, blacklist);
        cv.put(COL6, skip);
        long rowId = db.insert(TABLE_NAME,null,cv);
        Log.d("DB_SAT","insertData rowId="+rowId);
    }

    public boolean updateBlacklist(String id, String blacklist){
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("id required");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1,id);
        cv.put(COL5,blacklist);
        synchronized (lock) {
            db.beginTransaction();
            try {
                db.update(TABLE_NAME, cv, "ID = ?", new String[] {id});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        return true;
    }

    public boolean updateskip(String id, String skip){
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("id required");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1,id);
        cv.put(COL6,skip);
        synchronized (lock) {
            db.beginTransaction();
            try {
                db.update(TABLE_NAME, cv, "ID = ?", new String[] {id});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        return true;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from "+TABLE_NAME,null);
    }



    public boolean dbUpdate(String id, String newInt){
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("id required");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1,id);
        cv.put(COL2,newInt);
        synchronized (lock) {
            db.beginTransaction();
            try {
                db.update(TABLE_NAME, cv, "ID = ?", new String[] {id});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        return true;
    }

    public void updateFav(String id, String fav){
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("id required");
        if (fav == null || !("True".equalsIgnoreCase(fav) || "False".equalsIgnoreCase(fav))) throw new IllegalArgumentException("fav must be True/False");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1,id);
        cv.put(COL3,fav);
        synchronized (lock) {
            db.beginTransaction();
            try {
                db.update(TABLE_NAME, cv, "ID = ?", new String[] {id});
                db.setTransactionSuccessful();
                Log.d("DB_SAT","updateFav id="+id+" fav="+fav);
            } finally {
                db.endTransaction();
            }
        }
    }

    public void updateLearned(String id, String learned){
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("id required");
        if (learned == null || !("True".equalsIgnoreCase(learned) || "False".equalsIgnoreCase(learned))) throw new IllegalArgumentException("learned must be True/False");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1,id);
        cv.put(COL4,learned);
        synchronized (lock) {
            db.beginTransaction();
            try {
                db.update(TABLE_NAME, cv, "ID = ?", new String[] {id});
                db.setTransactionSuccessful();
                Log.d("DB_SAT","updateLearned id="+id+" learned="+learned);
            } finally {
                db.endTransaction();
            }
        }
    }


    public int deleteData(String id){
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("id required");
        SQLiteDatabase db = this.getWritableDatabase();
        synchronized (lock) {
            db.beginTransaction();
            try {
                int rows = db.delete(TABLE_NAME, "ID = ?", new String[]{id});
                db.setTransactionSuccessful();
                return rows;
            } finally {
                db.endTransaction();
            }
        }
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


