// IRemoteService.aidl
package x40241.jeffrey.lomibao.a4.aidl_example;

// Declare any non-default types here with import statements

interface IRemoteService {
    /** Request the process ID of this service, to do things with it. */
    int getPid();

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
