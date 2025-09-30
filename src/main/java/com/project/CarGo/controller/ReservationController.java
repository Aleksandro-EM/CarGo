package com.project.CarGo.controller;

import com.project.CarGo.entity.Reservation;
import com.project.CarGo.repository.ReservationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class ReservationController {

    private final ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/reservations")
    public String reservations(Model model) {
        List<Reservation> categories = reservationRepository.findAll();
        model.addAttribute("reservations", categories);
        return "reservations";
    }

    @PostMapping("/reservations/delete/{id}")
    public String deleteReservation(@PathVariable("id") Long id, RedirectAttributes ra) {

        reservationRepository.deleteById(id);
        ra.addFlashAttribute("success", "Reservation deleted successfully!");
        return "redirect:/admin/reservations";
    }
}
