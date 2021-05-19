package idatt2105.erlinssl.ikhovind.fullstackbooking.service;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Room;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class StatisticsService {
    @Autowired
    private ReservationRepository reservationRepository;

    public List<Object[]> getUserSums(Timestamp timeFrom, Timestamp timeTo){
        return reservationRepository.getUserSums(timeFrom, timeTo);
    }

    public List<Object[]> getRoomSums(Timestamp timeFrom, Timestamp timeTo){
        return reservationRepository.getRoomSums(timeFrom, timeTo);
    }

    public List<Object[]> getRoomSumsGroupByUser(Timestamp timeFrom, Timestamp timeTo){
        return reservationRepository.getRoomSumsGroupByUser(timeFrom, timeTo);
    }

    public List<Object[]> getRoomSectionSums(Timestamp timeFrom, Timestamp timeTo, Room room){
        return reservationRepository.getRoomSectionSums(timeFrom, timeTo, room);
    }

    public List<Object[]> getRoomSectionSumsGroupByUser(Timestamp timeFrom, Timestamp timeTo, Room room){
        return reservationRepository.getRoomSectionSumsGroupByUser(timeFrom, timeTo, room);
    }
}
