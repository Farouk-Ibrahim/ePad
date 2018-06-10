package com.example.farouk.jackretary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
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

public class Notes extends Activity {
    private SQLiteDatabase db;
    private Cursor mainCursor;
    private String queriableTable;
    private boolean editAppeared = false;
    private boolean removeButtonPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String tableName = intent.getStringExtra("name");
        queriableTable = tableName.replace(' ','_');
        setContentView(R.layout.activity_notes);
        TextView title = (TextView) findViewById(R.id.noteTitle);
        title.setText(tableName);
        ListView listView = (ListView) findViewById(R.id.noteList);
        try{
            JackOpenHelper jackOpenHelper = new JackOpenHelper(this);
            db = jackOpenHelper.getReadableDatabase();
            mainCursor = db.query(queriableTable,new String[]{"_id","note"},null,null,null,null,null);
            CursorAdapter theAdapter = new SimpleCursorAdapter(this,R.layout.note_item,mainCursor,new String[]
                    {"note"},new int[]{R.id.noteText},0);
            listView.setAdapter(theAdapter);
        }
        catch (Exception e){
            // write something meaningfull
        }
        AdapterView.OnItemClickListener itemClickListener  = new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> listView,View view, int position,long id){
                LinearLayout linearLayout = (LinearLayout) view;
                TextView textView = (TextView)linearLayout.getChildAt(1);
                String selectedName = textView.getText().toString();
                if(removeButtonPressed == true) {
                    JackOpenHelper jackOpenHelper = new JackOpenHelper(Notes.this);
                    db = jackOpenHelper.getWritableDatabase();
                    db.delete(queriableTable, "note = ?", new String[]{selectedName});
                    db.close();
                    db = jackOpenHelper.getReadableDatabase();
                    Cursor cursor = db.query(queriableTable, new String[]{"_id", "note"}, null, null, null, null, null);
                    ListView list = (ListView) findViewById(R.id.noteList);
                    CursorAdapter theAdapter = (CursorAdapter) listView.getAdapter();
                    theAdapter.changeCursor(cursor);
                    mainCursor = cursor;
                }else{
                    // pop up view
                }
            }
        };
        listView.setOnItemClickListener(itemClickListener);
    }
    public void onNoteAdd(View view){
        LinearLayout linearAdd = (LinearLayout) findViewById((R.id.linearAddNote));
        if(editAppeared == false){
            linearAdd.setVisibility(View.VISIBLE);
            editAppeared = true;
        }else{
            // show progress
            EditText addNote = (EditText) findViewById(R.id.addNote);
            String newNoteToAdd =  addNote.getText().toString();
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("note", newNoteToAdd);
                JackOpenHelper jackOpenHelper = new JackOpenHelper(this);
                db = jackOpenHelper.getWritableDatabase();
                db.insert(queriableTable, null, contentValues);
                linearAdd.setVisibility(View.GONE);
                addNote.getText().clear();
                db.close();
                db = jackOpenHelper.getReadableDatabase();
                Cursor cursor = db.query(queriableTable,new String[]{"_id","note"},null,null,null,null,null);
                ListView listView = (ListView) findViewById(R.id.noteList);
                CursorAdapter theAdapter = (CursorAdapter) listView.getAdapter();
                theAdapter.changeCursor(cursor);
                mainCursor = cursor;
            }catch(Exception e){
                Toast toast  = Toast.makeText(this,"Error, try again or turn your screen to refresh list",Toast.LENGTH_SHORT);
                toast.show();

            }
            editAppeared = false;

            // add it to the database
        }
    }
    public void onNoteRemove(View view){
        removeButtonPressed = !removeButtonPressed ;
        Button removeButton = (Button) findViewById(R.id.noteRemove);
        if(removeButtonPressed == true){
            removeButton.setBackgroundColor(Color.WHITE);
            Toast toast  = Toast.makeText(this,"Whatever your click will be deleted from the list",Toast.LENGTH_LONG);
            toast.show();
        }else
            removeButton.setBackgroundColor(Color.GRAY);

    }
    public void onAddCancel(View view){
        LinearLayout linearAdd = (LinearLayout) findViewById((R.id.linearAddNote));
        linearAdd.setVisibility(View.GONE);
        editAppeared = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuItem activation = menu.add(0,0,Menu.NONE,"Activation");
        activation.setIntent(new Intent(this,Activate.class));
        MenuItem  information = menu.add(0,1,Menu.NONE,"Information");
        information.setIntent(new Intent(this,Information.class));
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mainCursor.close();
        db.close();
    }

}
