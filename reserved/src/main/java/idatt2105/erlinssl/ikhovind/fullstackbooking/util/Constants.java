package idatt2105.erlinssl.ikhovind.fullstackbooking.util;

public final class Constants {
    /**
     * Used when generating tokens to decide their lifetime in ms.
     */
    public static final long TTL_MILLIS = 1000L * 60L * 60L * 24L;                  // 24 hours
    public static final long MAX_RESERVATION_MILLIS = 1000L * 60L * 60L * 3L;       // 3 hours
    public static final long MIN_RESERVATION_MILLIS = 1000L * 60L * 30L;            // 30 minutes
    public static final long MAX_TIME_UNTIL_RES = 1000L * 60L * 60L * 24L * 31L;    // 31 days
    public static final int OPENING_HOUR = 4;       // GMT+00:00
    public static final int CLOSING_HOUR = 22;      // GMT+00:00
    public static final String TESTING_SUBJECT = "u<R3@zck9+5_c7$2/!=9";
    public static final boolean TESTING_ENABLED = true;
    public static final int USER_TYPE = 0;
    public static final int ADMIN_TYPE = 9;
}
