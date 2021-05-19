package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final User user1 = new User("Reservation", "TestUser", "10101010",
            "resrvation@test.no", "Pass", new Timestamp(new Date().getTime()), Constants.USER_TYPE);
    private static final JSONObject user1Json = userToJson(user1);

    private static final User user2 = new User("Reservation2", "TestUser2", "10101010",
            "resrvation2@test2.no", "Pass", new Timestamp(new Date().getTime()), Constants.ADMIN_TYPE);
    private static final JSONObject user2Json = userToJson(user2);

    private static final Room room1 = new Room("Room 1");
    private static final Room room2 = new Room("Room 2");

    private static final Section section1 = new Section("Section 1", "Super cool section");
    private static final Section section2 = new Section("Section 2", "Not so cool section");

    String timestamp1 = "2021-06-12T16:03:42.493Z"; //  16:00:00
    String timestamp2 = "2021-06-12T16:23:11.123Z"; //  16:30:00
    String timestamp3 = "2021-06-12T16:59:30.224Z"; //  17:00:00
    String timestamp4 = "2021-06-12T17:32:00.000Z"; //  17:30:00

    private static JSONObject reservation1;
    private static JSONObject reservation2;
    private static JSONObject reservation3;
    private static JSONObject reservation4;
    private static JSONObject reservation5;
    private static JSONObject reservation6;

    @Test
    @Order(10)
    void postInitials() throws Exception {
        postUser(user1Json);
        postUser(user2Json);
        setToken(user1Json);
        setToken(user2Json);
        postRoom(room1);
        postRoom(room2);
        postSection(room1, section1);
        postSection(room1, section2);
        reservation1 = generateReservationJson(room1, null, timestamp1, timestamp3); // T
        reservation2 = generateReservationJson(room1, null, timestamp2, timestamp4); // F
        reservation3 = generateReservationJson(room1, section1, timestamp2, timestamp3);    // F
        reservation4 = generateReservationJson(room1, section1, timestamp3, timestamp4);    // T
        reservation5 = generateReservationJson(room1, section2, timestamp3, timestamp4);    // T
        reservation6 = generateReservationJson(room2, section2, timestamp1, timestamp3);    // F
    }

    @Test
    @Order(20)
    void reserveRoomTest() throws Exception {
        postRoomReservation(reservation1, user1Json, true);
    }

    @Test
    @Order(30)
    void failReserveRoomTest() throws Exception {
        // This reservation will fail, because the previous reservation overlaps
        postRoomReservation(reservation2, user1Json, false);
    }

    @Test
    @Order(40)
    void failReserveSectionTest() throws Exception {
        // This reservation will fail, because the room reservation from before overlaps
        postSectionReservation(reservation3, user1Json, false);
    }

    @Test
    @Order(50)
    void reserveBorderingSectionTest() throws Exception {
        // This reservation starts when the room reservation from earlier ends, and is thus accepted
        postSectionReservation(reservation4, user1Json, true);
    }

    @Test
    @Order(60)
    void reserveDifferentSectionTest() throws Exception {
        // This reservation overlaps with the previous one time- and room-wise,
        // but is in a different section so it should be accepted
        postSectionReservation(reservation5, user2Json, true);
    }

    @Test
    @Order(70)
    void failReserveInvalidSectionTest() throws Exception {
        // This
        postSectionReservation(reservation6, user2Json, false);
    }

    @Test
    @Order(80)
    void getAllReservationsTest() throws Exception {
        mockMvc.perform(get("/reservations")
                .header("token", user2Json.get("token")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(90)
    void failGetAllReservationsTest() throws Exception {
        // Normal users can get reservations, but should not
        // be able to see what user the reservation is for
        mockMvc.perform(get("/reservations")
                .header("token", user1Json.get("token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservations.*.user").doesNotExist());
    }

    @Test
    @Order(100)
    void getRoomReservationsTest() throws Exception {
        mockMvc.perform(get("/reservations/rooms/" + room1.getId())
                .header("token", user2Json.get("token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservations", hasSize(greaterThan(0))));
    }

    @Test
    @Order(110)
    void failGetRoomReservationsTest() throws Exception {
        // Normal users can get reservations, but should not
        // be able to see what user the reservation is for
        mockMvc.perform(get("/reservations/rooms/" + room1.getId())
                .header("token", user1Json.get("token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservations.*.user").doesNotExist());
    }

    @Test
    @Order(120)
    void getSectionReservationsTest() throws Exception {
        mockMvc.perform(get("/reservations/rooms/" +
                room1.getId() + "/sections/" + section1.getId())
                .header("token", user2Json.get("token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservations", hasSize(greaterThan(0))));
    }

    @Test
    @Order(130)
    void failGetSectionReservationsTest() throws Exception {
        // Normal users can get reservations, but should not
        // be able to see what user the reservation is for
        mockMvc.perform(get("/reservations/rooms/" +
                room1.getId() + "/sections/" + section1.getId())
                .header("token", user1Json.get("token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservations.*.user").doesNotExist());
    }

    @Test
    @Order(140)
    void editReservation() throws Exception {
        String expected = Utilities.timestampToString(Utilities.stringToTimestamp(timestamp2));
        expected = expected.replace("Z", "+00:00");
        mockMvc.perform(put("/reservations/" + reservation1.get("id"))
                .header("token", user2Json.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(getEditTimestamp(timestamp2, timestamp3)))
                .andExpect(jsonPath("$.reservation.timeFrom", is(expected)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(145)
    void failEditOverlappingReservation() throws Exception {
        mockMvc.perform(put("/reservations/" + reservation4.get("id"))
                .header("token", user2Json.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(getEditTimestamp(timestamp2, timestamp3)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(140)
    void failEditReservation() throws Exception {
        // Normal users are not able to rebook their reservations, therefore this should fail
        mockMvc.perform(put("/reservations/" + reservation1.get("id"))
                .header("token", user1Json.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(getEditTimestamp(timestamp2, timestamp3)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(150)
    void failDeleteReservationNonAdminTest() throws Exception {
        // Normal users are not allowed to delete reservations, therefore this should fail
        mockMvc.perform(delete("/reservations/"+reservation1.get("id"))
                .header("token", user1Json.get("token")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(160)
    void deleteReservationTest() throws Exception {
        mockMvc.perform(delete("/reservations/"+reservation1.get("id"))
                .header("token", user2Json.get("token")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(170)
    void failDeleteReservationTest() throws Exception {
        // This reservation was already deleted, therefore this should fail
        mockMvc.perform(delete("/reservations/"+reservation1.get("id"))
                .header("token", user2Json.get("token")))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(1000)
    void cleanUp() throws Exception {
        deleteUser(user1Json);
        deleteUser(user2Json);
        deleteRoom(room1);
        deleteRoom(room2);
    }

    private JSONObject generateReservationJson(Room room, Section section, String timeFrom, String timeTo) {
        System.out.println("Generating reservation json, roomId is currently " + room.getId());
        return new JSONObject()
                .put("roomId", room.getId())
                .put("sectionId", section == null ? null : section.getId())
                .put("timeFrom", timeFrom)
                .put("timeTo", timeTo);
    }

    private void postRoomReservation(JSONObject reservationJson, JSONObject userJson, boolean positive) throws Exception {
        MvcResult result = mockMvc.perform(post("/reservations/rooms/" +
                reservationJson.get("roomId"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationJson.toString())
                .header("token", userJson.getString("token")))
                .andExpect(positive ? status().isCreated() : status().isForbidden())
                .andExpect(positive ? jsonPath("$.result", is(true)) : jsonPath("$.result", is(false)))
                .andReturn();

        if (positive) {
            String roomId = Objects.requireNonNull(result.getResponse().getHeader("location")).substring(14);
            reservationJson.put("roomId", roomId);
            String resId = new JSONObject(result.getResponse().getContentAsString())
                    .getJSONObject("reservation").get("reservationId").toString();
            reservationJson.put("id", resId);
        }
    }

    private void postSectionReservation(JSONObject reservationJson, JSONObject userJson, boolean positive) throws Exception {
        MvcResult result = mockMvc.perform(post("/reservations/rooms/" +
                reservationJson.get("roomId") + "/sections/" +
                reservationJson.get("sectionId"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationJson.toString())
                .header("token", userJson.getString("token")))
                .andExpect(positive ? status().isCreated() : status().isForbidden())
                .andExpect(positive ? jsonPath("$.result", is(true)) : jsonPath("$.result", is(false)))
                .andReturn();

        if (positive) {
            String uid = Objects.requireNonNull(result.getResponse().getHeader("location")).substring(14);
            reservationJson.put("sectionId", uid);
            String resId = new JSONObject(result.getResponse().getContentAsString())
                    .getJSONObject("reservation").get("reservationId").toString();
            reservationJson.put("id", resId);
        }
    }

    private void postRoom(Room room) throws Exception {
        String response = mockMvc.perform(post("/rooms").contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"roomName\":\"" + room.getRoomName() + "\"\n" +
                        "}"))
                .andExpect(status().isCreated()).andExpect(
                        MockMvcResultMatchers.jsonPath("$.result").value(true))
                .andReturn().getResponse().getContentAsString();
        JSONObject responseJson = new JSONObject(response).getJSONObject("room");
        String uuid = responseJson.getString("roomId");
        room.setId(UUID.fromString(uuid));
        System.out.println(room.getRoomName() + "'s roomId was just set to " + uuid);
    }

    private String getBody(String name, String desc) {
        return "{\n" +
                "    \"sectionName\":\"" + name + "\",\n" +
                "    \"sectionDesc\":\"" + desc + "\"\n" +
                "}";
    }

    private String getEditTimestamp(String timeFrom, String timeTo) {
        return "{" +
                "\"timeFrom\": \"" + timeFrom + "\"," +
                "\"timeTo\": \"" + timeTo + "\" }";
    }

    private void postSection(Room room, Section section) throws Exception {
        String body = getBody(section.getSectionName(), section.getSectionDesc());

        String response = mockMvc.perform(post("/rooms/" + room.getId() + "/sections").contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated()).andExpect(
                        MockMvcResultMatchers.jsonPath("$.result").value(true))
                .andReturn().getResponse().getContentAsString();
        JSONArray sectionArray = new JSONObject(response).getJSONObject("room").getJSONArray("sections");
        JSONObject responseJson = sectionArray.getJSONObject(sectionArray.length() - 1);
        String uuid = responseJson.getString("sectionId");
        section.setId(UUID.fromString(uuid));
        System.out.println("Created section is " + section.toJson().toString());
    }

    private void deleteRoom(Room room) throws Exception {
        mockMvc.perform(delete("/rooms/" + room.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(
                MockMvcResultMatchers.jsonPath("$.result").value(true));
    }

    private void postUser(JSONObject u) throws Exception {
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(u.toString()))
                .andExpect(status().isCreated())
                .andReturn();
        String uid = Objects.requireNonNull(result.getResponse().getHeader("location")).substring(7);
        u.put("id", UUID.fromString(uid));
    }

    private void deleteUser(JSONObject u) throws Exception {
        mockMvc.perform(delete("/users/" + u.get("id"))
                .header("token", user2Json.get("token"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private static JSONObject userToJson(User u) {
        JSONObject res = u.toJson();
        res.put("password", u.getPassword());
        res.put("validUntil", Utilities.timestampToString(u.getValidUntil()));
        return res;
    }

    private String getLoginDetails(JSONObject userJson) {
        return "{" +
                "\"email\": \"" + userJson.get("email") + "\"," +
                "\"password\": \"" + userJson.get("password") + "\"" +
                "}";
    }

    private void setToken(JSONObject userJson) throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getLoginDetails(userJson)))
                .andExpect(status().isOk())
                .andReturn();

        userJson.put("token", new JSONObject(result.getResponse().getContentAsString()).getString("token"));
    }
}
