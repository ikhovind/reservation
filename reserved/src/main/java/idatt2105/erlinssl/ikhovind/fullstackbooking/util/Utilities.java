package idatt2105.erlinssl.ikhovind.fullstackbooking.util;

import idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions.PermissionDeniedException;
import idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions.TimestampParsingException;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.service.SecurityService;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * A class that stores methods that might need to be used several places in the project.
 * They are put here so they can be conveniently accessed.
 */
@Slf4j
public class Utilities {
    private static final SecurityService securityService = new SecurityService();

    /**
     * Parses a date string with format yyyy-MM-dd'T'HH:mm:ss.SSS'Z' into a UTC-based SQL {@link Timestamp}.
     * Based on ISO 8601.
     *
     * @param string of format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
     * @return parsed {@link Timestamp}
     * @see Utilities#timestampToString(Timestamp)
     */
    public static Timestamp stringToTimestamp(String string) {
        try {
            Date initialDate = Date.from(Instant.from(
                    DateTimeFormatter.ISO_INSTANT.parse(string)));
            Calendar roundedDate = Calendar.getInstance();
            roundedDate.setTime(initialDate);
            int mod = roundedDate.get(Calendar.MINUTE) % 15;
            roundedDate.add(Calendar.MINUTE, mod < 8 ? -mod : (15 - mod));
            roundedDate.set(Calendar.SECOND, 0);
            roundedDate.set(Calendar.MILLISECOND, 0);
            return new Timestamp(roundedDate.getTime().getTime());
        } catch (Exception e) {
            log.error("Timestamp could not be parsed", e);
            throw new TimestampParsingException();
        }
    }

    /**
     * The counterpart of {@link Utilities#stringToTimestamp(String)}. Parses an {@link Timestamp} into a datetime
     * string with format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'". Based on ISO 8601.
     *
     * @param timestamp SQL {@link Timestamp} to be parsed
     * @return the given timestamp in String format
     */
    public static String timestampToString(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(timestamp);
    }

    /**
     * Can be used to check if a given token belongs to a user that is an admin or not.
     * Compares the users userType, gotten from {@link SecurityService#getUserPartsByToken(String)},
     * to {@link Constants#ADMIN_TYPE}
     *
     * @param token JWT of the user being checked
     * @return true if the user is an admin, false if not
     */
    public static boolean isAdmin(String token) {
        return Integer.parseInt(securityService.getUserPartsByToken(token)[1]) == Constants.ADMIN_TYPE;
    }

    /**
     * Can be used to check if a given token belongs to a given user.
     * Compares the users UUID, gotten from {@link SecurityService#getUserPartsByToken(String)} to
     * the given {@link UUID} userId.
     *
     * @param token JWT of the user being compared
     * @param userId {@link UUID} belonging to the user we want to compare to
     */
    public static void uidMatch(String token, UUID userId) {
        if (!securityService.getUserPartsByToken(token)[0].equals(userId.toString())) {
            throw new PermissionDeniedException();
        }
    }

    /**
     * Checks whether two given {@link Timestamp}s are within the business opening hours.
     * These are defined as {@link Constants#OPENING_HOUR} and {@link Constants#CLOSING_HOUR}
     *
     * @param timeFrom a to-be-reservations start time
     * @param timeTo a to-be-reservations end time
     * @return true if they are within business hours, false if not
     */
    public static boolean withinBusinessHours(long timeFrom, long timeTo) {
        int timeFromHour = (int) ((timeFrom%(1000L * 60L * 60L * 24L))/(1000L * 60L * 60L));
        int timeToHour = (int) ((timeTo % (1000L * 60L * 60L * 24L))/(1000L * 60L * 60L));

        if(timeFromHour < Constants.OPENING_HOUR || timeFromHour > Constants.CLOSING_HOUR) {
            System.out.println("Bad timeFrom " + timeFromHour);
            return false;
        }
        System.out.println("timeTo " + timeToHour);
        if(timeToHour < Constants.OPENING_HOUR || timeToHour > Constants.CLOSING_HOUR) {
            return timeToHour == 0;
        }
        return true;
    }
}
