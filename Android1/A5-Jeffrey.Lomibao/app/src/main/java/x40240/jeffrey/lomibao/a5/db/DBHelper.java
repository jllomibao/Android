package x40240.jeffrey.lomibao.a5.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import x40240.jeffrey.lomibao.a5.model.DeviceInfo;

public final class DBHelper
{
    private static final String LOGTAG = DBHelper.class.getSimpleName();
    
    private static final String DATABASE_NAME    = "deviceConfiguration.db";
    private static final int    DATABASE_VERSION = 1;
    private static final String TABLE_NAME       = "deviceConfigurationTable";

    // Column Names
    public static final String KEY_ID           = "_id";
    public static final String KEY_TAG          = "tag";
    public static final String KEY_LRV          = "lrv";
    public static final String KEY_URV          = "urv";
    public static final String KEY_TEMP_COMP    = "tempComp";
    public static final String KEY_MANUAL_TEMP  = "manualTemp";

    // Column indexes
    public static final int COLUMN_ID           = 0;
    public static final int COLUMN_TAG          = 1;
    public static final int COLUMN_LRV          = 2;
    public static final int COLUMN_URV          = 3;
    public static final int COLUMN_TEMP_COMP    = 4;
    public static final int COLUMN_MANUAL_TEMP  = 5;

    private Context context;
    private SQLiteDatabase db;
    private SQLiteStatement sqlInsert;

    private static final String SQL_INSERT =
        "INSERT INTO " + TABLE_NAME + "(" +
        KEY_TAG + ", " +
        KEY_LRV + ", " +
        KEY_URV + ", " +
        KEY_TEMP_COMP + ", " +
        KEY_MANUAL_TEMP + ") values (?, ?, ?, ?, ?)";

    public DBHelper(final Context context)
    {
        this.context = context;
        final OpenHelper openHelper = new OpenHelper(this.context);
        db = openHelper.getWritableDatabase();
        sqlInsert = db.compileStatement(SQL_INSERT);
    }

    public long insert (final DeviceInfo deviceInfo)
    {
        sqlInsert.bindString(COLUMN_TAG, deviceInfo.getTag());
        sqlInsert.bindDouble(COLUMN_LRV, deviceInfo.getLowerRangeValue());
        sqlInsert.bindDouble(COLUMN_URV, deviceInfo.getUpperRangeValue());
        sqlInsert.bindLong(COLUMN_TEMP_COMP, deviceInfo.getTemperatureCompensation().ordinal());
        sqlInsert.bindDouble(COLUMN_MANUAL_TEMP, deviceInfo.getManualTemperature());

        final long value = sqlInsert.executeInsert();
        Log.d (LOGTAG, "sqlInsert value = "+value);
        return value;
    }

    public long update (final DeviceInfo  deviceInfo)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TAG, deviceInfo.getTag());
        cv.put(KEY_LRV, deviceInfo.getLowerRangeValue());
        cv.put(KEY_URV, deviceInfo.getUpperRangeValue());
        cv.put(KEY_TEMP_COMP, deviceInfo.getTemperatureCompensation().ordinal());
        cv.put(KEY_MANUAL_TEMP, deviceInfo.getManualTemperature());

        final long value = db.update(TABLE_NAME, cv, "_id="+deviceInfo.getId(), null);
        Log.d (LOGTAG, "db.update value = "+value);

        return value;
    }

    public void deleteAll()
    {
        db.delete(TABLE_NAME, null, null);
    }
    public void close() { db.close(); }

    public List<DeviceInfo> selectAll()
    {
        final List<DeviceInfo> list = new ArrayList<DeviceInfo>();
        final Cursor cursor = db.query(TABLE_NAME,
            new String[] { KEY_ID, KEY_TAG, KEY_LRV, KEY_URV, KEY_TEMP_COMP, KEY_MANUAL_TEMP },
                null, null, null, null, KEY_TAG);
        if (cursor.moveToFirst())
        {
            do {
                final DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setId(cursor.getInt(COLUMN_ID));
                deviceInfo.setTag(cursor.getString(COLUMN_TAG));
                deviceInfo.setLowerRangeValue(cursor.getDouble(COLUMN_LRV));
                deviceInfo.setUpperRangeValue(cursor.getDouble(COLUMN_URV));
                deviceInfo.setTemperatureCompensation(DeviceInfo.TemperatureCompensationEnum.values()[(int)cursor.getLong(COLUMN_TEMP_COMP)]);
                deviceInfo.setManualTemperature(cursor.getDouble(COLUMN_MANUAL_TEMP));
                list.add(deviceInfo);
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
                new String[] { KEY_ID, KEY_TAG, KEY_LRV, KEY_URV, KEY_TEMP_COMP, KEY_MANUAL_TEMP },
                null, null, null, null, KEY_TAG);
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
            KEY_TAG + " TEXT, " +
            KEY_LRV  + " REAL, " +
            KEY_URV  + " REAL, " +
            KEY_TEMP_COMP + " INTEGER, " +
            KEY_MANUAL_TEMP + " REAL);";

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
