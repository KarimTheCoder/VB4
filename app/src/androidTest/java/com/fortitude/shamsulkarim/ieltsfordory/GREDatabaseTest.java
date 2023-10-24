package com.fortitude.shamsulkarim.ieltsfordory;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GREDatabaseTest {

    GREWordDatabase database;


    @Before
    public void setUp(){

        Context context = ApplicationProvider.getApplicationContext();
        database = new GREWordDatabase(context);

    }

    @Test
    public void favorite() throws InterruptedException {

        List<String> list = new ArrayList<>();
        Cursor res = database.getData();

        while (res.moveToNext()){

            list.add(res.getString(1));
        }

        Log.i("Test GRE","Fav size: "+list.size());


    }

    @After
    public void close(){

        database.close();
    }






}
