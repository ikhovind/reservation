package idatt2105.erlinssl.ikhovind.fullstackbooking.service;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Reservation;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.ReservationRepository;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.RoomRepository;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

/**
 * Service class that can be used to access the room repository, as well as
 * handling business logic where necessary.
 */
@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    public Iterable<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room getRoomById(UUID roomId) {
        return roomRepository.getOne(roomId);
    }

    private Room getSingleRoom(UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(EntityNotFoundException::new);
    }

    public void deleteRoomById(UUID roomId) {
        Room room = getSingleRoom(roomId);
        for (Reservation r : reservationRepository.findByRoom(room)) {
            User user = userRepository.findById(r.getUser().getId())
                    .orElseThrow(EntityNotFoundException::new);
            user.removeReservation(r);
            userRepository.save(user);
            reservationRepository.delete(r);
        }
        roomRepository.delete(room);
    }

    public boolean roomContainsSection(UUID roomId, UUID sectionId) {
        //check if any of the given sections in the room match the given section id
        return roomRepository.getOne(roomId).getSection().stream().anyMatch(s -> s.getId().equals(sectionId));
    }

    public Room getRoomFromSection(Section section) {
        return roomRepository.findFirstBySectionContains(section);
    }

    public boolean editRoom(Room room) {
        if (roomRepository.existsById(room.getId())) {
            roomRepository.save(room);
        }
        return false;
    }
}
