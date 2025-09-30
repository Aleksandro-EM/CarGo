package com.project.CarGo.controller;

import com.project.CarGo.entity.*;
import com.project.CarGo.repository.CategoryRepository;
import com.project.CarGo.repository.VehicleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

/*        if(vehicleRepository.existsByTypeAndSubtype(category.getType(), category.getSubtype())) {
            redirectAttributes.addFlashAttribute("error", "This type + subtype already exists.");
            return "redirect:/admin/category/add";
        }*/

        if (bindingResult.hasErrors()) {
            model.addAttribute("types", CategoryType.values());
            model.addAttribute("subtypes", CategorySubtype.values());
            return "vehicle-form";
        }

        vehicle.setCreationDate(new Date());
        vehicle.setUpdateDate(new Date());
        vehicleRepository.save(vehicle);
        redirectAttributes.addFlashAttribute("success", "Vehicle was added successfully!");
        return "redirect:/admin/vehicle/add";
    }
}
