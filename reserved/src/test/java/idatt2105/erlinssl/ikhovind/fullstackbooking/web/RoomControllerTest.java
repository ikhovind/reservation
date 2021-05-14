package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.config.Config;
import idatt2105.erlinssl.ikhovind.fullstackbooking.config.HttpsConfig;
import idatt2105.erlinssl.ikhovind.fullstackbooking.config.JpaAuditingConfig;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Room room1 = new Room("romnavn 1");
    private Room room2 = new Room("romnavn 2");
    private Room room3 = new Room("romnavn 3");
    private Room room4 = new Room("romnavn 4");
    private Room room5 = new Room("romnavn 5");

    @BeforeEach
    void setUp() throws Exception {
        postRoom(room1);
        postRoom(room2);
    }

    @AfterEach
    void tearDown() throws Exception {
        deleteRoom(room1);
        deleteRoom(room2);
    }

    @Test
    void getAllRooms() throws Exception {
        mockMvc.perform(get("/rooms").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rooms.*",hasSize(2)))
                .andExpect(jsonPath("$.rooms.[*].id", containsInAnyOrder(room1.getId().toString(), room2.getId().toString())));
    }

    @Test
    void getSingleRoom() throws Exception {
        mockMvc.perform(get("/rooms/" + room2.getId().toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(jsonPath("$.room.roomId", is(room2.getId().toString())))
                .andExpect(jsonPath("$.room.roomName", is(room2.getRoomName())));
                //.andExpect();
        mockMvc.perform(get("/rooms/" + room3.getId().toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.error", is("room could not be found")));
    }

    @Test
    void saveSingleRoom() throws Exception {
        //only negative testing, positive tests handled implicitly
        mockMvc.perform(post("/rooms").contentType(MediaType.APPLICATION_JSON)
        .content("{\"roomName\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("roomname is null or blank")))
                .andExpect(jsonPath("$.result", is(false)));

        mockMvc.perform(post("/rooms").contentType(MediaType.APPLICATION_JSON)
                .content("{\"roomName\":\"" + room1.getRoomName() + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("room name must be unique")))
                .andExpect(jsonPath("$.result", is(false)));

        mockMvc.perform(post("/rooms").contentType(MediaType.APPLICATION_JSON)
                .content("{\"roomName\":\"                                                         \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("roomname is null or blank")))
                .andExpect(jsonPath("$.result", is(false)));
    }

    @Test
    void editRoom() throws Exception {
        mockMvc.perform(put("/rooms/" + room1.getId()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"roomName\":" + "\"" + room3.getRoomName() + "\"" + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(jsonPath("$.room.roomId", is(room1.getId().toString())))
                .andExpect(jsonPath("$.room.roomName", is(room3.getRoomName())));

        mockMvc.perform(put("/rooms/" + room4.getId()).contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "    \"roomName\":\"" + room1.getRoomName() + "\"" +
                        "}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.error", is("room cannot be found")));

        mockMvc.perform(put("/rooms/" + room1.getId()).contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"roomName\":\"" + room2.getRoomName() + "\"" +
                        "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.error", is("room name must be unique")));
    }

    @Test
    void deleteRoom() throws Exception {
        //only negative testing, positive is tested implicitly
        mockMvc.perform(delete("/rooms/" + room3.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.error", is("cannot find room to delete")));
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
        System.out.println("response is " + responseJson);
        String uuid = responseJson.getString("roomId");
        room.setId(UUID.fromString(uuid));
    }

    private void deleteRoom(Room room) throws Exception {
        mockMvc.perform(delete("/rooms/" + room.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(
                        MockMvcResultMatchers.jsonPath("$.result").value(true));
    }
}