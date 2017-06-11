package x40241.jeffrey.lomibao.a2.model;

import java.io.Serializable;

/**
 * @author Jeffrey Peacock (Jeffrey.Peacock@uci.edu)
 */
public final class StockInfo implements Serializable
{
    private long   sequence;
    private String name;
    private String symbol;
    private float  price;
    
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
        this.price = price;
    }
}
