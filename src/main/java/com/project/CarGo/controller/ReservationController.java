package com.project.CarGo.controller;

import com.project.CarGo.entity.Reservation;
import com.project.CarGo.entity.ReservationStatus;
import com.project.CarGo.entity.User;
import com.project.CarGo.repository.ReservationRepository;
import com.project.CarGo.repository.UserRepository;
import com.project.CarGo.repository.VehicleRepository;
import com.project.CarGo.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;


    public ReservationController(ReservationRepository reservationRepository, ReservationService reservationService, VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationService = reservationService;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/reservations")
    public String reservations(Model model) {
        var reservations = reservationRepository.findAllWithUser();
        model.addAttribute("reservations", reservations);
        return "reservations";
    }

    @GetMapping("/reservations/add")
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


    @GetMapping("/reservations/edit/{id}")
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

    @PostMapping("/reservations/delete/{id}")
    public String deleteReservation(@PathVariable("id") Long id, RedirectAttributes ra) {

        reservationRepository.deleteById(id);
        ra.addFlashAttribute("success", "Reservation deleted successfully!");
        return "redirect:/admin/reservations";
    }

    @PostMapping("/reservations/add")
    public String createReservation(@Valid @ModelAttribute("reservation") Reservation reservation,
                                    org.springframework.validation.BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes ra) {

        validateDates(reservation, bindingResult);

        // Ensure vehicle exists
        var vehicleOpt = vehicleRepository.findById(reservation.getVehicleId());
        if (vehicleOpt.isEmpty()) {
            bindingResult.rejectValue("vehicleId", "vehicle.notFound", "Vehicle not found.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false); // or true in edit
            model.addAttribute("statuses", ReservationStatus.values());
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("vehicles", vehicleRepository.findAll()); // <-- add this
            return "reservation-form";
        }

        // calculate price
        var vehicle = vehicleOpt.get();
        double total = reservationService.calculateTotalPrice(vehicle, reservation);
        reservation.setTotalPrice(total);

        // DEfault status
        if (reservation.getStatus() == null) {
            reservation.setStatus(ReservationStatus.PENDING);
        }

        reservationRepository.save(reservation);
        ra.addFlashAttribute("success", "Reservation created successfully.");
        return "redirect:/admin/reservations";
    }

    @PostMapping("/reservations/edit/{id}")
    public String updateReservation(@PathVariable Long id, @Valid @ModelAttribute("reservation") Reservation reservation,
                                    org.springframework.validation.BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes ra) {

        reservation.setId(id);

        validateDates(reservation, bindingResult);

        // Ensure vehicle exists
        var vehicleOpt = vehicleRepository.findById(reservation.getVehicleId());
        if (vehicleOpt.isEmpty()) {
            bindingResult.rejectValue("vehicleId", "vehicle.notFound", "Vehicle not found.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false); // or true in edit
            model.addAttribute("statuses", ReservationStatus.values());
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("vehicles", vehicleRepository.findAll()); // <-- add this
            return "reservation-form";
        }

        reservationRepository.findById(id).ifPresent(existing ->
                reservation.setCreationDate(existing.getCreationDate())
        );

        // Compute price
        var vehicle = vehicleOpt.get();
        double total = reservationService.calculateTotalPrice(vehicle, reservation);
        reservation.setTotalPrice(total);

        reservationRepository.save(reservation);
        ra.addFlashAttribute("success", "Reservation updated successfully.");
        return "redirect:/admin/reservations";
    }

    //check if dates make sense
    private void validateDates(Reservation r, org.springframework.validation.Errors errors) {
        if (r.getReservationStartDate() != null && r.getReservationEndDate() != null) {
            if (!r.getReservationEndDate().isAfter(r.getReservationStartDate())) {
                errors.rejectValue("reservationEndDate", "date.invalid",
                        "End date must be after start date.");
            }
        }
    }

}