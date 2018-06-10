package com.example.farouk.jackretary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import java.util.Timer;
import java.util.TimerTask;

public class Preloader extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preloader);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try{
                        JackOpenHelper jackOpenHelper = new JackOpenHelper(Preloader.this);
                        SQLiteDatabase db = jackOpenHelper.getReadableDatabase();
                        Cursor cursor = db.query("information",new String[]{"trials"},null,null,null,null,null);
                        if(cursor.moveToFirst()){
                            int trials = cursor.getInt(0);
                            if(trials  <= 10 && trials > 0){
                                int newtrials = trials - 1;
                                if(newtrials < 0)
                                    newtrials = 0;
                                String constraint = "trials = "+trials;
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("trials",newtrials);
                                db.update("information",contentValues,constraint,null);
                                startActivity(new Intent(Preloader.this,SubjectList.class));
                            }else if(trials == 20)
                                startActivity(new Intent(Preloader.this,SubjectList.class));
                            else{
                                Intent intent = new Intent(Preloader.this,Activate.class);
                                intent.putExtra("Source","preloader");
                                startActivity(intent);

                            }
                        }
                        db.close();
                    }catch (Exception e){
                        // write something meaningfull
                    }
                }
            },3000);



            }
        }
