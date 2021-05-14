package idatt2105.erlinssl.ikhovind.fullstackbooking.service;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.RoomRepository;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SectionService {
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private RoomRepository roomRepository;
    public Iterable<Section> getAllSectionsFromRoom(UUID roomId) {
        return sectionRepository.getSectionByRoomId(roomId);
    }

    public Section saveSection(Section section) {
        return sectionRepository.save(section);
    }

    public Section getSection(UUID sectionId) {
        return sectionRepository.getOne(sectionId);
    }
    public void deleteSection(UUID roomId, UUID sectionId) {
        Room room = roomRepository.getOne(roomId);
        if(room.removeSectionById(sectionId)) {
            roomRepository.save(room);
            sectionRepository.deleteById(sectionId);
            return;
        }
        throw new IllegalArgumentException("room and section not connected");
    }

    public void editSection(Section section) {
        if(sectionRepository.existsById(section.getId())) {
            sectionRepository.save(section);
        }
    }
}
