package x40241.jeffrey.lomibao.a3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import x40241.jeffrey.lomibao.a3.model.PriceData;
import x40241.jeffrey.lomibao.a3.model.StockInfo;
import x40241.jeffrey.lomibao.a3.model.StockQuote;

/*
 * Full documentation for SQLite can be found at http://sqlite.org
 */
public final class DBHelper
{
    private static final String LOGTAG = DBHelper.class.getSimpleName();
    private static final boolean DEBUG = true;
    
    private static final String DATABASE_NAME    = "stocks_x402_41_a3.db";
    private static final int    DATABASE_VERSION = 1;

    //  Data is static and Singleton in nature.  Enum's work well for this but lead to a little
    //  more verbosity when referencing in some cases below.
    //
    //  It might be possible to NOT use an explicit 'columnName' and use the name of
    //  the enum member -- e.g., STOCK_INFO_TABLE.SYMBOL.name() == "SYMBOL" -- as column names
    //  tend to be case insensitive.  However, that leans more toward co-incidence than explicit
    //  choice -- i.e., the name of the enum member is NOT really the name of the column since the
    //  enum is intended as a reference object -- i.e., to encapsulate the values/data.  Some will
    //  argue that is too pedantic, rename "ID" to "_ID", and do away with the columnName field.
    enum STOCK_INFO_TABLE {
        ID("_id"),
        SYMBOL("symbol"),
        NAME("name"),
        PRICE("price"),
        PREVIOUS_PRICE("price_prev"),
        COUNT("count"),
        MIN("min"),
        MAX("max"),
        AVG("avg"),
        MODIFIED("modified");
        
        public static final String TableName = "StockInfo_t";
        
        public final int    columnIndex = this.ordinal();
        public final String columnName;
        
        private STOCK_INFO_TABLE(final String columnName) {
            this.columnName = columnName;
            if (DEBUG) Log.d(LOGTAG, "STOCK_INFO_TABLE columnName = " + columnName);
        }
    }
    
    enum PRICE_DATA_TABLE {
        ID("_id"),
        SYMBOL_ID("symbol_id"),
        TIMESTAMP("timestamp"),
        PRICE("price");
        
        public static final String TableName = "PriceData_t";
        
        public final int    columnIndex = this.ordinal();
        public final String columnName;
        
        private PRICE_DATA_TABLE(final String columnName) {
            this.columnName = columnName;
            if (DEBUG) Log.d(LOGTAG, "PRICE_DATA_TABLE columnName = " + columnName);
        }
    }
    
    private static final String STOCK_INFO_QUERY_COLUMNS =
        STOCK_INFO_TABLE.SYMBOL.columnName + ", "
        + STOCK_INFO_TABLE.NAME.columnName + ", "
        + STOCK_INFO_TABLE.PRICE.columnName + ", "
        + STOCK_INFO_TABLE.PREVIOUS_PRICE.columnName + ", "
        + STOCK_INFO_TABLE.COUNT.columnName + ", "
        + STOCK_INFO_TABLE.MIN.columnName + ", "
        + STOCK_INFO_TABLE.MAX.columnName + ", "
        + STOCK_INFO_TABLE.AVG.columnName + ", "
        + STOCK_INFO_TABLE.MODIFIED.columnName;
    
    private static final String INSERT_STOCK_INFO =
        "INSERT INTO " + STOCK_INFO_TABLE.TableName
            + "(" + STOCK_INFO_QUERY_COLUMNS + ")"
            + " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_PRICE_DATA =
        "INSERT INTO " + PRICE_DATA_TABLE.TableName + "("
//            + PRICE_DATA_TABLE.ID.columnName + ", "
    		+ PRICE_DATA_TABLE.SYMBOL_ID.columnName + ", "
    		+ PRICE_DATA_TABLE.TIMESTAMP.columnName + ", "
    		+ PRICE_DATA_TABLE.PRICE.columnName + ") values (?, ?, ?)";
    
