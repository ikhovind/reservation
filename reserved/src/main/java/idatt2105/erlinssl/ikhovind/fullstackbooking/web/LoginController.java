package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.service.UserService;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.Constants;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.UserTokenRequired;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Endpoint for logging users in. Checks whether the given email belongs to a user,
     * and then if the given password is correct. If either of these are invalid, an
     * error is returned.
     *
     * @param map with values email and password
     * @return ResponseEntity, if result true body has userId, type and token.
     */
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
        jsonBody.put("result", true);
        jsonBody.put("userid", user.getId());
        String token = securityService.createToken(user.getId() + "=" + user.getUserType(), Constants.TTL_MILLIS);
        jsonBody.put("token", token);
        jsonBody.put("userType", user.getUserType());

        return ResponseEntity
                .ok()
                .body(jsonBody.toMap());
    }

    /**
     * Lightweight endpoint that can be used to simply if a users token is
     * still valid or not.
     *
     * @return ResponseEntity with error from {@link UserTokenRequired} or true if token is valid
     */
    @GetMapping("/verify")
    @UserTokenRequired
    private ResponseEntity verifyToken() {
        return ResponseEntity
                .ok()
                .body(new JSONObject()
                        .put("result", true)
                        .toMap());
    }

    /**
     * An endpoint that lets tests run without having to worry about sending a users
     * token with every request. Useful for testing for example rooms and sections,
     * so that you don't have to generate users as well, since they aren't functionally
     * necessary. This endpoint is enabled/disabled based on {@link Constants#TESTING_ENABLED}
     *
     * @return a token created with {@link Constants#TESTING_SUBJECT} if enabled, error if not.
     */
    @GetMapping(value = "/testing/only/endpoint/delete/me/or/change/constant")
    public ResponseEntity testTokenEndpoint() {
        JSONObject jsonBody = new JSONObject();
        if (Constants.TESTING_ENABLED) {
            jsonBody.put("token", securityService.createToken(Constants.TESTING_SUBJECT, Constants.TTL_MILLIS * 7L));
            return ResponseEntity
                    .ok(jsonBody.toMap());
        }
        jsonBody.put("error", "testing disabled");
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(jsonBody.toMap());
    }
}
