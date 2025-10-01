package com.project.CarGo.controller;

import com.project.CarGo.entity.Reservation;
import com.project.CarGo.entity.ReservationStatus;
import com.project.CarGo.entity.User;
import com.project.CarGo.repository.ReservationRepository;
import com.project.CarGo.repository.UserRepository;
import com.project.CarGo.repository.VehicleRepository;
import com.project.CarGo.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;


    public ReservationController(ReservationRepository reservationRepository, ReservationService reservationService, VehicleRepository vehicleRepository, UserRepository userRepository, SecurityProperties securityProperties) {
        this.reservationRepository = reservationRepository;
        this.reservationService = reservationService;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("admin/reservations")
    public String reservations(Model model) {
        var reservations = reservationRepository.findAllWithUser();
        model.addAttribute("reservations", reservations);
        return "reservations";
    }

    @GetMapping("admin/reservations/add")
    public String addForm(Model model) {
        if (!model.containsAttribute("reservation")) {
            var r = new Reservation();
            r.setUser(new User());
            model.addAttribute("reservation", r);
        }
        model.addAttribute("isEdit", false);
        model.addAttribute("statuses", ReservationStatus.values());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "reservation-form";
    }


    @GetMapping("admin/reservations/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        var reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));
        model.addAttribute("reservation", reservation);
        model.addAttribute("isEdit", true);
        model.addAttribute("statuses", ReservationStatus.values());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "reservation-form";
    }

    @PostMapping("admin/reservations/delete/{id}")
    public String deleteReservation(@PathVariable("id") Long id, RedirectAttributes ra) {

        reservationRepository.deleteById(id);
        ra.addFlashAttribute("success", "Reservation deleted successfully!");
        return "redirect:/admin/reservations";
    }

    @PostMapping("admin/reservations/add")
    public String createReservation(@Valid @ModelAttribute("reservation") Reservation reservation,
                                    org.springframework.validation.BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes ra) {

        if (reservation.getUser() == null || reservation.getUser().getId() == null) {
            bindingResult.rejectValue("user", "user.required", "Please select a user.");
        }
        if (reservation.getVehicleId() == null) {
            bindingResult.rejectValue("vehicleId", "vehicle.required", "Please select a vehicle.");
        }
        validateDates(reservation, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("statuses", ReservationStatus.values());
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("vehicles", vehicleRepository.findAll());
            if (reservation.getUser() == null) reservation.setUser(new User()); // keep th:field happy
            return "reservation-form";
        }

        var user = userRepository.findById(reservation.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Selected user not found."));
        var vehicle = vehicleRepository.findById(reservation.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Selected vehicle not found."));

        reservation.setUser(user);

        double total = reservationService.calculateTotalPrice(vehicle, reservation);
        reservation.setTotalPrice(total);
        if (reservation.getStatus() == null) reservation.setStatus(ReservationStatus.PENDING);

        reservationRepository.save(reservation);
        ra.addFlashAttribute("success", "Reservation created successfully.");
        return "redirect:/admin/reservations";
    }

    @PostMapping("admin/reservations/edit/{id}")
    public String updateReservation(@PathVariable Long id,
                                    @Valid @ModelAttribute("reservation") Reservation reservation,
                                    org.springframework.validation.BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes ra) {

        reservation.setId(id);

        if (reservation.getUser() == null || reservation.getUser().getId() == null) {
            bindingResult.rejectValue("user", "user.required", "Please select a user.");
        }
        if (reservation.getVehicleId() == null) {
            bindingResult.rejectValue("vehicleId", "vehicle.required", "Please select a vehicle.");
        }
        validateDates(reservation, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true); // <-- keep edit mode on error
            model.addAttribute("statuses", ReservationStatus.values());
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("vehicles", vehicleRepository.findAll());
            if (reservation.getUser() == null) reservation.setUser(new User());
            return "reservation-form";
        }

        var existing = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));
        reservation.setCreationDate(existing.getCreationDate());

        var user = userRepository.findById(reservation.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Selected user not found."));
        var vehicle = vehicleRepository.findById(reservation.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Selected vehicle not found."));

        reservation.setUser(user);

        double total = reservationService.calculateTotalPrice(vehicle, reservation);
        reservation.setTotalPrice(total);
        
        reservationRepository.save(reservation);
        ra.addFlashAttribute("success", "Reservation updated successfully.");
        return "redirect:/admin/reservations";
    }

    @GetMapping("/user/reservations")
    public String showReservationsByUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        List<Reservation> reservations  = reservationRepository.findAllByUser_Email(email);
        model.addAttribute("reservations", reservations);
        return "user-reservations";
    }

    //check if dates make sense
    private void validateDates(Reservation r, org.springframework.validation.Errors errors) {
        if (r.getReservationStartDate() == null) {
            errors.rejectValue("reservationStartDate", "date.required", "Please choose a start date.");
        }
        if (r.getReservationEndDate() == null) {
            errors.rejectValue("reservationEndDate", "date.required", "Please choose an end date.");
        }
        if (r.getReservationStartDate() != null && r.getReservationEndDate() != null
                && r.getReservationEndDate().before(r.getReservationStartDate())) {
            errors.rejectValue("reservationEndDate", "date.invalid", "End date must be after start date.");
        }
    }

}