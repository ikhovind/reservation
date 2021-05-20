package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.Constants;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.Utilities;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private User user1 = new User("User1F", "User1L", "11111111",
            "1@1.1", "1P", new Timestamp(new Date().getTime()), Constants.USER_TYPE);
    private User user2 = new User("User2F", "User2L", "22222222",
            "2@2.2", "2P", new Timestamp(new Date().getTime()), Constants.USER_TYPE);
    private User user3 = new User("User3F", "User3L", "33333333",
            "3@3.3", "3P", new Timestamp(new Date().getTime()), Constants.ADMIN_TYPE);
    private final JSONObject user1Json = userToJson(user1);
    private final JSONObject user2Json = userToJson(user2);
    private final JSONObject user3Json = userToJson(user3);
    private final JSONObject user1EditJson = editUserJson(user1Json);

    private static String testingToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1PFIzQHpjazkrNV9jNyQyLyE9OSIsImV4cCI6MTYyMjEwOTI0N30.SN9r84o79qslX_28i3FV5NFT283Akn4Tsk2BYvpia_c";

    private static int initialUsers = 0;

    @BeforeEach
    void setUp() throws Exception {
        // createUser is implicitly tested in the setUp
        postUser(user1Json);
        postUser(user2Json);
    }

    @AfterEach
    void tearDown() throws Exception {
        // deleteUser is implicitly tested in the setUp
        deleteUser(user1Json);
        deleteUser(user2Json);
    }


    @Test
    @Order(1)
    void getInitialUsersTest() throws Exception {
        // Gets a test token is to be used exclusively for test, which lets us bypass
        // otherwise, in the context of testing, inconvenient security measures
        MvcResult result = mockMvc.perform(get("/login/testing/only/endpoint/delete/me/or/change/constant"))
                .andReturn();
        testingToken = new JSONObject(result.getResponse().getContentAsString()).getString("token");

        result = mockMvc.perform(get("/users")
                .header("token", testingToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*.*", hasSize(greaterThan(1))))
                .andReturn();

        // Since the above only expects more than 1 objects to be found, we cannot be completely sure
        // everything worked as we intended on this basis, since there could be pre-existing users in
        // the database. Due to this, we keep track of the initial amount of users that were in the db
        // so that we can test it again later.
        JSONObject usersJson = new JSONObject(result.getResponse().getContentAsString());
        JSONArray test = usersJson.getJSONArray("users");
        initialUsers = test.length() - 2;
        System.out.println("Initial set to: " + initialUsers);
    }

    @Test
    void getSingleUserTest() throws Exception {
        mockMvc.perform(get("/users/" + user1Json.get("id"))
                .header("token", testingToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.firstName", is(user1Json.getString("firstName"))));
    }

    @Test
    void failGetSingleUserTest() throws Exception {
        // Negative test, getting a non-existent user
        mockMvc.perform(get("/users/" + user3Json.get("id"))
                .header("token", testingToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.error", is("user not found")));
    }

    @Test
    void failCreateUserTest() throws Exception {
        // Attempting to register a user with an email that already exists
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(user1Json.toString())
                .header("token", testingToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.error", is("email already registered")));
    }

    @Test
    void failDeleteUserTest() throws Exception {
        // Negative test, attempts to delete a non-existent user
        mockMvc.perform(delete("/users/" + user3Json.get("id"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", testingToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("that user does not exist")));
    }

    @Test
    void getAllUsersTest() throws Exception {
        // If any pre-existing users were created or deleted between this test and
        // getInitialUsersTest, this test will fail.
        mockMvc.perform(get("/users")
                .header("token", testingToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*.*", hasSize(initialUsers + 2)));
    }

    @Test
    void editUserTest() throws Exception {
        mockMvc.perform(put("/users/" + user1Json.get("id"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(user1EditJson.toString())
                .header("token", testingToken))
                .andExpect(jsonPath("$.user.firstName", is("Edited")))
                .andExpect(jsonPath("$.user.lastName", is("Edited")));
    }

    private void postUser(JSONObject u) throws Exception {
        MvcResult result = mockMvc.perform(post("/users")
                .header("token", testingToken)
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
                .header("token", testingToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private JSONObject userToJson(User u) {
        JSONObject res = u.toJson();
        res.put("password", u.getPassword());
        res.put("validUntil", Utilities.timestampToString(u.getValidUntil()));
        System.out.println(u.toSmallJson());
        return res;
    }

    private JSONObject editUserJson(JSONObject original) {
        return new JSONObject(original, JSONObject.getNames(original))
                .put("firstName", "Edited")
                .put("lastName", "Edited")
                .put("phone", 9999999);
    }
}
