package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.UserService;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.Utilities;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity createUser(@RequestBody Map<String, Object> map) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        User newUser;
        try {
            newUser = mapToUser(map);
            newUser = userService.registerNewUserAccount(newUser);

        } catch (ParseException e) {
            jsonBody.put("error", "could not parse timestamp");
            return ResponseEntity
                    .badRequest()
                    .body(jsonBody.toMap());

        } catch (IllegalArgumentException e) {
            jsonBody.put("error", "email already registered");
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
                .created(URI.create("/users/" + newUser.getId()))
                .body(jsonBody.toMap());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getUser(@PathVariable("id") UUID userId) {
        JSONObject jsonBody = new JSONObject();
        try {
            User user = userService.getSingleUser(userId);

            jsonBody.put("result", true);
            jsonBody.put("user", user.toJson());
            return ResponseEntity
                    .ok()
                    .body(jsonBody.toMap());
        } catch (EntityNotFoundException e) {
            jsonBody.put("result", false);
            jsonBody.put("error", "user not found");
            return ResponseEntity
                    .badRequest()
                    .body(jsonBody.toMap());
        }
    }

    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity getAllUsers(@RequestParam(value = "firstName", required = false, defaultValue = "") String firstName,
                                      @RequestParam(value = "lastName", required = false, defaultValue = "") String lastName) {
        JSONObject jsonBody = new JSONObject();
        try {
            JSONArray users = new JSONArray();
            if (firstName.isBlank() && lastName.isBlank()) {
                for (User u : userService.getAllUsers()) {
                    users.put(u.toJson());
                }
            } else {
                for (User u : userService.findUsersByName(firstName, lastName)) {
                    users.put(u.toJson());
                }
            }
            jsonBody.put("users", users.toList());

            return ResponseEntity
                    .ok()
                    .body(jsonBody.toMap());
        } catch (Exception e) {
            log.error("An unexpected error was caught", e);
            jsonBody.put("result", false);
            jsonBody.put("error", "unexpected error");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(jsonBody.toMap());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") UUID userId) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            userService.deleteUser(userId);

            jsonBody.put("result", true);
            return ResponseEntity
                    .ok()
                    .body(jsonBody.toMap());
        } catch (EmptyResultDataAccessException e) {
            jsonBody.put("error", "that user does not exist");
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

    @PutMapping("/{id}")
    public ResponseEntity editUser(@PathVariable("id") UUID userId,
                                   @RequestBody Map<String, Object> map) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            User user = userService.getSingleUser(userId);
            editUser(user, map, false);
            user = userService.updateUser(user);
            jsonBody.put("result", true);
            jsonBody.put("user", user);
            return ResponseEntity
                    .ok()
                    .body(jsonBody.toMap());

        } catch (EntityNotFoundException e) {
            jsonBody.put("error", "that user does not exist");
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

    private User editUser(User u, Map<String, Object> map, boolean admin) {
        if(map.containsKey("firstName") && !map.get("firstName").toString().isBlank()){
            u.setFirstName(map.get("firstName").toString());
        }
        if(map.containsKey("lastName") && !map.get("phone").toString().isBlank()){
            u.setLastName(map.get("lastName").toString());
        }
        if(map.containsKey("phone") && !map.get("phone").toString().isBlank()){
            u.setPhone(map.get("phone").toString());
        }
        if(map.containsKey("newPassword") && !map.get("newPassword").toString().isBlank()){
            u.setPassword(map.get("newPassword").toString());
        }
        if(admin) {
            if(map.containsKey("validUntil") && !map.get("validUntil").toString().isBlank()){
                u.setValidUntil(Utilities.toTimestamp(map.get("validUntil").toString()));
            }
            if(map.containsKey("userType") && !map.get("userType").toString().isBlank()){
                u.setUserType(Integer.parseInt(map.get("userType").toString()));
            }
        }
        return u;
    }

    private User mapToUser(Map<String, Object> map) throws ParseException {
        User newUser = new User();
        newUser.setFirstName(map.get("firstName").toString());
        newUser.setLastName(map.get("lastName").toString());
        newUser.setPhone(map.get("phone").toString());
        newUser.setEmail(map.get("email").toString());
        newUser.setPassword(map.get("password").toString());
        newUser.setValidUntil(Utilities.toTimestamp(map.get("validUntil").toString()));
        newUser.setUserType(Integer.parseInt(map.get("userType").toString()));
        return newUser;
    }
}
