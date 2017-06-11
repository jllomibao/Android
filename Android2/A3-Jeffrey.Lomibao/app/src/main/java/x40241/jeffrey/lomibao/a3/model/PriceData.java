package x40241.jeffrey.lomibao.a3.model;

/**
 * Created by jllom on 6/10/2017.
 */

public class PriceData {
    private static final String LOGTAG = "PriceData";
    private static final boolean DEBUG = true;

    private long id;
    private long symbolId;
    private long timestamp;
    private float  price;

    public long getId() {
        return id;
    }
    public void setId (long id) { this.id = id; }
    public long getSymbolId() {
        return symbolId;
    }
    public void setSymbolId (long symbolId) { this.symbolId = symbolId; }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp (long timestamp) { this.timestamp = timestamp; }
    public float getPrice() {
        return price;
    }
    public void setPrice (float price) { this.price = price; }

}
