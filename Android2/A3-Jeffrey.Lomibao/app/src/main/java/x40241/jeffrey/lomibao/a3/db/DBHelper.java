package x40241.jeffrey.lomibao.a3.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import x40241.jeffrey.lomibao.a3.model.StockInfo;

public final class DBHelper
{
    private static final String LOGTAG = DBHelper.class.getSimpleName();
    private static final boolean DEBUG = true;

    private static final String DATABASE_NAME       = "stocks_a3.db";
    private static final int    DATABASE_VERSION    = 1;

    private static final String STOCKS_INFO_TABLE_NAME = "stock_info";
    private static final String PRICE_DATA_TABLE_NAME = "price_data";

    // shared column names and indeces
    public static final String KEY_ID           = "_id";
    public static final String KEY_SYMBOL       = "symbol";
    public static final String KEY_NAME         = "name";
    public static final String KEY_PRICE        = "price";
    public static final String KEY_PREV_PRICE   = "previous_price";
    public static final String KEY_COUNT        = "count";
    public static final String KEY_MIN          = "min";
    public static final String KEY_MAX          = "max";
    public static final String KEY_AVG          = "avg";
    public static final String KEY_MODIFIED     = "modified";

    public static final String KEY_SYMBOL_ID    = "symbol_id";
    public static final String KEY_TIMESTAMP    = "timestamp";

    public static final int COLUMN_ID           = 0;
    public static final int COLUMN_SYMBOL       = 1;
    public static final int COLUMN_NAME         = 2;
    public static final int COLUMN_PRICE        = 3;
    public static final int COLUMN_PREV_PRICE   = 4;
    public static final int COLUMN_COUNT        = 5;
    public static final int COLUMN_MIN          = 6;
    public static final int COLUMN_MAX          = 7;
    public static final int COLUMN_AVG          = 8;
    public static final int COLUMN_MODIFIED     = 9;

    public static final int COLUMN_RAW_ID           = 0;
    public static final int COLUMN_RAW_SYMBOL_ID    = 1;
    public static final int COLUMN_RAW_TIMESTAMP    = 2;
    public static final int COLUMN_RAW_PRICE        = 3;

    static final class STOCK_INFO_TABLE {
        static final String TableName = STOCKS_INFO_TABLE_NAME;
        class ID            { static final int column = COLUMN_ID;          static final String columnName = KEY_ID;        }
        class SYMBOL        { static final int column = COLUMN_SYMBOL;      static final String columnName = KEY_SYMBOL;    }
        class NAME          { static final int column = COLUMN_NAME;        static final String columnName = KEY_NAME;      }
        class PRICE         { static final int column = COLUMN_PRICE;       static final String columnName = KEY_PRICE;     }
        class PREV_PRICE    { static final int column = COLUMN_PREV_PRICE;  static final String columnName = KEY_PREV_PRICE;}
        class COUNT         { static final int column = COLUMN_COUNT;       static final String columnName = KEY_COUNT;     }
        class MIN           { static final int column = COLUMN_MIN;         static final String columnName = KEY_MIN;       }
        class MAX           { static final int column = COLUMN_MAX;         static final String columnName = KEY_MAX;       }
        class AVG           { static final int column = COLUMN_AVG;         static final String columnName = KEY_AVG;       }
        class MODIFIED      { static final int column = COLUMN_MODIFIED;    static final String columnName = KEY_MODIFIED;  }
    }

    static final class PRICE_DATA_TABLE {
        static final String TableName = PRICE_DATA_TABLE_NAME;
        class ID            { static final int column = COLUMN_RAW_ID;          static final String columnName = KEY_ID;        }
        class SYMBOL_ID     { static final int column = COLUMN_RAW_SYMBOL_ID;   static final String columnName = KEY_SYMBOL_ID; }
        class TIMESTAMP     { static final int column = COLUMN_RAW_TIMESTAMP;   static final String columnName = KEY_TIMESTAMP; }
        class PRICE         { static final int column = COLUMN_RAW_PRICE;       static final String columnName = KEY_PRICE;     }
    }

