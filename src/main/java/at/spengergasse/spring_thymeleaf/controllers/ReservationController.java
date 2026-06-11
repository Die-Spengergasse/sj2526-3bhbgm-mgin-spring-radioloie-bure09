package at.spengergasse.spring_thymeleaf.controllers;

import at.spengergasse.spring_thymeleaf.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    //
    @PostMapping("/add")
    public String saveReservation(@RequestParam("patientId") int patientId,
                                  @RequestParam("deviceId") int deviceId,
                                  @ModelAttribute Reservation reservation) {

        Patient patient = patientRepository.findById(patientId).orElse(null);
        Device device = deviceRepository.findById(deviceId).orElse(null);

        reservation.setPatient(patient);
        reservation.setDevice(device);

        reservationRepository.save(reservation);

        return "redirect:/reservation/add";
    }

    @GetMapping("/list")
    public String showReservationList(@RequestParam(value = "deviceId", required = false) Integer deviceId,
                                      Model model) {

        model.addAttribute("devices", deviceRepository.findAll());

        if (deviceId != null) {
            model.addAttribute("reservations", reservationRepository.findByDeviceIdOrderByStartTimeAsc(deviceId));
        } else {
            model.addAttribute("reservations", reservationRepository.findAll());
        }

        return "reservationlist";
    }
}
