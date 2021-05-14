package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.Utilities;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private User user1 = new User("User1F", "User1L", "11111111",
            "1@1.1", "1P", new Timestamp(new Date().getTime()), 0);
    private User user2 = new User("User2F", "User2L", "22222222",
            "2@2.2", "2P", new Timestamp(new Date().getTime()), 0);
    private User user3 = new User("User3F", "User3L", "33333333",
            "3@3.3", "3P", new Timestamp(new Date().getTime()), 1);
    private JSONObject user1Json = userToJson(user1);
    private JSONObject user2Json = userToJson(user2);
    private JSONObject user3Json = userToJson(user3);

    private int initialUsers = 0;

    @BeforeEach
    void setUp() throws Exception {
        postUser(user1Json);
        postUser(user2Json);
    }

    @AfterEach
    void tearDown() throws Exception {
        deleteUser(user1Json);
        deleteUser(user2Json);
    }

    @Test
    void getAllUsersTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*.*", hasSize(greaterThan(1))))
                .andReturn();

        JSONObject usersJson = new JSONObject(result.getResponse().getContentAsString());
        JSONArray test = usersJson.getJSONArray("users");
        initialUsers = test.length()-3;
    }

    @Test
    void getSingleUserTest() throws Exception {
        mockMvc.perform(get("/users/"+user1Json.get("id")))
                .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.firstName", is(user1Json.getString("firstName"))));
    }

    @Test
    void failGetSingleUserTest() throws Exception {
        // Negative test, getting a non-existent user
        mockMvc.perform(get("/users/"+user3Json.get("id")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.error", is("user not found")));
    }

    @Test
    void failCreateUserTest() throws Exception {
        // Attempting to register a user with an email that already exists
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(user1Json.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.error", is("email already registered")));
    }

    @Test
    void failDeleteUserTest() throws Exception {
        // Negative test, attempts to delete a non-existent user
        mockMvc.perform(delete("/users/" + user3Json.get("id")).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("that user does not exist")));
    }

    // TODO Put tests

    private void postUser(JSONObject u) throws Exception {
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(u.toString()))
                .andExpect(status().isCreated())
                .andReturn();
        String uid = Objects.requireNonNull(result.getResponse().getHeader("location")).substring(7);
        System.out.println(uid);
        u.put("id", UUID.fromString(uid));
    }

    private void deleteUser(JSONObject u) throws Exception {
        mockMvc.perform(delete("/users/" + u.get("id"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private JSONObject userToJson(User u) {
        JSONObject res = u.toJson();
        res.put("password", u.getPassword());
        res.put("validUntil", Utilities.timestampToString(u.getValidUntil()));
        return res;
    }
}
