package com.fortitude.shamsulkarim.ieltsfordory.data.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JustLearnedDatabaseAdvance extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "JustLearnedDatabaseAdvance.db";
    public static final String TABLE_NAME = "JustLearnedDatabase_table";

    public static final String COL1 = "ID";
    public static final String COL2 = "WORDDATABASEPOS";
    public static final String COL3 = "WORD";
    public static final String COL4 = "TRANSLATION";
    public static final String COL5 = "SECONDTRANSLATION";
    public static final String COL6 = "PRONUN";
    public static final String COL7 = "GRAMMAR";
    public static final String COL8 = "EXAMPLE1";
    public static final String COL9 = "EXAMPLE2";
    public static final String COL10= "EXAMPLE3";
    public static final String COL11 = "VOCABULARYTYPE";
    public static final String COL12 = "LEARNED";
    public static final String COL13 = "FAV";
    public static final String COL14 = "MOSTMISTAKEN";


    public JustLearnedDatabaseAdvance(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }
    public void removeAll()
    {
//        // db.delete(String tableName, String whereClause, String[] whereArgs);
//        // If whereClause is null, it will delete all rows.
//        SQLiteDatabase db = getWritableDatabase(); // helper is object extends SQLiteOpenHelper
//        db.delete(COL1, null, null);
//        db.delete(COL2, null, null);

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);


    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT,WORDDATABASEPOS TEXT,WORD TEXT,TRANSLATION TEXT, SECONDTRANSLATION TEXT, PRONUN TEXT, GRAMMAR TEXT, EXAMPLE1 TEXT, EXAMPLE2 TEXT, EXAMPLE3 TEXT, VOCABULARYTYPE TEXT, LEARNED TEXT, FAV TEXT, MOSTMISTAKEN TEXT)");

    }

    public void insertData(int collumPos, String wordPos, String word, String translation, String secondTranslation, String pronun, String grammar, String example1, String example2, String example3, String vocabularyType, String learned, String fav, String mostMistaken){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL1,  collumPos+"");
        cv.put(COL2,  wordPos);
        cv.put(COL3,  word);
        cv.put(COL4,  translation);
        cv.put(COL5,  secondTranslation);
        cv.put(COL6,  pronun);
        cv.put(COL7,  grammar);
        cv.put(COL8,  example1);
        cv.put(COL9,  example2);
        cv.put(COL10, example3);
        cv.put(COL11, vocabularyType);
        cv.put(COL12, learned);
        cv.put(COL13, fav);
        cv.put(COL14, mostMistaken);


        long data = db.insert(TABLE_NAME,null,cv);


    }











    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }


    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_NAME,null);


    }
}
