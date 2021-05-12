package idatt2105.erlinssl.ikhovind.reserved.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Room extends BaseModel {
    private String roomName;
    @OneToMany
    private List<Section> section;

    public Room(String roomName) {
        this.roomName = roomName;
    }
}