    //  Get StockInfo.
    private static final String SELECT_ALL_STOCK_INFO =
        "SELECT "
            + STOCK_INFO_TABLE.ID.columnName + ", "
            + STOCK_INFO_QUERY_COLUMNS
            + " FROM " + STOCK_INFO_TABLE.TableName
            + " ORDER BY " + STOCK_INFO_TABLE.SYMBOL.columnName;
    
//    private static final String SELECT_SPECIFIC_STOCK_INFO =
//        "SELECT "
//            + STOCK_INFO_QUERY_COLUMNS
//            + " FROM " + STOCK_INFO_TABLE.TableName
//            + " WHERE " + STOCK_INFO_TABLE.SYMBOL.columnName + "=?"
//            + " ORDER BY " + STOCK_INFO_TABLE.SYMBOL.columnName;
    
    private static final String SELECT_STOCK_INFO_BY_ID =
        "SELECT  "
            + STOCK_INFO_TABLE.ID.columnName
            + " FROM " + STOCK_INFO_TABLE.TableName
            + " WHERE " + STOCK_INFO_TABLE.SYMBOL.columnName + "=?";
    
    private static final String SELECT_COUNT_STOCK_INFO = 
            "SELECT count(*) FROM " + STOCK_INFO_TABLE.TableName;
    
    private static final String SELECT_ALL_PRICE_DATA_FOR_SYMBOL =
        "SELECT  "
            + PRICE_DATA_TABLE.ID.columnName + ", "
            + PRICE_DATA_TABLE.SYMBOL_ID.columnName + ", "
            + PRICE_DATA_TABLE.TIMESTAMP.columnName + ", "
            + PRICE_DATA_TABLE.PRICE.columnName
            + "FROM " + PRICE_DATA_TABLE.TableName
            + " WHERE " + PRICE_DATA_TABLE.SYMBOL_ID.columnName + "="
            +   "(" + SELECT_STOCK_INFO_BY_ID +")"
            + " ORDER BY " + PRICE_DATA_TABLE.TIMESTAMP.columnName;
    
    private static final String SELECT_COUNT_PRICE_DATA = 
            "SELECT count(*) FROM " + PRICE_DATA_TABLE.TableName;
    
    //  We assume the list of stocks will be relatively small so we keep a simple cache/map
    //  of Symbol->StockInfo pairs.
    private static final HashMap<String, StockInfo> StockInfoCache = new HashMap<String, StockInfo>();
    
    
    private final SQLiteDatabase   db;
    private final SQLiteStatement  insertStockInfoStmt;
    private final SQLiteStatement  insertPriceDataStmt;

    private final Context context;
    
    public DBHelper (final Context context) {
        if (context == null)
            throw new IllegalArgumentException("context == null");
        this.context = context;
        db = new OpenHelper(this.context).getWritableDatabase();
        insertStockInfoStmt = db.compileStatement(INSERT_STOCK_INFO);
        insertPriceDataStmt = db.compileStatement(INSERT_PRICE_DATA);
    }
    
    private void loadStockInfoCache() {
        synchronized (StockInfoCache) {
            final List<StockInfo> list = this.selectAllStockInfo();
            for (final StockInfo stockInfo : list)
                StockInfoCache.put(stockInfo.getSymbol(), stockInfo);
        }
    }

    public List<StockInfo> getStockInfoFromCache() {
        synchronized (StockInfoCache) {
            List<StockInfo> list = new ArrayList<>(StockInfoCache.values());
            return list;
        }
    }

