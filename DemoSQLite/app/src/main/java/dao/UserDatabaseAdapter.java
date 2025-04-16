package dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;

import model.UserModel;
import utils.Utils;

public class UserDatabaseAdapter {
    static ArrayList<UserModel> users = new ArrayList<>();
    static final String DATABASE_NAME = "UsersDatabase.db";
    static final String TABLE_NAME = "USERS";
    static final int DATABASE_VERSION = 1;
    // tạo mới csdl
    static final String DATABASE_CREATE =
            "create table " + TABLE_NAME +
            " ( ID integer primary key autoincrement, " +
                    "user_name text, " +
                    "user_phone text, " +
                    "user_email text);";

    private  static final String TAG = "UsersDatabaseAdapter";
    public static SQLiteDatabase db;
    private Context context;
    private static DatabaseHelper dbHelper;

    public UserDatabaseAdapter(Context _context) {
        context = _context;
        dbHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Phương thức mở Database
    public UserDatabaseAdapter open() throws SQLException {
        // Mở hoặc tạo database ở chế độ ghi (write mode).
        // Nếu database chưa tồn tại, nó sẽ được tạo.
        // Lưu database vào biến db để dùng cho các thao tác sau này.
        db = dbHelper.getWritableDatabase();
        // Trả về chính đối tượng UserDatabaseAdapter,
        // giúp gọi tiếp các phương thức khác mà không cần tạo lại object.
        return this;
    }

    // Phương thức đóng Database
    public void close() {
        db.close();
    }

    // Phương thức trả về Instance của Database
    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    // Phương thức insert bản ghi vào table
    public boolean insertEntry(String user_name, String user_phone, String user_email) {
        boolean isInserted = false;
        try {
            ContentValues newValues = new ContentValues();
            // Gán dữ liệu cho mỗi cột
            newValues.put("user_name", user_name);
            newValues.put("user_phone", user_phone);
            newValues.put("user_email", user_email);

            // insert hàng dữ liệu vào table
            db = dbHelper.getWritableDatabase();
            long result = db.insert(TABLE_NAME, null, newValues);

            // Kiểm tra kết quả
            if (result == -1) {
                Utils.showToast(this.context.getApplicationContext(), "Thêm user thất bại!");
                return false;
            } else {
                Log.i("Row insert result ", String.valueOf(result));
                Utils.showToast(this.context.getApplicationContext(), "Thêm user thành công! Số user hiện tại: " + getRowCount());
                isInserted = true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return isInserted;
    }

    // Phương thức update bản ghi vào table
    public boolean updateEntry(String id, String user_name, String user_phone, String user_email) {
        boolean isUpdated = false;
        try {
            ContentValues updateValues = new ContentValues();
            // Gán dữ liệu cho mỗi cột
            updateValues.put("user_name", user_name);
            updateValues.put("user_phone", user_phone);
            updateValues.put("user_email", user_email);

            String where = "ID= ?";

            // update dữ liệu vào table
            db = dbHelper.getReadableDatabase();
            long result = db.update(TABLE_NAME, updateValues, where, new String[]{id});

            // Kiểm tra kết quả
            if (result == -1) {
                Utils.showToast(this.context.getApplicationContext(), "Update user thất bại!");
                return false;
            } else {
                Log.i("Row insert result ", String.valueOf(result));
                Utils.showToast(this.context.getApplicationContext(), "Update user thành công! Số user hiện tại: " + getRowCount());
                isUpdated = true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return isUpdated;
    }

    public static ArrayList<UserModel> getRows() throws JSONException {
        users.clear();
        UserModel user;
        db = dbHelper.getReadableDatabase();
        Cursor projCursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (projCursor.moveToNext()) {
            user = new UserModel();
            user.setId(projCursor.getString(projCursor.getColumnIndexOrThrow("ID")));
            user.setUserName(projCursor.getString(projCursor.getColumnIndexOrThrow("user_name")));
            user.setPhone(projCursor.getString(projCursor.getColumnIndexOrThrow("user_phone")));
            user.setEmail(projCursor.getString(projCursor.getColumnIndexOrThrow("user_email")));
            users.add(user);
        }
        projCursor.close();
        return users;
    }

    public int deleteEntry(String id) {
        String where = "ID=?";
        db = dbHelper.getWritableDatabase();
        int rowDeleted = db.delete(TABLE_NAME, where, new String[]{id});

        if (db != null && db.isOpen()) {
            db.close();
        }
        // trả về số dòng xoá được
        return rowDeleted;
    }

    // Hàm lấy số lượng dòng
    public int getRowCount() {
        int count = 0;
        db = dbHelper.getReadableDatabase(); // Mở database ở chế độ chỉ đọc
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0); // Lấy số lượng dòng từ kết quả truy vấn
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close(); // Đóng con trỏ
            db.close(); // Đóng database
        }

        return count;
    }

}
























