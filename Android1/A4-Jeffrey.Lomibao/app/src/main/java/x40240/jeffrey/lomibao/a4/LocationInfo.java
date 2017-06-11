package x40240.jeffrey.lomibao.a4;

/**
 * Created by jllom on 4/30/2017.
 */

public class LocationInfo {
    private double latitude;
    private double longitude;
    private double altitude;
    private String address;
    private String description;
    private String altitude_unit;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAltitude_unit() {
        return altitude_unit;
    }

    public void setAltitude_unit(String altitude_unit) {
        this.altitude_unit = altitude_unit;
    }
}
