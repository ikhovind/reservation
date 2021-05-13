package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;


    @GetMapping
    public ResponseEntity getAllRooms() {
        JSONArray rooms = new JSONArray();
        roomService.getAllRooms().forEach(rooms::put);
        return ResponseEntity.ok().body(rooms.toList());
    }

    @GetMapping(value = "/{roomId}")
    public ResponseEntity getSingleRoom(@PathVariable UUID roomId){
        log.info("getting single room");
        JSONObject response = new JSONObject();
        response.put("result", false);
        try{
            Room room = roomService.getRoomById(roomId);
            response.put("result", true);
            response.put("room", room.toJson().toMap());
            log.info("found single room");
            return ResponseEntity.ok().body(response.toMap());
        } catch (EntityNotFoundException e){
            log.error("room could not be found");
          response.put("error", "room could not be found");
          return ResponseEntity.badRequest().body(response.toMap());
        } catch (Exception e) {
            log.error("unknown error", e);
            response.put("error", "of type " + e.getClass().toGenericString());
            return ResponseEntity.badRequest().body(response.toMap());
        }
    }
    @PostMapping
    public ResponseEntity saveSingleRoom(@RequestBody HashMap<String, String> map) {
        log.info("saving single room");
        JSONObject response = new JSONObject();
        response.put("result", false);
        try {
            String roomName = map.get("roomName");
            if(roomName != null && !roomName.isBlank()) {
                Room newRoom = new Room(map.get("roomName"));
                roomService.saveRoom(newRoom);
                response.put("result", true);
                response.put("room", newRoom.toJson().toMap());
                log.info("single room saved successfully");
                return ResponseEntity.status(201).body(response.toMap());
            }
            response.put("error", "roomname is null or blank");
            return ResponseEntity.badRequest().body(response.toMap());
        } catch (DataIntegrityViolationException e){
            log.error("room name was not unique");
            response.put("error", "room name must be unique");
            return ResponseEntity.badRequest().body(response.toMap());
        }
        catch (Exception e){
            log.error("unknown error", e);
            response.put("error", "of type " + e.getClass().toGenericString());
            return ResponseEntity.badRequest().body(response.toMap());
        }
    }

    @PutMapping(value = "/{roomId}")
    public ResponseEntity editRoom(@PathVariable UUID roomId, @RequestBody HashMap<String, String> map){
        log.info("editing room");
        JSONObject response = new JSONObject();
        response.put("result", false);
        try{
            Room room = roomService.getRoomById(roomId);
            room.setRoomName(map.get("roomName"));
            if(map.get("roomName") != null && !map.get("roomName").isBlank()) {
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
        }
        catch (Exception e) {
            log.error("unknown error", e);
            response.put("error", "of type " + e.getClass().getName());
            return ResponseEntity.badRequest().body(response.toMap());
        }
    }

    @DeleteMapping(value = "/{roomId}")
    public ResponseEntity deleteRoom(@PathVariable UUID roomId) {
        log.info("deleting room");
        JSONObject response = new JSONObject();
        response.put("result", false);
        try{
            roomService.deleteRoomById(roomId);
            response.put("result", true);
            log.info("deletion successful");
            return ResponseEntity.ok().body(response.toMap());
        } catch (Exception e){
            log.error("unknown error", e);
            response.put("result", false);
            response.put("error", "of type " + e.getClass().toGenericString());
            return ResponseEntity.status(404).body(response.toMap());
        }
    }
}
