package idatt2105.erlinssl.ikhovind.fullstackbooking.web;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "")
    public ResponseEntity test(){
        User user = new User("1","2","3@2","4",false);
        System.out.println(user.getPassword());
        System.out.println("matches? " + userService.verifyPassword(user, "4"));
        userService.registerNewUserAccount(user);
        return ResponseEntity
                .ok().body("hei");
    }
}
