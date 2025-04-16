package dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static dao.UserDatabaseAdapter.TABLE_NAME;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase _db) {
        try {
            _db.execSQL(UserDatabaseAdapter.DATABASE_CREATE);
        } catch (Exception ex) {
            Log.e("Error","Exception");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
        _db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        _db.execSQL("DROP TABLE IF EXISTS " + "SEMESTER1");
        // Tạo một database mới
        onCreate(_db);
    }
}
