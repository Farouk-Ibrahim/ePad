package com.example.farouk.jackretary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Activate extends Activity {

    private String constraint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);
        JackOpenHelper jackOpenHelper = new JackOpenHelper(this);
        SQLiteDatabase db = jackOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("information",new String[]{"trials"},null,null,null,null,null);
        if(cursor.moveToFirst()) {
            int trials = cursor.getInt(0);
            if (trials == 0){
                TextView trialDisplay = (TextView) findViewById(R.id.trialCount);
                trialDisplay.setText("Your trials have expired!");
                trialDisplay.setTextColor(Color.RED);
                trialDisplay.setVisibility(View.VISIBLE);
                constraint = "trials = "+0;
            }
            else if(trials <=10) {
                TextView trialDisplay = (TextView) findViewById(R.id.trialCount);
                int trialCount = cursor.getInt(0);
                trialDisplay.setText("You have "+trialCount+" trial(s) left");
                trialDisplay.setVisibility(View.VISIBLE);
                constraint = "trials = "+trialCount;

            }else{
                TextView trialDisplay = (TextView) findViewById(R.id.trialCount);
                trialDisplay.setText("This product has been activated. Thanks for your support. Tap your mobile option for app info");
                trialDisplay.setTextColor(Color.GREEN);
                trialDisplay.setVisibility(View.VISIBLE);
                TextView actMessage =  (TextView) findViewById(R.id.activationMessage);
                actMessage.setVisibility(View.GONE);
                TextView activationKey = (TextView) findViewById(R.id.activationKey);
                activationKey.setVisibility(View.GONE);
                Button activateButton = (Button) findViewById(R.id.activationButton);
                activateButton.setVisibility(View.GONE);
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuItem  information = menu.add(0,1,Menu.NONE,"Information");
        information.setIntent(new Intent(this,Information.class));
        return true;
    }
    @Override
    public void onBackPressed(){
        String source =  getIntent().getStringExtra("Source");
        if(source.equals("preloader")){
        }else{
            finish();
        }
    }
    public void onActivate(View view){
        TextView activationKey = (TextView) findViewById(R.id.activationKey);
        String key = activationKey.getText().toString();
        if(key.equalsIgnoreCase("makeActivate")){
            JackOpenHelper jackOpenHelper = new JackOpenHelper(this);
            SQLiteDatabase db = jackOpenHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("trials",20);
            db.update("information",contentValues,constraint,null);
            db.close();
            startActivity(new Intent(this,SubjectList.class));
        }else{
            TextView trialDisplay = (TextView) findViewById(R.id.trialCount);
            trialDisplay.setText("Wrong Activation Key!");
            trialDisplay.setTextColor(Color.RED);

        }
    }
}
