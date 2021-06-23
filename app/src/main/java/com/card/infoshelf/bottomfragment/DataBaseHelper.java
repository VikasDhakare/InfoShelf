package com.card.infoshelf.bottomfragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "InfoShelfData.db";
    public static final String TABLE_NAME = "SearchFilter";

    public static final String COL_1 = "ID";
    public static final String COL_2 = "CHECKBOX";
    public static final String COL_3 = "COMPANYSPIN";
    public static final String COL_4 = "INTERESTSPIN";
    public DataBaseHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,CHECKBOX TEXT,COMPANYSPIN TEXT,INTERESTSPIN TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    }
    public boolean insertData(Integer checkbox, String companySpin, String interestSPin){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,checkbox);
        contentValues.put(COL_3,companySpin);
        contentValues.put(COL_4,interestSPin);
        long result = db.insert(TABLE_NAME,null,contentValues);
        db.close();
        if (result == -1){

            return false;
        }else{
            return true;
        }
    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TABLE_NAME,null);
        return res;
    }
    public  boolean updateData(String id,Integer checkbox, String companySpin, String interestSPin){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,checkbox);
        contentValues.put(COL_3,companySpin);
        contentValues.put(COL_4,interestSPin);
        int result = db.update(TABLE_NAME,contentValues,"ID = ?",new String[]{id});
        if (result>0){
            return true;
        }else {
            return false;
        }
    }
}
