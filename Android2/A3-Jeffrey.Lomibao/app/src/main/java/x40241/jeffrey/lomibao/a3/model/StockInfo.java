package x40241.jeffrey.lomibao.a3.model;

import java.io.Serializable;

/**
 * @author Jeffrey Peacock (Jeffrey.Peacock@uci.edu)
 */
public final class StockInfo implements Serializable
{
    private static final String LOGTAG = "StockInfo";
    private static final boolean DEBUG = true;
    static final float AVERAGING_WEIGHT = 0.33f;

    private long id;
    private String symbol;
    private String name;
    private float  price;
    private float previousPrice;
    private int count;
    private float min;
    private float max;
    private float avg;
    private long modified; // timestamp

    public long getId() {
        return id;
    }
    public void setId (long id) { this.id = id; }
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol (String symbol) {
        this.symbol = symbol;
    }
    public String getName() {
        return name;
    }
    public void setName (String name) {
        this.name = name;
    }
    public float getPrice() {
        return price;
    }
    public void setPrice (float price) { this.price = price; }
    public float getPreviousPrice() {
        return previousPrice;
    }
    public void setPreviousPrice(float previousPrice) {
        this.previousPrice = previousPrice;
    }
    public float getPriceChange() {
        return (count < 2) ? 0.0f:previousPrice - price;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public float getMin() {
        return min;
    }
    public void setMin(float min) { this.min = min; }
    public float getMax() {
        return max;
    }
    public void setMax(float max) { this.max = max; }
    public float getAvg() {
        return avg;
    }
    public void setAvg(float avg) { this.avg = avg; }
    public long getModified() { return modified; }
    public void setModified (long modified) {
        this.modified = modified;
    }

    @Override
    public boolean equals(Object object)
    {
        if ((object != null) && (object instanceof StockInfo))
        {
            return this.symbol.equals(((StockInfo)object).symbol);
        }

        return false;
    }

}
