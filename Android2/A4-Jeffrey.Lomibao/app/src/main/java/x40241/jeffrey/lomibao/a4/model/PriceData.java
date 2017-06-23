package x40241.jeffrey.lomibao.a4.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jllom on 6/10/2017.
 */

public class PriceData implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(symbolId);
        dest.writeLong(timestamp);
        dest.writeFloat(price);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PriceData createFromParcel(Parcel in) {
            return new PriceData(in);
        }
        public PriceData[] newArray(int size) {
            return new PriceData[size];
        }
    };

    public PriceData(Parcel in) {
        id = in.readLong();
        symbolId = in.readLong();
        timestamp = in.readLong();
        price = in.readFloat();
    }

    public PriceData() {
        id = 0;
    }

}