    public synchronized void update (final List<StockQuote> list) {
        for (final StockQuote quote : list) {
            StockInfo stockInfo = getStockInfo(quote.getSymbol());
            if (stockInfo == null)
                stockInfo = add(quote);
            else {
                final float price = quote.getPrice();
                stockInfo.setPreviousPrice(stockInfo.getPrice());
                stockInfo.setPrice(price);
                final int count = stockInfo.getCount();
                final int newCount = count+1;
                stockInfo.setCount(newCount);
                stockInfo.setMin(Math.min(stockInfo.getMin(), price));
                stockInfo.setMax(Math.max(stockInfo.getMax(), price));
                stockInfo.setAvg(((stockInfo.getAvg()*count)+price)/(newCount));
                stockInfo.setModified(quote.getSequence());
                if (dbUpdate(stockInfo) == 1)
                    updateStockInfo(stockInfo);
            }
            final PriceData priceData = new PriceData();
            priceData.setSymbolId(stockInfo.getId());
            priceData.setTimestamp(quote.getSequence());
            priceData.setPrice(quote.getPrice());
            priceData.setId(dbInsert (priceData));  // insert into DB and update id for completeness.
        }
    }
    
    //  fill out a new StockInfo object/record to be added to the DB; then do it
    private StockInfo add (final StockQuote quote) {
        final StockInfo stockInfo = new StockInfo();
        stockInfo.setSymbol(quote.getSymbol());
        stockInfo.setName(quote.getName());
        stockInfo.setPrice(quote.getPrice());
        stockInfo.setPreviousPrice(-1.0f);  // default
        stockInfo.setCount(1);
        stockInfo.setMin(quote.getPrice());
        stockInfo.setMax(quote.getPrice());
        stockInfo.setAvg(quote.getPrice());
        stockInfo.setModified(quote.getSequence());
        
        stockInfo.setId(dbInsert (stockInfo));
        putStockInfo(stockInfo);  //  add to cache
        return stockInfo;
    }
    
    /**
     * Delete all data (not tables) in the DB.
     */
    public synchronized void deleteAll() {
        if (DEBUG) {
            countStockInfoRecords();
            countPriceDataRecords();
        }
        //  PRICE_DATA_TABLE has a ForeignKey relationship with STOCK_INFO_TABLE
        //  so the deleting the table that holds the key will 'cascade' to PRICE_DATA_TABLE.
        Log.i(LOGTAG, "Deleting all data...");
        final long startTime = System.currentTimeMillis();
        final int recCount = db.delete(STOCK_INFO_TABLE.TableName, "1", null);
        //  reset autoincrement/sequence counters
        final ContentValues values = new ContentValues();
        values.put("seq", 0);
        db.update("sqlite_sequence", values, "name=?",new String[] {STOCK_INFO_TABLE.TableName});
        db.update("sqlite_sequence", values, "name=?",new String[] {PRICE_DATA_TABLE.TableName});

        final long elapsed = System.currentTimeMillis() - startTime;
        synchronized (StockInfoCache) {
            StockInfoCache.clear();
        }
        Log.i(LOGTAG, "Deleted "+ recCount + " records; time "+elapsed+"ms");
        if (DEBUG) {
            countStockInfoRecords();
            countPriceDataRecords();
        }
    }
    
    private int countStockInfoRecords() {
        final  Cursor cursor = db.rawQuery(SELECT_COUNT_STOCK_INFO, null);
        cursor.moveToFirst();
        final int count = cursor.getInt(0);
        cursor.close();
        if (DEBUG) {
            Log.d(LOGTAG, STOCK_INFO_TABLE.TableName +": Counted "+ count+ " records.");
        }
        return count;
    }
    
    private int countPriceDataRecords() {
        final  Cursor cursor = db.rawQuery(SELECT_COUNT_PRICE_DATA, null);
        cursor.moveToFirst();
        final int count = cursor.getInt(0);
        cursor.close();
        if (DEBUG) {
            Log.d(LOGTAG, PRICE_DATA_TABLE.TableName +": Counted "+ count+ " records.");
        }
        return count;
    }
    
