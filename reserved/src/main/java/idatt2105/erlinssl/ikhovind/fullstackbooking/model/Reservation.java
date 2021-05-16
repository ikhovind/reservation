package idatt2105.erlinssl.ikhovind.fullstackbooking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reservation extends BaseModel {
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Room room;
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Section section;
    private Timestamp timeFrom;
    private Timestamp timeTo;
    @ManyToOne
    private User user;

    public Reservation(Room room, Section section,
                       Timestamp timeFrom, Timestamp timeTo) {
        this.room = room;
        this.section = section;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
    }

    public JSONObject toJson() {
        JSONObject res = new JSONObject();
        res.put("reservationId", getId());
        res.put("room", new JSONObject()
                .put("roomId", this.room.getId())
                .put("roomName", this.room.getRoomName()));
        res.put("section", (this.section == null ? null : this.section.toJson()));
        res.put("timeFrom", timeFrom);
        res.put("timeTo", timeTo);
        return res;
    }

    public JSONObject toAdminJson() {
        JSONObject res = toJson();
        res.put("user", user.toSmallJson());
        return res;
    }
}
