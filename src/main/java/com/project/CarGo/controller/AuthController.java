package com.project.CarGo.controller;

import com.project.CarGo.entity.User;
import com.project.CarGo.repository.UserRepository;
import com.project.CarGo.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegistrationForm(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasFieldErrors("email") && userRepository.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "email.exists",
                    "An account with this email already exists");
        }

        //Matching passwords
        if (!bindingResult.hasErrors()) {
            String password = user.getPassword();
            String confirmPassword = user.getConfirmPassword();
            if (confirmPassword == null || !password.equals(confirmPassword)) {
                bindingResult.rejectValue("confirmPassword", "password.mismatch",
                        "Passwords do not match.");
            }
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreationDate(new Date());
        user.setUpdateDate(new Date());

        userRepository.save(user);

        emailService.sendEmail(user.getEmail());
        redirectAttributes.addFlashAttribute("message", "Registration Successful");

        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
