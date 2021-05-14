package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

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
        // TODO Check if any sections are already reserved during the given time
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Room room = roomService.getRoomById(roomId);
            User user = userService.getSingleUser(
                    UUID.fromString(securityService.getUserPartsByToken(token)[0]));
            Reservation reservation = new Reservation(room, null,
                    Utilities.toTimestamp(map.get("timeFrom")),
                    Utilities.toTimestamp(map.get("timeTo")));
            return addReservationToUser(jsonBody, user, reservation);

        } catch (EntityNotFoundException e) {
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

    @PostMapping("/rooms/{rId}/sections/{sId}")
    public ResponseEntity reserveSection(@PathVariable("rId") UUID roomId,
                                         @PathVariable("sId") UUID sectionId,
                                         @RequestHeader("token") String token,
                                         @RequestBody Map<String, String> map) {
        // TODO Check if the room is already reserved during the given time
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Room room = roomService.getRoomById(roomId);
            Section section = sectionService.getSection(sectionId);
            User user = userService.getSingleUser(
                    UUID.fromString(securityService.getUserPartsByToken(token)[0]));
            Reservation reservation = new Reservation(room, section,
                    Utilities.toTimestamp(map.get("timeFrom")),
                    Utilities.toTimestamp(map.get("timeTo")));
            return addReservationToUser(jsonBody, user, reservation);

        } catch (EntityNotFoundException e) {
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

    @GetMapping("")
    public ResponseEntity getAllReservations() {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            JSONArray reservations = new JSONArray();
            for (Reservation r : reservationService.getAllReservations()) {
                reservations.put(r.toJson());
            }
            jsonBody.put("reservations", reservations);
            jsonBody.put("result", true);
            return ResponseEntity
                    .ok()
                    .body(jsonBody.toMap());

        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            jsonBody.put("error", "unexpected error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(jsonBody.toMap());
        }
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity getRoomReservations(@PathVariable("id") UUID roomId,
                                              @RequestHeader("token") String token) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Room room = roomService.getRoomById(roomId);
            JSONArray reservations = new JSONArray();
            for (Reservation r : reservationService.getRoomReservations(room)) {
                reservations.put(r.toJson());
            }
            jsonBody.put("reservations", reservations);
            jsonBody.put("result", true);
            return ResponseEntity
                    .ok()
                    .body(jsonBody.toMap());

        } catch (EntityNotFoundException e) {
            jsonBody.put("error", "that room does not exist");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(jsonBody.toMap());
        }

    }

    @GetMapping("/rooms/{rId}/sections/{sId}")
    public ResponseEntity getSectionReservations(@PathVariable("rId") UUID roomId,
                                                 @PathVariable("sId") UUID sectionId,
                                                 @RequestHeader("token") String token) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Room room = roomService.getRoomById(roomId);
            Section section = sectionService.getSection(sectionId);
            JSONArray reservations = new JSONArray();
            for (Reservation r : reservationService.getSectionReservations(section)) {
                reservations.put(r.toJson());
            }
            jsonBody.put("reservations", reservations);
            jsonBody.put("result", true);
            return ResponseEntity
                    .ok()
                    .body(jsonBody.toMap());

        } catch (EntityNotFoundException e) {
            jsonBody.put("error", "that room does not exist");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(jsonBody.toMap());
        }

    }

    //@AdminTokenRequired
    @DeleteMapping("/{id}")
    public ResponseEntity deleteReservation(@PathVariable("id") UUID id) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Reservation res = reservationService.getReservationById(id);
            User user = res.getUser();
            user.removeReservation(res);
            reservationService.deleteReservationById(id);
            userService.updateUser(user);
            jsonBody.put("result", true);
            return ResponseEntity
                    .ok()
                    .body(jsonBody.toMap());

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
    }


    //@AdminTokenRequired
    @PutMapping("/{id}")
    public ResponseEntity rebookReservation(@PathVariable("id") UUID id,
                                            @RequestBody Map<String, String> map) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            Reservation reservation = reservationService.getReservationById(id);
            reservation.setTimeFrom(Utilities.toTimestamp(map.get("timeFrom")));
            reservation.setTimeTo(Utilities.toTimestamp(map.get("timeTo")));
            reservationService.saveReservation(reservation);
            jsonBody.put("reservation", reservation.toJson());
            jsonBody.put("result", true);
            return ResponseEntity
                    .ok()
                    .body(jsonBody.toMap());

        } catch (EntityNotFoundException e) {
            jsonBody.put("error", "that reservation does not exist");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(jsonBody.toMap());

        } catch (Exception e) {
            jsonBody.put("error", "unexpected error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(jsonBody.toMap());
        }
    }

    private ResponseEntity addReservationToUser(JSONObject jsonBody, User user,
                                                Reservation reservation) throws Exception {
        reservation.setUser(user);
        reservation = reservationService.saveReservation(reservation);
        user.addReservation(reservation);
        userService.updateUser(user);
        jsonBody.put("reservation", reservation.toJson());
        jsonBody.put("result", true);
        return ResponseEntity
                .created(URI.create("/reservations/" + reservation.getId().toString()))
                .body(jsonBody.toMap());
    }
}
