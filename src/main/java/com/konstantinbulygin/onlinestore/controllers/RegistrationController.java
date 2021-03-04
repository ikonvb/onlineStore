package com.konstantinbulygin.onlinestore.controllers;

import com.konstantinbulygin.onlinestore.model.AdminRepository;
import com.konstantinbulygin.onlinestore.model.UserRepository;
import com.konstantinbulygin.onlinestore.model.data.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository, AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String register(User user) {

        return "register";
    }
}
