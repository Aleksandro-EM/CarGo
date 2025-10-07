package com.project.CarGo.controller;

import com.project.CarGo.entity.User;
import com.project.CarGo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AppController {

    UserRepository userRepository;

    public AppController(UserRepository userRepository) {
        this.userRepository = userRepository;
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

}
