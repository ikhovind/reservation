package idatt2105.erlinssl.ikhovind.fullstackbooking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Room extends BaseModel {
    @Column(unique = true)
    private String roomName;
    @OneToMany
    private List<Section> section;

    public Room(String roomName) {
        this.roomName = roomName;
    }

    public JSONObject toJson() {
        JSONObject room = new JSONObject();
        room.put("uid", getUid());
        room.put("roomName", roomName);
        return room;
    }
}
