package at.spengergasse.spring_thymeleaf.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByDeviceIdOrderByStartTimeAsc(Integer deviceId);

    List<Reservation> findByDeviceIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Integer deviceId,
            LocalDateTime end,
            LocalDateTime start
    );

    List<Reservation> findByPatientIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Integer patientId,
            LocalDateTime end,
            LocalDateTime start
    );











}