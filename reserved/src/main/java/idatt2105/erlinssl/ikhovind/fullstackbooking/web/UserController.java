package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions.PermissionDeniedException;
import idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions.TimestampParsingException;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.UserService;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.Utilities;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.AdminTokenRequired;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.UserTokenRequired;
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
import java.text.ParseException;
import java.util.Map;
import java.util.UUID;

/**
 * Web-Controller for /users endpoints.
 * Endpoints in this class are used to create or interact with {@link User} objects.
 */
@Slf4j
@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * An admin-only endpoint that is used to create new user objects and add them to the database.
     *
     * @param map with necessary {@link User} information, see {@link User#toSmallJson()} for field names
     * @return HTTP Status 201 if successful, 400 if input could not be parsed, or 500 if something went wrong
     */
    @AdminTokenRequired
    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity createUser(@RequestBody Map<String, Object> map) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        User newUser;
        try {
            newUser = mapToUser(map);
            newUser = userService.registerNewUserAccount(newUser);

        } catch (ParseException | TimestampParsingException e) {
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

    /**
     * An endpoint that lets an admin see information about a given user.
     *
     * @param userId id of the user to be checked, of string type {@link UUID}
     * @return 201 OK with user as json if successful, 400 Bad Request if the user was not found
     */
    @AdminTokenRequired
    @GetMapping(value = "/{id}")
    public ResponseEntity getUser(@PathVariable("id") UUID userId) {
        JSONObject jsonBody = new JSONObject();
        try {
            User user = userService.getSingleUser(userId);
            jsonBody.put("user", user.toJson());
            jsonBody.put("result", true);
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

    /**
     * An endpoint admins can get all users in the database, and search through these to find a certain
     * user if they so wish.
     *
     * @param firstName String
     * @param lastName  String
     * @return JSONArray with {@link User} objects if successful, error if not
     */
    @AdminTokenRequired
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
            jsonBody.put("users", users);
            jsonBody.put("result", true);
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

    /**
     * Used by admins to delete a certain user.
     *
     * @param userId {@link UUID} of user to be deleted
     * @return 200 OK if successfully deleted, 400/500 with error message if something went wrong
     */
    @AdminTokenRequired
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

    /**
     * An endpoint that can be used by both admins and normal users.
     * The method checks whether the given token belongs to an admin, if not
     * an extra check has to be performed to make sure the user being edited belongs
     * to the owner of the token.
     *
     * @param userId {@link UUID} of the user to edit
     * @param map    with new values to use when updating user
     * @param token  Request Header with JWT
     * @return 200 OK if user was successfully edited, 403/404/500 if not
     */
    @UserTokenRequired
    @PutMapping("/{id}")
    public ResponseEntity editUser(@PathVariable("id") UUID userId,
                                   @RequestBody Map<String, Object> map,
                                   @RequestHeader("token") String token) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("result", false);
        try {
            boolean newPass;
            User user = userService.getSingleUser(userId);
            if (Utilities.isAdmin(token)) {
                newPass = editUser(user, map, true);
            } else {
                Utilities.uidMatch(token, userId);
                newPass = editUser(user, map, false);
            }
            user = userService.updateUser(user, newPass);
            jsonBody.put("user", user);
            jsonBody.put("result", true);
            return ResponseEntity
                    .ok()
                    .body(jsonBody.toMap());

        } catch (PermissionDeniedException e) {
            jsonBody.put("error", "that is not your user");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
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

    private boolean editUser(User u, Map<String, Object> map, boolean admin) {
        boolean passwordChanged = false;
        if (map.containsKey("firstName") && !map.get("firstName").toString().isBlank()) {
            u.setFirstName(map.get("firstName").toString());
        }
        if (map.containsKey("lastName") && !map.get("lastName").toString().isBlank()) {
            u.setLastName(map.get("lastName").toString());
        }
        if (map.containsKey("phone") && !map.get("phone").toString().isBlank()) {
            u.setPhone(map.get("phone").toString());
        }
        if (map.containsKey("newPassword") && !map.get("newPassword").toString().isBlank()) {
            u.setPassword(map.get("newPassword").toString());
            passwordChanged = true;
        }
        if (admin) {
            if (map.containsKey("validUntil") && !map.get("validUntil").toString().isBlank()) {
                u.setValidUntil(Utilities.stringToTimestamp(map.get("validUntil").toString()));
            }
            if (map.containsKey("userType") && !map.get("userType").toString().isBlank()) {
                u.setUserType(Integer.parseInt(map.get("userType").toString()));
            }
        }
        return passwordChanged;
    }

    private User mapToUser(Map<String, Object> map) throws ParseException {
        User newUser = new User();
        newUser.setFirstName(map.get("firstName").toString());
        newUser.setLastName(map.get("lastName").toString());
        newUser.setPhone(map.get("phone").toString());
        newUser.setEmail(map.get("email").toString());
        newUser.setPassword(map.get("password").toString());
        newUser.setValidUntil(Utilities.stringToTimestamp(map.get("validUntil").toString()));
        newUser.setUserType(Integer.parseInt(map.get("userType").toString()));
        return newUser;
    }
}