    private static final String INSERT_STOCK_INFO =
        "INSERT INTO " + STOCK_INFO_TABLE.TableName + "(" +
                STOCK_INFO_TABLE.SYMBOL.columnName + ", " +
                STOCK_INFO_TABLE.NAME.columnName + ", " +
                STOCK_INFO_TABLE.PRICE.columnName + ", " +
                STOCK_INFO_TABLE.PREV_PRICE.columnName + ", " +
                STOCK_INFO_TABLE.COUNT.columnName + ", " +
                STOCK_INFO_TABLE.MIN.columnName + ", " +
                STOCK_INFO_TABLE.MAX.columnName + ", " +
                STOCK_INFO_TABLE.AVG.columnName + ", " +
                STOCK_INFO_TABLE.MODIFIED.columnName +
                ") values (?, ?, ?, ?, ?, ?, ?, ?, ?)";


    private static final String INSERT_PRICE_DATA =
            "INSERT INTO " + PRICE_DATA_TABLE.TableName + "(" +
                    PRICE_DATA_TABLE.SYMBOL_ID.columnName + ", " +
                    PRICE_DATA_TABLE.TIMESTAMP.columnName + ", " +
                    PRICE_DATA_TABLE.PRICE.columnName + ", " +
                    ") values (?, ?, ?)";


    private Context context;
    private SQLiteDatabase db;
    private SQLiteStatement insertStmtStockInfo;
//    private SQLiteStatement insertStmtPriceData;

    public DBHelper(final Context context)
    {
        this.context = context;
        final OpenHelper openHelper = new OpenHelper(this.context);
        db = openHelper.getWritableDatabase();
        insertStmtStockInfo = db.compileStatement(INSERT_STOCK_INFO);
//        insertStmtPriceData = db.compileStatement(INSERT_PRICE_DATA);
    }

    public long insert (final StockInfo stockInfo)
    {
        insertStmtStockInfo.bindString(STOCK_INFO_TABLE.SYMBOL.column, stockInfo.getSymbol());
        insertStmtStockInfo.bindString(STOCK_INFO_TABLE.NAME.column, stockInfo.getName());
        insertStmtStockInfo.bindDouble(STOCK_INFO_TABLE.PRICE.column, stockInfo.getPrice());
        insertStmtStockInfo.bindDouble(STOCK_INFO_TABLE.PREV_PRICE.column, stockInfo.getPreviousPrice());
        insertStmtStockInfo.bindLong(STOCK_INFO_TABLE.COUNT.column, stockInfo.getCount());
        insertStmtStockInfo.bindDouble(STOCK_INFO_TABLE.MIN.column, stockInfo.getMinPrice());
        insertStmtStockInfo.bindDouble(STOCK_INFO_TABLE.MAX.column, stockInfo.getMaxPrice());
        insertStmtStockInfo.bindDouble(STOCK_INFO_TABLE.AVG.column, stockInfo.getAveragePrice());
        insertStmtStockInfo.bindLong(STOCK_INFO_TABLE.MODIFIED.column, stockInfo.isModified() ? 1:0);
        final long value = insertStmtStockInfo.executeInsert();
        if(DEBUG) Log.d (LOGTAG, "value="+value);

//        final Cursor cursor = db.execSQL("SELECT last_insert_rowid()" + STOCK_INFO_TABLE.TableName);
//        cursor.getLong(cursor.getColumnIndex("_id"))
//        insertStmtStockInfo.bindString(PRICE_DATA_TABLE.SYMBOL_ID.column, );
//        insertStmtStockInfo.bindString(PRICE_DATA_TABLE.TIMESTAMP.column, stockInfo.getSequence());
//        insertStmtStockInfo.bindString(PRICE_DATA_TABLE.PRICE.column, stockInfo.getPrice());

        return value;
    }

    public void deleteAll()
    {
        db.delete(STOCK_INFO_TABLE.TableName, null, null);
        db.delete(PRICE_DATA_TABLE.TableName, null, null);
    }
    public void close() { db.close(); }

