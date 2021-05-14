package idatt2105.erlinssl.ikhovind.fullstackbooking.repo;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Reservation;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Iterable<Reservation> findByRoom(Room room);
    Iterable<Reservation> findBySection(Section section);
}
