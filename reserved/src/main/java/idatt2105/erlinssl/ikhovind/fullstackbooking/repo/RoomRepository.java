package idatt2105.erlinssl.ikhovind.fullstackbooking.repo;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
}
