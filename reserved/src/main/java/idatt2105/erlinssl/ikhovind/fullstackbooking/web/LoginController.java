package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.UserService;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.Constants;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Slf4j
@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    UserService userService;
    @Autowired
    SecurityService securityService;

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity login(@RequestBody Map<String, String> map) {
        JSONObject jsonBody = new JSONObject();
        User user = userService.getSingleUserByEmail(map.get("email"));
        if (user == null || !userService.verifyPassword(user, map.get("password"))) {
            jsonBody.put("result", false);
            jsonBody.put("error", "invalid email or password");
            return ResponseEntity
                    .badRequest()
                    .body(jsonBody.toMap());
        }
        HttpHeaders headers = new HttpHeaders();
        jsonBody.put("result", true);
        jsonBody.put("userid", user.getId());
        String token = securityService.createToken(user.getId() + "=" + user.getUserType(), Constants.TTL_MILLIS);
        jsonBody.put("token", token);

        return ResponseEntity
                .ok()
                .body(jsonBody.toMap());
    }
}
