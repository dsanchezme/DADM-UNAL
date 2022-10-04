package com.dadm.reto08.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

import java.io.File;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "directory";
    private static final String COMPANY_TABLE_NAME = "company";
    private static final int DB_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + COMPANY_TABLE_NAME + "(" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "webPage TEXT, " +
                "phoneNumber TEXT, " +
                "email TEXT, " +
                "productsServices TEXT, " +
                "classification TEXT" + ")";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + COMPANY_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean deleteDatabase(){
        File data = Environment.getDataDirectory();
        String currentDBPath = "/data/com.dadm.reto08/databases/" + DB_NAME;
        File currentDB = new File(data, currentDBPath);
        return SQLiteDatabase.deleteDatabase(currentDB);
    }


}
