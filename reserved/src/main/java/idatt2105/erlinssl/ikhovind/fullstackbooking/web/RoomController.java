package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.RoomService;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.AdminTokenRequired;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Web-Controller for /rooms endpoints.
 * Endpoints in this class are used to create or interact with {@link Room} resources.
 */
@Slf4j
@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    /**
     * Used to get information about all the rooms (with sections) that exist in a system.
     *
     * @return JSONArray of {@link Room} objects in format {@link Room#toJson()}, or an error if something went wrong
     */
    @GetMapping
    public ResponseEntity getAllRooms() {
        JSONObject response = new JSONObject();
        JSONArray rooms = new JSONArray();
        try {
            for (Room r : roomService.getAllRooms()) {
                rooms.put(r.toJson());
            }
            response.put("result", true);
            response.put("rooms", rooms);
            return ResponseEntity.ok().body(response.toMap());
        } catch (Exception e) {
            response.put("result", false);
            response.put("error", "of type " + e.getClass().getName());
            return ResponseEntity.badRequest().body(response.toMap());
        }
    }

    /**
     * Used to get information about a single room and it's sections
     *
     * @param roomId {@link UUID} String belonging to the room
     * @return 200 OK with information if successful, 400 if not
     */
    @GetMapping(value = "/{roomId}")
    public ResponseEntity getSingleRoom(@PathVariable UUID roomId) {
        log.info("getting single room");
        JSONObject response = new JSONObject();
        response.put("result", false);
        try {
            Room room = roomService.getRoomById(roomId);
            response.put("room", room.toJson());
            log.info("found single room");
            response.put("result", true);
            return ResponseEntity.ok().body(response.toMap());
        } catch (EntityNotFoundException e) {
            log.error("room could not be found");
            response.put("error", "room could not be found");
            return ResponseEntity.badRequest().body(response.toMap());
        } catch (Exception e) {
            log.error("unknown error", e);
            response.put("error", "of type " + e.getClass().toGenericString());
            return ResponseEntity.badRequest().body(response.toMap());
        }
    }

    /**
     * Used by admins to create new rooms.
     *
     * @param map containing the new rooms name {"roomName": String}
     * @return {@link Room#toJson()}
     */
    @AdminTokenRequired
    @PostMapping
    public ResponseEntity saveSingleRoom(@RequestBody HashMap<String, String> map) {
        log.info("saving single room");
        JSONObject response = new JSONObject();
        response.put("result", false);
        try {
            String roomName = map.get("roomName");
            if (roomName != null && !roomName.isBlank()) {
                Room newRoom = new Room(map.get("roomName"));
                newRoom = roomService.saveRoom(newRoom);
                response.put("result", true);
                response.put("room", newRoom.toJson());
                log.info("single room saved successfully");
                return ResponseEntity.status(201).body(response.toMap());
            }
            response.put("error", "roomname is null or blank");
            return ResponseEntity.badRequest().body(response.toMap());
        } catch (DataIntegrityViolationException e) {
            log.error("room name was not unique");
            response.put("error", "room name must be unique");
            return ResponseEntity.badRequest().body(response.toMap());
        } catch (Exception e) {
            log.error("unknown error", e);
            response.put("error", "of type " + e.getClass().toGenericString());
            return ResponseEntity.badRequest().body(response.toMap());
        }
    }

    /**
     * Can be used by admins to edit the name of a room.
     *
     * @param roomId the room to be edited
     * @param map    containing {"roomName": String} with new name
     * @return 200 OK with {@link Room#toJson()} if successful. 400 if missing/invalid input. 404 if invalid room id.
     */
    @AdminTokenRequired
    @PutMapping(value = "/{roomId}")
    public ResponseEntity editRoom(@PathVariable UUID roomId, @RequestBody HashMap<String, String> map) {
        log.info("editing room");
        JSONObject response = new JSONObject();
        response.put("result", false);
        try {
            Room room = roomService.getRoomById(roomId);
            room.setRoomName(map.get("roomName"));
            if (map.get("roomName") != null && !map.get("roomName").isBlank()) {
                roomService.saveRoom(room);
                response.put("result", true);
                response.put("room", room.toJson());
                log.info("editing room succeeded");
                return ResponseEntity.ok().body(response.toMap());
            }
            response.put("error", "room name cannot be blank or null");
            return ResponseEntity.ok().body(response.toMap());
        } catch (DataIntegrityViolationException e) {
            log.error("room name is not unique");
            response.put("error", "room name must be unique");
            return ResponseEntity.badRequest().body(response.toMap());
        } catch (EntityNotFoundException e) {
            response.put("error", "room cannot be found");
            return ResponseEntity.status(404).body(response.toMap());
        } catch (Exception e) {
            log.error("unknown error", e);
            response.put("error", "of type " + e.getClass().getName());
            return ResponseEntity.badRequest().body(response.toMap());
        }
    }

    /**
     * Used to delete a room given by it's UUID. Requires admin permissions.
     *
     * @param roomId {@link UUID} of room to be deleted
     * @return 200 OK if successful. 404 NOT FOUND if invalid ID, or something went wrong
     */
    @AdminTokenRequired
    @DeleteMapping(value = "/{roomId}")
    public ResponseEntity deleteRoom(@PathVariable UUID roomId) {
        log.info("deleting room");
        JSONObject response = new JSONObject();
        response.put("result", false);
        try {
            roomService.deleteRoomById(roomId);
            response.put("result", true);
            log.info("deletion successful");
            return ResponseEntity.ok().body(response.toMap());
        } catch (EmptyResultDataAccessException | EntityNotFoundException e) {
            response.put("error", "cannot find room to delete");
            return ResponseEntity.status(404).body(response.toMap());
        } catch (Exception e) {
            log.error("unknown error", e);
            response.put("error", "of type " + e.getClass().toGenericString());
            return ResponseEntity.status(404).body(response.toMap());
        }
    }
}
