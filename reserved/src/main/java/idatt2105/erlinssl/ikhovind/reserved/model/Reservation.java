package idatt2105.erlinssl.ikhovind.reserved.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Timestamp time_from;
    private Timestamp time_to;

    public Reservation(Room room, Section section, Timestamp time_from, Timestamp time_to) {
        this.room = room;
        this.section = section;
        this.time_from = time_from;
        this.time_to = time_to;
    }
}
