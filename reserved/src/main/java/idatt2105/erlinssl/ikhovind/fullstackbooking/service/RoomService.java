package idatt2105.erlinssl.ikhovind.fullstackbooking.service;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    public Iterable<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public boolean saveRoom(Room room) {
        roomRepository.save(room);
        return true;
    }

    public Room getRoomById(UUID roomId){
        return roomRepository.getOne(roomId);
    }

    public void deleteRoomById(UUID roomId) {
        roomRepository.deleteById(roomId);
    }

    public boolean editRoom(Room room){
        if(roomRepository.existsById(room.getId())) {
            roomRepository.save(room);
        }
        return false;
    }
}
