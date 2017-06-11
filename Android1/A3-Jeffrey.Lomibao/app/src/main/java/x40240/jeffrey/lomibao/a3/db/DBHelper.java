package x40240.jeffrey.lomibao.a3.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import x40240.jeffrey.lomibao.a3.model.PersonInfo;

public final class DBHelper
{
    private static final String LOGTAG = DBHelper.class.getSimpleName();
    
    private static final String DATABASE_NAME    = "namedb_a3.db";
    private static final int    DATABASE_VERSION = 1;
    private static final String TABLE_NAME       = "names";

    // Column NamesJos
    public static final String KEY_ID            = "_id";
    public static final String KEY_FIRSTNAME     = "firstname";
    public static final String KEY_LASTNAME      = "lastname";
    public static final String KEY_GENDER        = "gender";
    public static final String KEY_EDUCATION     = "education";
    public static final String KEY_PETS          = "pets";

    // Column indexes
    public static final int COLUMN_ID         = 0;
    public static final int COLUMN_FIRSTNAME  = 1;
    public static final int COLUMN_LASTNAME   = 2;
    public static final int COLUMN_GENDER     = 3;
    public static final int COLUMN_EDUCATION  = 4;
    public static final int COLUMN_PETS       = 5;

    private Context context;
    private SQLiteDatabase db;
    private SQLiteStatement insertStmt;

    private static final String INSERT =
        "INSERT INTO " + TABLE_NAME + "(" +
        KEY_FIRSTNAME + ", " +
        KEY_LASTNAME + ", " +
        KEY_GENDER + ", " +
        KEY_EDUCATION + ", " +
        KEY_PETS + ") values (?, ?, ?, ?, ?)";

    public DBHelper(final Context context)
    {
        this.context = context;
        final OpenHelper openHelper = new OpenHelper(this.context);
        db = openHelper.getWritableDatabase();
        insertStmt = db.compileStatement(INSERT);
    }

    public long insert (final PersonInfo personInfo)
    {
        insertStmt.bindString(COLUMN_FIRSTNAME, personInfo.getFirstname());
        insertStmt.bindString(COLUMN_LASTNAME, personInfo.getLastname());
        insertStmt.bindLong(COLUMN_GENDER, personInfo.getGender().ordinal());
        insertStmt.bindLong(COLUMN_EDUCATION, personInfo.getEducation().ordinal());
        insertStmt.bindString(COLUMN_PETS, personInfo.getPets(this.context));

        final long value = insertStmt.executeInsert();
        Log.d (LOGTAG, "value="+value);
        return value;
    }

    public void deleteAll()
    {
        db.delete(TABLE_NAME, null, null);
    }
    public void close() { db.close(); }

    public List<PersonInfo> selectAll()
    {
        final List<PersonInfo> list = new ArrayList<PersonInfo>();
        final Cursor cursor = db.query(TABLE_NAME,
            new String[] { KEY_ID, KEY_FIRSTNAME, KEY_LASTNAME, KEY_GENDER, KEY_EDUCATION, KEY_PETS },
                null, null, null, null, KEY_LASTNAME);
        if (cursor.moveToFirst())
        {
            do {
                final PersonInfo personInfo = new PersonInfo();
                personInfo.setFirstname(cursor.getString(COLUMN_FIRSTNAME));
                personInfo.setLastname(cursor.getString(COLUMN_LASTNAME));
                personInfo.setGender(PersonInfo.Gender.values()[(int)cursor.getLong(COLUMN_GENDER)]);
                personInfo.setEducation(PersonInfo.Education.values()[(int)cursor.getLong(COLUMN_EDUCATION)]);
                personInfo.setPets(context, cursor.getString(COLUMN_PETS));
                list.add(personInfo);
            }
            while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    public Cursor selectAllCursor()
    {
        final Cursor cursor = db.query(TABLE_NAME,
                new String[] { KEY_ID, KEY_FIRSTNAME, KEY_LASTNAME, KEY_GENDER, KEY_EDUCATION, KEY_PETS },
                null, null, null, null, KEY_LASTNAME);
        return cursor;
    }

    static class OpenHelper
        extends SQLiteOpenHelper
    {
        private static final String LOGTAG = OpenHelper.class.getSimpleName();
        
        private static final String CREATE_TABLE =
            "CREATE TABLE " +
            TABLE_NAME +
            " (" + KEY_ID + " integer primary key autoincrement, " +
            KEY_FIRSTNAME + " TEXT, " +
            KEY_LASTNAME  + " TEXT, " +
            KEY_GENDER    + " INTEGER, " +
            KEY_EDUCATION + " INTEGER, " +
            KEY_PETS      + " TEXT);";

        OpenHelper (final Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate (final SQLiteDatabase db)
        {
            Log.d(LOGTAG, "onCreate!");
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade (final SQLiteDatabase db, final int oldVersion, final int newVersion)
        {
            Log.w("Example", "Upgrading database, this will drop tables and recreate.");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        @Override
        public void onDowngrade (final SQLiteDatabase db, final int oldVersion, final int newVersion)
        {
            Log.w("Example", "Downgrading database, this will drop tables and recreate.");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
