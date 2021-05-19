package idatt2105.erlinssl.ikhovind.fullstackbooking.repo;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Reservation;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
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

    // Statistics

    @Query(value = "SELECT user_id, SUM(totalSeconds) FROM (SELECT user_id, TIME_TO_SEC(SUM(timediff(time_to, time_from))) AS totalSeconds FROM reservation WHERE time_from BETWEEN :timeFrom AND :timeTo GROUP BY section_id, user_id) AS thing GROUP BY user_id",
            nativeQuery = true)
    List<Object[]> getUserSums(@Param("timeFrom") Timestamp timeFrom, @Param("timeTo") Timestamp timeTo);

    @Query(value = "SELECT room_id, CAST(SUM(timediff(time_to, time_from)) AS time) AS totalhours FROM reservation WHERE time_from BETWEEN :timeFrom AND :timeTo GROUP BY room_id",
            nativeQuery = true)
    List<Object[]> getRoomSums(@Param("timeFrom") Timestamp timeFrom, @Param("timeTo") Timestamp timeTo);

    @Query(value = "SELECT user_id, room_id, CAST(SUM(timediff(time_to, time_from)) AS time) AS totalhours FROM reservation WHERE time_from BETWEEN :timeFrom AND :timeTo GROUP BY room_id, user_id",
            nativeQuery = true)
    List<Object[]> getRoomSumsGroupByUser(@Param("timeFrom") Timestamp timeFrom, @Param("timeTo") Timestamp timeTo);

    @Query(value = "SELECT section_id, CAST(SUM(timediff(time_to, time_from)) AS time) AS totalhours FROM reservation WHERE room_id = :room AND time_from BETWEEN :timeFrom AND :timeTo GROUP BY section_id",
            nativeQuery = true)
    List<Object[]> getRoomSectionSums(@Param("timeFrom") Timestamp timeFrom, @Param("timeTo") Timestamp timeTo, @Param("room") Room room);

    @Query(value = "SELECT user_id, section_id, CAST(SUM(timediff(time_to, time_from)) AS time) AS totalhours FROM reservation WHERE room_id = :room AND time_from BETWEEN :timeFrom AND :timeTo GROUP BY section_id, user_id",
            nativeQuery = true)
    List<Object[]> getRoomSectionSumsGroupByUser(@Param("timeFrom") Timestamp timeFrom, @Param("timeTo") Timestamp timeTo, @Param("room") Room room);
}
