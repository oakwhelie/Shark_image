package pnj.ac.id.rifqi_afif.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "uts";

    private static final String USERS_TABLE_NAME = "users";
    private static final String USERS_KEY_ID = "id";
    private static final String USERS_KEY_USERNAME = "username";
    private static final String USERS_KEY_PASSWORD = "password";

    private static final String PICTURES_TABLE_NAME = "pictures";
    private static final String PICTURES_KEY_ID = "id";
    private static final String PICTURES_KEY_USERNAME = "username";
    private static final String PICTURES_KEY_NAME = "name";
    private static final String PICTURES_KEY_PICTURE = "picture";

    private static DBHelper sInstance;

    private static final String CREATE_USERS = MessageFormat.format(
            "CREATE TABLE IF NOT EXISTS {0} ({1} INTEGER PRIMARY KEY, {2} TEXT, {3} TEXT)",
            USERS_TABLE_NAME, USERS_KEY_ID, USERS_KEY_USERNAME, USERS_KEY_PASSWORD);

    private static final String CREATE_PICTURES = MessageFormat.format(
            "CREATE TABLE IF NOT EXISTS {0} ({1} INTEGER PRIMARY KEY, {2} TEXT, {3} TEXT, {4} BLOB)",
            PICTURES_TABLE_NAME, PICTURES_KEY_ID, PICTURES_KEY_USERNAME,PICTURES_KEY_NAME , PICTURES_KEY_PICTURE);

    private static final String DROP_USERS = "DROP TABLE IF EXISTS "+USERS_TABLE_NAME;
    private static final String DROP_PICTURES = "DROP TABLE IF EXISTS "+PICTURES_TABLE_NAME;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_USERS);
        db.execSQL(CREATE_PICTURES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(DROP_USERS);
        db.execSQL(DROP_PICTURES);
        this.onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldversion, int newversion)
    {
        this.onUpgrade(db, oldversion, newversion);
    }


    public static synchronized DBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    public long register(UsersTable user)
    {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        //Create a map having movie details to be inserted
        ContentValues content = new ContentValues();
        content.put(USERS_KEY_USERNAME, user.getUsername());
        content.put(USERS_KEY_PASSWORD, user.getPassword());

        long new_row_id = db.insert(USERS_TABLE_NAME, null, content);
        db.close();
        return new_row_id;
    }

    public UsersTable login(String username, String password)
    {
        UsersTable usersTable = new UsersTable();
        SQLiteDatabase db = this.getReadableDatabase();

        //specify the columns to be fetched
        String column[] = {USERS_KEY_ID, USERS_KEY_USERNAME};
        //Select condition
        String selection = USERS_KEY_USERNAME + " = ? AND " + USERS_KEY_PASSWORD + " = ?";
        //Arguments for selection
        String selection_args[] = {username, password};

        Cursor cursor = db.query(USERS_TABLE_NAME, column, selection, selection_args,
                null, null, null);

        if(cursor != null)
        {
            cursor.moveToFirst();
            usersTable.setId(cursor.getInt(0));
            usersTable.setUsername(cursor.getString(1));
        }
        db.close();
        return usersTable;
    }


    public long savePicture(PicturesTable picturesTable)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PICTURES_KEY_USERNAME, picturesTable.getUsername());
        contentValues.put(PICTURES_KEY_NAME, picturesTable.getName());
        contentValues.put(PICTURES_KEY_PICTURE, picturesTable.getPicture());

        long new_row_id = db.insert(PICTURES_TABLE_NAME, null, contentValues);
        db.close();
        Log.d("DB CALL", "savePicture");
        Log.d("DB CALL", String.valueOf(new_row_id));
        return new_row_id;
    }

    public PicturesTable getPicture(int picture_id)
    {
        PicturesTable picturesTable = new PicturesTable();
        SQLiteDatabase db = this.getReadableDatabase();

        String column[] = {PICTURES_KEY_ID, PICTURES_KEY_USERNAME, PICTURES_KEY_NAME, PICTURES_KEY_PICTURE};

        String selection = PICTURES_KEY_ID + " = ?";

        String selection_args[] = {String.valueOf(picture_id)};

        Cursor cursor = db.query(PICTURES_TABLE_NAME, column, selection, selection_args,
                null, null, null);

        if(cursor != null)
        {
            cursor.moveToFirst();
            picturesTable.setId(cursor.getInt(0));
            picturesTable.setUsername(cursor.getString(1));
            picturesTable.setName(cursor.getString(2));
            picturesTable.setPicture(cursor.getBlob(3));
        }
        db.close();
        return picturesTable;
    }

    public static final String PICTURES_SELECTION_USERNAME = PICTURES_KEY_USERNAME + " = ?";
    public static final String PICTURES_SELECTION_NAME = PICTURES_KEY_NAME + " LIKE ?";
    public static final String PICTURES_SELECTION_ALL = null;

    public List<PicturesTable> getPictures(String select, String args)
    {
        List picture_list = new ArrayList<PicturesTable>();
        SQLiteDatabase db = this.getReadableDatabase();

        String column[] = {PICTURES_KEY_ID, PICTURES_KEY_USERNAME, PICTURES_KEY_NAME, PICTURES_KEY_PICTURE};

        String selection = select;

        String selection_args[] = {args};

        Cursor cursor = db.query(PICTURES_TABLE_NAME, column, selection, selection_args,
                null, null, null);

        if(cursor != null)
        {
            if(cursor.moveToFirst())
            {
                do {
                    PicturesTable picturesTable = new PicturesTable();
                    picturesTable.setId(cursor.getInt(0));
                    picturesTable.setUsername(cursor.getString(1));
                    picturesTable.setName(cursor.getString(2));
                    picturesTable.setPicture(cursor.getBlob(3));

                    picture_list.add(picturesTable);

                }while(cursor.moveToNext());
            }
        }
        db.close();
        return picture_list;
    }

    public void deletePicture(int picture_id)
    {
        String selection_args[] = {String.valueOf(picture_id)};
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(PICTURES_TABLE_NAME, PICTURES_KEY_ID + " = ? ", selection_args);
        db.close();
    }

    public void deletePictures(String username)
    {
        String selection_args[] = {username};
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(PICTURES_TABLE_NAME, PICTURES_KEY_USERNAME + " = ? ", selection_args);
        db.close();
    }

    public void updatePictureName(PicturesTable picturesTable)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String id[] = {String.valueOf(picturesTable.getId())};

        ContentValues contentValues = new ContentValues();
        contentValues.put(PICTURES_KEY_NAME, picturesTable.getName());
        db.update(PICTURES_TABLE_NAME, contentValues, PICTURES_KEY_ID+" = ?", id);
        db.close();
    }

    //DBHelper is done
    //
    //next make ui and usage
}
