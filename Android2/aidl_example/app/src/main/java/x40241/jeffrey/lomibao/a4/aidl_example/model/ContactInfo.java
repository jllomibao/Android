package x40241.jeffrey.lomibao.a4.aidl_example.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jllom on 6/24/2017.
 */

public class ContactInfo implements Parcelable {

    private String name;
    private String surname;
    private int idx;
    private static int nextIndex = 1;

    // Constructors
    public ContactInfo(String name, String surname){
        this.name = name;
        this.surname = surname;
        this.idx = this.nextIndex++;
    }

    // get and set method
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    // parcellable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeString(getSurname());
        dest.writeInt(getIdx());
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public ContactInfo createFromParcel(Parcel in) {
            return new ContactInfo(in);
        }

        public ContactInfo[] newArray(int size) {
            return new ContactInfo[size];
        }
    };

    // "De-parcel object
    public ContactInfo(Parcel in) {
        setName(in.readString());
        setSurname(in.readString());
        setIdx(in.readInt());
    }

}
