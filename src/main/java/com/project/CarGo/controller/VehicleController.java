package com.project.CarGo.controller;

import com.project.CarGo.entity.*;
import com.project.CarGo.repository.CategoryRepository;
import com.project.CarGo.repository.VehicleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class VehicleController {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/admin/vehicles")
    public String showVehicles(Model model) {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        model.addAttribute("vehicles", vehicles);
        return "vehicles";
    }

    @GetMapping("/admin/vehicle/add")
    public String showAddVehicleForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("statuses", VehicleStatus.values());
        return "vehicle-form";
    }

    @PostMapping("/admin/vehicle/add")
    public String saveVehicle(@Valid @ModelAttribute("vehicle") Vehicle vehicle,
                               BindingResult bindingResult, Model model,
                               RedirectAttributes redirectAttributes) {

        if(vehicleRepository.existsByNumberPlate(vehicle.getNumberPlate())) {
            redirectAttributes.addFlashAttribute("error", "This number plate already exists.");
            return "redirect:/admin/vehicle/add";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("statuses", VehicleStatus.values());
            return "vehicle-form";
        }

        if(vehicle.getCurrentMileage() == null) {
            vehicle.setCurrentMileage(0);
        }

        if(vehicle.getDailyRate() == null) {
            vehicle.setDailyRate(BigDecimal.valueOf(0.00));
        }

        vehicleRepository.save(vehicle);
        redirectAttributes.addFlashAttribute("success", "Vehicle was added successfully!");
        return "redirect:/admin/vehicle/add";
    }

    @GetMapping("/admin/vehicle/edit/{id}")
    public String showEditVehicleForm(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle ID: " + id));

        model.addAttribute("vehicle", vehicle);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("statuses", VehicleStatus.values());
        return "vehicle-form";
    }

    @PostMapping("/admin/vehicle/edit/{id}")
    public String updateVehicle(@PathVariable Long id,
                                 @Valid @ModelAttribute("vehicle") Vehicle vehicle,
                                 BindingResult bindingResult, Model model,
                                 RedirectAttributes redirectAttributes) {

        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle ID: " + id));
        Vehicle v = vehicleRepository.findByNumberPlate(vehicle.getNumberPlate());

        if(!Objects.equals(v.getId(), id)) {
            redirectAttributes.addFlashAttribute("error", "This number plate already exists.");
            return "redirect:/admin/vehicle/edit/" + id;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("statuses", VehicleStatus.values());
            return "vehicle-form";
        }

        vehicle.setId(id);
        if(vehicle.getCurrentMileage() == null) {
            vehicle.setCurrentMileage(0);
        }

        if(vehicle.getDailyRate() == null) {
            vehicle.setDailyRate(BigDecimal.valueOf(0.00));
        }

        vehicle.setCreationDate(existingVehicle.getCreationDate());
        vehicleRepository.save(vehicle);
        redirectAttributes.addFlashAttribute("success", "Vehicle updated successfully!");
        return "redirect:/admin/vehicle/edit/" + id;
    }

    @PostMapping("/admin/vehicle/delete/{id}")
    public String deleteVehicle(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {

        vehicleRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Vehicle deleted successfully!");
        return "redirect:/admin/vehicles";
    }

    @GetMapping("/vehicles")
    public String showFormToDisplayAvailableVehicles(Model model) {
        model.addAttribute("categories", categoryRepository.findCategoriesWithVehicles());
        return "index-vehicles";
    }

    @GetMapping("/vehicles/available")
    public String findAvailableVehicles(
            @RequestParam(value= "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value= "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            Model model, RedirectAttributes redirectAttributes) {

        if(startDate == null && endDate == null) {
            redirectAttributes.addFlashAttribute("error", "Start and end dates are required");
            return "redirect:/vehicles";
        }
        else if(startDate == null) {
            redirectAttributes.addFlashAttribute("error", "Start date is required");
            return "redirect:/vehicles";
        }
        else if(endDate == null) {
            redirectAttributes.addFlashAttribute("error", "End date is required");
            return "redirect:/vehicles";
        }

        if(endDate.before(startDate)) {
            redirectAttributes.addFlashAttribute("error", "End date cannot be before start date.");
            return "redirect:/vehicles";
        }

        List<Vehicle> vehicles = vehicleRepository.findAvailableVehiclesByDateAndCategory(startDate, endDate, categoryId);

        List<Category> categories = categoryRepository.findCategoriesWithVehicles();

        long rentalDays = ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;

        for (Vehicle v : vehicles) {
            BigDecimal dailyRate = v.getDailyRate() != null ? v.getDailyRate() : BigDecimal.ZERO;
            BigDecimal totalPrice = dailyRate.multiply(BigDecimal.valueOf(rentalDays));

            v.setTotalPrice(totalPrice);
        }

        model.addAttribute("vehicles", vehicles);
        model.addAttribute("categories", categories);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("rentalDays", rentalDays);

        return "index-vehicles";
    }
}
