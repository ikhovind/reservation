package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions.NotUniqueSectionNameException;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.RoomService;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.SectionService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/rooms/{roomId}/sections")
public class SectionController {
    @Autowired
    private SectionService sectionService;
    @Autowired
    private RoomService roomService;

    @PostMapping
    public ResponseEntity addSectionToRoom(@PathVariable UUID roomId, @RequestBody Map<String, Object> map) {
        log.info("adding section to room");
        JSONObject response = new JSONObject();
        response.put("result", false);
        try{
            Section section = new Section(map.get("sectionName").toString(), map.get("sectionDesc").toString());

            Room room = roomService.getRoomById(roomId);
            //check if the section contains the room already
            if (!room.getSection().contains(section)) {
                Section savedSection = sectionService.saveSection(section);
                room.addSection(savedSection);
                Room editedRoom = roomService.saveRoom(room);

                response.put("result",true);
                response.put("room", editedRoom.toJson());
                return ResponseEntity.status(201).body(response.toMap());
            }
            response.put("error", "room already contains such a section");
            log.error("room already contains section with given name or id");
        } catch (IllegalArgumentException e) {
            log.error("invalid section name values", e);
            response.put("error", "invalid section name values");
            return ResponseEntity.status(400).body(response.toMap());
        } catch (EntityNotFoundException e) {
            log.error("room could not be found", e);
            response.put("error", "room could not be found");
        } catch (NullPointerException e) {
            log.error("one or more values were null");
            response.put("error", "one or more required values were null");
        }
        catch (Exception e) {
            log.error("cannot add section to room", e);
            response.put("error", "of type " + e.getClass().getName());
        }
        return ResponseEntity.badRequest().body(response.toMap());
    }

    @DeleteMapping(value = "/{sectionId}")
    public ResponseEntity deleteSection(@PathVariable UUID roomId, @PathVariable UUID sectionId) {
        log.info("deleting section");
        JSONObject response = new JSONObject();
        response.put("result", false);
        try {
            sectionService.deleteSection(roomId, sectionId);
            response.put("result", true);
            //todo error when section is not found?
            return ResponseEntity.ok().body(response.toMap());
        } catch (IllegalArgumentException e) {
            log.error("room and section not connected", e);
          response.put("error", "section does not belong to room");
        } catch(EntityNotFoundException e) {
            log.error("could not find entity", e);
            response.put("error", "could not find room");
            return ResponseEntity.status(404).body(response.toMap());
        } catch (EmptyResultDataAccessException e) {
            log.error("could not find section", e);
            response.put("error", "could not find section with given id");
            return ResponseEntity.status(404).body(response.toMap());
        } catch (Exception e) {
            log.error("cannot add section to room", e);
            response.put("error", "of type " + e.getClass().getName());
        }
        return ResponseEntity.badRequest().body(response.toMap());
    }

    @PutMapping(value = "/{sectionId}")
    public ResponseEntity editSection(@PathVariable UUID roomId,
                                      @PathVariable UUID sectionId,
                                      @RequestBody Map<String, Object> map) {
        log.info("editing section in room");
        JSONObject response = new JSONObject();
        response.put("result", false);
        try {
            Section section = sectionService.getSection(sectionId);
            if (roomService.roomContainsSection(roomId, sectionId)) {
                section.setSectionDesc(map.get("sectionDesc").toString());
                section.setSectionName(map.get("sectionName").toString());
                Section editedSection = sectionService.editSection(roomId, section);
                Room roomWithSection = roomService.getRoomFromSection(editedSection);
                response.put("result", true);
                response.put("room", roomWithSection.toJson());
                log.info("edit success");
                return ResponseEntity.ok().body(response.toMap());
            }
            log.info("given room does not contain section");
            response.put("error", "room does not contain section");
        } catch (NotUniqueSectionNameException e) {
          log.error("cannot edit section as given section name already exists");
          response.put("error", "the given section name is not unique");
        } catch (NullPointerException e) {
            log.error("one or more parameters is null");
            response.put("error", "one or more parameters is null");
        } catch (IllegalArgumentException e) {
          log.error("invalid parameters", e);
          response.put("error", "one or more illegal parameters");
        } catch (EntityNotFoundException e) {
            log.error("could not find room");
            response.put("error", "could not find room");
            return ResponseEntity.status(404).body(response.toMap());
        } catch (Exception e) {
            log.error("error when editing section", e);
            response.put("error", "of type " + e.getClass().getName());
        }
        return ResponseEntity.badRequest().body(response.toMap());
    }
}
