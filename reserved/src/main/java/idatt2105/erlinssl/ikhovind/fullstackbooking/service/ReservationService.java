package idatt2105.erlinssl.ikhovind.fullstackbooking.service;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Reservation;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.ReservationRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Slf4j
@Service
@NoArgsConstructor
@AllArgsConstructor
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public Iterable<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Iterable<Reservation> getRoomReservations(Room room) {
        return reservationRepository.findByRoom(room);
    }

    public Iterable<Reservation> getSectionReservations(Section section) {
        return reservationRepository.findBySection(section);
    }

    public Reservation getReservationById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Reservation res) {
        reservationRepository.delete(res);
    }

    public void deleteReservationById(UUID id) {
        reservationRepository.deleteById(id);
    }
}
