package com.project.CarGo.controller;

import com.project.CarGo.entity.User;
import com.project.CarGo.repository.UserRepository;
import com.project.CarGo.service.EmailService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class AppController {

    UserRepository userRepository;
    EmailService emailService;

    public AppController(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        model.addAttribute("user", user);
        return "admin-dashboard";
    }

    @GetMapping("/admin/users")
    public String showUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "user-list";
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/contact")
    public String contactInfo() {
        return "contact";

    @PostMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String promoteToAdmin(@PathVariable long id,
                                 java.security.Principal principal,
                                 RedirectAttributes ra) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));

        if (principal != null && user.getEmail().equalsIgnoreCase(principal.getName())) {
            ra.addFlashAttribute("error", "You can’t modify your own role here.");
            return "redirect:/admin/users";
        }
        if ("ROLE_ADMIN".equals(user.getRole())) {
            ra.addFlashAttribute("success", user.getEmail() + " is already an admin.");
            return "redirect:/admin/users";
        }

        int updated = userRepository.updateRole(id, "ROLE_ADMIN");
        if (updated == 1) {
            emailService.sendAdminEmail(user.getEmail());
            ra.addFlashAttribute("success", "Promoted " + user.getEmail() + " to admin.");
        } else {
            ra.addFlashAttribute("error", "Unable to promote user.");
        }
        return "redirect:/admin/users";
    }
}
