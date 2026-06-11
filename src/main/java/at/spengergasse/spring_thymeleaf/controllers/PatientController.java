package at.spengergasse.spring_thymeleaf.controllers;

import at.spengergasse.spring_thymeleaf.entities.Patient;
import at.spengergasse.spring_thymeleaf.entities.PatientRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping("/list")
    public String patients(Model model) {

        model.addAttribute("patients", patientRepository.findAll());

        return "patlist";
    }

    @GetMapping("/add")
    public String addPatient(Model model) {

        model.addAttribute("patient", new Patient());

        return "add_patient";
    }

    @PostMapping("/add")
    public String addPatient(@ModelAttribute("patient") Patient patient,
                             Model model) {

        // Geburtsdatum prüfen
        if (patient.getBirth() != null &&
                patient.getBirth().isAfter(LocalDate.now())) {

            model.addAttribute("error",
                    "Das Geburtsdatum darf nicht in der Zukunft liegen.");

            return "add_patient";
        }

        // SVNR prüfen
        if (patient.getSocialsecuritynumber() == null ||
                !patient.getSocialsecuritynumber().matches("\\d{10}")) {

            model.addAttribute("error",
                    "Ungültige Sozialversicherungsnummer. Es müssen 10 Ziffern eingegeben werden.");

            return "add_patient";
        }

        // Doppelte SVNR verhindern
        if (patientRepository.existsBySocialsecuritynumber(
                patient.getSocialsecuritynumber())) {

            model.addAttribute("error",
                    "Diese Sozialversicherungsnummer existiert bereits.");

            return "add_patient";
        }

        try {

            patientRepository.save(patient);

        } catch (DataAccessException e) {

            model.addAttribute("error",
                    "Datenbankfehler: MySQL ist nicht erreichbar.");

            return "add_patient";
        }

        return "redirect:/patient/list";
    }
}