package idatt2105.erlinssl.ikhovind.fullstackbooking.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Slf4j
public class Utilities {
    public static Timestamp toTimestamp(String string) {
        Timestamp time = new Timestamp(
                Date.from(Instant.from(
                        DateTimeFormatter.ISO_INSTANT.parse(string)))
                        .getTime());
        return time;
    }

    public static String timestampToString(Timestamp timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("CET"));
        return sdf.format(timestamp);
    }
}
