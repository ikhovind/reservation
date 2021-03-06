package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions.TimestampParsingException;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.RoomService;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.StatisticsService;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.Utilities;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.AdminTokenRequired;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.UserTokenRequired;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Web-Controller for /stats endpoints.
 * Endpoints in this class are used to get statistics about the registered
 * {@link idatt2105.erlinssl.ikhovind.fullstackbooking.model.Reservation} resources.
 */
@Slf4j
@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/stats")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SecurityService securityService;

    /**
     * Gets all sum of millisecond for all reservations, grouped by {@link User}, for a given timeframe.
     * This endpoint is only available to admins.
     *
     * @param timeFromString beginning of the timeframe, see {@link Utilities#stringToTimestamp(String)} for format
     * @param timeToString end of the timeframe, see {@link Utilities#stringToTimestamp(String)} for format
     * @param token JWT belonging to the {@link User} making the request
     * @return 200 OK with JSONArray of consisting of JSONObjects like {"user": String, "totalMillis": long}.
     * 400 BAD REQUEST if either of the time strings could not be parsed, or other exceptions occurred.
     */
    @AdminTokenRequired
    @GetMapping("/users")
    public ResponseEntity sortByUser(@RequestParam("timeFrom") String timeFromString,
                                     @RequestParam("timeTo") String timeToString,
                                     @RequestHeader("token") String token) {
        JSONObject jsonBody = new JSONObject();
        JSONArray sums = new JSONArray();
        boolean passed = true;
        try {
            Timestamp timeFrom = Utilities.stringToTimestamp(timeFromString);
            Timestamp timeTo = Utilities.stringToTimestamp(timeToString);

            for (Object[] sum : statisticsService.getUserSums(timeFrom, timeTo)) {
                JSONObject temp = new JSONObject();
                temp.put("user", sum[0].toString());
                temp.put("totalMillis", Long.parseLong(sum[1].toString())*1000L);
                sums.put(temp);
            }
        } catch (TimestampParsingException e) {
            jsonBody.put("error", e.getMessage());
            passed = false;
        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            jsonBody.put("error", "unexpected error");
            passed = false;
        }
        return getResponseEntity(jsonBody, sums, passed);
    }

    /**
     * Gets all sum of millisecond for all reservations, grouped by {@link Room}, for a given timeframe.
     * If sortBy=user, then the sums will also be grouped by {@link User}.
     * This endpoint is only available to admins.
     *
     * @param timeFromString beginning of the timeframe, see {@link Utilities#stringToTimestamp(String)} for format
     * @param timeToString end of the timeframe, see {@link Utilities#stringToTimestamp(String)} for format
     * @param sortType optional search feature that decides whether or not to group by user, can be omitted or "user"
     * @param token JWT belonging to the {@link User} making the request
     * @return 200 OK with JSONArray of consisting of JSONObjects like {"room": String, "totalMillis": long}
     * or {"user":String, "room": String, "totalMillis": long} if grouping by User.
     * 400 BAD REQUEST if either of the time strings could not be parsed, or other exceptions occurred.
     */
    @UserTokenRequired
    @GetMapping("/rooms")
    public ResponseEntity getAllRoomsStats(@RequestParam("timeFrom") String timeFromString,
                                           @RequestParam("timeTo") String timeToString,
                                           @RequestParam(value = "sortBy", required = false) String sortType,
                                           @RequestHeader("token") String token) {
        JSONObject jsonBody = new JSONObject();
        JSONArray sums = new JSONArray();
        boolean passed = true;
        try {
            Timestamp timeFrom = Utilities.stringToTimestamp(timeFromString);
            Timestamp timeTo = Utilities.stringToTimestamp(timeToString);

            if(sortByUser(sortType, token)) {
                for (Object[] sumPair : statisticsService.getRoomSumsGroupByUser(timeFrom, timeTo)) {
                    JSONObject temp = new JSONObject();
                    temp.put("user", sumPair[0].toString());
                    temp.put("room", sumPair[1].toString());
                    temp.put("totalMillis", parseSqlTime(sumPair[2].toString()));
                    sums.put(temp);
                }
            } else {
                for (Object[] sum : statisticsService.getRoomSums(timeFrom, timeTo)) {
                    JSONObject temp = new JSONObject();
                    temp.put("room", sum[0].toString());
                    temp.put("totalMillis", parseSqlTime(sum[1].toString()));
                    sums.put(temp);
                }
            }

        } catch (TimestampParsingException e) {
            jsonBody.put("error", e.getMessage());
            passed = false;
        } catch (Exception e) {
            log.error("An unexpected error occured", e);
            jsonBody.put("error", "unexpected error");
            passed = false;
        }
        return getResponseEntity(jsonBody, sums, passed);
    }

    /**
     * Gets all sum of millisecond for all reservations, grouped by {@link Section}, for a given timeframe.
     * If sortBy=user, then the sums will also be grouped by {@link User}.
     * This endpoint is only available to admins.
     *
     * @param timeFromString beginning of the timeframe, see {@link Utilities#stringToTimestamp(String)} for format
     * @param timeToString end of the timeframe, see {@link Utilities#stringToTimestamp(String)} for format
     * @param sortType optional search feature that decides whether or not to group by user, can be omitted or "user"
     * @param token JWT belonging to the {@link User} making the request
     * @return 200 OK with JSONArray of consisting of JSONObjects like {"section": String, "totalMillis": long}
     * or {"user":String, "section": String, "totalMillis": long} if grouping by User.
     * 400 BAD REQUEST if either of the time strings could not be parsed, or other exceptions occurred.
     */
    @UserTokenRequired
    @GetMapping("/rooms/{id}")
    public ResponseEntity getRoomSectionStats(@PathVariable("id") UUID roomId,
                                              @RequestParam("timeFrom") String timeFromString,
                                              @RequestParam("timeTo") String timeToString,
                                              @RequestParam(value = "sortBy", required = false) String sortType,
                                              @RequestHeader("token") String token) {
        JSONObject jsonBody = new JSONObject();
        JSONArray sums = new JSONArray();
        boolean passed = true;
        try {
            Room room = roomService.getRoomById(roomId);
            Timestamp timeFrom = Utilities.stringToTimestamp(timeFromString);
            Timestamp timeTo = Utilities.stringToTimestamp(timeToString);
            if (sortByUser(sortType, token)){
                for (Object[] sumPair : statisticsService.getRoomSectionSumsGroupByUser(timeFrom, timeTo, room)) {
                    JSONObject temp = new JSONObject();
                    String userId = sumPair[0].toString();
                    String sectionId;
                    String reserveType = "section";
                    if (sumPair[1] == null) {
                        reserveType = "room";
                        sectionId = room.getId().toString();
                    } else {
                        sectionId = sumPair[1].toString();
                    }
                    temp.put("user", userId);
                    temp.put(reserveType, sectionId);
                    temp.put("totalMillis", parseSqlTime(sumPair[2].toString()));
                    sums.put(temp);
                }
            } else {
                for (Object[] sumPair : statisticsService.getRoomSectionSums(timeFrom, timeTo, room)) {
                    JSONObject temp = new JSONObject();
                    String id;
                    String reserveType = "section";
                    if (sumPair[0] == null) {
                        reserveType = "room";
                        id = room.getId().toString();
                    } else {
                        id = sumPair[0].toString();
                    }
                    temp.put(reserveType, id);
                    temp.put("totalMillis", parseSqlTime(sumPair[1].toString()));
                    sums.put(temp);
                }
            }

        } catch (TimestampParsingException e) {
            jsonBody.put("error", e.getMessage());
            passed = false;
        } catch (Exception e) {
            log.error("An unexpected error occured", e);
            jsonBody.put("error", "unexpected error");
            passed = false;
        }
        return getResponseEntity(jsonBody, sums, passed);
    }

    private boolean sortByUser(String sortBy, String token) {
        if (sortBy !=null && sortBy.equalsIgnoreCase("user")) {
            return Integer.parseInt(securityService.getUserPartsByToken(token)[1]) != 0;
        }
        return false;
    }

    private ResponseEntity getResponseEntity(JSONObject jsonBody, JSONArray sums, boolean passed) {
        if (!passed) {
            jsonBody.put("result", false);
            return ResponseEntity
                    .badRequest()
                    .body(jsonBody.toMap());
        }

        jsonBody.put("sums", sums);
        jsonBody.put("result", true);
        return ResponseEntity
                .ok()
                .body(jsonBody.toMap());
    }

    private long parseSqlTime(String time) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date timeSum = timeFormat.parse(time);
        return timeSum.getTime();
    }
}
