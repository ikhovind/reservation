package idatt2105.erlinssl.ikhovind.fullstackbooking.web;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class SectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Room room1 = new Room("romnavn 1");
    private Room room2 = new Room("romnavn 2");
    private Room room3 = new Room("romnavn 3");
    private Room room4 = new Room("romnavn 4");
    private Room room5 = new Room("romnavn 5");

    private Section section1 = new Section("section1 name", "section1 desc");
    private Section section2 = new Section("section2 name", "section2 desc");
    private Section section1clone = new Section("section1 name", "section1 desc");
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
    void addSectionToRoom() throws Exception {
        postSection(room1, section1);
        mockMvc.perform(get("/rooms/" + room1.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.room.sections.[*]",hasSize(1)))
                .andExpect(jsonPath("$.room.sections[*].id",containsInAnyOrder(section1.getId().toString())));
        //test å legg til seksjon med samme navn som tildligere seksjon

        mockMvc.perform(post("/rooms/" + room1.getId() + "/sections").contentType(MediaType.APPLICATION_JSON)
                .content(getBody(section1clone.getSectionName(), section1clone.getSectionDesc())))
                .andExpect(status().isBadRequest()).andExpect(
                MockMvcResultMatchers.jsonPath("$.result").value(false));

        //test å legg til seksjon med null-verdier
        mockMvc.perform(post("/rooms/" + room1.getId() + "/sections").contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\n" +
                                "    \"sectionName\":" + null + ",\n" +
                                "    \"sectionDesc\":" + null + "\n" +
                                "}"
                ))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(false));

        //test å legg til seksjon med tomme verdier

        mockMvc.perform(post("/rooms/" + room1.getId() + "/sections").contentType(MediaType.APPLICATION_JSON)
                .content(getBody("", "")))
                .andExpect(status().isBadRequest()).andExpect(
                MockMvcResultMatchers.jsonPath("$.result").value(false));

        //test å legg til seksjon med samme navn som annen seksjon med til et annet rom

        postSection(room2, section1clone);
        mockMvc.perform(get("/rooms/" + room2.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.room.sections.[*]",hasSize(1)))
                .andExpect(jsonPath("$.room.sections[*].id",containsInAnyOrder(section1clone.getId().toString())));

    }

    private String getBody(String name, String desc) {
        return "{\n" +
                "    \"sectionName\":\"" + name + "\",\n" +
                "    \"sectionDesc\":\"" + desc + "\"\n" +
                "}";
    }

    @Test
    void deleteSection() throws Exception {
        postSection(room1, section1);
        //test å slette seksjon fra feil rom
        mockMvc.perform(delete("/rooms/" + room2.getId() + "/sections/" + section1.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.error", is("section does not belong to room")));
        //test å slette gyldig seksjon fra rom som ikke finnes
        mockMvc.perform(delete("/rooms/" + room3.getId() + "/sections/" + section1.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.error", is("could not find room")));
        //test å slette seksjon som ikke finnes fra gyldig rom
        mockMvc.perform(delete("/rooms/" + room1.getId() + "/sections/" + section2.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.error", is("section does not belong to room")));
        //test å slett seksjon
        mockMvc.perform(delete("/rooms/" + room1.getId() + "/sections/" + section1.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(true)));
    }

    @Test
    void editSection() {
        //test å redigere seksjon
        //test å redigere seksjon fra feil rom
        //test å redigere til tommne verdier
        //test å redigere til null-verdier
        //test å redigere ugyldig seksjon til gyldig rom
        //test å redigere gyldig seksjon fra ugyldig rom
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
    }

    private void postSection (Room room, Section section) throws Exception {
        String body = getBody(section.getSectionName(), section.getSectionDesc());

        String response = mockMvc.perform(post("/rooms/" + room.getId() + "/sections").contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated()).andExpect(
                        MockMvcResultMatchers.jsonPath("$.result").value(true))
                .andReturn().getResponse().getContentAsString();
        JSONArray sectionArray = new JSONObject(response).getJSONObject("room").getJSONArray("sections");
        JSONObject responseJson = sectionArray.getJSONObject(sectionArray.length() - 1);
        String uuid = responseJson.getString("id");
        section.setId(UUID.fromString(uuid));
    }

    private void deleteRoom(Room room) throws Exception {
        mockMvc.perform(delete("/rooms/" + room.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(
                MockMvcResultMatchers.jsonPath("$.result").value(true));
    }

}