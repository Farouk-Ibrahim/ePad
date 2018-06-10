package com.example.farouk.jackretary;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by farouk on 11/23/2017.
 */

public class JackOpenHelper extends SQLiteOpenHelper {


    public JackOpenHelper(Context context){

        super(context,"jackretary",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE subjects(_id INTEGER PRIMARY KEY AUTOINCREMENT, subjectName TEXT, errors INTEGER);");
        sqLiteDatabase.execSQL("CREATE TABLE information(_id INTEGER PRIMARY KEY AUTOINCREMENT,trials INTEGER);");
        ContentValues contentValues = new ContentValues();
        contentValues.put("trials", 10);
        sqLiteDatabase.insert("information",null,contentValues);ter[e[t]]
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase   , int i, int i1) {

    }
}
