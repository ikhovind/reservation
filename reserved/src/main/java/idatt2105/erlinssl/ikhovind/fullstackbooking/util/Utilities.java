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

@Slf4j
public class Utilities {
    private static SecurityService securityService = new SecurityService();

    public static Timestamp toTimestamp(String string) {
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

    public static String timestampToString(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(timestamp);
    }

    public static boolean isAdmin(String token) {
        return Integer.parseInt(securityService.getUserPartsByToken(token)[1]) == Constants.ADMIN_TYPE;
    }

    public static void uidMatch(String token, UUID userId) {
        if (!securityService.getUserPartsByToken(token)[0].equals(userId.toString())) {
            throw new PermissionDeniedException();
        }
    }
}
