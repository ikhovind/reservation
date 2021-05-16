package idatt2105.erlinssl.ikhovind.fullstackbooking.repo;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Reservation;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Iterable<Reservation> findByRoom(Room room);
    Iterable<Reservation> findBySection(Section section);

    @Query(value = "SELECT * FROM reservation r WHERE section_id = :section AND ((r.time_from BETWEEN :timeFF AND :timeFT) OR (r.time_to BETWEEN :timeTF AND :timeTT))",
            nativeQuery = true)
    List<Reservation> findSectionReservationsBetween(@Param("section") Section section, @Param("timeFF") Timestamp timeFF, @Param("timeFT") Timestamp timeFT,
                                                         @Param("timeTF") Timestamp timeTF, @Param("timeTT") Timestamp timeTT);

    @Query(value = "SELECT * FROM reservation r WHERE room_id = :room AND section_id IS NULL AND ((r.time_from BETWEEN :timeFF AND :timeFT) OR (r.time_to BETWEEN :timeTF AND :timeTT))",
            nativeQuery = true)
    List<Reservation> findRoomReservationsBetween(@Param("room") Room room, @Param("timeFF") Timestamp timeFF, @Param("timeFT") Timestamp timeFT,
                                                  @Param("timeTF") Timestamp timeTF, @Param("timeTT") Timestamp timeTT);

    @Query(value = "SELECT * FROM reservation r WHERE room_id = :room AND ((r.time_from BETWEEN :timeFF AND :timeFT) OR (r.time_to BETWEEN :timeTF AND :timeTT))",
            nativeQuery = true)
    List<Reservation> findRoomSectionReservationsBetween(@Param("room") Room room, @Param("timeFF") Timestamp timeFF, @Param("timeFT") Timestamp timeFT,
                                                  @Param("timeTF") Timestamp timeTF, @Param("timeTT") Timestamp timeTT);
}
