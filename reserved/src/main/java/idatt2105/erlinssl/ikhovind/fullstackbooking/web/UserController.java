package idatt2105.erlinssl.ikhovind.fullstackbooking.web;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "")
    public ResponseEntity test(){
        User user = new User("1","2","3@2","4",false);
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
    public ResponseEntity getAllUsers() {
        List<UUID> ids = new ArrayList<>();
        for (User u : userService.getAllUsers()) {
            ids.add(u.getUid());
        }
        return ResponseEntity
                .ok()
                .body(ids.toString());
    }
}