    /**
     * Retrieve the StockInfo object for the specified symbol.
     * @param symbol     a stock symbol
     * @return StockInfo for given stock symbol.
     */
    public StockInfo getStockInfo(final String symbol) {
        synchronized (StockInfoCache) {
            if (StockInfoCache.size() == 0)
                loadStockInfoCache();
            return StockInfoCache.get(symbol);
        }
    }
    
    //  predicate to determine if the StockInfo object is persisted
    private boolean isPersisted(final StockInfo stockInfo) {
        //  persisted objects have an id greater than 0
        if (stockInfo.getId() == 0)
            return false;
        return true;
    }
    
    //  cache the StockInfo object; but only if it is persisted
    private void putStockInfo(final StockInfo stockInfo) {
        if (! isPersisted(stockInfo))
            Log.e(LOGTAG, "stockInfo not persisted; symbol="+stockInfo.getSymbol());
        synchronized (StockInfoCache) {
            StockInfoCache.put(stockInfo.getSymbol(), stockInfo);
        }
    }
    
    //  update the cache with the given StockInfo; but only if it is persisted
    private void updateStockInfo(final StockInfo stockInfo) {
        if (! isPersisted(stockInfo))
            Log.e(LOGTAG, "stockInfo not persisted; symbol="+stockInfo.getSymbol());
        synchronized (StockInfoCache) {
            StockInfoCache.put(stockInfo.getSymbol(), stockInfo);
        }
    }
    
    //  persist the StockInfo into the DB; return the record id
    private long dbInsert (final StockInfo stockInfo) {
        insertStockInfoStmt.bindString(STOCK_INFO_TABLE.SYMBOL.columnIndex, stockInfo.getSymbol());
        insertStockInfoStmt.bindString(STOCK_INFO_TABLE.NAME.columnIndex, stockInfo.getName());
        insertStockInfoStmt.bindDouble(STOCK_INFO_TABLE.PRICE.columnIndex, stockInfo.getPrice());
        insertStockInfoStmt.bindDouble(STOCK_INFO_TABLE.PREVIOUS_PRICE.columnIndex, stockInfo.getPreviousPrice());
        insertStockInfoStmt.bindLong(STOCK_INFO_TABLE.COUNT.columnIndex, stockInfo.getCount());
        insertStockInfoStmt.bindDouble(STOCK_INFO_TABLE.MIN.columnIndex, stockInfo.getMin());
        insertStockInfoStmt.bindDouble(STOCK_INFO_TABLE.MAX.columnIndex, stockInfo.getMax());
        insertStockInfoStmt.bindDouble(STOCK_INFO_TABLE.AVG.columnIndex, stockInfo.getAvg());
        insertStockInfoStmt.bindLong(STOCK_INFO_TABLE.MODIFIED.columnIndex, stockInfo.getModified());
        
        final long id = insertStockInfoStmt.executeInsert();
        if (DEBUG) {
            Log.d (LOGTAG, "dbInsert.stockInfo.id="+id);
            Log.d (LOGTAG, "dbInsert.stockInfo.symbol="+stockInfo.getSymbol());
            Log.d (LOGTAG, "dbInsert.stockInfo.price="+stockInfo.getPrice());
            Log.d (LOGTAG, "dbInsert.stockInfo.count="+stockInfo.getCount());
        }
        return id;
    }

    private long dbInsert (final PriceData priceInfo) {
        if (DEBUG) {
            Log.d (LOGTAG, "dbInsert.priceInfo.symbol="+priceInfo.getSymbolId());
        }
        insertPriceDataStmt.bindLong(PRICE_DATA_TABLE.SYMBOL_ID.columnIndex, priceInfo.getSymbolId());
        insertPriceDataStmt.bindLong(PRICE_DATA_TABLE.TIMESTAMP.columnIndex, priceInfo.getTimestamp());
        insertPriceDataStmt.bindDouble(PRICE_DATA_TABLE.PRICE.columnIndex, priceInfo.getPrice());
        final long id = insertPriceDataStmt.executeInsert();
        if (DEBUG) {
            Log.d (LOGTAG, "dbInsert.priceInfo.id="+id);
//            Log.d (LOGTAG, "dbInsert.priceInfo.symbol="+priceInfo.getSymbolId());
        }
        return id;
    }
    
