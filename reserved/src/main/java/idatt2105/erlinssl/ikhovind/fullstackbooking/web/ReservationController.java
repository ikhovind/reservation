package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions.IllegalTimeframeException;
import idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions.TimestampParsingException;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Reservation;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.ReservationService;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.RoomService;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.SectionService;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.UserService;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.Utilities;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.AdminTokenRequired;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@CrossOrigin("*")
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @PostMapping("/rooms/{id}")
    public ResponseEntity reserveRoom(@PathVariable("id") UUID roomId,
                                      @RequestHeader("token") String token,
                                      @RequestBody Map<String, String> map) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Room room = roomService.getRoomById(roomId);
            User user = userService.getSingleUser(
                    UUID.fromString(securityService.getUserPartsByToken(token)[0]));
            Timestamp timeFrom = Utilities.stringToTimestamp(map.get("timeFrom"));
            Timestamp timeTo = Utilities.stringToTimestamp(map.get("timeTo"));
            if (!roomReservationNoOverlap(timeFrom, timeTo, room, null)) {
                jsonBody.put("error", "there are already reservations during that timeframe");
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(jsonBody.toMap());

            }
            Reservation reservation = new Reservation(room, null, timeFrom, timeTo);
            return addReservationToUser(jsonBody, user, reservation);

        } catch (EntityNotFoundException e) {
            jsonBody.put("error", "an invalid id was passed");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(jsonBody.toMap());

        } catch(IllegalTimeframeException e) {
            jsonBody.put("error", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(jsonBody.toMap());

        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            jsonBody.put("error", "unexpected error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(jsonBody.toMap());
        }
    }

    @PostMapping("/rooms/{rId}/sections/{sId}")
    public ResponseEntity reserveSection(@PathVariable("rId") UUID roomId,
                                         @PathVariable("sId") UUID sectionId,
                                         @RequestHeader("token") String token,
                                         @RequestBody Map<String, String> map) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Room room = roomService.getRoomById(roomId);
            System.out.println(room);   //TODO Find out why this prevents NullPointer. Possibly cache-related?
            Section section = sectionService.getSection(sectionId);
            if (!room.getSection().contains(section)) {
                jsonBody.put("error", "missing section-room relation");
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(jsonBody.toMap());

            }
            User user = userService.getSingleUser(
                    UUID.fromString(securityService.getUserPartsByToken(token)[0]));

            Timestamp timeFrom = Utilities.stringToTimestamp(map.get("timeFrom"));
            Timestamp timeTo = Utilities.stringToTimestamp(map.get("timeTo"));

            if (!sectionReservationNoOverlap(timeFrom, timeTo, room, section, null)) {
                jsonBody.put("error", "there are already reservations during that timeframe");
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(jsonBody.toMap());

            }

            Reservation reservation = new Reservation(room, section, timeFrom, timeTo);
            return addReservationToUser(jsonBody, user, reservation);

        } catch(IllegalTimeframeException e) {
            jsonBody.put("error", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(jsonBody.toMap());

        }  catch (EntityNotFoundException e) {
            jsonBody.put("error", "an invalid id was passed");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(jsonBody.toMap());

        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            jsonBody.put("error", "unexpected error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(jsonBody.toMap());
        }
    }

    @GetMapping("/test/{rId}/sections/{sId}")
    public ResponseEntity getReservationsBetweenTest(@PathVariable("rId") UUID roomId,
                                                     @PathVariable(value = "sId",
                                                             required = false) UUID sectionId,
                                                     @RequestBody Map<String, String> map) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Room room = roomService.getRoomById(roomId);
            JSONArray reservations = new JSONArray();
            Timestamp timeFrom = Utilities.stringToTimestamp(map.get("timeFrom"));
            Timestamp timeTo = Utilities.stringToTimestamp(map.get("timeTo"));
            if (sectionId != null) {
                log.warn("Inside section");
                Section section = sectionService.getSection(sectionId);
                for (Reservation r :
                        reservationService.getSectionReservationsBetween(timeFrom, timeTo, section)) {
                    reservations.put(r.toAdminJson());
                }
            } else {
                log.warn("Inside room");
                for (Reservation r :
                        reservationService.getRoomReservationsBetween(timeFrom, timeTo, room)) {
                    reservations.put(r.toAdminJson());
                }
            }
            jsonBody.put("reservations", reservations);

        } catch (EntityNotFoundException e) {
            jsonBody.put("error", "an invalid id was passed");
            return ResponseEntity
                    .badRequest()
                    .body(jsonBody.toMap());

        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            jsonBody.put("error", "unexpected error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(jsonBody.toMap());

        }

        jsonBody.put("result", true);
        return ResponseEntity
                .ok()
                .body(jsonBody.toMap());
    }

    @GetMapping("/test/{rId}")
    public ResponseEntity getReservationsBetweenTestRoom(@PathVariable("rId") UUID roomId,
                                                         @RequestBody Map<String, String> map) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Room room = roomService.getRoomById(roomId);
            JSONArray reservations = new JSONArray();
            Timestamp timeFrom = Utilities.stringToTimestamp(map.get("timeFrom"));
            Timestamp timeTo = Utilities.stringToTimestamp(map.get("timeTo"));
            log.warn("Inside room");
            for (Reservation r :
                    reservationService.getRoomReservationsBetween(timeFrom, timeTo, room)) {
                reservations.put(r.toAdminJson());
            }
            jsonBody.put("reservations", reservations);

        } catch (EntityNotFoundException e) {
            jsonBody.put("error", "an invalid id was passed");
            return ResponseEntity
                    .badRequest()
                    .body(jsonBody.toMap());

        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            jsonBody.put("error", "unexpected error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(jsonBody.toMap());

        }

        jsonBody.put("result", true);
        return ResponseEntity
                .ok()
                .body(jsonBody.toMap());
    }

    @UserTokenRequired
    @GetMapping("")
    public ResponseEntity getAllReservations(@RequestHeader("token") String token) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            JSONArray reservations = new JSONArray();
            boolean admin = Utilities.isAdmin(token);
            if (admin) {
                for (Reservation r : reservationService.getAllReservations()) {
                    reservations.put(r.toAdminJson());
                }
            } else {
                for (Reservation r : reservationService.getAllReservations()) {
                    reservations.put(r.toJson());
                }
            }
            jsonBody.put("reservations", reservations);

        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            jsonBody.put("error", "unexpected error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(jsonBody.toMap());
        }

        jsonBody.put("result", true);
        return ResponseEntity
                .ok()
                .body(jsonBody.toMap());
    }

    @UserTokenRequired
    @GetMapping("/rooms/{id}")
    public ResponseEntity getRoomReservations(@PathVariable("id") UUID roomId,
                                              @RequestHeader("token") String token) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Room room = roomService.getRoomById(roomId);
            JSONArray reservations = new JSONArray();
            if (Utilities.isAdmin(token)) {
                for (Reservation r : reservationService.getRoomReservations(room)) {
                    reservations.put(r.toAdminJson());
                }
            } else {
                for (Reservation r : reservationService.getRoomReservations(room)) {
                    reservations.put(r.toJson());
                }
            }
            jsonBody.put("reservations", reservations);
        } catch (EntityNotFoundException e) {
            jsonBody.put("error", "that room does not exist");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(jsonBody.toMap());
        }

        jsonBody.put("result", true);
        return ResponseEntity
                .ok()
                .body(jsonBody.toMap());
    }

    @UserTokenRequired
    @GetMapping("/rooms/{rId}/sections/{sId}")
    public ResponseEntity getSectionReservations(@PathVariable("rId") UUID roomId,
                                                 @PathVariable("sId") UUID sectionId,
                                                 @RequestHeader("token") String token) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Room room = roomService.getRoomById(roomId);
            System.out.println(room);
            Section section = sectionService.getSection(sectionId);
            JSONArray reservations = new JSONArray();
            if (Utilities.isAdmin(token)) {
                for (Reservation r : reservationService.getSectionReservations(section)) {
                    reservations.put(r.toAdminJson());
                }
            } else {
                for (Reservation r : reservationService.getSectionReservations(section)) {
                    reservations.put(r.toJson());
                }
            }
            jsonBody.put("reservations", reservations);

        } catch (EntityNotFoundException e) {
            jsonBody.put("error", "an invalid id was passed");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(jsonBody.toMap());
        }

        jsonBody.put("result", true);
        return ResponseEntity
                .ok()
                .body(jsonBody.toMap());
    }

    @AdminTokenRequired
    @DeleteMapping("/{id}")
    public ResponseEntity deleteReservation(@PathVariable("id") UUID id) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Reservation res = reservationService.getReservationById(id);
            User user = res.getUser();
            user.removeReservation(res);
            reservationService.deleteReservationById(id);
            userService.updateUser(user, false);

        } catch (EntityNotFoundException e) {
            jsonBody.put("error", "that reservation does not exist");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(jsonBody.toMap());

        } catch (Exception e) {
            log.error("An unexpected error was caught", e);
            jsonBody.put("error", "unexpected error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(jsonBody.toMap());
        }

        jsonBody.put("result", true);
        return ResponseEntity
                .ok()
                .body(jsonBody.toMap());
    }

    @AdminTokenRequired
    @PutMapping("/{id}")
    public ResponseEntity rebookReservation(@PathVariable("id") UUID id,
                                            @RequestBody Map<String, String> map) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Reservation reservation = reservationService.getReservationById(id);

            Timestamp timeFrom = Utilities.stringToTimestamp(map.get("timeFrom"));
            Timestamp timeTo = Utilities.stringToTimestamp(map.get("timeTo"));

            boolean noOverlap;
            if (reservation.getSection() == null) {
                noOverlap = roomReservationNoOverlap(timeFrom, timeTo,
                        reservation.getRoom(), reservation);
            } else {
                noOverlap = sectionReservationNoOverlap(timeFrom, timeTo,
                        reservation.getRoom(), reservation.getSection(), reservation);
            }
            if(!noOverlap) {
                jsonBody.put("error", "there are already reservations during that timeframe");
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(jsonBody.toMap());
            }

            reservation.setTimeFrom(timeFrom);
            reservation.setTimeTo(timeTo);
            reservationService.saveReservation(reservation);
            jsonBody.put("reservation", reservation.toJson());

        } catch (TimestampParsingException e) {
            jsonBody.put("error", "an invalid timestamp was passed");
            return ResponseEntity
                    .badRequest()
                    .body(jsonBody.toMap());

        } catch (EntityNotFoundException e) {
            jsonBody.put("error", "that reservation does not exist");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(jsonBody.toMap());

        } catch(IllegalTimeframeException e) {
            jsonBody.put("error", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(jsonBody.toMap());

        } catch (Exception e) {
            jsonBody.put("error", "unexpected error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(jsonBody.toMap());
        }

        jsonBody.put("result", true);
        return ResponseEntity
                .ok()
                .body(jsonBody.toMap());
    }

    private ResponseEntity addReservationToUser(JSONObject jsonBody, User user,
                                                Reservation reservation) throws IllegalTimeframeException{
        reservation.setUser(user);
        reservation = reservationService.saveReservation(reservation);
        user.addReservation(reservation);
        userService.updateUser(user, false);
        jsonBody.put("reservation", reservation.toJson());
        jsonBody.put("result", true);
        return ResponseEntity
                .created(URI.create("/reservations/" + reservation.getId().toString()))
                .body(jsonBody.toMap());
    }

    private boolean roomReservationNoOverlap(Timestamp timeFrom, Timestamp timeTo, Room room, Reservation self) {
        List<Reservation> roomReservations = reservationService.getRoomAndSectionReservationsBetween(timeFrom, timeTo, room);
        if (roomReservations.size() == 0) {
            System.out.println("No overlaps found");
            return true;
        } else if (roomReservations.size() == 1) {
            System.out.println("Single overlap found, it is " + roomReservations.get(0).equals(self));
            for (Reservation r :
                    roomReservations) {
                System.out.println(r.toJson().toString());
            }
            return roomReservations.get(0).equals(self);
        }
        log.error("THIS SECTION SHOULD NOT BE REACHED, THERE SEEMS TO BE AN OVERLAP IN THE SYSTEM, SIZE WAS " + roomReservations.size());
        return false;
    }

    private boolean sectionReservationNoOverlap(Timestamp timeFrom, Timestamp timeTo, Room room, Section section, Reservation self) {
        if (reservationService.getRoomReservationsBetween(timeFrom, timeTo, room).size() == 0) {
            List<Reservation> sectionReservations = reservationService.getSectionReservationsBetween(timeFrom, timeTo, section);
            if (sectionReservations.size() == 0) {
                return true;
            } else if (sectionReservations.size() == 1) {
                return sectionReservations.get(0).equals(self);
            }
            log.error("THIS SECTION SHOULD NOT BE REACHED, THERE SEEMS TO BE AN OVERLAP IN THE SYSTEM, SIZE WAS " + sectionReservations.size());
        }
        return false;
    }
}
