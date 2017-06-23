package x40241.jeffrey.lomibao.a4.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jllom on 6/10/2017.
 */

public class StockQuote implements Parcelable {
    private static final String LOGTAG = "PriceData";
    private static final boolean DEBUG = true;
    private long sequence;
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
    public void setPrice (float price) { this.price = price; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(sequence);
        dest.writeString(name);
        dest.writeString(symbol);
        dest.writeFloat(price);

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public StockQuote createFromParcel(Parcel in) {
            return new StockQuote(in);
        }
        public StockQuote[] newArray(int size) {
            return new StockQuote[size];
        }
    };

    public StockQuote(Parcel in) {
        sequence = in.readLong();
        name = in.readString();
        symbol = in.readString();
        price = in.readFloat();
    }

    public StockQuote() {
        sequence = 0;
    }

}
