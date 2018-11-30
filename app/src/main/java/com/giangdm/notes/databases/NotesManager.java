package com.giangdm.notes.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.giangdm.notes.models.Notes;

import java.util.ArrayList;
import java.util.List;

public class NotesManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final String TAG = "tag_note";

    private static final String TABLE_NAME = "notes";
    private static final String COLUMN_ID = "column_id";
    private static final String COLUMN_TITLE = "column_title";
    private static final String COLUMN_CONTENT = "column_content";
    private static final String COLUMN_DATE = "column_date";


    public NotesManager(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String strQuery = "CREATE TABLE " + TABLE_NAME + "( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_DATE + " TEXT)";
        db.execSQL(strQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addNote(Notes notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, notes.getTitle());
        values.put(COLUMN_CONTENT, notes.getContent());
        values.put(COLUMN_DATE, notes.getDateTime());
        if (db.insert(TABLE_NAME, null, values) > 0) {
            Log.d(TAG, "addNote: success");
        } else {
            Log.d(TAG, "addNote: fail");
        }
    }

    public List<Notes> getAllNotes() {
        List<Notes> list = new ArrayList<>();
        String strQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATE + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Notes(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Notes getNote(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String strQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(strQuery, new String[]{id});
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Notes notes = new Notes(cursor.getInt(0), cursor.getString(1),
                cursor.getString(2), cursor.getString(3));
        cursor.close();
        return notes;
    }

    public int updateNote(String id, String title, String content, String dateTime) {
        int result;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_DATE, dateTime);
        result = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[] {id});
        if (result > 0) {
            Log.d(TAG, "updateNote: success");
        } else {
            Log.d(TAG, "updateNote: fail");
        }

        return result;
    }

    public void deleteNote(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] {id}) > 0){
            Log.d(TAG, "deleteNote: success");
        } else {
            Log.d(TAG, "deleteNote: fail");
        }
        db.close();
    }

}
