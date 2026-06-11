package at.spengergasse.spring_thymeleaf.controllers;

import at.spengergasse.spring_thymeleaf.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @GetMapping("/add")
    public String showForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("devices", deviceRepository.findAll());
        return "add_reservation";
    }

    @PostMapping("/add")
    public String saveReservation(
            @RequestParam("patientId") int patientId,
            @RequestParam("deviceId") int deviceId,
            @ModelAttribute Reservation reservation,
            Model model) {

        Patient patient = patientRepository.findById(patientId).orElse(null);
        Device device = deviceRepository.findById(deviceId).orElse(null);

        reservation.setPatient(patient);
        reservation.setDevice(device);

        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("devices", deviceRepository.findAll());

        if (reservation.getStartTime().isBefore(LocalDateTime.now())) {

            model.addAttribute("error",
                    "Ein Termin darf nicht in der Vergangenheit reserviert werden.");

            return "add_reservation";
        }

        List<Reservation> deviceConflicts =
                reservationRepository
                        .findByDeviceIdAndStartTimeLessThanAndEndTimeGreaterThan(
                                deviceId,
                                reservation.getEndTime(),
                                reservation.getStartTime());

        if (!deviceConflicts.isEmpty()) {

            model.addAttribute("error",
                    "Dieses Gerät ist zu diesem Zeitpunkt bereits reserviert.");

            return "add_reservation";
        }

        List<Reservation> patientConflicts =
                reservationRepository
                        .findByPatientIdAndStartTimeLessThanAndEndTimeGreaterThan(
                                patientId,
                                reservation.getEndTime(),
                                reservation.getStartTime());

        if (!patientConflicts.isEmpty()) {

            model.addAttribute("error",
                    "Der Patient hat bereits einen Termin zu diesem Zeitpunkt.");

            return "add_reservation";
        }

        try {

            reservationRepository.save(reservation);

        } catch (DataAccessException e) {

            model.addAttribute("error",
                    "Datenbankfehler: MySQL ist nicht erreichbar.");

            return "add_reservation";
        }

        return "redirect:/reservation/list";
    }

    @GetMapping("/list")
    public String showReservationList(
            @RequestParam(value = "deviceId", required = false)
            Integer deviceId,
            Model model) {

        model.addAttribute("devices", deviceRepository.findAll());

        if (deviceId != null) {

            model.addAttribute(
                    "reservations",
                    reservationRepository
                            .findByDeviceIdOrderByStartTimeAsc(deviceId));

        } else {

            model.addAttribute(
                    "reservations",
                    reservationRepository.findAll());
        }

        return "reservationlist";
    }
}