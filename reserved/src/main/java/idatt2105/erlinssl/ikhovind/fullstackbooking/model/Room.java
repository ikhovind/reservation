package idatt2105.erlinssl.ikhovind.fullstackbooking.model;

import idatt2105.erlinssl.ikhovind.fullstackbooking.service.SectionService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Room extends BaseModel {
    @Column(unique = true)
    private String roomName;
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Section> section;

    public Room(String roomName) {
        this.roomName = roomName;
        this.section = new ArrayList<>();
    }

    public boolean addSection(Section s) {
        if(!section.contains(s)) {
            section.add(s);
            return true;
        }
        return false;
    }

    public boolean removeSection(Section s) {
        return section.remove(s);
    }

    public boolean removeSectionById(UUID sectionId) {
        for (int i = 0; i < section.size(); i++) {
            Section section1 = section.get(i);
            if (section1.getId().equals(sectionId)) {
                section.remove(i);
                return true;
            }
        }
        return false;
    }

    public JSONObject toJson() {
        JSONObject room = new JSONObject();
        room.put("roomId", getId());
        room.put("roomName", roomName);
        JSONArray sections = new JSONArray();
        for (Section s :
                this.section) {
            sections.put(s.toJson());
        }
        room.put("sections", sections);
        return room;
    }
}
