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

    private long   sequence;
    private String name;
    private String symbol;
    private float  price;
    private float minPrice;
    private float maxPrice;
    private float averagePrice;
    private float previousPrice;
    private int count;
    private boolean modified;

    public long getSequence() {
        return sequence;
    }
    public void setSequence (long sequence) {
        this.sequence = sequence;
    }
    public String getName() {
        return name;
    }
    public void setName (String name) {
        this.name = name;
    }
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol (String symbol) {
        this.symbol = symbol;
    }
    public float getPrice() {
        return price;
    }
    public void setPrice (float price) {
        if(this.price != price) {
            modified = true;
            count++;
            previousPrice = this.price;
            this.price = price;
        }
    }
    public float getPreviousPrice() {
        return previousPrice;
    }
    public void setPreviousPrice(float previousPrice) {
        this.previousPrice = previousPrice;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public boolean isModified() {
        return modified;
    }
    public void setModified(boolean modified) {
        this.modified = modified;
    }
    public float getMinPrice() {
        return minPrice;
    }
    public void setMinPrice(float minPrice) { this.minPrice = minPrice; }
    public float getMaxPrice() {
        return maxPrice;
    }
    public void setMaxPrice(float maxPrice) { this.maxPrice = maxPrice; }
    public float getAveragePrice() {
        return averagePrice;
    }
    public void setAveragePrice(float averagePrice) { this.averagePrice = averagePrice; }
    public float getPriceChange() {
        return price - previousPrice;
    }

    public StockInfo() {
        resetPriceTrends();
    }

    public void resetPriceTrends() {
        minPrice = Float.NaN;
        maxPrice = Float.NaN;
        averagePrice = Float.NaN;
        count = 0;
    }

    public void updatePriceTrends(float newPrice) {
        if(Float.isNaN(minPrice) || (newPrice < minPrice)) {
            minPrice = newPrice;
        }
        if(Float.isNaN(maxPrice) || (newPrice > maxPrice)) {
            maxPrice = newPrice;
        }
        if(Float.isNaN(averagePrice)) {
            averagePrice = newPrice;
        } else {
            float priceChange = newPrice - averagePrice;
            averagePrice += priceChange * AVERAGING_WEIGHT;
        }
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
