package com.project.CarGo.controller;

import com.project.CarGo.entity.*;
import com.project.CarGo.repository.CategoryRepository;
import com.project.CarGo.repository.VehicleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class VehicleController {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/vehicles")
    public String showVehicles(Model model) {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        model.addAttribute("vehicles", vehicles);
        return "vehicles";
    }

    @GetMapping("/vehicle/add")
    public String showAddVehicleForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("statuses", VehicleStatus.values());
        return "vehicle-form";
    }

    @PostMapping("/vehicle/add")
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

        vehicle.setCreationDate(new Date());
        vehicle.setUpdateDate(new Date());
        vehicleRepository.save(vehicle);
        redirectAttributes.addFlashAttribute("success", "Vehicle was added successfully!");
        return "redirect:/admin/vehicle/add";
    }

    @GetMapping("/vehicle/edit/{id}")
    public String showEditVehicleForm(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle ID: " + id));

        model.addAttribute("vehicle", vehicle);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("statuses", VehicleStatus.values());
        return "vehicle-form";
    }

    @PostMapping("/vehicle/edit/{id}")
    public String updateVehicle(@PathVariable Long id,
                                 @Valid @ModelAttribute("vehicle") Vehicle vehicle,
                                 BindingResult bindingResult, Model model,
                                 RedirectAttributes redirectAttributes) {

        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle ID: " + id));

        if(!existingVehicle.getId().equals(id)) {
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
        vehicle.setUpdateDate(new Date());
        vehicleRepository.save(vehicle);
        redirectAttributes.addFlashAttribute("success", "Vehicle updated successfully!");
        return "redirect:/admin/vehicle/edit/" + id;
    }

    @PostMapping("/vehicle/delete/{id}")
    public String deleteVehicle(@PathVariable("id") Long id, RedirectAttributes ra) {

        vehicleRepository.deleteById(id);
        ra.addFlashAttribute("success", "Vehicle deleted successfully!");
        return "redirect:/admin/vehicles";
    }
}
