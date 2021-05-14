package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.UserService;
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

    @PostMapping(value = "/test")
    public ResponseEntity test() {
        User user = new User("1", "2", "1234", "email2", "4", new Timestamp(new Date().getTime()), 0);
        System.out.println(user.getPassword());
        System.out.println("matches? " + userService.verifyPassword(user, "4"));
        userService.registerNewUserAccount(user);
        return ResponseEntity
                .ok().body("hei");
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity createUser(@RequestBody Map<String, Object> map) {
        JSONObject jsonBody = new JSONObject();
        User newUser;
        try {
            newUser = mapToUser(map);
            newUser = userService.registerNewUserAccount(newUser);
        } catch (ParseException e) {
            jsonBody.put("result", false);
            jsonBody.put("error", "could not parse timestamp");

            return ResponseEntity
                    .badRequest()
                    .body(jsonBody.toMap());
        } catch (IllegalArgumentException e) {
            jsonBody.put("result", false);
            jsonBody.put("error", "email already registered");

            return ResponseEntity
                    .badRequest()
                    .body(jsonBody.toMap());
        } catch (Exception e) {
            jsonBody.put("result", false);
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
        try {
            userService.deleteUser(userId);

            jsonBody.put("result", true);
            return ResponseEntity
                    .ok()
                    .body(jsonBody.toMap());
        } catch(EmptyResultDataAccessException e) {
            jsonBody.put("result", false);
            jsonBody.put("error", "that user does not exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonBody.toMap());
        }catch (Exception e) {
            log.error("An unexpected error was caught", e);
            jsonBody.put("result", false);
            jsonBody.put("error", "unexpected error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(jsonBody.toMap());
        }
    }

    private User mapToUser(Map<String, Object> map) throws ParseException {
        User newUser = new User();
        newUser.setFirstName(map.get("firstName").toString());
        newUser.setLastName(map.get("lastName").toString());
        newUser.setPhone(map.get("phone").toString());
        newUser.setEmail(map.get("email").toString());
        newUser.setPassword(map.get("password").toString());
        log.info("Received timestamp:[" + map.get("validUntil").toString() + "]");
        Timestamp time = new Timestamp(Date.from(Instant.from(
                DateTimeFormatter.ISO_INSTANT.parse(map.get("validUntil").toString()))).getTime());
        newUser.setValid_until(time);
        newUser.setUserType(Integer.parseInt(map.get("userType").toString()));
        return newUser;
    }
}
