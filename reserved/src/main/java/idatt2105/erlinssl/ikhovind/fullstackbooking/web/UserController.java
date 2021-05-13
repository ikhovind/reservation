package idatt2105.erlinssl.ikhovind.fullstackbooking.web;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.UserService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "")
    public ResponseEntity test(){
        User user = new User("1","2","1234", "email2","4", new Timestamp(new Date().getTime()), false);
        System.out.println(user.getPassword());
        System.out.println("matches? " + userService.verifyPassword(user, "4"));
        userService.registerNewUserAccount(user);
        return ResponseEntity
                .ok().body("hei");
    }

    @GetMapping(value = "/{uid}")
    public ResponseEntity getUser(@PathVariable UUID uid) {
        return ResponseEntity
                .ok()
                .body(userService.getSingleUser(uid));
    }

    @GetMapping(value = "")
    public ResponseEntity getAllUsers(@RequestParam(value = "firstName", required = false, defaultValue = "") String firstName,
                                      @RequestParam(value = "lastName", required = false, defaultValue = "") String lastName) {

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

        return ResponseEntity
                .ok()
                .body(users.toList());
    }
}
