package idatt2105.erlinssl.ikhovind.fullstackbooking.web;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/test")
    public ResponseEntity test(){
        User user = new User("1","2","1234", "email2","4", new Timestamp(new Date().getTime()), 0);
        System.out.println(user.getPassword());
        System.out.println("matches? " + userService.verifyPassword(user, "4"));
        userService.registerNewUserAccount(user);
        return ResponseEntity
                .ok().body("hei");
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity getUser(@PathVariable("id") UUID userId) {
        JSONObject json = new JSONObject();
        User user = userService.getSingleUser(userId);

        if(user == null) {
            return ResponseEntity
                    .badRequest()
                    .body(json.toMap());
        }
        json.put("user", user.toJson());
        return ResponseEntity
                .ok()
                .body(json.toMap());
    }

    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity getAllUsers(@RequestParam(value = "firstName", required = false, defaultValue = "") String firstName,
                                      @RequestParam(value = "lastName", required = false, defaultValue = "") String lastName) {
        JSONObject json = new JSONObject();
        try {
            JSONArray users = new JSONArray();
            if(firstName.isBlank() && lastName.isBlank()) {
                for (User u : userService.getAllUsers()) {
                    users.put(u.toJson());
                }
            } else {
                for (User u : userService.findUsersByName(firstName, lastName)) {
                    users.put(u.toJson());
                }
            }

            json.put("users", users.toList());
            return ResponseEntity
                    .ok()
                    .body(json.toMap());
        } catch (Exception e) {
            log.error("An unexpected error was caught", e);
            json.put("result", false);
            json.put("error", "unexpected error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(json.toMap());
        }
    }
}
