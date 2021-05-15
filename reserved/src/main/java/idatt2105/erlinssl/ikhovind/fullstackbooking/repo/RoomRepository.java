package idatt2105.erlinssl.ikhovind.fullstackbooking.repo;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    Room findFirstBySectionContains(Section s);
}
