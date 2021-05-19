package idatt2105.erlinssl.ikhovind.fullstackbooking.service;

import idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions.IllegalTimeframeException;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Reservation;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Section;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.ReservationRepository;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.Constants;
import idatt2105.erlinssl.ikhovind.fullstackbooking.util.Utilities;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.*;

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

    public List<Reservation> getRoomReservationsBetween(Timestamp timeFrom, Timestamp timeTo, Room room) {
        Timestamp timeFrom2 = borderTimeFrom(timeFrom);
        Timestamp timeTo2 = borderTimeTo(timeTo);
        return reservationRepository.findRoomReservationsBetween(room, timeFrom, timeTo2, timeFrom2, timeTo);
    }

    public Iterable<Reservation> getSectionReservations(Section section) {
        return reservationRepository.findBySection(section);
    }

    public List<Reservation> getSectionReservationsBetween(Timestamp timeFrom, Timestamp timeTo, Section section) {
        Timestamp timeFrom2 = borderTimeFrom(timeFrom);
        Timestamp timeTo2 = borderTimeTo(timeTo);
        return reservationRepository.findSectionReservationsBetween(section, timeFrom, timeTo2, timeFrom2, timeTo);
    }

    public List<Reservation> getRoomAndSectionReservationsBetween(Timestamp timeFrom, Timestamp timeTo, Room room) {
        Timestamp timeFrom2 = borderTimeFrom(timeFrom);
        Timestamp timeTo2 = borderTimeTo(timeTo);
        log.info("time_from [" + timeFrom + "] and [" + timeTo2 + "] | time_to [" + timeFrom2 + "] and [" + timeTo + "]");
        return reservationRepository.findRoomSectionReservationsBetween(room, timeFrom, timeTo2, timeFrom2, timeTo);
    }

    public Reservation getReservationById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Reservation saveReservation(Reservation reservation) throws IllegalTimeframeException {
        validateTimeframe(reservation.getTimeFrom(), reservation.getTimeTo());
        return reservationRepository.save(reservation);
    }

    public void deleteReservationById(UUID id) {
        reservationRepository.deleteById(id);
    }

    private static final int POOF = 1;

    private void validateTimeframe(Timestamp timeFrom, Timestamp timeTo) {
        long timeFromMillis = timeFrom.getTime();
        long timeToMillis = timeTo.getTime();
        if (!Utilities.withinBusinessHours(timeFromMillis, timeToMillis)) {
            throw new IllegalTimeframeException("the selected time is not within business hours");
        }
        long timeUntil = timeFromMillis - new Date().getTime();
        if (timeUntil < 0) {
            throw new IllegalTimeframeException("we do not possess a time machine");
        }
        if (timeUntil > Constants.MAX_TIME_UNTIL_RES) {
            throw new IllegalTimeframeException("you cannot reserve that far ahead");
        }
        long length = timeToMillis - timeFromMillis;
        if (length < Constants.MIN_RESERVATION_MILLIS) {
            throw new IllegalTimeframeException("the given timeframe is too short");
        }
        if (length > Constants.MAX_RESERVATION_MILLIS) {
            throw new IllegalTimeframeException("the given timeframe is too long");
        }
    }

    private Timestamp borderTimeFrom(Timestamp time) {
        return new Timestamp(time.getTime() + POOF);
    }

    private Timestamp borderTimeTo(Timestamp time) {
        return new Timestamp(time.getTime() - POOF);
    }
}
