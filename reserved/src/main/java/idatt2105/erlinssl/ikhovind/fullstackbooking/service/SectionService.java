package idatt2105.erlinssl.ikhovind.fullstackbooking.service;

import idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions.NotUniqueSectionNameException;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.RoomRepository;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service class that can be used to access the section repository, as well as
 * handling business logic where necessary.
 */
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

    /**
     * Used to call {@link SectionRepository#deleteById(Object)}, which deletes a section.
     * First checks whether the {@link Room} is valid, as well as whether the section
     * is actually part of that room or not.
     *
     * @param roomId    {@link UUID} belonging to the room the section belongs to
     * @param sectionId {@link UUID} belonging to the section
     */
    public void deleteSection(UUID roomId, UUID sectionId) {
        Room room = roomRepository.getOne(roomId);
        if (room.removeSectionById(sectionId)) {
            roomRepository.save(room);
            sectionRepository.deleteById(sectionId);
            return;
        }
        throw new IllegalArgumentException("room and section not connected");
    }

    /**
     * Used when trying to edit a {@link Section}'s name and/or description, first checks
     * whether the {@link Room} is valid, as well as whether the section is actually part
     * of that room or not. Also checks if the room has other sections with the same name.
     *
     * @param roomId the {@link Room} the section belongs to
     * @param section the {@link Section} to edit
     * @return the new edited {@link Section}, or null if something went wrong
     * @throws NotUniqueSectionNameException if a section with the same name already exists in the given room
     */
    public Section editSection(UUID roomId, Section section) {
        if (sectionRepository.existsById(section.getId())) {
            if (roomRepository.getOne(roomId).getSection().stream().anyMatch(
                    s -> s.getSectionName().equals(section.getSectionName())
                            && !s.getId().equals(section.getId()))) {
                throw new NotUniqueSectionNameException("this rooms already contains a section with that name");
            }
            return sectionRepository.save(section);
        }
        return null;
    }
}
