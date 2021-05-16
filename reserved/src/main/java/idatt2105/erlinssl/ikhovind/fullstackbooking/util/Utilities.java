package idatt2105.erlinssl.ikhovind.fullstackbooking.util;

import idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions.PermissionDeniedException;
import idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions.TimestampParsingException;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.UUID;

@Slf4j
public class Utilities {
    private static SecurityService securityService = new SecurityService();

    public static Timestamp toTimestamp(String string) {
        try {
            Timestamp time = new Timestamp(
                    Date.from(Instant.from(
                            DateTimeFormatter.ISO_INSTANT.parse(string)))
                            .getTime());
            return time;
        } catch (Exception e) {
            throw new TimestampParsingException();
        }
    }

    public static String timestampToString(Timestamp timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("CET"));
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
