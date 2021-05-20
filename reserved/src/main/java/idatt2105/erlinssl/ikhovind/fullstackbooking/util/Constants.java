package idatt2105.erlinssl.ikhovind.fullstackbooking.util;

/**
 * A final class that is used to store variables so that they can be used anywhere in the project, and easily
 * changed when needed.
 */
public final class Constants {
    /**
     * Used when generating tokens to decide their lifetime in ms.
     */
    public static final long TTL_MILLIS = 1000L * 60L * 60L * 24L;                  // 24 hours
    /**
     * The longest amount of time a reservation can be in ms.
     */
    public static final long MAX_RESERVATION_MILLIS = 1000L * 60L * 60L * 3L;       // 3 hours
    /**
     * The shortest amount of time a reservation can be in ms.
     */
    public static final long MIN_RESERVATION_MILLIS = 1000L * 60L * 30L;            // 30 minutes
    /**
     * The longest amount of time into the future a reservation can be reserved.
     */
    public static final long MAX_TIME_UNTIL_RES = 1000L * 60L * 60L * 24L * 31L;    // 31 days
    /**
     * The opening time of the business using the system.
     */
    public static final int OPENING_HOUR = 6;       // GMT+02:00
    /**
     * The closing time of the business using the system.
     */
    public static final int CLOSING_HOUR = 24;      // GMT+02:00
    /**
     * An arbitrary string that is used to generate a "testing token" that can bypass TokenRequired.
     * DO NOT leave this in a finalized system, see also {@link Constants#TESTING_ENABLED}.
     */
    public static final String TESTING_SUBJECT = "u<R3@zck9+5_c7$2/!=9";
    /**
     * A boolean that decides whether or not TokenRequired annotations can be bypassed or not.
     * @deprecated DO NOT leave this boolean as true in a finalized system
     */
    public static final boolean TESTING_ENABLED = true;
    /**
     * An arbitrary int that decides what number is used for normal users.
     */
    public static final int USER_TYPE = 0;
    /**
     * An arbitrary int that decides what number is used for admin users.
     */
    public static final int ADMIN_TYPE = 9;
}