    private long dbUpdate (final StockInfo stockInfo) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(STOCK_INFO_TABLE.PRICE.columnName, stockInfo.getPrice());
        contentValues.put(STOCK_INFO_TABLE.PREVIOUS_PRICE.columnName, stockInfo.getPreviousPrice());
        contentValues.put(STOCK_INFO_TABLE.COUNT.columnName, stockInfo.getCount());
        contentValues.put(STOCK_INFO_TABLE.MIN.columnName, stockInfo.getMin());
        contentValues.put(STOCK_INFO_TABLE.MAX.columnName, stockInfo.getMax());
        contentValues.put(STOCK_INFO_TABLE.AVG.columnName, stockInfo.getAvg());
        contentValues.put(STOCK_INFO_TABLE.MODIFIED.columnName, stockInfo.getModified());
        final String whereClause = STOCK_INFO_TABLE.ID.columnName + "=" + stockInfo.getId();
        final int numRows = db.update(STOCK_INFO_TABLE.TableName,
                contentValues, whereClause, null );
        if (DEBUG) {
            Log.d(LOGTAG, "dbUpdate.id="+stockInfo.getId());
            Log.d(LOGTAG, "dbUpdate.symbol="+stockInfo.getSymbol());
            Log.d(LOGTAG, "dbUpdate.price="+stockInfo.getPrice());
            Log.d(LOGTAG, "dbUpdate.whereClause="+whereClause);
            Log.d(LOGTAG, "dbUpdate.numRows="+numRows);
        }
        if (numRows == 0) {
            Log.e(LOGTAG, "Expected 1; got 'numRows == 0'");
        }
        if (numRows > 1) {
            Log.e(LOGTAG, "Expected 1; got 'numRows > 1'");
        }
        return numRows;
    }

    /**
     * Retrieve all StockInfo data from the DB.
     * 
     * @return
     */
    //  NOTE: This is not the way to handle large DB's
    public List<StockInfo> selectAllStockInfo() {
        final ArrayList<StockInfo> list = new ArrayList<StockInfo>();
        final Cursor cursor = db.rawQuery(SELECT_ALL_STOCK_INFO, new String[] {});
        Log.i(LOGTAG, "cursor.getCount()="+cursor.getCount());
        final StockInfoCursorReader cursorReader = new StockInfoCursorReader(cursor);
//        if (cursor.moveToFirst())
//        {
//            final int ID_INDEX         = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.ID.columnName);
//            final int SYMBOL_INDEX     = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.SYMBOL.columnName);
//            final int NAME_INDEX       = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.NAME.columnName);
//            final int PRICE_INDEX      = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.PRICE.columnName);
//            final int PRICE_PREV_INDEX = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.PREVIOUS_PRICE.columnName);
//            final int COUNT_INDEX      = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.COUNT.columnName);
//            final int MIN_INDEX        = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.MIN.columnName);
//            final int MAX_INDEX        = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.MAX.columnName);
//            final int AVG_INDEX        = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.AVG.columnName);
//            final int MODIFIED_INDEX   = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.MODIFIED.columnName);
//            
//            do {
//                StockInfo stockInfo = new StockInfo();
//                stockInfo.setId(cursor.getLong(ID_INDEX));
//                stockInfo.setSymbol(cursor.getString(SYMBOL_INDEX));
//                stockInfo.setName(cursor.getString(NAME_INDEX));
//                stockInfo.setPrice((float)cursor.getFloat(PRICE_INDEX));
//                stockInfo.setPreviousPrice((float)cursor.getFloat(PRICE_PREV_INDEX));
//                stockInfo.setCount((int)cursor.getLong(COUNT_INDEX));
//                stockInfo.setMin((float)cursor.getFloat(MIN_INDEX));
//                stockInfo.setMax((float)cursor.getFloat(MAX_INDEX));
//                stockInfo.setAvg((float)cursor.getFloat(AVG_INDEX));
//                stockInfo.setModified(cursor.getLong(MODIFIED_INDEX));
//                list.add(stockInfo);
//            }
//            while (cursor.moveToNext());
//        }
        for (;;) {
            final StockInfo stockInfo = cursorReader.getNext();
            if (stockInfo == null) break;
            list.add(stockInfo);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }
    
    /**
     * A convenience class to assist navigating a cursor to read StockInfo data.  Replaces code
     * commented-out above, which is not reusable.  This class could be used in a UI context to manage
     * the cursor/data.
     */
    static class StockInfoCursorReader {
        final int ID_INDEX;
        final int SYMBOL_INDEX;
        final int NAME_INDEX;
        final int PRICE_INDEX;
        final int PRICE_PREV_INDEX;
        final int COUNT_INDEX;
        final int MIN_INDEX;
        final int MAX_INDEX;
        final int AVG_INDEX;
        final int MODIFIED_INDEX;
        
        private final Cursor cursor;
        StockInfoCursorReader (final Cursor cursor) {
            this.cursor = cursor;
            ID_INDEX         = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.ID.columnName);
            SYMBOL_INDEX     = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.SYMBOL.columnName);
            NAME_INDEX       = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.NAME.columnName);
            PRICE_INDEX      = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.PRICE.columnName);
            PRICE_PREV_INDEX = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.PREVIOUS_PRICE.columnName);
            COUNT_INDEX      = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.COUNT.columnName);
            MIN_INDEX        = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.MIN.columnName);
            MAX_INDEX        = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.MAX.columnName);
            AVG_INDEX        = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.AVG.columnName);
            MODIFIED_INDEX   = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.MODIFIED.columnName);
        }
        
        public StockInfo getNext() {
            if (cursor.isBeforeFirst()) {
                if (! cursor.moveToFirst())
                    return null;
            }
            else
            if (! cursor.moveToNext()) {
                return null;
            }
            final StockInfo stockInfo = new StockInfo();
            stockInfo.setId(cursor.getLong(ID_INDEX));
            stockInfo.setSymbol(cursor.getString(SYMBOL_INDEX));
            stockInfo.setName(cursor.getString(NAME_INDEX));
            stockInfo.setPrice((float)cursor.getFloat(PRICE_INDEX));
            stockInfo.setPreviousPrice((float)cursor.getFloat(PRICE_PREV_INDEX));
            stockInfo.setCount((int)cursor.getLong(COUNT_INDEX));
            stockInfo.setMin((float)cursor.getFloat(MIN_INDEX));
            stockInfo.setMax((float)cursor.getFloat(MAX_INDEX));
            stockInfo.setAvg((float)cursor.getFloat(AVG_INDEX));
            stockInfo.setModified(cursor.getLong(MODIFIED_INDEX));
            
            return stockInfo;
        }
    }
    
    //  This is not the way to handle large DB's
    public List<PriceData> selectAllPriceData(final String symbol) {
        final List<PriceData> list = new ArrayList<PriceData>();
        final Cursor cursor = db.rawQuery(SELECT_ALL_PRICE_DATA_FOR_SYMBOL, new String[] {symbol});
        if (cursor.moveToFirst())
        {
            final int ID_INDEX       = cursor.getColumnIndexOrThrow(PRICE_DATA_TABLE.ID.columnName);
            final int SYMBOL_ID_INDEX = cursor.getColumnIndex(PRICE_DATA_TABLE.SYMBOL_ID.columnName);
            final int TIMESTAMP_INDEX = cursor.getColumnIndex(PRICE_DATA_TABLE.TIMESTAMP.columnName);
            final int PRICE_INDEX = cursor.getColumnIndex(PRICE_DATA_TABLE.PRICE.columnName);
            do {
                PriceData priceData = new PriceData();
                priceData.setId(cursor.getLong(ID_INDEX));
                priceData.setSymbolId(cursor.getLong(SYMBOL_ID_INDEX));
                priceData.setTimestamp(cursor.getLong(TIMESTAMP_INDEX));
                priceData.setPrice((float)cursor.getFloat(PRICE_INDEX));
                list.add(priceData);
            }
            while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }
    
//    public List<PriceData> selectAllPriceData(final String symbol) {
//        final List<PriceData> list = new ArrayList<PriceData>();
//        final Cursor cursor = db.rawQuery(SELECT_ALL_PRICE_DATA_FOR_SYMBOL, new String[] {symbol});
//        if (cursor.moveToFirst())
//        {
//            final int ID_INDEX       = cursor.getColumnIndexOrThrow(PRICE_DATA_TABLE.ID.columnName);
//            final int SYMBOL_ID_INDEX = cursor.getColumnIndex(PRICE_DATA_TABLE.SYMBOL_ID.columnName);
//            final int TIMESTAMP_INDEX = cursor.getColumnIndex(PRICE_DATA_TABLE.TIMESTAMP.columnName);
//            final int PRICE_INDEX = cursor.getColumnIndex(PRICE_DATA_TABLE.PRICE.columnName);
//            do {
//                PriceData priceData = new PriceData();
//                priceData.setId(cursor.getLong(ID_INDEX));
//                priceData.setSymbolId(cursor.getLong(SYMBOL_ID_INDEX));
//                priceData.setTimestamp(cursor.getLong(TIMESTAMP_INDEX));
//                priceData.setPrice((float)cursor.getFloat(PRICE_INDEX));
//                list.add(priceData);
//            }
//            while (cursor.moveToNext());
//        }
//        if (cursor != null && !cursor.isClosed()) {
//            cursor.close();
//        }
//        return list;
//    }
    
//    static class PriceDataCursorReader {
//        final int ID_INDEX;
//        final int SYMBOL_INDEX;
//        final int NAME_INDEX;
//        final int PRICE_INDEX;
//        final int PRICE_PREV_INDEX;
//        final int COUNT_INDEX;
//        final int MIN_INDEX;
//        final int MAX_INDEX;
//        final int AVG_INDEX;
//        final int MODIFIED_INDEX;
//        
//        private final Cursor cursor;
//        StockInfoCursorReader (final Cursor cursor) {
//            this.cursor = cursor;
//            ID_INDEX         = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.ID.columnName);
//            SYMBOL_INDEX     = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.SYMBOL.columnName);
//            NAME_INDEX       = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.NAME.columnName);
//            PRICE_INDEX      = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.PRICE.columnName);
//            PRICE_PREV_INDEX = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.PREVIOUS_PRICE.columnName);
//            COUNT_INDEX      = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.COUNT.columnName);
//            MIN_INDEX        = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.MIN.columnName);
//            MAX_INDEX        = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.MAX.columnName);
//            AVG_INDEX        = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.AVG.columnName);
//            MODIFIED_INDEX   = cursor.getColumnIndexOrThrow(STOCK_INFO_TABLE.MODIFIED.columnName);
//        }
//        
//        public StockInfo getNext() {
//            if (cursor.isBeforeFirst()) {
//                if (! cursor.moveToFirst())
//                    return null;
//            }
//            else
//            if (! cursor.moveToNext()) {
//                return null;
//            }
//            final StockInfo stockInfo = new StockInfo();
//            stockInfo.setId(cursor.getLong(ID_INDEX));
//            stockInfo.setSymbol(cursor.getString(SYMBOL_INDEX));
//            stockInfo.setName(cursor.getString(NAME_INDEX));
//            stockInfo.setPrice((float)cursor.getFloat(PRICE_INDEX));
//            stockInfo.setPreviousPrice((float)cursor.getFloat(PRICE_PREV_INDEX));
//            stockInfo.setCount((int)cursor.getLong(COUNT_INDEX));
//            stockInfo.setMin((float)cursor.getFloat(MIN_INDEX));
//            stockInfo.setMax((float)cursor.getFloat(MAX_INDEX));
//            stockInfo.setAvg((float)cursor.getFloat(AVG_INDEX));
//            stockInfo.setModified(cursor.getLong(MODIFIED_INDEX));
//            
//            return stockInfo;
//        }
//    }
    
    
    static class OpenHelper
        extends SQLiteOpenHelper
    {
        private static final String LOGTAG = OpenHelper.class.getSimpleName();
        private static final boolean DEBUG = true;
    
        private static final String CREATE_STOCK_INFO_TABLE =
                "CREATE TABLE "
                + STOCK_INFO_TABLE.TableName
                + " (" + STOCK_INFO_TABLE.ID.columnName   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STOCK_INFO_TABLE.SYMBOL.columnName      + " TEXT NOT NULL UNIQUE, "
                + STOCK_INFO_TABLE.NAME.columnName        + " TEXT NOT NULL, "
                + STOCK_INFO_TABLE.PRICE.columnName       + " REAL NOT NULL, "
                + STOCK_INFO_TABLE.PREVIOUS_PRICE.columnName  + " REAL NOT NULL DEFAULT -1.0, "
                + STOCK_INFO_TABLE.COUNT.columnName       + " INTEGER NOT NULL, "
                + STOCK_INFO_TABLE.MIN.columnName         + " REAL NOT NULL, "
                + STOCK_INFO_TABLE.MAX.columnName         + " REAL NOT NULL, "
                + STOCK_INFO_TABLE.AVG.columnName         + " REAL NOT NULL, "
                + STOCK_INFO_TABLE.MODIFIED.columnName    + " LONG NOT NULL);";
                
        private static final String CREATE_PRICE_DATA_TABLE =
                "CREATE TABLE "
                + PRICE_DATA_TABLE.TableName + " ("
                + PRICE_DATA_TABLE.ID.columnName        + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PRICE_DATA_TABLE.SYMBOL_ID.columnName + " INTEGER NOT NULL,"
                + PRICE_DATA_TABLE.TIMESTAMP.columnName + " LONG NOT NULL,"
                + PRICE_DATA_TABLE.PRICE.columnName     + " REAL NOT NULL, "
                + "FOREIGN KEY(" + PRICE_DATA_TABLE.SYMBOL_ID.columnName + ") REFERENCES "
                +    STOCK_INFO_TABLE.TableName + "(" + STOCK_INFO_TABLE.ID.columnName +") ON DELETE CASCADE);";
        

        OpenHelper (final Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate (final SQLiteDatabase db) {
            if (DEBUG) {
                Log.d(LOGTAG, "onCreate!");
                Log.d(LOGTAG, "CREATE_STOCK_INFO_TABLE="+CREATE_STOCK_INFO_TABLE);
                Log.d(LOGTAG, "CREATE_PRICE_DATA_TABLE="+CREATE_PRICE_DATA_TABLE);
            }
            db.execSQL(CREATE_STOCK_INFO_TABLE);
            db.execSQL(CREATE_PRICE_DATA_TABLE);
        }

        @Override
        public void onOpen(final SQLiteDatabase db) {
            //  Needed to enable FK's and cascade deletes
            db.execSQL("PRAGMA foreign_keys=ON");
        }
        @Override
        public void onUpgrade (final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            Log.w("Example", "Upgrading database, this will drop tables and recreate.");
            db.execSQL("DROP TABLE IF EXISTS " + PRICE_DATA_TABLE.TableName);
            db.execSQL("DROP TABLE IF EXISTS " + STOCK_INFO_TABLE.TableName);
            onCreate(db);
        }
    }
}