    public List<StockInfo> selectAll()
    {
        final List<StockInfo> list = new ArrayList<StockInfo>();
        final Cursor cursor = db.query(STOCK_INFO_TABLE.TableName,
            new String[] { KEY_ID, KEY_SYMBOL, KEY_NAME, KEY_PRICE, KEY_PREV_PRICE, KEY_COUNT, KEY_MIN, KEY_MAX, KEY_AVG, KEY_MODIFIED },
                null, null, null, null, KEY_NAME);
        if (cursor.moveToFirst())
        {
            do {
                final StockInfo stockInfo = new StockInfo();
                stockInfo.setSymbol(cursor.getString(COLUMN_SYMBOL));
                stockInfo.setName(cursor.getString(COLUMN_NAME));
                stockInfo.setPrice((int)cursor.getFloat(COLUMN_PRICE));
                stockInfo.setPreviousPrice((int)cursor.getFloat(COLUMN_PREV_PRICE));
                stockInfo.setCount((int)cursor.getInt(COLUMN_COUNT));
                stockInfo.setMinPrice((int)cursor.getFloat(COLUMN_MIN));
                stockInfo.setMaxPrice((int)cursor.getFloat(COLUMN_MAX));
                stockInfo.setAveragePrice((int)cursor.getFloat(COLUMN_AVG));
                stockInfo.setModified(cursor.getLong(COLUMN_MODIFIED) == 1);
                list.add(stockInfo);
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
        final Cursor cursor = db.query(STOCK_INFO_TABLE.TableName,
                new String[] { KEY_ID, KEY_SYMBOL, KEY_NAME, KEY_PRICE, KEY_PREV_PRICE, KEY_COUNT, KEY_MIN, KEY_MAX, KEY_AVG, KEY_MODIFIED },
                null, null, null, null, KEY_NAME);
        return cursor;
    }

    static class OpenHelper
        extends SQLiteOpenHelper
    {
        private static final String LOGTAG = OpenHelper.class.getSimpleName();

        private static final String CREATE_STOCK_INFO_TABLE =
                "CREATE TABLE IF NOT EXISTS " + STOCK_INFO_TABLE.TableName + " ("
                        + STOCK_INFO_TABLE.ID.columnName   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + STOCK_INFO_TABLE.SYMBOL.columnName      + " TEXT NOT NULL UNIQUE, "
                        + STOCK_INFO_TABLE.NAME.columnName        + " TEXT NOT NULL, "
                        + STOCK_INFO_TABLE.PRICE.columnName       + " REAL NOT NULL, "
                        + STOCK_INFO_TABLE.PREV_PRICE.columnName  + " REAL NOT NULL DEFAULT -1.0, "
                        + STOCK_INFO_TABLE.COUNT.columnName       + " INTEGER NOT NULL, "
                        + STOCK_INFO_TABLE.MIN.columnName         + " REAL NOT NULL, "
                        + STOCK_INFO_TABLE.MAX.columnName         + " REAL NOT NULL, "
                        + STOCK_INFO_TABLE.AVG.columnName         + " REAL NOT NULL, "
                        + STOCK_INFO_TABLE.MODIFIED.columnName    + " LONG NOT NULL);";

        private static final String CREATE_PRICE_DATA_TABLE =
                "CREATE TABLE IF NOT EXISTS " + PRICE_DATA_TABLE.TableName + " ("
                        + PRICE_DATA_TABLE.ID.columnName        + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + PRICE_DATA_TABLE.SYMBOL_ID.columnName + " INTEGER NOT NULL,"
                        + PRICE_DATA_TABLE.TIMESTAMP.columnName + " LONG NOT NULL,"
                        + PRICE_DATA_TABLE.PRICE.columnName     + " REAL NOT NULL, "
                        + "FOREIGN KEY(" + PRICE_DATA_TABLE.SYMBOL_ID.columnName + ") REFERENCES "
                        +    STOCK_INFO_TABLE.TableName + "(" + STOCK_INFO_TABLE.ID.columnName +") ON DELETE CASCADE);";

        OpenHelper (final Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate (final SQLiteDatabase db)
        {
            Log.d(LOGTAG, "onCreate!");
            db.execSQL(CREATE_STOCK_INFO_TABLE);
            db.execSQL(CREATE_PRICE_DATA_TABLE);
        }

        @Override
        public void onUpgrade (final SQLiteDatabase db, final int oldVersion, final int newVersion)
        {
            Log.w("Example", "Upgrading database, this will drop tables and recreate.");
            db.execSQL("DROP TABLE IF EXISTS " + STOCK_INFO_TABLE.TableName);
            db.execSQL("DROP TABLE IF EXISTS " + PRICE_DATA_TABLE.TableName);
            onCreate(db);
        }

        @Override
        public void onDowngrade (final SQLiteDatabase db, final int oldVersion, final int newVersion)
        {
            Log.w("Example", "Downgrading database, this will drop tables and recreate.");
            db.execSQL("DROP TABLE IF EXISTS " + STOCK_INFO_TABLE.TableName);
            db.execSQL("DROP TABLE IF EXISTS " + PRICE_DATA_TABLE.TableName);
            onCreate(db);
        }
    }
}
