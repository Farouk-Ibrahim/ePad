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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class SubjectList extends Activity {
    private boolean editAppeared;
    private boolean removeButtonPressed;
    private SQLiteDatabase db;
    private Cursor mainCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);
        editAppeared = false;
        removeButtonPressed = false;
        ListView listView = (ListView) findViewById(R.id.subjectList);
        try{
            JackOpenHelper jackOpenHelper = new JackOpenHelper(this);
            db = jackOpenHelper.getReadableDatabase();
            mainCursor = db.query("subjects",new String[]{"_id","subjectName","errors"},null,null,null,null,null);
            CursorAdapter theAdapter = new SimpleCursorAdapter(this,R.layout.subject_item,mainCursor,new String[]
                    {"subjectName","errors"},new int[]{R.id.subNameText,R.id.subErrorText},0);

            listView.setAdapter(theAdapter);
        }catch (Exception e){
           // write something meaningfull
        }
        AdapterView.OnItemClickListener itemClickListener  = new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> listView,View view, int position,long id){
                LinearLayout linearLayout = (LinearLayout) view;
                TextView textView = (TextView)linearLayout.getChildAt(1);
                String selectedName = textView.getText().toString();

                if(removeButtonPressed == true) {
                    JackOpenHelper jackOpenHelper = new JackOpenHelper(SubjectList.this);
                    db = jackOpenHelper.getWritableDatabase();
                    String replacedName = selectedName.replace(' ','_');
                    db.delete("subjects", "subjectName = ?", new String[]{selectedName});
                    Cursor cursorA = db.query(replacedName,new String[]{"contentName"},null,null,null,null,null);
                    if(cursorA.moveToFirst()){
                        String topicName = cursorA.getString(0);
                        topicName = topicName.replace(" ","_");
                        db.execSQL("DROP TABLE IF EXISTS "+topicName);
                        while(cursorA.moveToNext()){
                            String nextTopicName = cursorA.getString(0);
                            nextTopicName = nextTopicName.replace(" ","_");
                            db.execSQL("DROP TABLE IF EXISTS "+nextTopicName);
                        }

                    }
                    db.execSQL("DROP TABLE IF EXISTS "+replacedName);
                    db.close();
                    db = jackOpenHelper.getReadableDatabase();
                    Cursor cursor = db.query("subjects", new String[]{"_id", "subjectName", "errors"}, null, null, null, null, null);
                    ListView list = (ListView) findViewById(R.id.subjectList);
                    CursorAdapter theAdapter = (CursorAdapter) listView.getAdapter();
                    theAdapter.changeCursor(cursor);
                    mainCursor = cursor;
                    db.close();
                }else{
                    Intent intent = new Intent(SubjectList.this,EachSubject.class);
                    intent.putExtra("name",selectedName);
                    startActivity(intent);
                }
            }
        };
        listView.setOnItemClickListener(itemClickListener);
    }

    public void onAdd(View view){
        LinearLayout linearAdd = (LinearLayout) findViewById((R.id.linearAdd));
        if(editAppeared == false){
            linearAdd.setVisibility(View.VISIBLE);
            editAppeared = true;
        }else{
            // show progress
            EditText addSubject = (EditText) findViewById(R.id.addSubject);
            String newSubjectToAdd =  addSubject.getText().toString();
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("subjectName", newSubjectToAdd);
                contentValues.put("errors", 0);
                JackOpenHelper jackOpenHelper = new JackOpenHelper(this);
                db = jackOpenHelper.getWritableDatabase();
                db.insert("subjects", null, contentValues);
                linearAdd.setVisibility(View.GONE);
                addSubject.getText().clear();
                newSubjectToAdd = newSubjectToAdd.replace(' ','_');
                db.execSQL("CREATE TABLE "+newSubjectToAdd+"(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "contentName TEXT,contentType TEXT);");
                db.close();
                db = jackOpenHelper.getReadableDatabase();
                Cursor cursor = db.query("subjects",new String[]{"_id","subjectName","errors"},null,null,null,null,null);
                ListView listView = (ListView) findViewById(R.id.subjectList);
                CursorAdapter theAdapter = (CursorAdapter) listView.getAdapter();
                theAdapter.changeCursor(cursor);
                mainCursor = cursor;
            }catch(Exception e){
                Toast toast  = Toast.makeText(this,"Error, try again or turn your screen to refresh list",Toast.LENGTH_SHORT);
                toast.show();
            }
            editAppeared = false;

        }
    }
    public void onRemove(View view){
        removeButtonPressed = !removeButtonPressed ;
        Button removeButton = (Button) findViewById(R.id.remove);
        if(removeButtonPressed == true){
            removeButton.setBackgroundColor(Color.WHITE);
            Toast toast  = Toast.makeText(this,"Whatever your click will be deleted from the list",Toast.LENGTH_LONG);
            toast.show();
        }else
            removeButton.setBackgroundColor(Color.GRAY);
    }
    public void onCancelAdd(View view){
        LinearLayout linearAdd = (LinearLayout) findViewById((R.id.linearAdd));
        linearAdd.setVisibility(View.GONE);
        editAppeared = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuItem activation = menu.add(0,0,Menu.NONE,"Activation");
        Intent intent = new Intent(this,Activate.class);
        intent.putExtra("Source","subjectList");
        activation.setIntent(intent);
        MenuItem  information = menu.add(0,1,Menu.NONE,"Information");
        information.setIntent(new Intent(this,Information.class));
        return true;
    }

    @Override
    public void onBackPressed(){
        // dot go
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mainCursor.close();
        db.close();
    }

}
